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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Set;

/**
 * Parser for the Folder JSON response.
 */
public class FolderParser implements JsonObjectParser<FolderModel>{
    @Override
    public FolderModel parse(JSONObject json) throws JSONException {
        var result = new FolderModel();
        result.Title = json.getString("name");
        result.ParentId = Integer.parseInt(json.getString("parentId"));
        result.Description = json.getString("description");
        result.Id = Integer.parseInt(json.getString("id"));

        var issues = json.getJSONArray("issues");
        for (int i = 0; i < issues.length(); i++) {
            var issue = issues.getJSONObject(i);
            var issueData = issue.getJSONObject("data");
            var issueKey = issueData.getString("key");
            result.ContainsIssueKeys.add(issueKey);
        }

        var subfolders = json.getJSONArray("folders");
        for (int i = 0; i < subfolders.length(); i++) {
            var subfolder = subfolders.getJSONObject(i);
            var subfolderId = Integer.parseInt(subfolder.getString("id"));
            result.SubfolderIds.add(subfolderId);
        }

        return result;
    }
}
