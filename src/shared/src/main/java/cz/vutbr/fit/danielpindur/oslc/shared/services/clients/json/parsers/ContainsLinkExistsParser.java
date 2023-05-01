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
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Parser for the Folder Contains Link Exists JSON response.
 */
public class ContainsLinkExistsParser implements JsonArrayParser<Boolean> {
    private final String projectKey;

    public ContainsLinkExistsParser(final String projectKey) {
        this.projectKey = projectKey;
    }

    @Override
    public Boolean parse(JSONArray json) throws JSONException {
        if (json.length() < 1) {
            return false;
        }

        var link = json.getJSONObject(0);
        var paths = link.getJSONArray("paths");

        for (int i = 0; i < paths.length(); i++) {
            var path = paths.getJSONObject(i);
            var pathProjectKey = path.getString("prjKey");
            if (pathProjectKey.equalsIgnoreCase(projectKey)) {
                return true;
            }
        }

        return false;
    }
}
