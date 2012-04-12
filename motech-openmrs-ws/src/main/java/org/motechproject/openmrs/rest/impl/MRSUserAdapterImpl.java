package org.motechproject.openmrs.rest.impl;

import java.util.List;
import java.util.Map;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MRSUserAdapterImpl implements MRSUserAdapter {

	@Override
    public void changeCurrentUserPassword(String arg0, String arg1) {
		throw new NotImplementedException();
    }

	@Override
    public List<MRSUser> getAllUsers() {
		throw new NotImplementedException();    
	}

	@Override
    public MRSUser getUserByUserName(String arg0) {
		throw new NotImplementedException();    
	}

	@Override
    public Map saveUser(MRSUser user) throws UserAlreadyExistsException {
		throw new NotImplementedException();
	}

	@Override
    public String setNewPasswordForUser(String arg0) throws UsernameNotFoundException {
		throw new NotImplementedException();
	}

	@Override
    public Map updateUser(MRSUser arg0) {
		throw new NotImplementedException();
	}
}
