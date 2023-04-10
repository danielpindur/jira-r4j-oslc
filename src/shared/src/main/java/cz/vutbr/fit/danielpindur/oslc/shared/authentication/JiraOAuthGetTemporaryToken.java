package cz.vutbr.fit.danielpindur.oslc.shared.authentication;

import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;

public class JiraOAuthGetTemporaryToken extends OAuthGetTemporaryToken {

    public JiraOAuthGetTemporaryToken(String authorizationServerUrl) {
        super(authorizationServerUrl);
        this.usePost = true;
    }

}
