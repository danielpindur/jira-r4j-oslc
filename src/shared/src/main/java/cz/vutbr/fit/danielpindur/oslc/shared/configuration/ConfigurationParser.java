package cz.vutbr.fit.danielpindur.oslc.shared.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;

public class ConfigurationParser {

    public Configuration Parse(final String json) throws RuntimeException {
        var mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ERROR: Failed to parse configuration file=" + json + ", inner exception=" + e.getMessage());
        }
    }
}