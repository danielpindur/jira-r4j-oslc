package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousUserRestClient;
import com.atlassian.jira.rest.client.internal.json.UsersJsonParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Set;

public class UserRestClientExtended extends AsynchronousUserRestClient {

    private final URI baseUri;

    public UserRestClientExtended(URI baseUri, HttpClient client) {
        super(baseUri, client);
        this.baseUri = baseUri;
    }

    public Promise<Iterable<User>> searchUsersByEmail(final String email) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("user").path("search").queryParam("username", new Object[]{email}).build();
        return getAndParse(uri, new UsersJsonParser());
    }
}
