package cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.inputs.FolderInput;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class FolderInputJsonGenerator implements JsonGenerator<FolderInput> {
    @Override
    public JSONObject generate(FolderInput input) throws JSONException {
        var jsonObject = new JSONObject();
        jsonObject.put("name", input.Title);
        jsonObject.put("description", input.Description);

        var parentObject = new JSONObject();
        parentObject.put("id", input.ParentFolderId);
        jsonObject.put("parent", parentObject);

        return jsonObject;
    }
}
