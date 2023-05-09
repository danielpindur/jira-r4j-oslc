/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.IssueLinkIdsForIssueParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

/**
 * Asynchronous REST client for the Jira IssueLink API.
 */
public class IssueLinkRestClient extends AbstractAsynchronousRestClient implements Closeable {
    private final URI baseUri;
    private final DisposableHttpClient client;

    public IssueLinkRestClient(final URI baseUri, final DisposableHttpClient client) {
        super(client);
        this.baseUri = baseUri;
        this.client = client;
    }

    public void close() {
        try {
            this.client.destroy();
        } catch (Exception ignored) { }
    }

    /**
     * Delete an issue link by id using the Jira API.
     * 
     * @param linkId Id of the issue link to delete.
     * 
     * @return Promise of the deletion.
     */
    public Promise<Void> deleteLink(final String linkId) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("issueLink").path(linkId).build();
        return delete(uri);
    }

    /**
     * Get issue link ids for an issue using the Jira API.
     * 
     * @param issueId Id of the issue to get the issue link ids for.
     * @param issueLinkName Name of the issue link type to get the issue link ids for.
     * 
     * @return Promise of the issue link ids.
     */
    public Promise<Set<String>> getAdaptorIssueLinkIdsForIssue(final String issueId, final String issueLinkName) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("issue").path(issueId).build();
        return getAndParse(uri, new IssueLinkIdsForIssueParser(issueLinkName));
    }

    /**
     * Get issue link ids for an issue using the Jira API.
     * 
     * @param issueId Id of the issue to get the issue link ids for.
     * 
     * @return Promise of the issue link ids.
     */
    public Promise<Set<String>> getIssueLinkIdsForIssue(final String issueId) {
        return getAdaptorIssueLinkIdsForIssue(issueId, null);
    }
}
