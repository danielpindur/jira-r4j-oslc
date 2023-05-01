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
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.ContainsLinkExistsParser;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.EnabledProjectsKeysParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Asynchronous REST client for the R4J API for Folder v1.
 */
public class OldFolderRestClient extends AbstractAsynchronousRestClient implements Closeable {
    private final URI baseUri;
    private final DisposableHttpClient client;

    public OldFolderRestClient(final URI baseUri, final DisposableHttpClient client) {
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
     * Check if contains link exists for the given issue in the given project.
     * 
     * @param issueKey Issue key to check.
     * @param projectKey Project key to check.
     * 
     * @return True if the contains link exists, false otherwise.
     */
    public Promise<Boolean> existsContainsLink(final String issueKey, final String projectKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("issue").path("req-path").path("key="+issueKey).build();
        return getAndParse(uri, new ContainsLinkExistsParser(projectKey));
    }

    /**
     * Get all enabled project keys.
     * 
     * @return List of all enabled project keys.
     */
    public Promise<List<String>> getEnabledProjectKeys() {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").build();
        return getAndParse(uri, new EnabledProjectsKeysParser());
    }
}
