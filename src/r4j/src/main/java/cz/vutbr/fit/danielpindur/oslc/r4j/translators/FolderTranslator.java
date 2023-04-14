package cz.vutbr.fit.danielpindur.oslc.r4j.translators;

import cz.vutbr.fit.danielpindur.oslc.r4j.filters.FolderFilterInput;
import cz.vutbr.fit.danielpindur.oslc.shared.translators.TranslatorBase;
import org.eclipse.lyo.core.query.SimpleTerm;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.LinkedList;

public class FolderTranslator extends TranslatorBase {
    public FolderFilterInput filterInput = new FolderFilterInput();

    public FolderTranslator() {
        super();
        propertiesMap.put("dcterms:identifier", ""); // string
        propertiesMap.put("jira_r4j:contains", ""); // UriList
        propertiesMap.put("jira_r4j:parent", ""); // Uri
    }

    @Override
    public String translate(final SimpleTerm term) {
        var searchString = term.toString();
        var property = term.property().toString();
        var type = term.type();

        if (type == SimpleTerm.Type.COMPARISON) {
            var allowedOperators = new LinkedList<Character>();
            allowedOperators.add('=');
            allowedOperators.add('!');

            var operator = getComparisonOperatorFromSearch(searchString, property, allowedOperators);

            var operand = searchString.replace(property, "").replace(operator, "").replace("\"", "");

            if (property.equalsIgnoreCase("dcterms:identifier")) {
                if (operator.equals("=")) {
                    filterInput.addToList(operand, filterInput.Identifiers);
                } else if (operator.equals("!=")) {
                    filterInput.addToList(operand, filterInput.NotIdentifiers);
                }
            }
            else if (property.equalsIgnoreCase("jira_r4j:contains")) {
                if (operator.equals("=")) {
                    filterInput.addToList(cleanUri(operand), filterInput.ContainUris);
                } else if (operator.equals("!=")) {
                    filterInput.addToList(cleanUri(operand), filterInput.NotContainUris);
                }
            }
            else if (property.equalsIgnoreCase("jira_r4j:parent")) {
                if (operator.equals("=")) {
                    filterInput.addToList(translateUriToIds(operand), filterInput.ParentFolderIds);
                } else if (operator.equals("!=")) {
                    filterInput.addToList(translateUriToIds(operand), filterInput.NotParentFolderIds);
                }
            }
            else {
                throw new WebApplicationException("Unknown query parameter " + property, Response.Status.BAD_REQUEST);
            }

            return null;
        }
        else if (type == SimpleTerm.Type.IN_TERM) {
            var operator = originalTypeMap.get(type);

            var operands = searchString.replace(property, "").replace(operator, "")
                    .replace("[", "").replace("]", "").replace("\"", "");

            if (property.equalsIgnoreCase("dcterms:identifier")) {
                filterInput.addToListMultiple(operands, filterInput.Identifiers);
            }
            else if (property.equalsIgnoreCase("jira_r4j:contains")) {
                filterInput.addToListMultiple(cleanUri(operands), filterInput.ContainUris);
            }
            else if (property.equalsIgnoreCase("jira_r4j:parent")) {
                filterInput.addToListMultiple(translateUriToIds(operands), filterInput.ParentFolderIds);
            }
            else {
                throw new WebApplicationException("Unknown query parameter " + property, Response.Status.BAD_REQUEST);
            }

            return null;
        }

        throw new WebApplicationException("Unknown Query operator (" + type.toString() + ")", Response.Status.BAD_REQUEST);
    }
}
