package cz.vutbr.fit.danielpindur.oslc.jira.translators;

import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.UriHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.translators.TranslatorBase;
import org.eclipse.lyo.core.query.SimpleTerm;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

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
    public String translate(final SimpleTerm term) {
        var searchString = term.toString();
        var property = term.property().toString();
        var type = term.type();

        if (type == SimpleTerm.Type.COMPARISON) {
            var operator = getComparisonOperatorFromSearch(searchString, property);

            var operand = searchString.replace(property, "").replace(operator, "").replace("\"", "");

            if (property.equalsIgnoreCase("dcterms:identifier") && configuration.SaveIdentifierInLabelsField) {
                operand = translateIdentifiersInLabel(operand);
            }

            if (property.equalsIgnoreCase("jira:project")) {
                operand = translateUriToIds(operand);
            }

            if (property.equalsIgnoreCase("dcterms:creator")) {
                operand = translateEmails(translateUriToIds(operand));
            }

            if (property.equalsIgnoreCase("dcterms:created") || property.equalsIgnoreCase("dcterms:modified")) {
                operand = translateDateTime(operand);
            }

            return propertiesMap.get(property) + " " + operator + " " + operand;
        }
        else if (type == SimpleTerm.Type.IN_TERM) {
            var operator = originalTypeMap.get(type);

            var operands = searchString.replace(property, "").replace(operator, "")
                    .replace("[", "").replace("]", "").replace("\"", "");

            if (property.equalsIgnoreCase("dcterms:identifier") && configuration.SaveIdentifierInLabelsField) {
                operands = translateIdentifiersInLabel(operands);
            }

            if (property.equalsIgnoreCase("jira:project")) {
                operands = translateUriToIds(operands);
            }

            if (property.equalsIgnoreCase("dcterms:creator")) {
                operands = translateEmails(translateUriToIds(operands));
            }

            return propertiesMap.get(property) + replaceTypeMap.get(type) + "(" + operands + ")";
        }

        throw new WebApplicationException("Unknown Query operator (" + type.toString() + ")", Response.Status.BAD_REQUEST);
    }
}
