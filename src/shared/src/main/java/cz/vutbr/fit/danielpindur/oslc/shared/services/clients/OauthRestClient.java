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

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Client for the Oauth2 authorization and token endpoints.
 */
public class OauthRestClient {
    private final URI baseUri;
    private final HttpClient httpClient;

    public OauthRestClient() {
        this.baseUri = URI.create(ConfigurationProvider.GetConfiguration().JiraServer.Url + "/rest/oauth2/latest");
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Request authorization code from the Jira API.
     * 
     * @param query Query string to send to the API.
     * 
     * @return Response from the API.
     */
    public HttpResponse<String> authorize(final String query) throws IOException, InterruptedException {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("authorize").replaceQuery(query).build();
        var request = HttpRequest.newBuilder().GET().uri(uri).build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Request access token from the Jira API.
     * 
     * @param query Query string to send to the API.
     * 
     * @return Response from the API.
     */
    public HttpResponse<String> token(final String query) throws IOException, InterruptedException {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("token").replaceQuery(query).build();
        var request = HttpRequest.newBuilder().GET().uri(uri).build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
