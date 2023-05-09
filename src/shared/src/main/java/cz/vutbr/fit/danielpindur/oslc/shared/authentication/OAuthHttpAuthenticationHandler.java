/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.authentication;

import com.atlassian.httpclient.api.Request;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;

/**
 * OAuth HTTP authentication handler.
 */
public class OAuthHttpAuthenticationHandler implements AuthenticationHandler {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String token;

    public OAuthHttpAuthenticationHandler(String token) {
        this.token = token;
    }

    @Override
    public void configure(Request.Builder builder) {
        builder.setHeader("Authorization", "Bearer " + token);
    }
}
