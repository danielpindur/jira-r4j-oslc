package cz.vutbr.fit.danielpindur.oslc.shared.translators;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import org.apache.jena.atlas.lib.NotImplemented;
import org.eclipse.lyo.core.query.SimpleTerm;

import java.util.HashMap;
import java.util.Map;

public class TranslatorBase {
    protected final Map<String, String> propertiesMap = new HashMap<>();
    protected final Map<SimpleTerm.Type, String> originalTypeMap = new HashMap<>();
    protected final Map<SimpleTerm.Type, String> replaceTypeMap = new HashMap<>();

    protected final Configuration configuration;

    public TranslatorBase() {
        configuration = ConfigurationProvider.GetConfiguration();

        originalTypeMap.put(SimpleTerm.Type.COMPARISON, "=");
        originalTypeMap.put(SimpleTerm.Type.IN_TERM, " in ");

        replaceTypeMap.put(SimpleTerm.Type.COMPARISON, " = ");
        replaceTypeMap.put(SimpleTerm.Type.IN_TERM, " IN ");
    }

    public String translate(final SimpleTerm term) {
        throw new NotImplemented();
    }
}
