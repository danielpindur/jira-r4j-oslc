package cz.vutbr.fit.danielpindur.oslc.jira.translators;

import cz.vutbr.fit.danielpindur.oslc.shared.translators.TranslatorBase;
import org.apache.jena.atlas.lib.NotImplemented;

public class IssueTranslator extends TranslatorBase {

    public IssueTranslator() {
        super();
        propertiesMap.put("oslc:shortTitle", "key");
        propertiesMap.put("dcterms:subject", configuration.LabelsFieldName);
        propertiesMap.put("dcterms:modified", "updated");
        propertiesMap.put("dcterms:creator", "creator");
        propertiesMap.put("dcterms:created", "created");
        propertiesMap.put("jira:jiraId", "id");
        propertiesMap.put("jira:project", "project");
        propertiesMap.put("dcterms:identifier", configuration.IdentifierFieldName);
    }

    @Override
    public String translate(final String term) {
        throw new NotImplemented();
    }
}
