package cz.vutbr.fit.danielpindur.oslc.configuration.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JiraServer {
    @JsonProperty("Url")
    public String Url;

    @JsonProperty("EnableBasicAuth")
    public Boolean EnableBasicAuth;

    @JsonProperty("EnableOAuth")
    public Boolean EnableOAuth;
}
