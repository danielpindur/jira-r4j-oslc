package cz.vutbr.fit.danielpindur.oslc.shared.services;

import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.OauthRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

@Path("/oauth")
public class OAuthService {
    @Context
    protected HttpServletRequest httpRequest;
    @Context
    protected HttpServletResponse httpResponse;
    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    public OAuthService() { }

    @GET
    @Path("/authorize")
    public Response doAuthorize() throws IOException, ServletException {
        var query = httpRequest.getQueryString();
        var oauthClient = new OauthRestClient();
        HttpResponse<String> jiraResponse = null;

        try {
            jiraResponse = oauthClient.authorize(query);
        } catch (Exception e) {
            log.error("ERROR: Problem when reaching Jira instance occurred!");
            throw new ServletException(e);
        }

        if (jiraResponse.statusCode() != 303) {
            log.error("ERROR: Unexpected Status code received when calling OauthService (expected: 303, actual: " + jiraResponse.statusCode() + ")!");
            throw new ServletException();
        }

        var jiraResponseHeaders = jiraResponse.headers();
        var location = jiraResponseHeaders.map().get("location");

        if (location.isEmpty()) {
            log.error("ERROR: location header not present in response from OauthService!");
            throw new ServletException();
        }

        return Response.status(303).location(URI.create(location.get(0))).build();
    }

    @POST
    @Path("/token")
    public Response doToken() throws IOException, ServletException {
        var params = httpRequest;

        var test = httpRequest.getParameterNames();
        //var test1 = httpRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        return null;

        /*
        try {

        } catch (OAuthException oAuthException) {
            return this.respondWithOAuthProblem(oAuthException);
        }
        */
    }
    /*
    protected Response respondWithOAuthProblem(OAuthException e) throws IOException, ServletException {
        try {
            OAuthServlet.handleException(this.httpResponse, e, OAuthConfiguration.getInstance().getApplication().getRealm(this.httpRequest));
        } catch (OAuthProblemException oAuthProblemException) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    */
}
