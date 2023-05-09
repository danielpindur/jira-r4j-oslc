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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;

/**
 * Configuration JSON parser.
 */
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
