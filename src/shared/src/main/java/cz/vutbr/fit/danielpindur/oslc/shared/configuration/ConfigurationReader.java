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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Configuration reader.
 */
public class ConfigurationReader {
    /**
     * Reads the configuration file.
     * 
     * @return Configuration.
     * 
     * @throws FileNotFoundException Thrown when the configuration file is not found.
     */
    public Configuration Read() throws FileNotFoundException {
        var currentRelativePath = Paths.get("");
        var exploded = currentRelativePath.toAbsolutePath().toString().split("/");
        var rootAbsolutePath = String.join("/", Arrays.copyOf(exploded, exploded.length - 2));
        var configFilePath =  rootAbsolutePath + "/config/configuration.json";

        try {
            Path filePath = Path.of(configFilePath);
            String content = Files.readString(filePath);
            var parser = new ConfigurationParser();
            return parser.Parse(content);
        } catch (IOException e) {
            throw new FileNotFoundException("Configuration file not found in " + configFilePath + ", current path: " + currentRelativePath.toAbsolutePath() + ", inner exception=" + e.getMessage());
        }
    }
}
