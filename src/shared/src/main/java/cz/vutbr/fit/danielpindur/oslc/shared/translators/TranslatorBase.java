package cz.vutbr.fit.danielpindur.oslc.shared.translators;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import org.apache.jena.atlas.lib.NotImplemented;

import java.util.HashMap;
import java.util.Map;

public class TranslatorBase {
    protected final Map<String, String> propertiesMap = new HashMap<>();
    //protected final Map<>
    protected final Configuration configuration;

    protected TranslatorBase() {
        configuration = ConfigurationProvider.GetConfiguration();
    }

    public String translate(final String term) {
        throw new NotImplemented();
    }
}
