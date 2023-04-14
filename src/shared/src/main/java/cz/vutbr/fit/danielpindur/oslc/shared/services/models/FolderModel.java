package cz.vutbr.fit.danielpindur.oslc.shared.services.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
