/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.User;
import cz.vutbr.fit.danielpindur.oslc.jira.ResourcesFactory;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Person;
import cz.vutbr.fit.danielpindur.oslc.shared.services.facades.BaseFacade;
import org.apache.jena.atlas.lib.NotImplemented;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

/**
 * Facade for Person resource
 */
public class PersonFacade extends BaseFacade {
    @Inject ResourcesFactory resourcesFactory;

    /**
     * Maps API user resource to OSLC person resource
     * 
     * @param resource API user resource
     * 
     * @return OSLC person resource
     */
    private Person MapResourceToResult(final User resource) {
        var result = new Person();
        result.setIdentifier(resource.getName());
        result.setName(resource.getDisplayName());
        result.setMbox(resource.getEmailAddress());
        result.setAbout(resourcesFactory.constructURIForPerson(resource.getName()));

        return result;
    }

    /**
     * Gets user by email
     * 
     * @param email Email to search by
     * 
     * @return User resource
     */
    protected User GetUserByEmail(final String email) {
        var userResources = getUserClient().searchUsersByEmail(email).claim();
        User result = null;

        for (User user : userResources) {
            // Only one user should make it here (email is unique property), resulting in single iteration
            result = user;
        }

        return result;
    }

    /**
     * Gets user by username
     * 
     * @param username Username to search by
     * 
     * @return User resource
     */
    public Person get(final String id) {
        User userResource = null;

        try {
            userResource = getUserClient().getUser(id).claim();
        } catch (RestClientException e) {
            if (!e.getStatusCode().isPresent() || e.getStatusCode().get() != 404) {
                throw e;
            }
        }

        if (userResource == null) {
            // Try search by email
            userResource = GetUserByEmail(id);
        }

        return userResource != null ? MapResourceToResult(userResource) : null;
    }

    /**
     * Search for users by terms
     * 
     * @param terms Terms to search by
     * 
     * @return List of users
     */    
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
