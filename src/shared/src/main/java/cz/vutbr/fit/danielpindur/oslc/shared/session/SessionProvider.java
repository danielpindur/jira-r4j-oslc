package cz.vutbr.fit.danielpindur.oslc.shared.session;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;

public final class SessionProvider {
    public static String BASIC_USERNAME = "Username";
    public static String BASIC_PASSWORD = "Password";
    public static String OAUTH_TOKEN = "OAuth_Token";

    private static SessionProvider INSTANCE;

    private HttpSession session;
    private List<DisposableHttpClient> disposableHttpClients;
    private List<JiraRestClient> jiraRestClients;

    private static SessionProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionProvider();
        }

        return INSTANCE;
    }

    public static void SetSession(final HttpSession session) {
        getInstance().session = session;
        SetupClients();
    }

    public static void ClearSession() {
        getInstance().session = null;
        ClearClients();
    }

    public static HttpSession GetSession() {
        return getInstance().session;
    }

    public static void ClearClients() {
        if (getInstance().disposableHttpClients != null && !getInstance().disposableHttpClients.isEmpty()) {
            for (var client : getInstance().disposableHttpClients) {
                try {
                    client.destroy();
                } catch (Exception ignored) { }
            }
        }

        if (getInstance().jiraRestClients != null && !getInstance().jiraRestClients.isEmpty()) {
            for (var client : getInstance().jiraRestClients) {
                try {
                    client.close();
                } catch (Exception ignored) { }
            }
        }
    }

    private static void SetupClients() {
        ClearClients();
        getInstance().disposableHttpClients = new LinkedList<>();
        getInstance().jiraRestClients = new LinkedList<>();
    }

    public static void AddClient(final DisposableHttpClient client) {
        getInstance().disposableHttpClients.add(client);
    }

    public static void AddJiraClient(final JiraRestClient client) {
        getInstance().jiraRestClients.add(client);
    }
}
