/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.inputs.FolderInput;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Generator for the Folder update JSON request.
 */
public class UpdateFolderInputJsonGenerator implements JsonGenerator<FolderInput> {
    final JSONArray watchers;

    public UpdateFolderInputJsonGenerator(JSONArray watchers) {
        this.watchers = watchers;
    }

    @Override
    public JSONObject generate(FolderInput input) throws JSONException {
        var jsonObject = new JSONObject();
        jsonObject.put("name", input.Title);
        jsonObject.put("description", input.Description);

        if (watchers != null) {
            jsonObject.put("watchers", watchers);
        }

        return jsonObject;
    }
}
