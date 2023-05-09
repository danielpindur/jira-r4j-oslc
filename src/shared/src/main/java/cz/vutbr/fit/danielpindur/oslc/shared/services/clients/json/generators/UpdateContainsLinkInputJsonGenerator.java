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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Generator for the Folder update contains link JSON request.
 */
public class UpdateContainsLinkInputJsonGenerator implements JsonGenerator<Integer> {
    @Override
    public JSONObject generate(Integer folderId) throws JSONException {
        var jsonObject = new JSONObject();

        var folderObject = new JSONObject();
        folderObject.put("id", folderId);

        jsonObject.put("folder", folderObject);

        return jsonObject;
    }
}
