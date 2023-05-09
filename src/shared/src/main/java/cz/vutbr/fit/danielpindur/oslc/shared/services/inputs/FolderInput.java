/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.services.inputs;

/**
 * Input model for creating and updating a folder through the R4J client.
 */
public class FolderInput {

    public final String Title;

    public final String Description;

    public final Integer ParentFolderId;

    public FolderInput(final String title, final String description, final Integer parentFolderId) {
        Title = title;
        Description = description;
        ParentFolderId = parentFolderId;
    }
}
