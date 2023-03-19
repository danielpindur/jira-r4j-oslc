package cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
