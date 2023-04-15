package cz.vutbr.fit.danielpindur.oslc.shared.translators;

import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.UriHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.services.facades.BaseFacade;
import org.apache.jena.atlas.lib.NotImplemented;
import org.eclipse.lyo.core.query.SimpleTerm;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

    protected String translateIdentifiersInLabel(final String operands) {
        var exploded = operands.split(",");

        for (int i = 0; i < exploded.length; i++) {
            var identifier = exploded[i];
            identifier = IssueHelper.GetFormattedLabelsIdentifier(identifier);
            exploded[i] = identifier;
        }

        return String.join(",", exploded);
    }

    protected String cleanUri(final String operands) {
        return operands.replace("<", "").replace(">", "");
    }

    protected String translateUriToIds(final String operands) {
        var cleaned = cleanUri(operands);
        var exploded = cleaned.split(",");

        for (int i = 0; i < exploded.length; i++) {
            var uri = exploded[i];
            var identifier = UriHelper.GetIdFromUri(uri);
            exploded[i] = identifier;
        }

        return String.join(",", exploded);
    }

    protected String translateIssueUrisToKeys(final String operands) {
        var cleaned = cleanUri(operands);
        var exploded = cleaned.split(",");
        var keys = new LinkedList<String>();

        for (String uri : exploded) {
            var identifier = UriHelper.GetIdFromUri(uri);
            var issue = BaseFacade.getIssueClient().searchIssueByIdentifier(identifier);

            if ((IssueHelper.IsRequirement(issue) && UriHelper.IsRequirementUri(uri)) ||
                    IssueHelper.IsRequirementCollection(issue) && UriHelper.IsRequirementCollectionUri(uri)) {
                keys.add(issue.getKey());
            }
        }

        return String.join(",", keys);
    }

    protected String translateEmails(final String operands) {
        var exploded = operands.split(",");

        for (int i = 0; i < exploded.length; i++) {
            var user = exploded[i];
            user = user.replace("@", "\\u0040");
            exploded[i] = user;
        }

        return String.join(",", exploded);
    }

    protected String getComparisonOperatorFromSearch(final String search, final String property) {
        var initial = new LinkedList<Character>();
        initial.add('>');
        initial.add('<');
        initial.add('=');
        initial.add('!');

        return getComparisonOperatorFromSearch(search, property, initial);
    }

    protected String getComparisonOperatorFromSearch(final String search, final String property, final List<Character> initialAllow) {
        var cleaned = search.replace(property, "");
        var cursor = 0;
        var operator = "";
        var expected = initialAllow;

        while (cursor < cleaned.length()) {
            var character = cleaned.charAt(cursor);

            switch (character) {
                case ' ':
                    break;

                case '=':
                    if (!expected.contains('=')) {
                        throw new WebApplicationException("Unexpected '='", Response.Status.BAD_REQUEST);
                    }
                    return operator + "=";

                case '!':
                    if (!expected.contains('!')) {
                        throw new WebApplicationException("Unexpected '!'", Response.Status.BAD_REQUEST);
                    }
                    operator = "!";
                    expected.clear();
                    expected.add('=');
                    break;

                case '>':
                    if (!expected.contains('>')) {
                        throw new WebApplicationException("Unexpected '>'", Response.Status.BAD_REQUEST);
                    }
                    return ">";

                case '<':
                    if (!expected.contains('<')) {
                        throw new WebApplicationException("Unexpected '<'", Response.Status.BAD_REQUEST);
                    }
                    return "<";

                default:
                    throw new WebApplicationException("Expected comparison operator, found "  + character, Response.Status.BAD_REQUEST);
            }

            cursor++;
        }

        return operator;
    }

    protected String translateDateTime(final String operands) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime dateTime = LocalDateTime.parse(operands, inputFormatter);
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(java.time.ZoneId.systemDefault().getRules().getOffset(dateTime).getTotalSeconds());
        LocalDateTime localDateTime = dateTime.atOffset(ZoneOffset.UTC).withOffsetSameInstant(offset).toLocalDateTime();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        return "\"" + localDateTime.format(outputFormatter) + "\"";
    }

    public String translate(final SimpleTerm term) {
        throw new NotImplemented();
    }
}
