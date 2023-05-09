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

package cz.vutbr.fit.danielpindur.oslc.jira.resources;

import org.eclipse.lyo.oslc4j.core.model.OslcConstants;


// Start of user code imports
// End of user code

public interface JiraDomainConstants
{
    // Start of user code user constants
    // End of user code

    /**
     * @deprecated use {@link JiraDomainConstants#JIRA_NAMSPACE} or {@link JiraDomainConstants#JIRA_DOMAIN_NAME} instead
     */
    @Deprecated(since = "5.0.1")
    public static String JIRA_DOMAIN = "http://fit.vutbr.cz/group/verifit/oslc/ns/jira#";
    public static String JIRA_DOMAIN_NAME = "JIRA";
    public static String JIRA_NAMSPACE = "http://fit.vutbr.cz/group/verifit/oslc/ns/jira#"; //Vocabulary namespace for the resources and resource properties, when no explicit vocabulary (describes, or propertyDefinition) is defined 
    public static String JIRA_NAMSPACE_PREFIX = "jira"; //Vocabulary prefix for the resources and resource properties, when no explicit vocabulary (describes, or propertyDefinition) is defined

    public static String PROJECT_PATH = "project";  //the relative path of the resource shape URL.
    public static String PROJECT_NAMESPACE = JiraDomainConstants.JIRA_NAMSPACE; //namespace of the rdfs:class the resource describes
    public static String PROJECT_LOCALNAME = "Project"; //localName of the rdfs:class the resource describes
    public static String PROJECT_TYPE = PROJECT_NAMESPACE + PROJECT_LOCALNAME; //fullname of the rdfs:class the resource describes
}
