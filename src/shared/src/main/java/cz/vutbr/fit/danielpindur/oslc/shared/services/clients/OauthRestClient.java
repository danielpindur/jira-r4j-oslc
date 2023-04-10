package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OauthRestClient {
    private final URI baseUri;
    private final HttpClient httpClient;

    public OauthRestClient() {
        this.baseUri = URI.create(ConfigurationProvider.GetConfiguration().JiraServer.Url + "/rest/oauth2/latest");
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> authorize(final String query) throws IOException, InterruptedException {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("authorize").replaceQuery(query).build();
        var request = HttpRequest.newBuilder().GET().uri(uri).build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> token(final String query) throws IOException, InterruptedException {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("authorize").replaceQuery(query).build();
        var request = HttpRequest.newBuilder().GET().uri(uri).build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
