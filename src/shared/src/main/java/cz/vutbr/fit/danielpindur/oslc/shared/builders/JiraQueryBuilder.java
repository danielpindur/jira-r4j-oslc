package cz.vutbr.fit.danielpindur.oslc.shared.builders;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.IssueRestClientExtended;

public class JiraQueryBuilder {
    private String query = "";
    private final Configuration configuration;

    public JiraQueryBuilder() {
        configuration = ConfigurationProvider.GetConfiguration();
    }

    private void appendToQuery(final String append) {
        if (query.isEmpty()) {
            query = append;
        }
        else {
            query = query + " AND " + append;
        }
    }

    public String build() {
        return query;
    }

    public JiraQueryBuilder Identifier(final String identifier) {
        if (configuration.SaveIdentifierInLabelsField) {
            appendToQuery(configuration.LabelsFieldName + " = " + IssueHelper.GetFormattedLabelsIdentifier(identifier));
        } else {
            appendToQuery(configuration.IdentifierFieldName + " ~ " + identifier);
        }

        return this;
    }

    public JiraQueryBuilder Terms(final String terms)
    {
        if (terms != null) {
            appendToQuery("text ~ " + terms);
        }

        return this;
    }

    public JiraQueryBuilder IssueType(final String issueTypeName)
    {
        appendToQuery("issuetype = " + issueTypeName);

        return this;
    }

    public JiraQueryBuilder addTerm() {
        return null;
    }
}
