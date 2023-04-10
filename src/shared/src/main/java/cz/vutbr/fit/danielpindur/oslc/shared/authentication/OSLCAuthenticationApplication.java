package cz.vutbr.fit.danielpindur.oslc.shared.authentication;

import org.eclipse.lyo.server.oauth.core.Application;

import javax.servlet.http.HttpServletRequest;

public interface OSLCAuthenticationApplication extends Application {
    public String getApplicationConnector(String oauth1Token);

    public void putApplicationConnector(String oauth1Token, String applicationConnector);

    public void moveApplicationConnector(String oldOauth1Token, String newOauth1Token);

    public String getApplicationConnectorFromSession(HttpServletRequest request);
}
