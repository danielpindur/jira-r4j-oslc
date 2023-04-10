package cz.vutbr.fit.danielpindur.oslc.shared.authentication;

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthParameters;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class JiraOAuthClient {

    public final String jiraBaseUrl;
    private final JiraOAuthTokenFactory oAuthGetAccessTokenFactory;
    private final String authorizationUrl;

    public JiraOAuthClient() {
        jiraBaseUrl = ConfigurationProvider.GetConfiguration().JiraServer.Url;
        this.oAuthGetAccessTokenFactory = new JiraOAuthTokenFactory(this.jiraBaseUrl);
        authorizationUrl = jiraBaseUrl + "/plugins/servlet/oauth/authorize";
    }

    public OAuthCredentialsResponse getAndAuthorizeTemporaryToken(String consumerKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        JiraOAuthGetTemporaryToken temporaryToken = oAuthGetAccessTokenFactory.getTemporaryToken(consumerKey, privateKey);
        return temporaryToken.execute();
    }

    public String getAccessToken(String tmpToken, String secret, String consumerKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        JiraOAuthGetAccessToken oAuthAccessToken = oAuthGetAccessTokenFactory.getJiraOAuthGetAccessToken(tmpToken, secret, consumerKey, privateKey);
        OAuthCredentialsResponse response = oAuthAccessToken.execute();

        System.out.println("Access token:\t\t\t" + response.token);
        return response.token;
    }

    public OAuthParameters getParameters(String tmpToken, String secret, String consumerKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JiraOAuthGetAccessToken oAuthAccessToken = oAuthGetAccessTokenFactory.getJiraOAuthGetAccessToken(tmpToken, secret, consumerKey, privateKey);
        oAuthAccessToken.verifier = secret;
        return oAuthAccessToken.createParameters();
    }
}
