/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.helpers;

import java.net.URI;

/**
 * Helper class for working with URIs.
 */
public final class UriHelper {
    /**
     * Gets the ID from the resource URI.
     * 
     * @param uri URI of the resource.
     * 
     * @return ID of the resource.
     */
    public static String GetIdFromUri(final URI uri) {
        return GetIdFromUri(uri.toString());
    }

    /**
     * Gets the ID from the resource URI.
     * 
     * @param uri URI of the resource.
     * 
     * @return ID of the resource.
     */
    public static String GetIdFromUri(final String uri) {
        var exploded = uri.split("/");
        return exploded[exploded.length - 1];
    }

    /**
     * Validate if the URI is a requirement URI.
     * 
     * @param uri URI to validate.
     * 
     * @return True if the URI is a requirement URI, false otherwise.
     */
    public static boolean IsRequirementUri(final URI uri) {
        return IsRequirementUri(uri.toString());
    }

    /**
     * Validate if the URI is a requirement URI.
     * 
     * @param uri URI to validate.
     * 
     * @return True if the URI is a requirement URI, false otherwise.
     */
    public static boolean IsRequirementUri(final String uri) {
        var exploded = uri.split("/");
        return exploded[exploded.length - 2].equalsIgnoreCase("Requirement");
    }

    /**
     * Validate if the URI is a requirement collection URI.
     * 
     * @param uri URI to validate.
     * 
     * @return True if the URI is a requirement collection URI, false otherwise.
     */
    public static boolean IsRequirementCollectionUri(final URI uri) {
        return IsRequirementCollectionUri(uri.toString());
    }

    /**
     * Validate if the URI is a requirement collection URI.
     * 
     * @param uri URI to validate.
     * 
     * @return True if the URI is a requirement collection URI, false otherwise.
     */
    public static boolean IsRequirementCollectionUri(final String uri) {
        var exploded = uri.split("/");
        return exploded[exploded.length - 2].equalsIgnoreCase("RequirementCollection");
    }
}
