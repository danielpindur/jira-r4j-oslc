/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.services.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of the R4J folder.
 */
public class FolderModel {
    public String Title;
    public String Description;
    public Set<Integer> SubfolderIds = new HashSet<Integer>();
    public Integer ParentId;
    public Set<String> ContainsIssueKeys = new HashSet<String>();
    public Integer Id;

    public FolderModel() { }

    public FolderModel(final FolderTreeModel folderTreeModel) {
        Title = folderTreeModel.Title;
        Description = folderTreeModel.Description;
        ParentId = folderTreeModel.ParentId;
        Id = folderTreeModel.Id;;
        SubfolderIds = folderTreeModel.Folders.stream().map(x -> x.Id).collect(Collectors.toSet());
        ContainsIssueKeys = folderTreeModel.ContainsIssueKeys;
    }
}
