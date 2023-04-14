package cz.vutbr.fit.danielpindur.oslc.shared.services.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void CopyValues(final FolderTreeModel folderTreeModel) {
        CopyValues(folderTreeModel.Title, folderTreeModel.Description, folderTreeModel.ParentId, folderTreeModel.Id, folderTreeModel.ContainsIssueKeys, folderTreeModel.Folders);
    }

    public void CopyValues(final String title, final String description, final Integer parentId, final Integer id, final Set<String> containsIssueKeys, final Set<FolderTreeModel> folders) {
        Title = title;
        Description = description;
        ParentId = parentId;
        Id = id;
        ContainsIssueKeys = containsIssueKeys;
        Folders = folders.stream().map(x -> new FolderTreeModel(x.Id)).collect(Collectors.toSet());
    }
}
