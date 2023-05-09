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
 * Generator for the Folder move JSON request.
 */
public class MoveFolderInputJsonGenerator implements JsonGenerator<FolderInput> {
    @Override
    public JSONObject generate(FolderInput input) throws JSONException {
        var jsonObject = new JSONObject();

        var folderObject = new JSONObject();
        folderObject.put("id", input.ParentFolderId);

        jsonObject.put("folder", folderObject);

        return jsonObject;
    }
}
