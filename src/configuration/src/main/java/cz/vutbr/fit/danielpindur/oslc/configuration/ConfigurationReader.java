package cz.vutbr.fit.danielpindur.oslc.configuration;

import cz.vutbr.fit.danielpindur.oslc.configuration.models.Configuration;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigurationReader {
    private final String configFilePath = "../../config/configuration.json";

    public Configuration Read() throws FileNotFoundException {
        var file = new FileReader(configFilePath);
        var parser = new ConfigurationParser();
        return parser.Parse(file.toString());
    }
}
