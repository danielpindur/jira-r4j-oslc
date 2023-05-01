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
 * Representation of the R4J folder tree structure.
 */
public class FolderTreeModel {
    public String Title;
    public String Description;
    public Integer ParentId;
    public Integer Id;
    public Set<FolderTreeModel> Folders = new HashSet<FolderTreeModel>();
    public Set<String> ContainsIssueKeys = new HashSet<String>();

    public FolderTreeModel() { }

    private FolderTreeModel(final Integer id) {
        Id = id;
    }

    /**
     * Shallow copy of the folder tree model.
     * 
     * @param folderTreeModel Folder tree model to copy values from.
     */
    public void CopyValues(final FolderTreeModel folderTreeModel) {
        CopyValues(folderTreeModel.Title, folderTreeModel.Description, folderTreeModel.ParentId, folderTreeModel.Id, folderTreeModel.ContainsIssueKeys, folderTreeModel.Folders);
    }

    /**
     * Shallow copy of the folder tree model.
     * 
     * @param title Title of the folder.
     * @param description Description of the folder.
     * @param parentId Id of the parent folder.
     * @param id Id of the folder.
     * @param containsIssueKeys Set of issue keys contained in the folder.
     * @param folders Set of subfolders.
     */
    public void CopyValues(final String title, final String description, final Integer parentId, final Integer id, final Set<String> containsIssueKeys, final Set<FolderTreeModel> folders) {
        Title = title;
        Description = description;
        ParentId = parentId;
        Id = id;
        ContainsIssueKeys = containsIssueKeys;
        Folders = folders.stream().map(x -> new FolderTreeModel(x.Id)).collect(Collectors.toSet());
    }
}
