/*
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
package org.motechproject.rules.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.framework.ServiceRegistry;
import org.drools.KnowledgeBaseFactoryService;
import org.drools.builder.*;
import org.motechproject.rules.Activator;
import org.motechproject.rules.domain.Rule;
import org.apache.commons.io.IOUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.io.ResourceFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.motechproject.rules.repository.AllRules;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBaseManager implements KnowledgeBaseManagerInterface {

    private static Logger logger = LoggerFactory.getLogger(KnowledgeBaseManager.class);

    private Map<String, KnowledgeBase> knowledgeBaseLookup = new ConcurrentHashMap<String, KnowledgeBase>();

    @Autowired
    private AllRules allRules;

    @Autowired
    private KnowledgeBaseFactoryService knowledgeBaseFactoryService;

    @Autowired
    private KnowledgeBuilderFactoryService knowledgeBuilderFactoryService;

    /**
     * 
     * @param ruleFile
     * @throws IOException
     */
    @Override
    public void addOrUpdateRule(File ruleFile, ClassLoader... cl) throws IOException {
        addOrUpdateRule(ruleFile, null, cl);
    }
    
    /**
     * 
     * @param ruleFile
     * @param bundleSymbolicName
     * @throws IOException
     */
    @Override
    public void addOrUpdateRule(File ruleFile, String bundleSymbolicName, ClassLoader... cl) throws IOException {
        InputStream inputStream = new FileInputStream(ruleFile);
        addOrUpdateRule(ruleFile.getName(), bundleSymbolicName, inputStream, cl);
        inputStream.close();
    }

    /**
     * Add or update a rule in the repository and update the in-memory knowledgeBaseLookup
     * 
     * TODO: this might need re-work if we want to support changing rules on the fly.
     * 
     * @param ruleId
     * @param bundleSymbolicName
     * @param inputStream
     * @throws IOException
     */
    @Override
    public void addOrUpdateRule(String ruleId, String bundleSymbolicName, InputStream inputStream, ClassLoader... cl) throws IOException {
        logger.debug("Adding rule [" + ruleId + "," + bundleSymbolicName + "]");

//        Bundle bundle = FrameworkUtil.getBundle(Activator.class);
//        BundleContext bundleContext = bundle.getBundleContext();
//
//        ServiceReference serviceRef = bundleContext.getServiceReference( ServiceRegistry.class.getName() );
//        ServiceReference serviceRefKBFS = bundleContext.getServiceReference( KnowledgeBaseFactoryService.class.getName() );
//        ServiceRegistry registry = (ServiceRegistry) bundleContext.getService( serviceRef );
//
//        KnowledgeBuilderFactoryService knowledgeBuilderFactoryService = (KnowledgeBuilderFactoryService) registry.getService(bundle, serviceRefKBFS);

//        KnowledgeBaseFactoryService knowledgeBaseFactoryService = registry.get( KnowledgeBaseFactoryService.class );
//
//        KnowledgeBuilderConfiguration kbConf = knowledgeBuilderFactoryService.newKnowledgeBuilderConfiguration( null,  getClass().getClassLoader() );

        Rule rule = null;
        if (allRules.contains(ruleId)) {
            rule = allRules.get(ruleId);
        } else {
            rule = new Rule();
            rule.setId(ruleId);
        }
        rule.setContent(IOUtils.toString(inputStream));
        rule.setBundleSymbolicName(bundleSymbolicName);

        if (rule.isNew()) {
            allRules.add(rule);
        } else {
            allRules.update(rule);
        }

        // update the in-memory knowledgeBaseLookup
        KnowledgeBuilderConfiguration kbuilderConf = knowledgeBuilderFactoryService.newKnowledgeBuilderConfiguration(null, cl);
        KnowledgeBuilder kbuilder = knowledgeBuilderFactoryService.newKnowledgeBuilder(kbuilderConf);
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(rule.getContent())), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            logger.error(kbuilder.getErrors().toString());
        } else {
            KnowledgeBaseConfiguration kbaseConf = knowledgeBaseFactoryService.newKnowledgeBaseConfiguration(null, cl);
            KnowledgeBase kbase = knowledgeBaseFactoryService.newKnowledgeBase(kbaseConf);
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
            knowledgeBaseLookup.put(rule.getId(), kbase);
        }
    }

    /**
     * Get a KnowledgeBase instance by a rule id
     * 
     * @param ruleId
     * @return
     */
    @Override
    public KnowledgeBase getKnowledgeBase(String ruleId) {
    	if (ruleId != null) {
    		return knowledgeBaseLookup.get(ruleId);
		} else {
			return null;
		}
    }
    
    @Override
    public void setAllRules(AllRules allRules) {
        this.allRules = allRules;
    }

}
