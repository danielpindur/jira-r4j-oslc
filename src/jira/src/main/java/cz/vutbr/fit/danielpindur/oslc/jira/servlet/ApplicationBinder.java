// Start of user code Copyright
/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Simple
 *
 * This file is generated by Lyo Designer (https://www.eclipse.org/lyo/)
 */
// End of user code

package cz.vutbr.fit.danielpindur.oslc.jira.servlet;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

import cz.vutbr.fit.danielpindur.oslc.jira.RestDelegate;
import cz.vutbr.fit.danielpindur.oslc.jira.ResourcesFactory;

import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
// Start of user code imports
import cz.vutbr.fit.danielpindur.oslc.jira.facades.PersonFacade;
import cz.vutbr.fit.danielpindur.oslc.jira.facades.ProjectFacade;
import cz.vutbr.fit.danielpindur.oslc.jira.facades.RequirementFacade;
import cz.vutbr.fit.danielpindur.oslc.jira.facades.RequirementCollectionFacade;
// End of user code

// Start of user code pre_class_code
// End of user code

public class ApplicationBinder extends AbstractBinder {

    private static final Logger log = LoggerFactory.getLogger(ApplicationBinder.class);

    // Start of user code class_attributes
    // End of user code

    // Start of user code class_methods
    // End of user code

    public ApplicationBinder()
    {
        log.info("HK2 contract binding init");
    }

    @Override
    protected void configure() {
        log.info("HK2 contract binding start");

        // Start of user code ConfigureInitialise
        bindAsContract(PersonFacade.class).in(Singleton.class);
        bindAsContract(ProjectFacade.class).in(Singleton.class);
        bindAsContract(RequirementFacade.class).in(Singleton.class);
        bindAsContract(RequirementCollectionFacade.class).in(Singleton.class);
        // End of user code
        bindAsContract(RestDelegate.class).in(Singleton.class);
        bindFactory(ResourcesFactoryFactory.class).to(ResourcesFactory.class).in(Singleton.class);
        // Start of user code ConfigureFinalize
        // End of user code
    }
    static class ResourcesFactoryFactory implements Factory<ResourcesFactory> {
        @Override
        public ResourcesFactory provide() {
            return new ResourcesFactory(OSLC4JUtils.getServletURI());
        }
    
        @Override
        public void dispose(ResourcesFactory instance) {
        }
    }
    
}
