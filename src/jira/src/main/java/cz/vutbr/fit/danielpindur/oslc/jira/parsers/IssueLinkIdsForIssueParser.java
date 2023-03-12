package cz.vutbr.fit.danielpindur.oslc.jira.parsers;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashSet;
import java.util.Set;


public class IssueLinkIdsForIssueParser implements JsonObjectParser<Set<String>> {

    private final String issueLinkName;

    public IssueLinkIdsForIssueParser(final String issueLinkName) {
        this.issueLinkName = issueLinkName;
    }

    @Override
    public Set<String> parse(JSONObject json) throws JSONException {
        var result = new HashSet<String>();

        var fieldsJson = json.getJSONObject("fields");
        var issueLinksJson = fieldsJson.getJSONArray("issuelinks");
        if (issueLinksJson == null) {
            return result;
        }

        for (int i = 0; i < issueLinksJson.length(); i++) {
            var issueLinkJson = issueLinksJson.getJSONObject(i);
            var issueLinkType = issueLinkJson.getJSONObject("type");
            var issueLinkName = issueLinkType.getString("name");

            if (this.issueLinkName == null || issueLinkName.equalsIgnoreCase(this.issueLinkName)) {
                var issueLinkId = issueLinkJson.getString("id");
                if (issueLinkId != null) {
                    result.add(issueLinkId);
                }
            }
        }
        
        return result;
    }
}
