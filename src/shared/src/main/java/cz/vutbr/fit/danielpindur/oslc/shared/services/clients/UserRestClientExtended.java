package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousUserRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.jira.rest.client.internal.json.UsersJsonParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class UserRestClientExtended extends AsynchronousUserRestClient implements Closeable {

    private final URI baseUri;
    private final DisposableHttpClient client;

    public UserRestClientExtended(URI baseUri, DisposableHttpClient client) {
        super(baseUri, client);
        this.baseUri = baseUri;
        this.client = client;
    }

    public void close() {
        try {
            this.client.destroy();
        } catch (Exception ignored) { }
    }

    public Promise<Iterable<User>> searchUsersByEmail(final String email) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("user").path("search").queryParam("username", new Object[]{email}).build();
        return getAndParse(uri, new UsersJsonParser());
    }
}
