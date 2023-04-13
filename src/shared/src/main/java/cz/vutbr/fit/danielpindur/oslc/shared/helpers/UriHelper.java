package cz.vutbr.fit.danielpindur.oslc.shared.helpers;

import java.net.URI;

public final class UriHelper {
    public static String GetIdFromUri(final URI uri) {
        var exploded = uri.toString().split("/");
        return exploded[exploded.length - 1];
    }

    public static boolean IsRequirementUri(final URI uri) {
        var exploded = uri.toString().split("/");
        return exploded[exploded.length - 2].equalsIgnoreCase("Requirement");
    }

    public static boolean IsRequirementCollectionUri(final URI uri) {
        var exploded = uri.toString().split("/");
        return exploded[exploded.length - 2].equalsIgnoreCase("RequirementCollection");
    }
}
