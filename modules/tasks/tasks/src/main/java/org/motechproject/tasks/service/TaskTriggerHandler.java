package org.motechproject.tasks.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.DataProviderLookup;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.tasks.domain.AdditionalData;
import org.motechproject.tasks.domain.EventParamType;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.OperatorType;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.tasks.util.TaskUtil.getSubject;

@Service
public class TaskTriggerHandler {
    private static final String SERVICE_NAME = "taskTriggerHandler";
    private static final String TASK_POSSIBLE_ERRORS_KEY = "task.possible.errors";

    private static final String TRIGGER_PREFIX = "trigger";
    private static final String ADDITIONAL_DATA_PREFIX = "ad";

    private static final Logger LOG = LoggerFactory.getLogger(TaskTriggerHandler.class);

    private TaskService taskService;
    private TaskActivityService activityService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;
    private SettingsFacade settingsFacade;
    private List<DataProviderLookup> dataProviders;

    @Autowired
    public TaskTriggerHandler(final TaskService taskService, final TaskActivityService activityService,
                              final EventListenerRegistryService registryService, final EventRelay eventRelay,
                              final SettingsFacade settingsFacade) {
        this.taskService = taskService;
        this.activityService = activityService;
        this.registryService = registryService;
        this.eventRelay = eventRelay;
        this.settingsFacade = settingsFacade;

        registerHandler();
    }

    public void handle(final MotechEvent triggerEvent) {
        TaskEvent trigger = null;

        try {
            String subject = triggerEvent.getSubject();
            trigger = taskService.findTrigger(subject);
            LOG.info("Found trigger for subject: " + subject);
        } catch (TriggerNotFoundException e) {
            LOG.error(e.getMessage());
        }

        if (trigger != null) {
            for (Task task : taskService.findTasksForTrigger(trigger)) {
                if (!task.isEnabled()) {
                    logOmittedTask(task, new Exception("Task is disabled"));
                    continue;
                }

                TaskEvent action;

                try {
                    action = taskService.getActionEventFor(task);
                    LOG.info("Found action for task: " + task);
                } catch (ActionNotFoundException e) {
                    TaskException exception = new TaskException("error.actionNotFound", e);
                    registerError(task, exception);
                    logOmittedTask(task, exception);
                    continue;
                }

                String subject = action.getSubject();

                if (StringUtils.isBlank(subject)) {
                    TaskException exception = new TaskException("error.actionWithoutSubject");
                    registerError(task, exception);
                    logOmittedTask(task, exception);
                    continue;
                }

                if (task.hasFilters() && !checkFilters(task.getFilters(), triggerEvent.getParameters())) {
                    logOmittedTask(task, new Exception("Filter criteria not met"));
                    continue;
                }

                try {
                    Map<String, Object> parameters = createParameters(task, action.getEventParameters(), triggerEvent);
                    eventRelay.sendEventMessage(new MotechEvent(subject, parameters));
                    activityService.addSuccess(task);
                } catch (TaskException e) {
                    registerError(task, e);
                    logOmittedTask(task, e);
                }
            }
        }
    }

    public final void registerHandlerFor(final String subject) {
        Method method = ReflectionUtils.findMethod(AopUtils.getTargetClass(this), "handle", MotechEvent.class);

        try {
            if (method != null) {
                EventListener proxy = new MotechListenerEventProxy(SERVICE_NAME, this, method);
                registryService.registerListener(proxy, subject);
                LOG.info(String.format("Register TaskTriggerHandler for subject: '%s'", subject));
            }
        } catch (Exception e) {
            LOG.error(String.format("Cant register TaskTriggerHandler for subject: %s", subject), e);
        }
    }

    private void registerHandler() {
        List<Task> tasks = taskService.getAllTasks();

        for (Task t : tasks) {
            registerHandlerFor(getSubject(t.getTrigger()));
        }
    }

    private void registerError(final Task task, final TaskException e) {
        activityService.addError(task, e);

        int errorRunsCount = activityService.errorsFromLastRun(task).size();
        int possibleErrorRun = Integer.valueOf(settingsFacade.getProperty(TASK_POSSIBLE_ERRORS_KEY));

        if (errorRunsCount >= possibleErrorRun) {
            task.setEnabled(false);
            taskService.save(task);
            activityService.addWarning(task);
        }
    }

    private Map<String, Object> createParameters(Task task, List<EventParameter> actionParameters, MotechEvent event) throws TaskException {
        Map<String, Object> parameters = new HashMap<>(actionParameters.size());

        for (EventParameter param : actionParameters) {
            final String key = param.getEventKey();
            String template = task.getActionInputFields().get(key);

            if (template == null) {
                throw new TaskException("error.templateNull", key);
            }

            String userInput = replaceAll(template, event, task);

            Object value;

            if (param.getType().isNumber()) {
                BigDecimal decimal;

                try {
                    decimal = new BigDecimal(userInput);
                } catch (Exception e) {
                    throw new TaskException("error.convertToNumber", key, e);
                }

                if (decimal.signum() == 0 || decimal.scale() <= 0 || decimal.stripTrailingZeros().scale() <= 0) {
                    value = decimal.intValueExact();
                } else {
                    value = decimal.doubleValue();
                }
            } else if (param.getType().isDate()) {
                try {
                    value = DateTime.parse(userInput, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z"));
                } catch (IllegalArgumentException e) {
                    throw new TaskException("error.convertToDate", key, e);
                }
            } else {
                value = userInput;
            }

            parameters.put(key, value);
        }

        return parameters;
    }

    private String replaceAll(final String template, final MotechEvent event, Task task) throws TaskException {
        String replaced = template;
        Key keyInfo;
        List<String> keys = getKeys(replaced);
        for (String key : keys) {
            keyInfo = new Key(key);

            if (keyInfo.getPrefix().equalsIgnoreCase(TRIGGER_PREFIX)) {
                replaced = replaceTriggerKey(event, replaced, keyInfo, key);
            } else if (keyInfo.getPrefix().equalsIgnoreCase(ADDITIONAL_DATA_PREFIX)) {
                replaced = replaceAdditionalDataKey(event, task, replaced, keyInfo, key);
            }
        }

        return replaced;
    }

    private String replaceTriggerKey(MotechEvent event, String template, Key keyInfo, String key) throws TaskException {
        String replaced = "";

        if (event.getParameters().containsKey(keyInfo.getEventKey())) {
            Object obj = event.getParameters().get(keyInfo.getEventKey());

            if (obj == null) {
                obj = "";
            }

            String value = String.valueOf(obj);
            String replaceValue = manipulateValue(value, getManipulation(key));
            replaced = template.replace(String.format("{{%s}}", key), replaceValue);
        }

        return replaced;
    }

    private String replaceAdditionalDataKey(MotechEvent event, Task task, String template, Key keyInfo, String key) throws TaskException {
        String replaced;

        if (dataProviders == null || dataProviders.isEmpty()) {
            throw new TaskException("error.notFoundDataProvider", keyInfo.getObjectType());
        }

        DataProviderLookup provider = findDataProvider(keyInfo.getDataProviderName(), keyInfo.getObjectType());

        if (provider == null || !provider.supports(keyInfo.getObjectType())) {
            throw new TaskException("error.notFoundDataProvider", keyInfo.getObjectType());
        }

        AdditionalData ad = findAdditionalData(task, keyInfo);
        Map<String, String> lookupFields = new HashMap<>();
        lookupFields.put(ad.getLookupField(), event.getParameters().get(ad.getLookupValue()).toString());

        Object found = provider.lookup(keyInfo.getObjectType(), lookupFields);

        if (found == null) {
            throw new TaskException("error.notFoundObjectForType", keyInfo.getObjectType());
        }

        String objectValue = getValueFromObject(found, keyInfo.getEventKey());
        String replaceValue = manipulateValue(objectValue, getManipulation(key));
        replaced = template.replace(String.format("{{%s}}", key), replaceValue);

        return replaced;
    }

    private String manipulateValue(String value, List<String> manipulations) throws TaskException {
        String manipulateValue = value;
        for (String manipulation : manipulations) {
            if (!manipulation.contains("join") && !manipulation.contains("dateTime")) {
                switch (manipulation) {
                    case "toUpper":
                        manipulateValue = manipulateValue.toUpperCase();
                        break;
                    case "toLower":
                        manipulateValue = manipulateValue.toLowerCase();
                        break;
                    case "capitalize":
                        manipulateValue = WordUtils.capitalize(manipulateValue);
                        break;
                    default:
                        break;
                }
            } else if (manipulation.contains("join")) {
                String[] splitValue = manipulateValue.split(" ");
                manipulation = manipulation.substring(5, manipulation.length() - 1);
                manipulateValue = StringUtils.join(splitValue, manipulation);
            } else if (manipulation.contains("dateTime")) {
                try {
                    manipulation = manipulation.substring(9, manipulation.length() - 1);
                    DateTimeFormatter format = DateTimeFormat.forPattern(manipulation);
                    DateTime date = new DateTime(manipulateValue);
                    manipulateValue = format.print(date);
                } catch (IllegalArgumentException e) {
                    throw new TaskException("error.date.format", manipulation, e);
                }
            }
        }
        return manipulateValue;
    }

    private List<String> getKeys(String replaced) {
        List<String> keys = new ArrayList<>();
        String key = "";
        int iteration = 0;
        for (char c : replaced.toCharArray()) {
            if (c == '{') {
                iteration++;
            } else if (c == '}') {
                if (iteration == 2) {
                    keys.add(key);
                    key = "";
                }
                iteration--;
            }
            if (iteration == 2 && c != '{') {
                key = key + c;
            }
        }
        return keys;
    }

    private List<String> getManipulation(String key) {
        List<String> manipulation = new ArrayList<>(Arrays.asList(key.split("\\?")));
        manipulation.remove(0);
        return manipulation;
    }

    private boolean checkFilters(List<Filter> filters, Map<String, Object> triggerParameters) {
        boolean filterCheck = false;

        for (Filter filter : filters) {
            EventParameter eventParameter = filter.getEventParameter();

            if (triggerParameters.containsKey(eventParameter.getEventKey())) {
                EventParamType type = eventParameter.getType();
                Object object = triggerParameters.get(eventParameter.getEventKey());

                if (type.isString()) {
                    filterCheck = checkFilterForString(filter, (String) object);
                } else if (type.isNumber()) {
                    filterCheck = checkFilterForNumber(filter, new BigDecimal(object.toString()));
                }

                if (!filter.isNegationOperator()) {
                    filterCheck = !filterCheck;
                }
            }

            if (!filterCheck) {
                break;
            }
        }

        return filterCheck;
    }

    private boolean checkFilterForString(Filter filter, String param) {
        String expression = filter.getExpression();

        switch (OperatorType.fromString(filter.getOperator())) {
            case EQUALS:
                return param.equals(expression);
            case CONTAINS:
                return param.contains(expression);
            case EXIST:
                return true;
            case STARTSWITH:
                return param.startsWith(expression);
            case ENDSWITH:
                return param.endsWith(expression);
            default:
                return false;
        }
    }

    private boolean checkFilterForNumber(Filter filter, BigDecimal param) {
        if (OperatorType.fromString(filter.getOperator()) == OperatorType.EXIST) {
            return true;
        }

        int compare = param.compareTo(new BigDecimal(filter.getExpression()));

        switch (OperatorType.fromString(filter.getOperator())) {
            case EQUALS:
                return compare == 0;
            case GT:
                return compare == 1;
            case LT:
                return compare == -1;
            default:
                return false;
        }
    }

    private DataProviderLookup findDataProvider(String name, String type) {
        DataProviderLookup providerWithGivenName = null;
        DataProviderLookup providerSupportsGivenType = null;

        for (DataProviderLookup p : dataProviders) {
            if (p.getName().equalsIgnoreCase(name)) {
                providerWithGivenName = p;
                break;
            }

            if (providerSupportsGivenType == null && p.supports(type)) {
                providerSupportsGivenType = p;
            }
        }

        return providerWithGivenName != null ? providerWithGivenName : providerSupportsGivenType;
    }


    private String getValueFromObject(Object object, String eventKey) throws TaskException {
        String[] fields = eventKey.split("\\.");
        Object current = object;

        for (String f : fields) {
            try {
                Method method = current.getClass().getMethod("get" + WordUtils.capitalize(f));
                current = method.invoke(current);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new TaskException("error.objectNotContainsField", e);
            }
        }

        return current.toString();
    }

    private AdditionalData findAdditionalData(Task t, Key key) {
        List<AdditionalData> additionalDatas = t.getAdditionalData(key.getDataProviderName());
        AdditionalData additionalData = null;

        for (AdditionalData ad : additionalDatas) {
            if (ad.getId() == key.getObjectId() && ad.getType().equalsIgnoreCase(key.getObjectType())) {
                additionalData = ad;
                break;
            }
        }

        return additionalData;
    }

    private void logOmittedTask(Task task, Throwable e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Omitted task with ID: %s because: ", task.getId()), e);
        }
    }

    public void addDataProvider(DataProviderLookup provider) {
        if (dataProviders == null) {
            dataProviders = new ArrayList<>();
        }

        dataProviders.add(provider);
    }

    public void removeDataProvider(DataProviderLookup provider) {
        if (dataProviders != null && !dataProviders.isEmpty()) {
            dataProviders.remove(provider);
        }
    }

    void setDataProviders(List<DataProviderLookup> dataProviders) {
        this.dataProviders = dataProviders;
    }

    private final class Key {
        private String prefix;
        private String dataProviderName;
        private String objectType;
        private Long objectId;
        private String eventKey;

        private Key(String key) {
            int questionMarkIndex = key.indexOf('?');
            String withoutManipulation = questionMarkIndex == -1 ? key : key.substring(0, questionMarkIndex);
            int endPrefixIndex = withoutManipulation.indexOf('.');
            prefix = withoutManipulation.substring(0, endPrefixIndex);

            if (prefix.equalsIgnoreCase(TRIGGER_PREFIX)) {
                eventKey = withoutManipulation.substring(endPrefixIndex + 1);
            } else if (prefix.equalsIgnoreCase(ADDITIONAL_DATA_PREFIX)) {
                int endDataProviderNameIndex = withoutManipulation.indexOf('.', endPrefixIndex + 1);
                int endObjectTypeIndex = withoutManipulation.indexOf('.', endDataProviderNameIndex + 1);
                int endObjectId = withoutManipulation.indexOf('#', endDataProviderNameIndex + 1);
                int startObjectId = endObjectTypeIndex - (endObjectTypeIndex - endObjectId);

                dataProviderName = withoutManipulation.substring(endPrefixIndex + 1, endDataProviderNameIndex);
                objectType = withoutManipulation.substring(endDataProviderNameIndex + 1, startObjectId);
                objectId = Long.valueOf(withoutManipulation.substring(startObjectId + 1, endObjectTypeIndex));
                eventKey = withoutManipulation.substring(endObjectTypeIndex + 1);
            }
        }

        public String getPrefix() {
            return prefix;
        }

        public String getDataProviderName() {
            return dataProviderName;
        }

        public String getObjectType() {
            return objectType;
        }

        public Long getObjectId() {
            return objectId;
        }

        public String getEventKey() {
            return eventKey;
        }
    }
}
