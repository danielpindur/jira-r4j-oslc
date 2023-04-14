package cz.vutbr.fit.danielpindur.oslc.r4j.helpers;

import cz.vutbr.fit.danielpindur.oslc.r4j.clients.JiraAdaptorClient;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.RequirementCollection;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import org.eclipse.lyo.oslc4j.core.model.Link;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;

public final class FolderHelper {
    private static final Configuration configuration = ConfigurationProvider.GetConfiguration();

    public static String ConstructFolderIdentifier(final String projectKey, final Integer folderId) {
        String secondPart = null;
        if (folderId == 0) {
            return null;
        }
        if (folderId == -1) {
            secondPart = configuration.RootFolderId;
        }
        else {
            secondPart = folderId.toString();
        }

        return projectKey + "-" + secondPart;
    }

    public static String GetIssueKeyFromLink(final Link link) {
        return GetIssueKeyFromUri(link.getValue());
    }

    public static String GetIssueKeyFromUri(final URI uri) {
        Requirement requirement = null;
        RequirementCollection requirementCollection = null;

        try {
            requirement = JiraAdaptorClient.getRequirement(uri.toString());
        } catch (Exception ignored) { }

        try {
            requirementCollection = JiraAdaptorClient.getRequirementCollection(uri.toString());
        } catch (Exception ignored) { }

        if (requirement != null) {
            return requirement.getShortTitle();
        } else if (requirementCollection != null) {
            return requirementCollection.getShortTitle();
        } else {
            throw new WebApplicationException("Failed to find resource for " + uri.toString(), Response.Status.NOT_FOUND);
        }
    }
}
