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

import com.atlassian.jira.rest.client.internal.json.JsonArrayParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Parser for the Enabled projects keys JSON response.
 */
public class EnabledProjectsKeysParser implements JsonArrayParser<List<String>> {

    @Override
    public List<String> parse(JSONArray json) throws JSONException {
        var result = new LinkedList<String>();

        if (json.length() < 1) {
            return result;
        }

        for (int i = 0; i < json.length(); i++) {
            var project = json.getJSONObject(i);
            var projectKey = project.getString("prjKey");
            result.add(projectKey);
        }

        return result;
    }
}
