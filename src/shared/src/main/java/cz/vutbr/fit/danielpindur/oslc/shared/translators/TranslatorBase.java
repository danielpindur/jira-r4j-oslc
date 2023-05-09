/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.translators;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.UriHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.services.facades.BaseFacade;
import org.apache.jena.atlas.lib.NotImplemented;
import org.eclipse.lyo.core.query.SimpleTerm;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Base class for all translators. Contains common methods.
 */
public class TranslatorBase {
    protected final Map<String, String> propertiesMap = new HashMap<>();
    protected final Map<SimpleTerm.Type, String> originalTypeMap = new HashMap<>();
    protected final Map<SimpleTerm.Type, String> replaceTypeMap = new HashMap<>();

    protected final Configuration configuration;

    public TranslatorBase() {
        configuration = ConfigurationProvider.GetConfiguration();

        originalTypeMap.put(SimpleTerm.Type.COMPARISON, "=");
        originalTypeMap.put(SimpleTerm.Type.IN_TERM, " in ");

        replaceTypeMap.put(SimpleTerm.Type.COMPARISON, " = ");
        replaceTypeMap.put(SimpleTerm.Type.IN_TERM, " IN ");
    }

    /**
     * Translate list of identifiers to list of their representation in lables field
     * 
     * @param operands List of identifiers as string.
     * 
     * @return List of formatted labels identifiers as a string.
     */
    protected String translateIdentifiersInLabel(final String operands) {
        var exploded = operands.split(",");

        for (int i = 0; i < exploded.length; i++) {
            var identifier = exploded[i];
            identifier = IssueHelper.GetFormattedLabelsIdentifier(identifier);
            exploded[i] = identifier;
        }

        return String.join(",", exploded);
    }

    /**
     * Cleans uri string from < and > characters.
     * 
     * @param operands Uri string.
     * 
     * @return Cleaned uri string.
     */
    protected String cleanUri(final String operands) {
        return operands.replace("<", "").replace(">", "");
    }

    /**
     * Translate list of resource uris to list of the resource identifiers
     * 
     * @param operands List of uris as string.
     * 
     * @return List of identifiers as a string.
     */
    protected String translateUriToIds(final String operands) {
        var cleaned = cleanUri(operands);
        var exploded = cleaned.split(",");

        for (int i = 0; i < exploded.length; i++) {
            var uri = exploded[i];
            var identifier = UriHelper.GetIdFromUri(uri);
            exploded[i] = identifier;
        }

        return String.join(",", exploded);
    }

    /**
     * Translate list of resource uris to list of the issue keys
     * 
     * @param operands List of uris as string.
     * 
     * @return List of keys as a string.
     */
    protected String translateIssueUrisToKeys(final String operands) {
        var cleaned = cleanUri(operands);
        var exploded = cleaned.split(",");
        var keys = new LinkedList<String>();

        for (String uri : exploded) {
            var identifier = UriHelper.GetIdFromUri(uri);
            var issue = BaseFacade.getIssueClient().searchIssueByIdentifier(identifier);

            if ((IssueHelper.IsRequirement(issue) && UriHelper.IsRequirementUri(uri)) ||
                    IssueHelper.IsRequirementCollection(issue) && UriHelper.IsRequirementCollectionUri(uri)) {
                keys.add(issue.getKey());
            }
        }

        return String.join(",", keys);
    }

    /**
     * Replace all occurrences of '@' symbol in the list of emails with unicode representation.
     * 
     * @param operands List of emails as string.
     * 
     * @return List of emails with unicode representation of '@' symbol.
     */
    protected String translateEmails(final String operands) {
        var exploded = operands.split(",");

        for (int i = 0; i < exploded.length; i++) {
            var user = exploded[i];
            user = user.replace("@", "\\u0040");
            exploded[i] = user;
        }

        return String.join(",", exploded);
    }

    /**
     * Parse the comparison operator from the search string.
     * 
     * @param search Search string.
     * @param property Property name.
     * 
     * @return Comparison operator.
     */
    protected String getComparisonOperatorFromSearch(final String search, final String property) {
        var initial = new LinkedList<Character>();
        initial.add('>');
        initial.add('<');
        initial.add('=');
        initial.add('!');

        return getComparisonOperatorFromSearch(search, property, initial);
    }

    /**
     * Parse the comparison operator from the search string.
     * 
     * @param search Search string.
     * @param property Property name.
     * @param initialAllow List of allowed operators.
     * 
     * @return Comparison operator.
     */
    protected String getComparisonOperatorFromSearch(final String search, final String property, final List<Character> initialAllow) {
        var cleaned = search.replace(property, "");
        var cursor = 0;
        var operator = "";
        var expected = initialAllow;

        while (cursor < cleaned.length()) {
            var character = cleaned.charAt(cursor);

            switch (character) {
                case ' ':
                    break;

                case '=':
                    if (!expected.contains('=')) {
                        throw new WebApplicationException("Unexpected '='", Response.Status.BAD_REQUEST);
                    }
                    return operator + "=";

                case '!':
                    if (!expected.contains('!')) {
                        throw new WebApplicationException("Unexpected '!'", Response.Status.BAD_REQUEST);
                    }
                    operator = "!";
                    expected.clear();
                    expected.add('=');
                    break;

                case '>':
                    if (!expected.contains('>')) {
                        throw new WebApplicationException("Unexpected '>'", Response.Status.BAD_REQUEST);
                    }
                    return ">";

                case '<':
                    if (!expected.contains('<')) {
                        throw new WebApplicationException("Unexpected '<'", Response.Status.BAD_REQUEST);
                    }
                    return "<";

                default:
                    throw new WebApplicationException("Expected comparison operator, found "  + character, Response.Status.BAD_REQUEST);
            }

            cursor++;
        }

        return operator;
    }

    /**
     * Translate date time from ISO 8601 to JQL format.
     * 
     * @param operands Date time in ISO 8601 format.
     * 
     * @return Date time in JQL format.
     */
    protected String translateDateTime(final String operands) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime dateTime = LocalDateTime.parse(operands, inputFormatter);
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(java.time.ZoneId.systemDefault().getRules().getOffset(dateTime).getTotalSeconds());
        LocalDateTime localDateTime = dateTime.atOffset(ZoneOffset.UTC).withOffsetSameInstant(offset).toLocalDateTime();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        return "\"" + localDateTime.format(outputFormatter) + "\"";
    }

    /**
     * Translate single term.
     * 
     * @param term Term to translate.
     * 
     * @return Translated representation of the term.
     */
    public String translate(final SimpleTerm term) {
        throw new NotImplemented();
    }
}
