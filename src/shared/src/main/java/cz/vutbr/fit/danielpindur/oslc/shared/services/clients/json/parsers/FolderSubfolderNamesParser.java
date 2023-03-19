package cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class FolderSubfolderNamesParser implements JsonObjectParser<Set<String>>{
    @Override
    public Set<String> parse(JSONObject json) throws JSONException {
        var results = new HashSet<String>();

        var subfolders = json.getJSONArray("folders");

        for (int i = 0; i < subfolders.length(); i++) {
            var subfolder = subfolders.getJSONObject(i);
            var name = subfolder.getString("name");
            results.add(name);
        }

        return results;
    }
}
