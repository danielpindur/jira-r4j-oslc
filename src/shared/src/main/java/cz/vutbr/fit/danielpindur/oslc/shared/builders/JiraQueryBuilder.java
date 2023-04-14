package cz.vutbr.fit.danielpindur.oslc.shared.builders;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.IssueRestClientExtended;
import cz.vutbr.fit.danielpindur.oslc.shared.translators.TranslatorBase;
import org.eclipse.lyo.core.query.SimpleTerm;

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

    private void appendToQuery(final String append) {
        if (append == null || append.isEmpty()) return;

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

    public JiraQueryBuilder Terms(final String terms) {
        if (terms != null) {
            appendToQuery("text ~ " + terms);
        }

        return this;
    }

    public JiraQueryBuilder IssueType(final String issueTypeName) {
        appendToQuery("issuetype = " + issueTypeName);

        return this;
    }

    public JiraQueryBuilder addTerm(final SimpleTerm term) {
        appendToQuery(translator.translate(term));

        return this;
    }
}
