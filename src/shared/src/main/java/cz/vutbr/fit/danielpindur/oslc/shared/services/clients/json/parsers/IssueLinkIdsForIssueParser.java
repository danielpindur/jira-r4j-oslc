/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Parser for the IssueLink JSON response.
 */
public class IssueLinkIdsForIssueParser implements JsonObjectParser<Set<String>> {

    private final String issueLinkName;

    public IssueLinkIdsForIssueParser(final String issueLinkName) {
        this.issueLinkName = issueLinkName;
    }

    @Override
    public Set<String> parse(JSONObject json) throws JSONException {
        var result = new HashSet<String>();

        var fieldsJson = json.getJSONObject("fields");
        var issueLinksJson = fieldsJson.getJSONArray("issuelinks");
        if (issueLinksJson == null) {
            return result;
        }

        for (int i = 0; i < issueLinksJson.length(); i++) {
            var issueLinkJson = issueLinksJson.getJSONObject(i);
            var issueLinkType = issueLinkJson.getJSONObject("type");
            var issueLinkName = issueLinkType.getString("name");

            if (this.issueLinkName == null || issueLinkName.equalsIgnoreCase(this.issueLinkName)) {
                var issueLinkId = issueLinkJson.getString("id");
                if (issueLinkId != null) {
                    result.add(issueLinkId);
                }
            }
        }
        
        return result;
    }
}
