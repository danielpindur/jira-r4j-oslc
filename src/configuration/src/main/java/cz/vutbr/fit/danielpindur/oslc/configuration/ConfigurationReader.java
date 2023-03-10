package cz.vutbr.fit.danielpindur.oslc.configuration;

import cz.vutbr.fit.danielpindur.oslc.configuration.models.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ConfigurationReader {
    public Configuration Read() throws FileNotFoundException {
        var currentRelativePath = Paths.get("");
        var exploded = currentRelativePath.toAbsolutePath().toString().split("/");
        var rootAbsolutePath = String.join("/", Arrays.copyOf(exploded, exploded.length - 2));
        var configFilePath =  rootAbsolutePath + "/config/configuration.json";

        try {
            Path filePath = Path.of(configFilePath);
            String content = Files.readString(filePath);
            var parser = new ConfigurationParser();
            var configuration = parser.Parse(content);
            return configuration;
        } catch (IOException e) {
            throw new FileNotFoundException("Configuration file not found in " + configFilePath + ", current path: " + currentRelativePath.toAbsolutePath() + ", inner exception=" + e.getMessage());
        }
    }
}
