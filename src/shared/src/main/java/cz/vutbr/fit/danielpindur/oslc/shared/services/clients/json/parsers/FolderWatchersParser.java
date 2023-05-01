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
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderModel;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Parser for the Folder watchers JSON response.
 */
public class FolderWatchersParser implements JsonObjectParser<JSONArray>{
    @Override
    public JSONArray parse(JSONObject json) throws JSONException {
        var results = new JSONArray();

        var watchers = json.getJSONArray("watchers");

        for (int i = 0; i < watchers.length(); i++) {
            var watcher = watchers.getJSONObject(i);
            var name = watcher.getString("name");
            var result = new JSONObject();
            result.put("name", name);
            results.put(result);
        }

        return results;
    }
}
