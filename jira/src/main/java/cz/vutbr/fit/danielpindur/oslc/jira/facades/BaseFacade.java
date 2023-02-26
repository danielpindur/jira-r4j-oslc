package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import cz.vutbr.fit.danielpindur.oslc.jira.ResourcesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URI;
import java.util.Objects;

public class BaseFacade {
    @Inject ResourcesFactory resourcesFactory;

    private final JiraRestClient restClient;
    protected static final Logger log = LoggerFactory.getLogger(BaseFacade.class);

    public BaseFacade() {
        // TODO: This needs to come from some config reader
        restClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create("http://localhost:8080"), "xpindu01", "testpassword");
    }

    protected boolean containsTerms(final String target, final String terms) {
        if (target == null || terms == null) {
            return false;
        }

        var capitalizedTarget = target.toUpperCase();
        var capitalizedTerms = terms.toUpperCase();

        return capitalizedTarget.contains(capitalizedTerms);
    }

    protected String SafeConvert(final Long number) {
        return Objects.requireNonNull(number).toString();
    }

    protected UserRestClient getUserClient() {
        return restClient.getUserClient();
    }

    protected ProjectRestClient getProjectClient() {
        return restClient.getProjectClient();
    }

    protected IssueRestClient getIssueClient() {
        return restClient.getIssueClient();
    }

    protected MetadataRestClient getMetadataClient() { return restClient.getMetadataClient(); }

    protected SearchRestClient getSearchClient() { return restClient.getSearchClient(); }
}
