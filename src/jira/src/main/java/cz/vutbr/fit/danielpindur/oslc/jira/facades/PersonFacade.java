package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.User;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Person;

import java.util.LinkedList;
import java.util.List;

public class PersonFacade extends BaseFacade {
    private Person MapResourceToResult(final User resource) {
        var result = new Person();
        result.setIdentifier(resource.getName());
        result.setName(resource.getDisplayName());
        result.setMbox(resource.getEmailAddress());
        result.setAbout(resourcesFactory.constructURIForPerson(resource.getName()));

        return result;
    }

    public Person get(final String id) {
        var userResource = getUserClient().getUser(id).claim();

        if (userResource == null) {
            return null;
        }

        return MapResourceToResult(userResource);
    }

    public List<Person> search(final String terms) {
        var userResources = getUserClient().findUsers(terms).claim();
        var results = new LinkedList<Person>();

        for (User userResource : userResources) {
            var result = MapResourceToResult(userResource);
            results.add(result);
        }

        return results;
    }

}
