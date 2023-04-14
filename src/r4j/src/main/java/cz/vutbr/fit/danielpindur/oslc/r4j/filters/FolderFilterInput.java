package cz.vutbr.fit.danielpindur.oslc.r4j.filters;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FolderFilterInput {
    public List<String> Identifiers = new LinkedList<>();
    public List<String> NotIdentifiers = new LinkedList<>();
    public List<String> ParentFolderIds = new LinkedList<>();
    public List<String> NotParentFolderIds = new LinkedList<>();
    public List<String> ContainUris = new LinkedList<>();
    public List<String> NotContainUris = new LinkedList<>();

    public void addToList(final String item, final List<String> target) {
        if (!target.isEmpty()) {
            throw new WebApplicationException("Multiple filters for single criteria detected", Response.Status.BAD_REQUEST);
        }

        target.add(item);
    }

    public void addToListMultiple(final String items, final List<String> target) {
        var exploded = items.split(",");

        var explodedList = new LinkedList<String>(Arrays.asList(exploded));
        addToListMultiple(explodedList, target);
    }


    public void addToListMultiple(final List<String> items, final List<String> target) {
        if (!target.isEmpty()) {
            throw new WebApplicationException("Multiple filters for single criteria detected", Response.Status.BAD_REQUEST);
        }

        target.addAll(items);
    }
}
