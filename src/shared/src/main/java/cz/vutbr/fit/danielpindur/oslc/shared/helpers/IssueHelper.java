/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.helpers;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;

import static java.util.UUID.randomUUID;

/**
 * Helper class for the Issue.
 */
public final class IssueHelper {
    private static final Configuration configuration = ConfigurationProvider.GetConfiguration();

    /**
     * Validate if the issue is a requirement.
     *
     * @param issue Issue to validate.
     *
     * @return True if the issue is a requirement, false otherwise.
     */
    public static boolean IsRequirement(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase(configuration.RequirementIssueTypeName);
    }

    /**
     * Validate if the issue is a requirement collection.
     *
     * @param issue Issue to validate.
     *
     * @return True if the issue is a requirement collection, false otherwise.
     */
    public static boolean IsRequirementCollection(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase(configuration.RequirementCollectionIssueTypeName);
    }

    /**
     * Get indentifier for storing in labels field formated according to the configuration.
     * 
     * @param identifier Identifier to format.
     * 
     * @return Formatted identifier.
     */
    public static String GetFormattedLabelsIdentifier(final String identifier) {
        var format = ConfigurationProvider.GetConfiguration().LabelsIdentifierFormat;
        return format.replace("{0}", identifier);
    }

    /**
     * Create a GUID for the issue.
     * 
     * @return GUID for the issue.
     */
    public static String CreateIssueGUID() {
        return randomUUID().toString();
    }
}
