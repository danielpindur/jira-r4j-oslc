package cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.inputs.FolderInput;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
