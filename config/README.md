# Adaptor configuration

This file provides an overview of available configuration options for the adaptor and their meaning.

## Structure

```json
{
    "JiraServer" : {
        "Url" : "https://localhost:8080",
        "EnableBasicAuth" : true,
        "EnableOAuth" : true
    },
    "RequirementIssueTypeName" : "Bug",
    "RequirementCollectionIssueTypeName" : "Story",
    "IssueLinkTypeName" : "Blocks",
    "IdentifierFieldName" : "Labels",
    "LabelsFieldName" : "Labels",
    "LabelsIdentifierFormat" : "Identifier:{0}",
    "RootFolderId" : "ROOT",
    "SaveIdentifierInLabelsField": true,
    "JiraAdaptorUrl" : "http://localhost:8081"
}
```

## Configuration options

```JiraServer.Url``` - URL of the Jira server the adaptor is supposed to provide the functionality for

```JiraServer.EnableBasicAuth``` - whether the adaptor should allow the usage of BASIC authentication

```JiraServer.EnableOAuth``` - whether the adaptor should allow the usage of OAuth authentication

```RequirementIssueTypeName``` - name of the issue type, that should be used for saving ```oslc_rm:Requirement``` as issues

```RequirementCollectionIssueTypeName``` - name of the issue type, that should be used for saving ```oslc_rm:RequirementCollection``` as issues

```IssueLinkTypeName``` - name of the issue link type, that should be used for saving the ```oslc_rm:decomposes``` and ```oslc_rm:decomposedBy``` property for ```oslc_rm:Requirement``` and ```oslc_rm:RequirementCollection```

```IdentifierFieldName``` - name of the custom field, that should be used for saving the ```dcterms:identifier``` property for ```oslc_rm:Requirement``` and ```oslc_rm:RequirementCollection```

```LabelsFieldName``` - name of the custom field, that should be used for saving the ```dcterms:subject``` property for ```oslc_rm:Requirement``` and ```oslc_rm:RequirementCollection```

```SaveIdentifierInLabelsField``` - whether the adaptor should save the ```dcterms:identifier``` property for ```oslc_rm:Requirement``` and ```oslc_rm:RequirementCollection``` in the ```LabelsFieldName``` field or use the ```IdentifierFieldName``` field

```LabelsIdentifierFormat``` - format of the of the identifier string, which is used when storing the identifier in labels

```RootFolderId``` - identifier that should be used to represent the root folder of the requirement tree structure in the OSLC URI

```JiraAdaptorUrl``` - URL on which the Jira adaptor is running, used for calls from R4J adaptor to Jira adaptor