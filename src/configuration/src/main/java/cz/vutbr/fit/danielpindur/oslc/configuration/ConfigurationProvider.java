package cz.vutbr.fit.danielpindur.oslc.configuration;

import cz.vutbr.fit.danielpindur.oslc.configuration.models.Configuration;

import java.io.FileNotFoundException;

public final class ConfigurationProvider {
    private static ConfigurationProvider INSTANCE;

    private final Configuration configuration;

    private ConfigurationProvider() {
        var configurationReader = new ConfigurationReader();

        try {
            configuration = configurationReader.Read();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("ERROR: Configuration file not found!");
        }
    }

    public static ConfigurationProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigurationProvider();
        }

        return INSTANCE;
    }

    public Configuration GetConfiguration() {
        return configuration;
    }
}
