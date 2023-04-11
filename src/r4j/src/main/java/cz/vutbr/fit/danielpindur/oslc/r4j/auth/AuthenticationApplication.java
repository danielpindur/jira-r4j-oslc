// Start of user code Copyright
/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Simple
 */
// End of user code

package cz.vutbr.fit.danielpindur.oslc.r4j.auth;

import java.util.Base64;
import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.session.SessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.lyo.server.oauth.core.Application;
import org.eclipse.lyo.server.oauth.core.token.LRUCache;
import org.eclipse.lyo.server.oauth.core.AuthenticationException;


// Start of user code imports
// End of user code

// Start of user code pre_class_code
// End of user code

public class AuthenticationApplication implements Application {

    public final static String APPLICATION_NAME = "R4JAdaptor";
    public final static String OAUTH_REALM = "R4JAdaptor";
    protected final static String APPLICATION_CONNECTOR_SESSION_ATTRIBUTE = "cz.vutbr.fit.danielpindur.oslc.r4j.auth.ApplicationConnector";
    protected final static String APPLICATION_CONNECTOR_ADMIN_SESSION_ATTRIBUTE = "cz.vutbr.fit.danielpindur.oslc.r4j.auth.AdminSession";
    private final static Logger log = LoggerFactory.getLogger(AuthenticationApplication.class);

    public final static String AUTHORIZATION_HEADER = "Authorization";
    public final static String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";
    private final static String BASIC_AUTHORIZATION_PREFIX = "Basic ";
    public final static String BASIC_AUTHENTICATION_CHALLENGE = BASIC_AUTHORIZATION_PREFIX + "realm=\"" + OAUTH_REALM + "\"";
    private final static String OAUTH_AUTHORIZATION_PREFIX = "OAuth ";
    public final static String OAUTH_AUTHENTICATION_CHALLENGE = OAUTH_AUTHORIZATION_PREFIX + "realm=\"" + OAUTH_REALM + "\"";

    private static AuthenticationApplication authenticationApplication;

    // Start of user code class_attributes
    // End of user code

    private String oslcConsumerStoreFilename;
    // TODO: Cleanup this cache so that entries from old keys/token are removed.
    // Currently, this list simply grows all the time.
    private LRUCache<String, String> oauth1TokenToApplicationConnector;

    // Start of user code instance_attributes
    // End of user code

    // Start of user code class_methods
    // End of user code

    private AuthenticationApplication() {
        // Start of user code constructor_init
        // End of user code
        oslcConsumerStoreFilename= "./oslcOAuthStore.xml";
        oauth1TokenToApplicationConnector = new LRUCache<String, String>(2000);
        // Start of user code constructor_finalize
        // End of user code
    }

    // Start of user code instance_methods
    public void getTokenAuthenticationFromRequest(HttpServletRequest request) throws AuthenticationException {
        if (!ConfigurationProvider.GetConfiguration().JiraServer.EnableOAuth) {
            throw new AuthenticationException("OAuth Authentication is not enabled for this adaptor!");
        }

        var token = getTokenFromRequest(request);
        if (token == null) {
            throw new AuthenticationException("Expected to find OAuth token, but found nothing!");
        }

        bindApplicationConnectorToSession(request, token);
        request.getSession().setAttribute(APPLICATION_CONNECTOR_ADMIN_SESSION_ATTRIBUTE, false);
        request.getSession().setAttribute(SessionProvider.OAUTH_TOKEN, token);
    }

    private String getTokenFromRequest(final HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        if (header == null) return null;

        return header.replaceAll("^Bearer\\s+", "");
    }
    // End of user code

    public static AuthenticationApplication getApplication() {
        // Start of user code getApplication_init
        // End of user code
        if (null == authenticationApplication) {
            synchronized ("authenticationApplication") {
                if (null == authenticationApplication) {
                    authenticationApplication = new AuthenticationApplication();
                    // Start of user code getApplication_mid
                    // End of user code
                }
            }
        }
        // Start of user code getApplication_final
        // End of user code
        return authenticationApplication;
    }

    public String getOslcConsumerStoreFilename() {
        return oslcConsumerStoreFilename;
    }

    @Override
    public String getName() {
        // Display name for this application.
        return APPLICATION_NAME;
    }

    @Override
    public String getRealm(HttpServletRequest request) {
        return OAUTH_REALM;
    }

    @Override
    public void login(HttpServletRequest request, String username, String password) throws AuthenticationException {
        // Start of user code login
        if (!ConfigurationProvider.GetConfiguration().JiraServer.EnableBasicAuth) {
            throw new AuthenticationException("Basic Authentication is not enabled for this adaptor!");
        }

        bindApplicationConnectorToSession(request, username);
        request.getSession().setAttribute(APPLICATION_CONNECTOR_ADMIN_SESSION_ATTRIBUTE, false);
        request.getSession().setAttribute(SessionProvider.BASIC_USERNAME, username);
        request.getSession().setAttribute(SessionProvider.BASIC_PASSWORD, password);
        // End of user code
        return;
    }

    /**
     * Get & Login based on the credentials in the <code>Authorization</code> request header
     * if successful, bind the credentials to the request session.
     *
     * @throws AuthenticationException on problems reading the credentials from the <code>Authorization</code> request header
     */
    public Optional<SimpleEntry> getBasicAuthenticationFromRequest(HttpServletRequest request) throws AuthenticationException {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(BASIC_AUTHORIZATION_PREFIX)) {
            String encodedString = authorizationHeader.substring(BASIC_AUTHORIZATION_PREFIX.length());
            String unencodedString = new String(Base64.getDecoder().decode(encodedString), StandardCharsets.UTF_8);
            int seperator = unencodedString.indexOf(':');
            if (seperator == -1) {
                throw new AuthenticationException("Invalid Authorization header value.");
            }
            String username = unencodedString.substring(0, seperator);
            String password = unencodedString.substring(seperator + 1);
            login(request, username, password);

            bindApplicationConnectorToSession(request, unencodedString);
            return Optional.of(new SimpleEntry<>(username, password));
        }
        return Optional.empty();
    }

    @Override
    public boolean isAuthenticated(HttpServletRequest request) {
        boolean auth = (null != getApplicationConnectorFromSession(request));
        // Start of user code isAuthenticated
        // End of user code
        return auth;
    }

    @Override
    public boolean isAdminSession(HttpServletRequest request) {
        boolean admin = Boolean.TRUE.equals(request.getSession().getAttribute(APPLICATION_CONNECTOR_ADMIN_SESSION_ATTRIBUTE));
        // Start of user code isAdminSession
        // End of user code
        return admin;
    }

    // TODO: instead of saving to a session, consider saving to a cookie, so that
    // the login survives longer than a single web session.
    public void bindApplicationConnectorToSession(HttpServletRequest request, String applicationConnector) {
        // Start of user code bindApplicationConnectorToSession
        // End of user code
        request.getSession().setAttribute(APPLICATION_CONNECTOR_SESSION_ATTRIBUTE, applicationConnector);
    }

    public String getApplicationConnectorFromSession(HttpServletRequest request) {
        // Start of user code getApplicationConnectorFromSession
        // End of user code
        return (String) request.getSession().getAttribute(APPLICATION_CONNECTOR_SESSION_ATTRIBUTE);
    }

    public void removeApplicationConnectorFromSession(HttpServletRequest request) {
        // Start of user code removeApplicationConnectorFromSession
        // End of user code
        request.getSession().removeAttribute(APPLICATION_CONNECTOR_SESSION_ATTRIBUTE);
    }

    public String getApplicationConnector(String oauth1Token) {
        // Start of user code getApplicationConnector
        // End of user code
        return oauth1TokenToApplicationConnector.get(oauth1Token);
    }

    public void putApplicationConnector(String oauth1Token, String applicationConnector) {
        // Start of user code putApplicationConnector
        // End of user code
        oauth1TokenToApplicationConnector.put(oauth1Token, applicationConnector);
    }

    public void moveApplicationConnector(String oldOauth1Token, String newOauth1Token) {
        // Start of user code moveApplicationConnector
        // End of user code
        String applicationConnector = oauth1TokenToApplicationConnector.remove(oldOauth1Token);
        oauth1TokenToApplicationConnector.put(newOauth1Token, applicationConnector);
    }

    public void removeForOauth1Token(String oauth1Token) {
        // Start of user code removeForOauth1Token
        // End of user code
        oauth1TokenToApplicationConnector.remove(oauth1Token);
    }

    /**
     * Send error response when the request was not authorized
     *
     * @param response an error response
     * @param e        Exception with error message
     * @throws IOException
     * @throws ServletException
     */
    public void sendUnauthorizedResponse(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException, ServletException {
        // Start of user code sendUnauthorizedResponse_init
        // End of user code
        // Accept basic access or OAuth authentication.
        response.addHeader(WWW_AUTHENTICATE_HEADER, OAUTH_AUTHENTICATION_CHALLENGE);
        response.addHeader(WWW_AUTHENTICATE_HEADER, BASIC_AUTHENTICATION_CHALLENGE);
        // Start of user code sendUnauthorizedResponse_mid
        // End of user code
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        // Start of user code sendUnauthorizedResponse_final
        // End of user code
    }
}
