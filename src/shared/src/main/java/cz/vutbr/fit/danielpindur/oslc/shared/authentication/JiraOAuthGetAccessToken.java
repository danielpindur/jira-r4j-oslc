package cz.vutbr.fit.danielpindur.oslc.shared.authentication;

import com.google.api.client.auth.oauth.OAuthGetAccessToken;

public class JiraOAuthGetAccessToken extends OAuthGetAccessToken {

    public JiraOAuthGetAccessToken(String authorizationServerUrl) {
        super(authorizationServerUrl);
        this.usePost = true;
    }

}
