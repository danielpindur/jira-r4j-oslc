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

import cz.vutbr.fit.danielpindur.oslc.r4j.helpers.FolderHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderModel;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderTreeModel;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Folder resources filter.
 */
public class FolderFilter {
    private final FolderFilterInput folderFilterInput;
    private final String terms;
    private final String projectKey;

    public FolderFilter(final FolderFilterInput folderFilterInput, final String terms, final String projectKey) {
        this.folderFilterInput = folderFilterInput;
        this.terms = terms;
        this.projectKey = projectKey;
    }

    /**
     * Converts folder tree to lists of folders by depth traversal.
     * 
     * @param folderTreeModel Folder tree model.
     * 
     * @return List of folders.
     */
    private List<FolderTreeModel> convertTreeToList(final FolderTreeModel folderTreeModel) {
        var list = new LinkedList<FolderTreeModel>();
        var copy = new FolderTreeModel();
        copy.CopyValues(folderTreeModel);

        list.add(copy);
        for (var folder : folderTreeModel.Folders) {
            list.addAll(convertTreeToList(folder));
        }

        return list;
    }

    /**
     * Filter folders by full text.
     * 
     * @param folderTreeModel List of folders.
     * 
     * @return Filtered list of folders.
     */
    private List<FolderTreeModel> filterTerms(List<FolderTreeModel> folderTreeModels) {
        if (terms == null || terms.isEmpty()) {
            return folderTreeModels;
        }

        if (folderTreeModels.isEmpty()) {
            return folderTreeModels;
        }

        return folderTreeModels.stream().filter(x ->
                x.Description.toUpperCase().contains(terms.toUpperCase()) ||
                x.Title.toUpperCase().contains(terms.toUpperCase())).collect(Collectors.toList());
    }

    /**
     * Filter folders by identifiers.
     * 
     * @param folderTreeModel List of folders.
     * 
     * @return Filtered list of folders.
     */
    private List<FolderTreeModel> filterIdentifiers(List<FolderTreeModel> folderTreeModels) {
        if ((folderFilterInput.Identifiers == null || folderFilterInput.Identifiers.isEmpty()) &&
                (folderFilterInput.NotIdentifiers == null || folderFilterInput.NotIdentifiers.isEmpty())) {
            return folderTreeModels;
        }

        if (folderTreeModels.isEmpty()) {
            return folderTreeModels;
        }

        var positive = folderTreeModels.stream();
        if (folderFilterInput.Identifiers != null && !folderFilterInput.Identifiers.isEmpty()) {
            positive = positive.filter(x ->
                    folderFilterInput.Identifiers.contains(FolderHelper.ConstructFolderIdentifier(projectKey, x.Id)));
        }

        var negative = positive;
        if (folderFilterInput.NotIdentifiers != null && !folderFilterInput.NotIdentifiers.isEmpty()) {
            negative = negative.filter(x ->
                    !folderFilterInput.NotIdentifiers.contains(FolderHelper.ConstructFolderIdentifier(projectKey, x.Id)));
        }

        return negative.collect(Collectors.toList());
    }

    /**
     * Filter folders by parent identifiers.
     * 
     * @param folderTreeModel List of folders.
     * 
     * @return Filtered list of folders.
     */
    private List<FolderTreeModel> filterParentIds(List<FolderTreeModel> folderTreeModels) {
        if ((folderFilterInput.ParentFolderIds == null || folderFilterInput.ParentFolderIds.isEmpty()) &&
                (folderFilterInput.NotParentFolderIds == null || folderFilterInput.NotParentFolderIds.isEmpty())) {
            return folderTreeModels;
        }

        if (folderTreeModels.isEmpty()) {
            return folderTreeModels;
        }

        var positive = folderTreeModels.stream();
        if (folderFilterInput.ParentFolderIds != null && !folderFilterInput.ParentFolderIds.isEmpty()) {
            positive = positive.filter(x ->
                    folderFilterInput.ParentFolderIds.contains(FolderHelper.ConstructFolderIdentifier(projectKey, x.ParentId)));
        }

        var negative = positive;
        if (folderFilterInput.NotParentFolderIds != null && !folderFilterInput.NotParentFolderIds.isEmpty()) {
            negative = negative.filter(x ->
                    !folderFilterInput.NotParentFolderIds.contains(FolderHelper.ConstructFolderIdentifier(projectKey, x.ParentId)));
        }

        return negative.collect(Collectors.toList());
    }

    /**
     * Filter folders by contains.
     * 
     * @param folderTreeModel List of folders.
     * 
     * @return Filtered list of folders.
     */
    private List<FolderTreeModel> filterContains(List<FolderTreeModel> folderTreeModels) {
        if ((folderFilterInput.ContainUris == null || folderFilterInput.ContainUris.isEmpty()) &&
                (folderFilterInput.NotContainUris == null || folderFilterInput.NotContainUris.isEmpty())) {
            return folderTreeModels;
        }

        if (folderTreeModels.isEmpty()) {
            return folderTreeModels;
        }

        List<String> issueKeysContains = new LinkedList<>();
        if (folderFilterInput.ContainUris != null) {
            issueKeysContains = folderFilterInput.ContainUris.stream().map(x ->
                    FolderHelper.GetIssueKeyFromUri(URI.create(x))).collect(Collectors.toList());
        }

        List<String> issueKeysNotContains = new LinkedList<>();
        if (folderFilterInput.NotContainUris != null) {
            issueKeysNotContains = folderFilterInput.NotContainUris.stream().map(x ->
                    FolderHelper.GetIssueKeyFromUri(URI.create(x))).collect(Collectors.toList());
        }

        var positive = folderTreeModels.stream();
        List<String> finalIssueKeysContains = issueKeysContains;
        if (!finalIssueKeysContains.isEmpty()) {
            positive = positive.filter(x ->
                    x.ContainsIssueKeys.stream().anyMatch(y -> finalIssueKeysContains.stream().anyMatch(y::equalsIgnoreCase)));
        }

        var negative = positive;
        List<String> finalIssueKeysNotContains = issueKeysNotContains;
        if (!finalIssueKeysNotContains.isEmpty()) {
            negative = negative.filter(x ->
                    x.ContainsIssueKeys.stream().noneMatch(y -> finalIssueKeysNotContains.stream().anyMatch(y::equalsIgnoreCase)));
        }

        return negative.collect(Collectors.toList());
    }

    /**
     * Map folder tree to folder model.
     * 
     * @param folderTreeModel Folder tree.
     * 
     * @return Folder model.
     */
    private List<FolderModel> mapTreeToModel(List<FolderTreeModel> folderTreeModels) {
        return folderTreeModels.stream().map(FolderModel::new).collect(Collectors.toList());
    }

    /**
     * Filter folders.
     * 
     * @param folderTreeModel Folder tree.
     * 
     * @return Filtered folders.
     */
    public List<FolderModel> filter(final FolderTreeModel folderTreeModel) {
        var converted = convertTreeToList(folderTreeModel);

        var identifierFiltered = filterIdentifiers(converted);
        var parentIdFiltered = filterParentIds(identifierFiltered);
        var termsFiltered = filterTerms(parentIdFiltered);
        var containsFiltered = filterContains(termsFiltered);

        return mapTreeToModel(containsFiltered);
    }
}
