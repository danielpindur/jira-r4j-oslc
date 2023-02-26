// Start of user code Copyright
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

package cz.vutbr.fit.danielpindur.oslc.r4j.resources;

import org.eclipse.lyo.oslc4j.core.model.OslcConstants;


// Start of user code imports
// End of user code

public interface Jira_r4jDomainConstants
{
    // Start of user code user constants
    // End of user code

    /**
     * @deprecated use {@link Jira_r4jDomainConstants#JIRA_R4J_NAMSPACE} or {@link Jira_r4jDomainConstants#JIRA_R4J_DOMAIN_NAME} instead
     */
    @Deprecated(since = "5.0.1")
    public static String JIRA_R4J_DOMAIN = "http://stud.fit.vutbr.cz/~xpindu01/oslc/ns/jira_r4j#";
    public static String JIRA_R4J_DOMAIN_NAME = "JIRA R4J";
    public static String JIRA_R4J_NAMSPACE = "http://stud.fit.vutbr.cz/~xpindu01/oslc/ns/jira_r4j#"; //Vocabulary namespace for the resources and resource properties, when no explicit vocabulary (describes, or propertyDefinition) is defined 
    public static String JIRA_R4J_NAMSPACE_PREFIX = "jira_r4j"; //Vocabulary prefix for the resources and resource properties, when no explicit vocabulary (describes, or propertyDefinition) is defined

    public static String FOLDER_PATH = "folder";  //the relative path of the resource shape URL.
    public static String FOLDER_NAMESPACE = Jira_r4jDomainConstants.JIRA_R4J_NAMSPACE; //namespace of the rdfs:class the resource describes
    public static String FOLDER_LOCALNAME = "Folder"; //localName of the rdfs:class the resource describes
    public static String FOLDER_TYPE = FOLDER_NAMESPACE + FOLDER_LOCALNAME; //fullname of the rdfs:class the resource describes
}
