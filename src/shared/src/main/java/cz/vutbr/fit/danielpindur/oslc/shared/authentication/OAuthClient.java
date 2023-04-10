package cz.vutbr.fit.danielpindur.oslc.shared.authentication;

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;

import java.io.IOException;
import java.util.List;

public class OAuthClient {
    private final JiraOAuthClient jiraOAuthClient;

    public OAuthClient() {
        this.jiraOAuthClient = new JiraOAuthClient();
    }

    public OAuthCredentialsResponse GetRequestToken() throws Exception {
        var oAuthConfiguration = "s";
        return jiraOAuthClient.getAndAuthorizeTemporaryToken("s", "s");
    }

    private void handleGetAccessToken(List<String> arguments) throws Exception {
        /*
        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
        String tmpToken = properties.get(REQUEST_TOKEN);
        String secret = arguments.get(0);

        try {
            String accessToken = jiraOAuthClient.getAccessToken(tmpToken, secret, properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
            properties.put(ACCESS_TOKEN, accessToken);
            properties.put(SECRET, secret);
            propertiesClient.savePropertiesToFile(properties);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
        */
    }

    private void handleGetRequest(List<String> arguments) throws Exception {
        /*
        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
        String tmpToken = properties.get(ACCESS_TOKEN);
        String secret = properties.get(SECRET);
        String url = arguments.get(0);
        propertiesClient.savePropertiesToFile(properties);

        try {
            OAuthParameters parameters = jiraOAuthClient.getParameters(tmpToken, secret, properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
            HttpResponse response = getResponseFromUrl(parameters, new GenericUrl(url));
            parseResponse(response);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
         */
    }

    // TODO: ????
    private static HttpResponse getResponseFromUrl(OAuthParameters parameters, GenericUrl jiraUrl) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
        HttpRequest request = requestFactory.buildGetRequest(jiraUrl);
        return request.execute();
    }
}
