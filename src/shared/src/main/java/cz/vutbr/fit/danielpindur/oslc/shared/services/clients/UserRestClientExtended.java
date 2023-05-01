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
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousUserRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.jira.rest.client.internal.json.UsersJsonParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

/**
 * Extended version of the AsynchronousUserRestClient from Jira API SDK.
 */
public class UserRestClientExtended extends AsynchronousUserRestClient implements Closeable {

    private final URI baseUri;
    private final DisposableHttpClient client;

    public UserRestClientExtended(URI baseUri, DisposableHttpClient client) {
        super(baseUri, client);
        this.baseUri = baseUri;
        this.client = client;
    }

    public void close() {
        try {
            this.client.destroy();
        } catch (Exception ignored) { }
    }

    /**
     * Search users by email using the Jira API.
     *
     * @param email Email to search for.
     * 
     * @return Set of users matching the email.
     */
    public Promise<Iterable<User>> searchUsersByEmail(final String email) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("user").path("search").queryParam("username", new Object[]{email}).build();
        return getAndParse(uri, new UsersJsonParser());
    }
}
