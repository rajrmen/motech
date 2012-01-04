package org.motechproject.CampaignDemo.dao;
/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */


import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.CampaignDemo.model.Patient;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Component
public class PatientsCouchDBDAOImpl extends MotechAuditableRepository<Patient> implements PatientDAO
{

    @Autowired
    public PatientsCouchDBDAOImpl(@Qualifier("patientDatabase") CouchDbConnector db) {
        super(Patient.class, db);
    }

	public void addPatient(Patient patient) {
		db.create(patient);
	}

	public void updatePatient(Patient patient) {
		db.update(patient);
	}

	public Patient getPatient(String externalid) {
        Patient patient = db.get(Patient.class, externalid);
        return patient;
	}

	public void removePatient(String externalid) {
		List<Patient> patientList = findByExternalid(externalid);
		if (patientList.size() == 0) {
			return;
		}
        Patient patient = patientList.get(0);
        if (patient == null) {
        	return;
        }
        removePatient(patient);
	}
	
	@GenerateView
	public List<Patient> findByExternalid(String externalid) {
        List<Patient> ret = queryView("by_externalid", externalid);
        if (null == ret) {
            ret  = Collections.<Patient>emptyList();
        }

        return ret;
	}

	public void removePatient(Patient patient) {
		db.delete(patient);
	}
	
    @View(name = "findAllPatients", map = "function(doc) {if (doc.type == 'PATIENT') {emit(null, doc._id);}}")
	public List<Patient> findAllPatients() {
		List<Patient> ret = queryView("findAllPatients");
		if (null == ret) {
			ret = Collections.<Patient>emptyList();
			
		}
		
		return ret;
	}
	
	
}
