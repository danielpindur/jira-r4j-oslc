package cz.vutbr.fit.danielpindur.oslc.shared.configuration;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;

import java.io.FileNotFoundException;

public final class ConfigurationProvider {
    private static ConfigurationProvider INSTANCE;

    private final Configuration configuration;

    private ConfigurationProvider() throws FileNotFoundException {
        var configurationReader = new ConfigurationReader();

        configuration = configurationReader.Read();
    }

    public static ConfigurationProvider getInstance() {
        return INSTANCE;
    }

    public static void Initialize() throws FileNotFoundException {
        INSTANCE = new ConfigurationProvider();
    }

    public Configuration GetConfiguration() {
        return configuration;
    }
}
