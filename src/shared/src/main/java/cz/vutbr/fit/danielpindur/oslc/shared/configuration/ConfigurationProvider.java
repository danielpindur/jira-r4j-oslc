/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.configuration;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;

import java.io.FileNotFoundException;

/**
 * Singleton provider for the configuration.
 */
public final class ConfigurationProvider {
    private static ConfigurationProvider INSTANCE;

    private final Configuration configuration;

    private ConfigurationProvider() throws FileNotFoundException {
        var configurationReader = new ConfigurationReader();

        configuration = configurationReader.Read();
        Validate();
    }

    /**
     * Validates the configuration.
     */
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

    /**
     * Get the singleton instance of the configuration provider.
     */
    private static ConfigurationProvider getInstance() {
        if (INSTANCE == null) {
            try {
                Initialize();
            } catch (Exception ignored) { }
        }

        return INSTANCE;
    }

    /**
     * Initialize the singleton instance of the configuration provider.
     */
    public static void Initialize() throws FileNotFoundException {
        INSTANCE = new ConfigurationProvider();
    }

    /**
     * Get the configuration.
     */
    public static Configuration GetConfiguration() {
        return getInstance().configuration;
    }
}
