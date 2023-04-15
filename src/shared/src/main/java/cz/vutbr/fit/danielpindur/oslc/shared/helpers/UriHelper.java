package cz.vutbr.fit.danielpindur.oslc.shared.helpers;

import java.net.URI;

public final class UriHelper {
    public static String GetIdFromUri(final URI uri) {
        return GetIdFromUri(uri.toString());
    }

    public static String GetIdFromUri(final String uri) {
        var exploded = uri.split("/");
        return exploded[exploded.length - 1];
    }

    public static boolean IsRequirementUri(final URI uri) {
        return IsRequirementUri(uri.toString());
    }

    public static boolean IsRequirementUri(final String uri) {
        var exploded = uri.split("/");
        return exploded[exploded.length - 2].equalsIgnoreCase("Requirement");
    }

    public static boolean IsRequirementCollectionUri(final URI uri) {
        return IsRequirementCollectionUri(uri.toString());
    }

    public static boolean IsRequirementCollectionUri(final String uri) {
        var exploded = uri.split("/");
        return exploded[exploded.length - 2].equalsIgnoreCase("RequirementCollection");
    }
}
