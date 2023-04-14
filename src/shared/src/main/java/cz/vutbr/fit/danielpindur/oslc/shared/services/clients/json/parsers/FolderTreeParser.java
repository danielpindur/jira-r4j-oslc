package cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderModel;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderTreeModel;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class FolderTreeParser implements JsonObjectParser<FolderTreeModel>{
    @Override
    public FolderTreeModel parse(JSONObject json) throws JSONException {
        var result = new FolderTreeModel();

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

        var folders = json.getJSONArray("folders");
        for (int i = 0; i < folders.length(); i++) {
            result.Folders.add(parse(folders.getJSONObject(i)));
        }

        return result;
    }
}
