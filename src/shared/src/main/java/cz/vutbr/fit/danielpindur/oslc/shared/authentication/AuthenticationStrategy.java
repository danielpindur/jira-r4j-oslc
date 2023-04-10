package cz.vutbr.fit.danielpindur.oslc.shared.authentication;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import org.eclipse.lyo.server.oauth.core.OAuthRequest;
import org.eclipse.lyo.server.oauth.core.token.LRUCache;
import org.eclipse.lyo.server.oauth.core.token.SimpleTokenStrategy;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class AuthenticationStrategy extends SimpleTokenStrategy {

    private static final int REQUEST_TOKEN_MAX_ENTIRES = 500;
    private static final int ACCESS_TOKEN_MAX_ENTRIES = 5000;
    private Map<String, RequestTokenData> requestTokens;
    private Map<String, String> accessTokens;
    private Map<String, String> tokenSecrets;

    private final OSLCAuthenticationApplication authenticationApplication;
    private final OAuthClient oAuthClient;

    public AuthenticationStrategy(OSLCAuthenticationApplication authenticationApplication) {
        this(authenticationApplication, REQUEST_TOKEN_MAX_ENTIRES, ACCESS_TOKEN_MAX_ENTRIES);
    }

    public AuthenticationStrategy(OSLCAuthenticationApplication authenticationApplication, int requestTokenMaxCount, int accessTokenMaxCount) {
        this.authenticationApplication = authenticationApplication;
        this.requestTokens = new LRUCache(requestTokenMaxCount);
        this.accessTokens = new LRUCache(accessTokenMaxCount);
        this.tokenSecrets = new LRUCache(requestTokenMaxCount + accessTokenMaxCount);
        this.oAuthClient = new OAuthClient();
    }

    @Override
    public void generateRequestToken(OAuthRequest oAuthRequest) throws IOException {
        OAuthAccessor accessor = oAuthRequest.getAccessor();

        try {
            var response = oAuthClient.GetRequestToken();
            accessor.requestToken = response.token;
            accessor.tokenSecret = response.tokenSecret;
        } catch (Exception e) {
            throw new IOException("Failed to retrieve OAuth data from JIRA");
        }

        String callback = oAuthRequest.getMessage().getParameter("oauth_callback");

        synchronized(this.requestTokens) {
            this.requestTokens.put(accessor.requestToken, new RequestTokenData(accessor.consumer.consumerKey, callback));
        }

        synchronized(this.tokenSecrets) {
            this.tokenSecrets.put(accessor.requestToken, accessor.tokenSecret);
        }
    }

    // TODO: check if okay
    @Override
    public void markRequestTokenAuthorized(HttpServletRequest httpRequest, String requestToken)
            throws OAuthProblemException {
        authenticationApplication.putApplicationConnector(requestToken, authenticationApplication.getApplicationConnectorFromSession(httpRequest));
        super.markRequestTokenAuthorized(httpRequest, requestToken);
    }

    // TODO: check if okay
    @Override
    public void generateAccessToken(OAuthRequest oAuthRequest) throws OAuthProblemException, IOException {
        String requestToken = oAuthRequest.getMessage().getToken();
        super.generateAccessToken(oAuthRequest);
        authenticationApplication.moveApplicationConnector(requestToken, oAuthRequest.getAccessor().accessToken);
    }

    @Override
    public String getTokenSecret(HttpServletRequest httpRequest, String token) throws OAuthProblemException {
        synchronized(this.tokenSecrets) {
            String tokenSecret = (String)this.tokenSecrets.get(token);
            if (tokenSecret == null) {
                throw new OAuthProblemException("token_rejected");
            } else {
                return tokenSecret;
            }
        }
    }

    @Override
    public String validateRequestToken(HttpServletRequest httpRequest, OAuthMessage message) throws OAuthException, IOException {
        return this.getRequestTokenData(message.getToken()).getConsumerKey();
    }

    @Override
    public String getCallback(HttpServletRequest httpRequest, String requestToken) throws OAuthProblemException {
        return this.getRequestTokenData(requestToken).getCallback();
    }

    @Override
    public boolean isRequestTokenAuthorized(HttpServletRequest httpRequest, String requestToken) throws OAuthProblemException {
        return this.getRequestTokenData(requestToken).isAuthorized();
    }

    @Override
    public String generateVerificationCode(HttpServletRequest httpRequest, String requestToken) throws OAuthProblemException {
        String verificationCode = this.generateTokenString();
        this.getRequestTokenData(requestToken).setVerificationCode(verificationCode);
        return verificationCode;
    }

    @Override
    public void validateVerificationCode(OAuthRequest oAuthRequest) throws OAuthException, IOException {
        String verificationCode = oAuthRequest.getMessage().getParameter("oauth_verifier");
        if (verificationCode == null) {
            throw new OAuthProblemException("oauth_parameters_absent");
        } else {
            RequestTokenData tokenData = this.getRequestTokenData(oAuthRequest);
            if (!verificationCode.equals(tokenData.getVerificationCode())) {
                throw new OAuthProblemException("oauth_parameters_rejected");
            }
        }
    }

    @Override
    public void validateAccessToken(OAuthRequest oAuthRequest) throws OAuthException, IOException {
        synchronized(this.accessTokens) {
            String actualValue = (String)this.accessTokens.get(oAuthRequest.getMessage().getToken());
            if (!oAuthRequest.getConsumer().consumerKey.equals(actualValue)) {
                throw new OAuthProblemException("token_rejected");
            }
        }
    }

    @Override
    protected String generateTokenString() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected RequestTokenData getRequestTokenData(OAuthRequest oAuthRequest) throws OAuthProblemException, IOException {
        return this.getRequestTokenData(oAuthRequest.getMessage().getToken());
    }

    @Override
    protected RequestTokenData getRequestTokenData(String requestToken) throws OAuthProblemException {
        synchronized(this.requestTokens) {
            RequestTokenData tokenData = (RequestTokenData)this.requestTokens.get(requestToken);
            if (tokenData == null) {
                throw new OAuthProblemException("token_rejected");
            } else {
                return tokenData;
            }
        }
    }
}
