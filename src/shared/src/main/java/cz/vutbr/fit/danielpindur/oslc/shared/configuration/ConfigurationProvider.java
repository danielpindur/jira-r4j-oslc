package cz.vutbr.fit.danielpindur.oslc.shared.configuration;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;

import java.io.FileNotFoundException;

public final class ConfigurationProvider {
    private static ConfigurationProvider INSTANCE;

    private final Configuration configuration;

    private ConfigurationProvider() throws FileNotFoundException {
        var configurationReader = new ConfigurationReader();

        configuration = configurationReader.Read();
        Validate();
    }

    private void Validate() {
        if (configuration == null) {
            throw new RuntimeException("Configuration is missing");
        }

        if (configuration.IdentifierFieldName.equalsIgnoreCase(configuration.LabelsFieldName) && !configuration.SaveIdentifierInLabelsField) {
            throw new RuntimeException("Cannot save both Identifier and Labels to " + configuration.IdentifierFieldName + ", if you want the Identifier to be stored in Labels field enable SaveIdentifierInLabelsField to override this error!");
        }

        if (configuration.RequirementCollectionIssueTypeName.equalsIgnoreCase(configuration.RequirementIssueTypeName)) {
            throw new RuntimeException("IssueType for Requirement and Requirement collection cannot be same!");
        }
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
