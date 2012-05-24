package org.motechproject.openmrs.rest.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.motechproject.openmrs.rest.util.PasswordCreatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("userAdapter")
public class MRSUserAdapterImpl implements MRSUserAdapter {
	private final static Logger logger = LoggerFactory.getLogger(MRSUserAdapterImpl.class);

	public static final String USER_KEY = "mrsUser";
	public static final String PASS_KEY = "mrsPassword";

	private final Map<String, String> cachedRoles = new HashMap<String, String>();

	private final MRSPersonAdapterImpl personAdapter;
	private final RestfulClient restfulClient;
	private final OpenMrsUrlHolder urlHolder;

	@Autowired
	public MRSUserAdapterImpl(MRSPersonAdapterImpl personAdapter, RestfulClient restfulClient,
	        OpenMrsUrlHolder urlHolder) {
		this.personAdapter = personAdapter;
		this.restfulClient = restfulClient;
		this.urlHolder = urlHolder;
	}

	@Override
	public void changeCurrentUserPassword(String currentPassword, String newPassword) {
		// no way of doing this operation because you cannot retrieve the
		// password
		// for a user through the web services
		throw new UnsupportedOperationException();
	}

	@Override
	public List<MRSUser> getAllUsers() {
		List<MRSUser> users = new ArrayList<MRSUser>();

		try {
			JsonNode resultsArray = restfulClient.getEntityByJsonNode(urlHolder.getUserListFullPath()).get("results");
			for (int i = 0; i < resultsArray.size(); i++) {
				MRSUser user = createMrsUserFromJson(resultsArray.get(0));
				users.add(user);
			}
		} catch (HttpException e) {
			logger.error("Failed to get all users from OpenMRS: " + e.getMessage());
			throw new MRSException(e);
		}

		return users;
	}

	@Override
	public MRSUser getUserByUserName(String username) {
		Validate.notEmpty(username, "Username cannot be empty");

		try {
			JsonNode response = restfulClient.getEntityByJsonNode(urlHolder.getUserListFullByTerm(username));
			response = response.get("results");
			if (response.size() == 0) {
				return null;
			} else if (response.size() != 1) {
				logger.warn("Found multipe user accounts with username: " + username);
			}

			JsonNode userObj = response.get(0);
			MRSUser user = createMrsUserFromJson(userObj);

			return user;
		} catch (HttpException e) {
			logger.error("Failed to retrieve user by username: " + username + " with error: " + e.getMessage());
			throw new MRSException(e);
		}
	}

	private MRSUser createMrsUserFromJson(JsonNode userObj) {
		MRSPerson person = personAdapter.getPerson(userObj.get("person").get("uuid").getValueAsText());

		MRSUser user = new MRSUser();
		user.person(person);
		user.id(userObj.get("uuid").getValueAsText());
		user.userName(userObj.get("username").getValueAsText());
		user.systemId(userObj.get("systemId").getValueAsText());
		user.securityRole(userObj.get("roles").get(0).get("name").getValueAsText());

		return user;
	}

	@Override
	public Map saveUser(MRSUser user) throws UserAlreadyExistsException {
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(user.getUserName(), "Username cannot be empty");

		if (getUserByUserName(user.getUserName()) != null) {
			logger.warn("Already found user with username: " + user.getUserName());
			throw new UserAlreadyExistsException();
		}

		MRSPerson savedPerson = personAdapter.savePerson(user.getPerson());

		String password = PasswordCreatorUtil.create(8);
		ObjectNode userObj = buildJsonFromMrsUser(user, savedPerson.getId(), password);

		try {
			JsonNode response = restfulClient.postForJsonNode(urlHolder.getUserResource(), userObj);
			user.id(response.get("uuid").getValueAsText());
			user.systemId(response.get("systemId").getValueAsText());
			user.getPerson().id(response.get("person").get("uuid").getValueAsText());
		} catch (HttpException e) {
			logger.error("Failed to save user: " + e.getMessage());
			throw new MRSException(e);
		}

		Map<String, Object> values = new HashMap<String, Object>();
		values.put(USER_KEY, user);
		values.put(PASS_KEY, password);

		return values;
	}

	private ObjectNode buildJsonFromMrsUser(MRSUser user, String personId, String password) {
		if (!roleIsPresentInOpenMrs(user.getSecurityRole())) {
			logger.error("Could not find a role in OpenMRS with name: " + user.getSecurityRole());
			throw new MRSException(new RuntimeException("No OpenMRS role found with name: " + user.getSecurityRole()));
		}

		ObjectNode userObj = JsonNodeFactory.instance.objectNode();
		userObj.put("person", personId);
		userObj.put("username", user.getUserName());

		if (StringUtils.isNotEmpty(password)) {
			userObj.put("password", password);
		}

		ArrayNode rolesArray = JsonNodeFactory.instance.arrayNode();
		rolesArray.add(cachedRoles.get(user.getSecurityRole()));

		userObj.put("roles", rolesArray);
		return userObj;
	}

	private boolean roleIsPresentInOpenMrs(String securityRole) {
		if (cachedRoles.containsKey(securityRole)) {
			return true;
		}

		populateRoleCache();
		return cachedRoles.containsKey(securityRole);
	}

	private void populateRoleCache() {
		cachedRoles.clear();
		try {
			JsonNode response = restfulClient.getEntityByJsonNode(urlHolder.getRoleResourceListFull()).get("results");
			for (int i = 0; i < response.size(); i++) {
				JsonNode roleObj = response.get(i);
				cachedRoles.put(roleObj.get("name").getValueAsText(), roleObj.get("uuid").getValueAsText());
			}
		} catch (HttpException e) {
			logger.error("Failed to retrieve the list of roles: " + e.getMessage());
			throw new MRSException(e);
		}
	}

	@Override
	public String setNewPasswordForUser(String username) throws UsernameNotFoundException {
		Validate.notEmpty(username, "Username cannot be empty");
		String newPassword = null;
		try {
			newPassword = PasswordCreatorUtil.create(8);

			JsonNode resultArray = restfulClient.getEntityByJsonNode(urlHolder.getUserByUsername(username)).get(
			        "results");

			if (resultArray.size() == 0) {
				logger.warn("No user foudn with username: " + username);
				throw new UsernameNotFoundException("No user found with username: " + username);
			}

			String uuid = resultArray.get(0).get("uuid").getValueAsText();
			ObjectNode userObj = JsonNodeFactory.instance.objectNode();
			userObj.put("password", newPassword);
			restfulClient.postWithEmptyResponseBody(urlHolder.getUserResourceById(uuid), userObj);

			return newPassword;
		} catch (HttpException e) {
			logger.error("Failed to set new password for user: " + username + " with password: " + newPassword);
			throw new MRSException(e);
		}
	}

	@Override
	public Map<String, Object> updateUser(MRSUser user) {
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(user.getId(), "User id cannot be empty");
		Validate.notEmpty(user.getUserName(), "Username cannot be empty");
		Validate.notEmpty(user.getPerson().getId(), "User person id cannot be empty");

		personAdapter.updatePerson(user.getPerson());
		String password = PasswordCreatorUtil.create(8);
		ObjectNode userObj = buildJsonFromMrsUser(user, user.getPerson().getId(), password);

		try {
			restfulClient.postWithEmptyResponseBody(urlHolder.getUserResourceById(user.getId()), userObj);
		} catch (HttpException e) {
			logger.error("Failed to update user: " + user.getUserName());
			throw new MRSException(e);
		}

		Map<String, Object> values = new HashMap<String, Object>();
		values.put(USER_KEY, user);
		values.put(PASS_KEY, password);

		return values;
	}
}
