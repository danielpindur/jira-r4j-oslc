package cz.vutbr.fit.danielpindur.oslc.shared.session;

import javax.servlet.http.HttpSession;

public final class SessionProvider {
    public static String BASIC_USERNAME = "Username";
    public static String BASIC_PASSWORD = "Password";
    public static String OAUTH_TOKEN = "OAuth_Token";

    private static SessionProvider INSTANCE;

    private HttpSession session;

    private static SessionProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionProvider();
        }

        return INSTANCE;
    }

    public static void SetSession(final HttpSession session) {
        getInstance().session = session;
    }

    public static void ClearSession() {
        getInstance().session = null;
    }

    public static HttpSession GetSession() {
        return getInstance().session;
    }
}
