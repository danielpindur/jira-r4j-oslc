package cz.vutbr.fit.danielpindur.oslc.shared.helpers;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;

import static java.util.UUID.randomUUID;

public final class IssueHelper {
    private static final Configuration configuration = ConfigurationProvider.GetConfiguration();

    public static boolean IsRequirement(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase(configuration.RequirementIssueTypeName);
    }

    public static boolean IsRequirementCollection(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase(configuration.RequirementCollectionIssueTypeName);
    }

    public static String GetFormattedLabelsIdentifier(final String identifier) {
        var format = ConfigurationProvider.GetConfiguration().LabelsIdentifierFormat;
        return format.replace("{0}", identifier);
    }

    public static String CreateIssueGUID() {
        return randomUUID().toString();
    }
}
