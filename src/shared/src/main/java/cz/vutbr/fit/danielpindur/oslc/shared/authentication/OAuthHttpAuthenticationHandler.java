package cz.vutbr.fit.danielpindur.oslc.shared.authentication;

import com.atlassian.httpclient.api.Request;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;

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
