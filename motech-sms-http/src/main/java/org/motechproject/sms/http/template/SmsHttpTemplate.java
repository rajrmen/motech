package org.motechproject.sms.http.template;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.motechproject.sms.http.domain.HttpMethodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SmsHttpTemplate {
    public static final String MESSAGE_PLACEHOLDER = "$message";
    public static final String RECIPIENTS_PLACEHOLDER = "$recipients";

    private Outgoing outgoing;
    private Incoming incoming;

    public List<HttpMethod> generateRequestFor(List<String> recipients, String message) {
        List<HttpMethod> httpMethods = new ArrayList<HttpMethod>();

        if (isMultiRecipientSupported()) {
            String recipientList = StringUtils.join(recipients.iterator(), outgoing.getRequest().getRecipientsSeparator());
            httpMethods.add(generateRequestFor(recipientList, message));
        } else {
            for (String recipient : recipients)
                httpMethods.add(generateRequestFor(recipient, message));
        }
        return httpMethods;
    }

    private HttpMethod generateRequestFor(String recipient, String message) {
        HttpMethod httpMethod;
        if (HttpMethodType.POST.equals(outgoing.getRequest().getType())) {
            httpMethod = new PostMethod(outgoing.getRequest().getUrlPath());
            addBodyParameters((PostMethod) httpMethod, recipient, message);
        } else
            httpMethod = new GetMethod(outgoing.getRequest().getUrlPath());

        httpMethod.setQueryString(addQueryParameters(recipient, message));
        return httpMethod;
    }

    private NameValuePair[] addQueryParameters(String recipient, String message) {
        List<NameValuePair> queryStringValues = new ArrayList<NameValuePair>();
        Map<String, String> queryParameters = outgoing.getRequest().getQueryParameters();
        for (String key : queryParameters.keySet()) {
            String value = placeHolderOrLiteral(queryParameters.get(key), recipient, message);
            queryStringValues.add(new NameValuePair(key, value));
        }
        return queryStringValues.toArray(new NameValuePair[queryStringValues.size()]);
    }

    private void addBodyParameters(PostMethod postMethod, String recipient, String message) {
        Map<String, String> bodyParameters = outgoing.getRequest().getBodyParameters();
        for (String key : bodyParameters.keySet()) {
            String value = placeHolderOrLiteral(bodyParameters.get(key), recipient, message);
            postMethod.setParameter(key, value);
        }
    }

    private String placeHolderOrLiteral(String value, String recipient, String message) {
        if (value.equals(MESSAGE_PLACEHOLDER))
            return message;
        if (value.equals(RECIPIENTS_PLACEHOLDER))
            return recipient;
        return value;
    }

    public Outgoing getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(Outgoing outgoing) {
        this.outgoing = outgoing;
    }

    public Incoming getIncoming() {
        return incoming;
    }

    public void setIncoming(Incoming incoming) {
        this.incoming = incoming;
    }

    public Authentication getAuthentication() {
        return outgoing.getRequest().getAuthentication();
    }

    public String getResponseSuccessCode() {
        return outgoing.getResponse().getSuccess();
    }

    public boolean isMultiRecipientSupported() {
        return outgoing.getRequest().isMultiRecipientSupported();
    }
}
