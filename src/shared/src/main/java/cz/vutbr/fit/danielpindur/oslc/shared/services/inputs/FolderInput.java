package cz.vutbr.fit.danielpindur.oslc.shared.services.inputs;

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
