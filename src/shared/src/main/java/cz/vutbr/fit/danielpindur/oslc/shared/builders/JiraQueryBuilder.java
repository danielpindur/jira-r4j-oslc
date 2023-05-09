/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.builders;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.IssueRestClientExtended;
import cz.vutbr.fit.danielpindur.oslc.shared.translators.TranslatorBase;
import org.eclipse.lyo.core.query.SimpleTerm;

/**
 * Jira query builder.
 */
public class JiraQueryBuilder {
    private String query = "";
    private final Configuration configuration;
    private final TranslatorBase translator;

    public JiraQueryBuilder() {
        configuration = ConfigurationProvider.GetConfiguration();
        translator = new TranslatorBase();
    }

    public JiraQueryBuilder(final TranslatorBase translator) {
        configuration = ConfigurationProvider.GetConfiguration();
        this.translator = translator;
    }

    /**
     * Append term to query.
     * 
     * @param append Term to append.
     */
    private void appendToQuery(final String append) {
        if (append == null || append.isEmpty()) return;

        if (query.isEmpty()) {
            query = append;
        }
        else {
            query = query + " AND " + append;
        }
    }

    /**
     * Build query.
     * 
     * @return Query.
     */
    public String build() {
        return query;
    }

    /**
     * Append identifier to query.
     * 
     * @param identifier Identifier to append.
     * 
     * @return Jira query builder.
     */
    public JiraQueryBuilder Identifier(final String identifier) {
        if (configuration.SaveIdentifierInLabelsField) {
            appendToQuery(configuration.LabelsFieldName + " = \"" + IssueHelper.GetFormattedLabelsIdentifier(identifier) + "\"");
        } else {
            appendToQuery(configuration.IdentifierFieldName + " ~ " + identifier);
        }

        return this;
    }

    /**
     * Append project to query.
     * 
     * @param project Project to append.
     * 
     * @return Jira query builder.
     */
    public JiraQueryBuilder Terms(final String terms) {
        if (terms != null) {
            appendToQuery("text ~ \"" + terms + "\"");
        }

        return this;
    }

    /**
     * Append issue type to query.
     * 
     * @param issueTypeName Issue type to append.
     * 
     * @return Jira query builder.
     */
    public JiraQueryBuilder IssueType(final String issueTypeName) {
        appendToQuery("issuetype = \"" + issueTypeName + "\"");

        return this;
    }

    /**
     * Append term to query.
     * 
     * @param term Term to append.
     * 
     * @return Jira query builder.
     */
    public JiraQueryBuilder addTerm(final SimpleTerm term) {
        appendToQuery(translator.translate(term));

        return this;
    }
}
