package cz.vutbr.fit.danielpindur.oslc.shared.services.models;

import java.util.HashSet;
import java.util.Set;

public class FolderModel {
    public String Title;
    public String Description;
    public Set<Integer> SubfolderIds = new HashSet<Integer>();
    public Integer ParentId;
    public Set<String> ContainsIssueKeys = new HashSet<String>();
}
