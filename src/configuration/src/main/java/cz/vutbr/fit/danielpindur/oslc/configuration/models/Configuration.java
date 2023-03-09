package cz.vutbr.fit.danielpindur.oslc.configuration.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration {
    @JsonProperty("RequirementIssueTypeName")
    public String RequirementIssueTypeName;

    @JsonProperty("RequirementCollectionIssueTypeName")
    public String RequirementCollectionIssueTypeName;

    @JsonProperty("IssueLinkTypeName")
    public String IssueLinkTypeName;

    @JsonProperty("IdentifierFieldName")
    public String IdentifierFieldName;

    @JsonProperty("JiraServer")
    public JiraServer JiraServer;
}
