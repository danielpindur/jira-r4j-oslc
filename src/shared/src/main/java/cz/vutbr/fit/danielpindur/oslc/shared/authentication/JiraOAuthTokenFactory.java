package cz.vutbr.fit.danielpindur.oslc.shared.authentication;

import com.google.api.client.auth.oauth.OAuthRsaSigner;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class JiraOAuthTokenFactory {
    protected final String accessTokenUrl;
    protected final String requestTokenUrl;


    public JiraOAuthTokenFactory(String jiraBaseUrl) {
        this.accessTokenUrl = jiraBaseUrl + "/plugins/servlet/oauth/access-token";
        requestTokenUrl = jiraBaseUrl + "/plugins/servlet/oauth/request-token";
    }

    public JiraOAuthGetAccessToken getJiraOAuthGetAccessToken(String tmpToken, String secret, String consumerKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JiraOAuthGetAccessToken accessToken = new JiraOAuthGetAccessToken(accessTokenUrl);
        accessToken.consumerKey = consumerKey;
        accessToken.signer = getOAuthRsaSigner(privateKey);
        accessToken.transport = new ApacheHttpTransport();
        accessToken.verifier = secret;
        accessToken.temporaryToken = tmpToken;
        return accessToken;
    }

    public JiraOAuthGetTemporaryToken getTemporaryToken(String consumerKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JiraOAuthGetTemporaryToken oAuthGetTemporaryToken = new JiraOAuthGetTemporaryToken(requestTokenUrl);
        oAuthGetTemporaryToken.consumerKey = consumerKey;
        oAuthGetTemporaryToken.signer = getOAuthRsaSigner(privateKey);
        oAuthGetTemporaryToken.transport = new ApacheHttpTransport();
        oAuthGetTemporaryToken.callback = "oob";
        return oAuthGetTemporaryToken;
    }

    private OAuthRsaSigner getOAuthRsaSigner(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        OAuthRsaSigner oAuthRsaSigner = new OAuthRsaSigner();
        oAuthRsaSigner.privateKey = getPrivateKey(privateKey);
        return oAuthRsaSigner;
    }

    private PrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }
}
