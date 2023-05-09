/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.jira.translators;

import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.translators.TranslatorBase;
import org.eclipse.lyo.core.query.SimpleTerm;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Issue query translator to JQL.
 */
public class IssueTranslator extends TranslatorBase {

    public IssueTranslator(final String decomposedByLinkName, final String decomposesLinkName) {
        super();
        propertiesMap.put("oslc:shortTitle", "key");
        propertiesMap.put("dcterms:subject", configuration.LabelsFieldName);
        propertiesMap.put("dcterms:modified", "updated");
        propertiesMap.put("dcterms:creator", "creator");
        propertiesMap.put("dcterms:created", "created");
        propertiesMap.put("jira:jiraId", "id");
        propertiesMap.put("oslc_rm:decomposedBy", decomposedByLinkName);
        propertiesMap.put("oslc_rm:decomposes", decomposesLinkName);
        propertiesMap.put("jira:project", "project");
        propertiesMap.put("dcterms:identifier", configuration.IdentifierFieldName);
    }

    /**
     * Translate issue keys to linkeIssues JQL query.
     * 
     * @param operands issue keys
     * 
     * @return JQL query
     */
    private String issueKeysToLinkedIssuesTranslator(final String operands) {
        var query = "";
        var exploded = operands.split(",");

        for (var key : exploded) {
            if (query.isEmpty()) {
                query = "(issue in linkedIssues (\"" + key + "\")";
            }
            else {
                query = query + " OR issue in linkedIssues (\"" + key + "\")";
            }
        }

        return query + ")";
    }

    @Override
    public String translate(final SimpleTerm term) {
        var searchString = term.toString();
        var property = term.property().toString();
        var type = term.type();

        if (type == SimpleTerm.Type.COMPARISON) {
            var operator = getComparisonOperatorFromSearch(searchString, property);

            var operand = searchString.replace(property, "").replace(operator, "").replace("\"", "");

            if (property.equalsIgnoreCase("dcterms:identifier") && configuration.SaveIdentifierInLabelsField) {
                operand = translateIdentifiersInLabel(operand);
            }

            if (property.equalsIgnoreCase("jira:project")) {
                operand = translateUriToIds(operand);
            }

            if (property.equalsIgnoreCase("dcterms:creator")) {
                operand = translateEmails(translateUriToIds(operand));
            }

            if (property.equalsIgnoreCase("dcterms:created") || property.equalsIgnoreCase("dcterms:modified")) {
                operand = translateDateTime(operand);
            }

            if (property.equalsIgnoreCase("oslc_rm:decomposedBy") || property.equalsIgnoreCase("oslc_rm:decomposes")) {
                var issueKey = translateIssueUrisToKeys(operand);

                if (operator.equals("=")) {
                    return "(issueLinkType" + " = \"" + propertiesMap.get(property) + "\" AND issue in linkedIssues (" + issueKey + "))";
                }

                if (operator.equals("!=")) {
                    return "(issueLinkType" + " != \"" + propertiesMap.get(property) + "\" OR issue not in linkedIssues (" + issueKey + "))";
                }

                throw new WebApplicationException("Unexpected operator!", Response.Status.BAD_REQUEST);
            }

            return propertiesMap.get(property) + " " + operator + " " + operand;
        }
        else if (type == SimpleTerm.Type.IN_TERM) {
            var operator = originalTypeMap.get(type);

            var operands = searchString.replace(property, "").replace(operator, "")
                    .replace("[", "").replace("]", "").replace("\"", "");

            if (property.equalsIgnoreCase("dcterms:identifier") && configuration.SaveIdentifierInLabelsField) {
                operands = translateIdentifiersInLabel(operands);
            }

            if (property.equalsIgnoreCase("jira:project")) {
                operands = translateUriToIds(operands);
            }

            if (property.equalsIgnoreCase("dcterms:creator")) {
                operands = translateEmails(translateUriToIds(operands));
            }

            if (property.equalsIgnoreCase("oslc_rm:decomposedBy") || property.equalsIgnoreCase("oslc_rm:decomposes")) {
                var issueKeys = translateIssueUrisToKeys(operands);
                return "(issueLinkType" + " = \"" + propertiesMap.get(property) + "\" AND " + issueKeysToLinkedIssuesTranslator(issueKeys) + ")";
            }

            return propertiesMap.get(property) + replaceTypeMap.get(type) + "(" + operands + ")";
        }

        throw new WebApplicationException("Unknown Query operator (" + type.toString() + ")", Response.Status.BAD_REQUEST);
    }
}
