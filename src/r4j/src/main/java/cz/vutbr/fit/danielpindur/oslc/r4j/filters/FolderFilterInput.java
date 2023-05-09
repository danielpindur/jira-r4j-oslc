/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.r4j.filters;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Folder resources filter input.
 */
public class FolderFilterInput {
    public List<String> Identifiers = new LinkedList<>();
    public List<String> NotIdentifiers = new LinkedList<>();
    public List<String> ParentFolderIds = new LinkedList<>();
    public List<String> NotParentFolderIds = new LinkedList<>();
    public List<String> ContainUris = new LinkedList<>();
    public List<String> NotContainUris = new LinkedList<>();

    /**
     * Add item to list with check if list is empty, if not, throw exception.
     * 
     * @param item   Item to add.
     * @param target Target list.
     */
    public void addToList(final String item, final List<String> target) {
        if (!target.isEmpty()) {
            throw new WebApplicationException("Multiple filters for single criteria detected", Response.Status.BAD_REQUEST);
        }

        target.add(item);
    }

    /**
     * Add multiple items to list with check if list is empty, if not, throw exception.
     * 
     * @param items  Items to add.
     * @param target Target list.
     */
    public void addToListMultiple(final String items, final List<String> target) {
        var exploded = items.split(",");

        var explodedList = new LinkedList<String>(Arrays.asList(exploded));
        addToListMultiple(explodedList, target);
    }


    /**
     * Add multiple items to list with check if list is empty, if not, throw exception.
     * 
     * @param items  Items to add.
     * @param target Target list.
     */
    public void addToListMultiple(final List<String> items, final List<String> target) {
        if (!target.isEmpty()) {
            throw new WebApplicationException("Multiple filters for single criteria detected", Response.Status.BAD_REQUEST);
        }

        target.addAll(items);
    }
}
