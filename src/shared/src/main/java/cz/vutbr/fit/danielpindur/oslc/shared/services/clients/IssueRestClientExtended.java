package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.SessionRestClient;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousIssueRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.UUID.randomUUID;

public class IssueRestClientExtended extends AsynchronousIssueRestClient {
    private final URI baseUri;
    private final Configuration configuration;

    private final MetadataRestClient metadataRestClient;

    public IssueRestClientExtended(URI baseUri, HttpClient client, SessionRestClient sessionRestClient, MetadataRestClient metadataRestClient) {
        super(baseUri, client, sessionRestClient, metadataRestClient);
        this.baseUri = baseUri;
        configuration = ConfigurationProvider.getInstance().GetConfiguration();
        this.metadataRestClient = metadataRestClient;
    }

    public boolean IsRequirement(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase(configuration.RequirementIssueTypeName);
    }

    public boolean IsRequirementCollection(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase(configuration.RequirementCollectionIssueTypeName);
    }

    public String GetFieldId(final String fieldName, Iterable<Field> fields) {
        for (var field : fields ) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                return field.getId();
            }
        }

        return null;
    }

    public String GetFieldId(final String fieldName) {
        var fields = metadataRestClient.getFields().claim();
        return GetFieldId(fieldName, fields);
    }

    public String getFieldStringValue(final String fieldName, final Issue issue) {
        var fieldId = GetFieldId(fieldName);
        if (fieldId == null) {
            throw new WebApplicationException("Failed to find fieldId for " + fieldName + "!", Response.Status.CONFLICT);
        }

        if (fieldId.equalsIgnoreCase("Labels")) {
            return String.join(",", issue.getLabels());
        }

        var field = issue.getField(fieldId);
        if (field == null) {
            throw new WebApplicationException("Failed to find field for fieldId " + fieldId + "!", Response.Status.CONFLICT);
        }

        return field.getValue() != null ? field.getValue().toString() : null;
    }

    private Set<String> getFieldStringSetValue(final String fieldName, final Issue issue) {
        var fieldId = GetFieldId(fieldName);
        if (fieldId == null) {
            throw new WebApplicationException("Failed to find fieldId for " + fieldName + "!", Response.Status.CONFLICT);
        }

        if (fieldId.equalsIgnoreCase("Labels")) {
            return issue.getLabels();
        }

        var field = issue.getField(fieldId);
        if (field == null) {
            throw new WebApplicationException("Failed to find field for fieldId " + fieldId + "!", Response.Status.CONFLICT);
        }

        return field.getValue() != null ? (HashSet<String>) field.getValue() : new HashSet<String>();
    }

    public Set<String> getFieldStringSetValueWithoutIdentifier(final String fieldName, final Issue issue) {
        var values = getFieldStringSetValue(fieldName, issue);
        var filteredValues = new HashSet<String>();
        var identifierFieldFormat = configuration.LabelsIdentifierFormat;
        var pattern = Pattern.compile(identifierFieldFormat.replace("{0}", "(.+)"));

        for (var value : values) {
            var matcher = pattern.matcher(value);

            if (!matcher.find()) {
                filteredValues.add(value);
            }
        }

        return filteredValues;
    }

    private String getIssueGUIDFromIdentifierField(final Issue issue) {
        var identifierFieldName = configuration.IdentifierFieldName;
        return getFieldStringValue(identifierFieldName, issue);
    }

    private String getIssueGUIDFromLabelsField(final Issue issue) {
        var identifierFieldFormat = configuration.LabelsIdentifierFormat;
        var pattern = Pattern.compile(identifierFieldFormat.replace("{0}", "(.+)"));
        var labels = getFieldStringSetValue(configuration.LabelsFieldName, issue);

        for (var label : labels) {
            var matcher = pattern.matcher(label);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    private void setIssueGUIDInIdentifierField(final Issue issue, final String identifier) {
        var identifierFieldId = GetFieldId(configuration.IdentifierFieldName);

        if (identifierFieldId == null) {
            throw new WebApplicationException("Field Identifier not found, failed to update Issue GUID!", Response.Status.CONFLICT);
        }

        updateIssue(issue.getKey(),
                new IssueInputBuilder(issue.getProject().getKey(), issue.getIssueType().getId())
                        .setFieldInput(new FieldInput(identifierFieldId, identifier))
                        .build())
                .claim();
    }

    private void setIssueGUIDInLabelsField(final Issue issue, final String identifier) {
        var labelsFieldId = GetFieldId(configuration.LabelsFieldName);

        if (labelsFieldId == null) {
            throw new WebApplicationException("Field Labels not found, failed to update Issue GUID!", Response.Status.CONFLICT);
        }

        var subject = issue.getLabels();
        subject.add(getFormattedLabelsIdentifier(identifier));

        updateIssue(issue.getKey(),
                new IssueInputBuilder(issue.getProject().getKey(), issue.getIssueType().getId())
                        .setFieldInput(new FieldInput(labelsFieldId, subject))
                        .build())
                .claim();
    }

    private void setIssueGUID(final Issue issue, final String identifier) {
        if (configuration.SaveIdentifierInLabelsField) {
            setIssueGUIDInLabelsField(issue, identifier);
        } else {
            setIssueGUIDInIdentifierField(issue, identifier);
        }
    }

    public String getFormattedLabelsIdentifier(final String identifier) {
        var format = configuration.LabelsIdentifierFormat;
        return format.replace("{0}", identifier);
    }

    public String CreateIssueGUID() {
        return randomUUID().toString();
    }

    public String getIssueGUID(final Issue issue, final boolean first) {
        if (issue == null) {
            return null;
        }

        String identifier = null;
        if (configuration.SaveIdentifierInLabelsField) {
            identifier = getIssueGUIDFromLabelsField(issue);
        } else {
            identifier = getIssueGUIDFromIdentifierField(issue);
        }

        if (identifier != null) {
            return identifier;
        }

        if (!first) {
            throw new WebApplicationException("Failed to generate GUID for issue " + issue.getKey() + "!", Response.Status.INTERNAL_SERVER_ERROR);
        }

        identifier = CreateIssueGUID();
        setIssueGUID(issue, identifier);

        var updated = getIssue(issue.getKey()).claim();

        return getIssueGUID(updated, false);
    }
}
