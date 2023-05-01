##########################
# Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
#
# This program and the accompanying materials are made available under
# the terms of the Eclipse Public License 2.0 which is available at
# https://www.eclipse.org/legal/epl-2.0
#
# SPDX-License-Identifier: EPL-2.0
##########################

import argparse
import os
from utils import Log 
from reqif.parser import ReqIFParser
from reqif.unparser import ReqIFUnparser
from reqif.reqif_bundle import ReqIFBundle
from reqif.models.reqif_reqif_header import ReqIFReqIFHeader
from reqif.models.reqif_core_content import ReqIFCoreContent
from reqif.models.reqif_req_if_content import ReqIFReqIFContent
from reqif.models.reqif_data_type import ReqIFDataTypeDefinitionString
from reqif.models.reqif_spec_object_type import ReqIFSpecObjectType, SpecAttributeDefinition
from reqif.models.reqif_spec_relation_type import ReqIFSpecRelationType
from reqif.models.reqif_spec_object import ReqIFSpecObject, SpecObjectAttribute
from reqif.models.reqif_spec_relation import ReqIFSpecRelation
from reqif.models.reqif_types import SpecObjectAttributeType
from http.client import HTTPConnection
from base64 import b64encode
import json
import rdflib
import uuid
import datetime
import re

class ReqIFBuilder:
    def __init__(self, projectId, config):
        self.bundle = ReqIFBundle.create_empty('http://www.omg.org/spec/ReqIF/20110401/reqif.xsd', 'http://www.omg.org/spec/ReqIF/20110401/reqif.xsd')
        self.addHeader(projectId)
        self.config = config

        self.stringTypeId = 'Text'
        self.descriptionTypeId = 'TXT'

    def build(self):
        """Returns the output of the builder."""
        self.bundle.core_content = self.buildCoreContent()
        return ReqIFUnparser.unparse(self.bundle)
    
    def addHeader(self, projectId):
        """Adds a header to the bundle."""
        header = ReqIFReqIFHeader(uuid.uuid4(), 
                                  None, 
                                  str(datetime.datetime.now().isoformat()), 
                                  None,
                                  None,
                                  None,
                                  None,
                                  "ReqIF export for Project " + str(projectId) + " from OSLC adapter for Jira")
        self.bundle.req_if_header = header

    def getDataTypes(self):
        """Returns the data types of the bundle."""
        dataTypes = []

        stringType = ReqIFDataTypeDefinitionString.create(self.stringTypeId)
        dataTypes.append(stringType)

        return dataTypes
    
    def getAttributeDeifnitions(self, specTypeId):
        """Returns the attribute definitions of the bundle."""
        attributeDefinitions = []

        descriptionAttribute = SpecAttributeDefinition.create(SpecObjectAttributeType.STRING,
                                                              f'{specTypeId}-{self.descriptionTypeId}',
                                                              self.stringTypeId,
                                                              'Description')
        attributeDefinitions.append(descriptionAttribute)

        return attributeDefinitions

    def getSpecTypes(self):
        """Returns the specification types of the bundle."""
        reqIFSpecObjectTypes = []
        reqIFSpecRelationTypes = []
        reqIFSpecificationTypes = []

        requirementSpecObjectType = ReqIFSpecObjectType.create(self.config['requirementSpecType'], 
                                                               'Requirement', 
                                                               None, 
                                                               None,
                                                               self.getAttributeDeifnitions(self.config['requirementSpecType']))
        
        requirementCollectionSpecObjectType = ReqIFSpecObjectType.create(self.config['requirementCollectionSpecType'], 
                                                                         'Requirement Collection',
                                                                         None, 
                                                                         None,
                                                                         self.getAttributeDeifnitions(self.config['requirementCollectionSpecType']))
        
        reqIFSpecObjectTypes.append(requirementSpecObjectType)
        reqIFSpecObjectTypes.append(requirementCollectionSpecObjectType)
        
        decomposedByRelationType = ReqIFSpecRelationType(self.config['decomposedBySpecType'],
                                                         'Decomposed By', 
                                                         None,
                                                         'Decomposed By')
        decomposesRelationType = ReqIFSpecRelationType(self.config['decomposesSpecType'],
                                                       'Decomposes',
                                                        None,
                                                        'Decomposes')

        reqIFSpecRelationTypes.append(decomposedByRelationType)
        reqIFSpecRelationTypes.append(decomposesRelationType)

        return reqIFSpecObjectTypes + reqIFSpecRelationTypes + reqIFSpecificationTypes
        
    def requirementToSpecObject(self, requirement):
        """Converts a requirement to a specification object."""
        descriptionAttribute = SpecObjectAttribute(SpecObjectAttributeType.STRING,
                                                   f'{self.config["requirementSpecType"]}-{self.descriptionTypeId}',
                                                   requirement.description)

        specObject = ReqIFSpecObject.create(requirement.identifier, self.config['requirementSpecType'], [descriptionAttribute])
        specObject.long_name = requirement.title
        return specObject

    def requirementCollectionToSpecObject(self, requirementCollection):
        """Converts a requirement collection to a specification object."""
        descriptionAttribute = SpecObjectAttribute(SpecObjectAttributeType.STRING,
                                                   f'{self.config["requirementCollectionSpecType"]}-{self.descriptionTypeId}',
                                                   requirementCollection.description)

        specObject = ReqIFSpecObject.create(requirementCollection.identifier, self.config['requirementCollectionSpecType'], [descriptionAttribute])
        specObject.long_name = requirementCollection.title
        return specObject

    def getSpecObjects(self):
        """Returns the specification objects of the bundle."""
        specObjects = []

        for key, value in self.requirements.items():
            specObjects.append(self.requirementToSpecObject(value))

        for key, value in self.collections.items():
            specObjects.append(self.requirementCollectionToSpecObject(value))

        return specObjects
    
    def requirementToSpecRelation(self, requirement):
        """Converts a requirement to a specification relation."""
        specRelations = []

        for target in requirement.decomposedBy:
            relation = ReqIFSpecRelation(uuid.uuid4(),
                                         self.config['decomposedBySpecType'],
                                         requirement.identifier,
                                         target)
            specRelations.append(relation)

        for target in requirement.decomposes:
            relation = ReqIFSpecRelation(uuid.uuid4(),
                                         self.config['decomposesSpecType'],
                                         requirement.identifier,
                                         target)
            specRelations.append(relation)

        return specRelations

    def requirementCollectionToSpecRelation(self, requirementCollection):
        """Converts a requirement collection to a specification relation."""
        specRelations = []

        for target in requirementCollection.decomposedBy:
            relation = ReqIFSpecRelation(uuid.uuid4(),
                                         self.config['decomposedBySpecType'],
                                         requirementCollection.identifier,
                                         target)
            specRelations.append(relation)

        for target in requirementCollection.decomposes:
            relation = ReqIFSpecRelation(uuid.uuid4(),
                                         self.config['decomposesSpecType'],
                                         requirementCollection.identifier,
                                         target)
            specRelations.append(relation)

        return specRelations

    def getSpecRelations(self):
        """Returns the specification relations of the bundle."""
        specRelations = []

        for key, value in self.requirements.items():
            specRelations += self.requirementToSpecRelation(value)

        for key, value in self.collections.items():
            specRelations += self.requirementCollectionToSpecRelation(value)

        return specRelations

    def getContent(self):
        """Returns the content of the bundle."""
        return ReqIFReqIFContent(self.getDataTypes(), self.getSpecTypes(), self.getSpecObjects(), self.getSpecRelations())

    def buildCoreContent(self):
        """Returns the core content of the bundle."""
        return ReqIFCoreContent(self.getContent())

    def addRequirements(self, requirements):
        """Adds requirements to the bundle."""
        self.requirements = requirements

    def addRequirementCollections(self, collections):
        """Adds requirement collections to the bundle."""
        self.collections = collections

class OSLCCLient:
    def __init__(self, authType, token, user, password, serverUrl, verbose):
        self.token = token
        self.authType = authType
        self.serverUrl = serverUrl
        self.user = user
        self.password = password
        self.verbose = verbose

    def getAuthHeader(self):
        """Returns the authentication header."""
        if self.authType == 'basic':
            token = b64encode(f"{self.user}:{self.password}".encode('utf-8')).decode("ascii")
            return 'Basic ' + token
        elif self.authType == 'oauth':
            return 'Bearer ' + self.token


    def get(self, url):
        """Sends a GET request to the specified URL."""
        if self.verbose:
            Log.Info('GET: ' + url)

        conection = HTTPConnection(self.serverUrl)
        headers = {'Authorization': self.getAuthHeader()}
        conection.request('GET', url, headers=headers)
        response = conection.getresponse()

        if response.status != 200:
            Log.Error('Failed to get resource: ' + url + ', status code: ' + str(response.status), 1)

        return response.read()

    def post(self, url, data):
        """Sends a POST request to the specified URL."""
        if self.verbose:
            identifier = data.split('<dcterms:identifier>')[1].split('</dcterms:identifier>')[0]
            Log.Info('POST: ' + url + ', identifier: ' + identifier)

        conection = HTTPConnection(self.serverUrl)
        headers = {'Authorization': self.getAuthHeader(), 'Content-Type': 'application/xml'}
        conection.request('POST', url, data, headers=headers)
        response = conection.getresponse()

        if response.status != 200 and response.status != 201:
            Log.Error('Failed to post resource: ' + url + ', status code: ' + str(response.status), 1)
        
        return response.read()
    
    def put(self, url, data):
        """Sends a PUT request to the specified URL."""
        if self.verbose:
            identifier = data.split('<dcterms:identifier>')[1].split('</dcterms:identifier>')[0]
            decomposedByCaptured = re.findall(r'<oslc_rm:decomposedBy rdf:resource="(http[s]?://)?\S+/[\w/-]+/([\w-]+)"', data)
            decomposedBy = [x[1] for x in decomposedByCaptured]
            decomposesCaptured = re.findall(r'<oslc_rm:decomposes rdf:resource="(http[s]?://)?\S+/[\w/-]+/([\w-]+)"', data)
            decomposes = [x[1] for x in decomposesCaptured]

            Log.Info('PUT: ' + url + ', identifier: ' + identifier + ', decomposedBy: ' + str(decomposedBy) + ', decomposes: ' + str(decomposes))

        conection = HTTPConnection(self.serverUrl)
        headers = {'Authorization': self.getAuthHeader(), 'Content-Type': 'application/xml'}
        conection.request('PUT', url, data, headers=headers)
        response = conection.getresponse()

        if response.status != 200 and response.status != 201:
            Log.Error('Failed to update resource: ' + url + ', status code: ' + str(response.status), 1)
        
        return response.read()

class BaseObject:
    def getHeader(self):
        """Returns the header of the object."""
        header = '<?xml version="1.0" encoding="UTF-8"?>'
        header += '<rdf:RDF\n'
        header += 'xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"\n'
        header += 'xmlns:dcterms="http://purl.org/dc/terms/"\n'
        header += 'xmlns:oslc_data="http://open-services.net/ns/servicemanagement/1.0/"\n'
        header += 'xmlns:oslc="http://open-services.net/ns/core#"\n'
        header += 'xmlns:oslc_rm="http://open-services.net/ns/rm#"\n'
        header += 'xmlns:jira="http://fit.vutbr.cz/group/verifit/oslc/ns/jira#"\n'
        header += 'xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"\n'
        header += 'xmlns:foaf="http://xmlns.com/foaf/0.1/">\n'
        return header
    
    def getFooter(self):
        """Returns the footer of the object."""
        return '</rdf:RDF>\n'

class Requirement(BaseObject):
    """Requirement class"""

    def __init__(self, identifier, title, description, project, decomposedBy, decomposes):
        self.identifier = identifier
        self.title = title
        self.description = description
        self.project = project
        self.decomposedBy = decomposedBy
        self.decomposes = decomposes

    def generateRequest(self, withLinks):
        """Generates the request for the specified requirement."""
        request = "<oslc_rm:Requirement>\n"
        request += "<dcterms:title rdf:parseType=\"Literal\">" + self.title + "</dcterms:title>\n"
        request += "<dcterms:description rdf:parseType=\"Literal\">" + self.description + "</dcterms:description>\n"
        request += "<jira:project rdf:resource=\"http://localhost:8081/jira/services/project/Project/" + str(self.project) + "\"/>\n"
        request += "<dcterms:identifier>" + self.identifier + "</dcterms:identifier>\n"

        if withLinks:
            for decomposedBy in self.decomposedBy:
                request += "<oslc_rm:decomposedBy rdf:resource=\"" + decomposedBy + "\"/>\n"

            for decomposes in self.decomposes:
                request += "<oslc_rm:decomposes rdf:resource=\"" + decomposes + "\"/>\n"

        return self.getHeader() + request + "</oslc_rm:Requirement>\n" + self.getFooter()
    
    def create(self, client):
        """Creates the specified requirement."""
        request = self.generateRequest(False)
        url = '/jira/services/service1/resources/createRequirement'
        client.post(url, request)

    def updateWithLinks(self, client):
        """Updates the specified requirement with links."""
        request = self.generateRequest(True)
        url = f'/jira/services/requirement/Requirement/{self.identifier}'
        client.put(url, request)

    @staticmethod 
    def Parse(queryResult):
        """Parses the query result."""
        graph = rdflib.Graph()
        graph.parse(data=queryResult, format='xml')

        requirements = {}

        for subject, predicate, object in graph:
            if '/jira/services/requirement/Requirement/' in subject:
                identifier = subject.split('/')[-1]
                if identifier not in requirements:
                    requirements[identifier] = Requirement(identifier, '', '', '', [], [])
                
                containsHash = '#' in predicate
                parsedPredicate = ""
                if containsHash:
                    parsedPredicate = predicate.split('#')[-1]
                else:
                    parsedPredicate = predicate.split('/')[-1]

                if parsedPredicate == 'title':
                    requirements[identifier].title = str(object)
                elif parsedPredicate == 'description':
                    requirements[identifier].description = str(object)
                elif parsedPredicate == 'project':
                    requirements[identifier].project = object.split('/')[-1]
                elif parsedPredicate == 'decomposedBy':
                    targetIdentifier = object.split('/')[-1]
                    requirements[identifier].decomposedBy.append(targetIdentifier)
                elif parsedPredicate == 'decomposes':
                    targetIdentifier = object.split('/')[-1]
                    requirements[identifier].decomposes.append(targetIdentifier)

        return requirements
        

    @staticmethod
    def GetAll(client, project):
        """Gets all requirements."""
        url = f'/jira/services/service1/resources/queryRequirement?oslc.pageSize=20&oslc.paging=true&oslc.prefix=jira=<http://fit.vutbr.cz/group/verifit/oslc/ns/jira%23>&oslc.where=jira:project=<http://localhost:8081/jira/services/project/Project/{project}>&page='
        results = {}
        page = 0

        while True:
            queryResult = Requirement.Parse(client.get(url + str(page)))
            if len(queryResult) == 0:
                break
            
            results.update(queryResult)
            page += 1
        
        return results

class RequirementCollection(BaseObject):
    """Requirement class"""

    def __init__(self, identifier, title, description, project, decomposedBy, decomposes):
        self.identifier = identifier
        self.title = title
        self.description = description
        self.project = project
        self.decomposedBy = decomposedBy
        self.decomposes = decomposes

    def generateRequest(self, withLinks):
        """Generates the request for the specified requirement collection."""
        request = "<oslc_rm:RequirementCollection>\n"
        request += "<dcterms:title rdf:parseType=\"Literal\">" + self.title + "</dcterms:title>\n"
        request += "<dcterms:description rdf:parseType=\"Literal\">" + self.description + "</dcterms:description>\n"
        request += "<jira:project rdf:resource=\"http://localhost:8081/jira/services/project/Project/" + str(self.project) + "\"/>\n"
        request += "<dcterms:identifier>" + self.identifier + "</dcterms:identifier>\n"

        if withLinks:
            for decomposedBy in self.decomposedBy:
                request += "<oslc_rm:decomposedBy rdf:resource=\"" + decomposedBy + "\"/>\n"

            for decomposes in self.decomposes:
                request += "<oslc_rm:decomposes rdf:resource=\"" + decomposes + "\"/>\n"

        return self.getHeader() + request + "</oslc_rm:RequirementCollection>\n" + self.getFooter()
    
    def create(self, client):
        """Creates the specified requirement collection."""
        request = self.generateRequest(False)
        url = '/jira/services/service1/resources/createRequirementCollection'
        client.post(url, request)

    def updateWithLinks(self, client):
        """Updates the specified requirement collection with links."""
        request = self.generateRequest(True)
        url = f'/jira/services/requirementCollection/RequirementCollection/{self.identifier}'
        client.put(url, request)

    @staticmethod 
    def Parse(queryResult):
        """Parses the query result."""
        graph = rdflib.Graph()
        graph.parse(data=queryResult, format='xml')

        requirementCollections = {}

        for subject, predicate, object in graph:
            if '/jira/services/requirementCollection/RequirementCollection/' in subject:
                identifier = subject.split('/')[-1]
                if identifier not in requirementCollections:
                    requirementCollections[identifier] = RequirementCollection(identifier, '', '', '', [], [])
                
                containsHash = '#' in predicate
                parsedPredicate = ""
                if containsHash:
                    parsedPredicate = predicate.split('#')[-1]
                else:
                    parsedPredicate = predicate.split('/')[-1]

                if parsedPredicate == 'title':
                    requirementCollections[identifier].title = str(object)
                elif parsedPredicate == 'description':
                    requirementCollections[identifier].description = str(object)
                elif parsedPredicate == 'project':
                    requirementCollections[identifier].project = object.split('/')[-1]
                elif parsedPredicate == 'decomposedBy':
                    targetIdentifier = object.split('/')[-1]
                    requirementCollections[identifier].decomposedBy.append(targetIdentifier)
                elif parsedPredicate == 'decomposes':
                    targetIdentifier = object.split('/')[-1]
                    requirementCollections[identifier].decomposes.append(targetIdentifier)

        return requirementCollections

    @staticmethod
    def GetAll(client, project):
        """Gets all requirement collections."""
        url = f'/jira/services/service1/resources/queryRequirementCollection?oslc.pageSize=20&oslc.paging=true&oslc.prefix=jira=<http://fit.vutbr.cz/group/verifit/oslc/ns/jira%23>&oslc.where=jira:project=<http://localhost:8081/jira/services/project/Project/{project}>&page='
        results = {}
        page = 0

        while True:
            queryResult = RequirementCollection.Parse(client.get(url + str(page)))
            if len(queryResult) == 0:
                break
            
            results.update(queryResult)
            page += 1
        
        return results

class Server:
    """Server class"""

    def __init__(self, url, authType, token, user, password, project, ignore, generate, verbose, config):
        self.url = url
        self.authType = authType
        self.token = token
        self.user = user
        self.password = password
        self.project = project
        self.ignore = ignore
        self.generate = generate
        self.config = config
        self.verbose = verbose

    def getClient(self):
        """Returns the client for the server."""
        return OSLCCLient(self.authType, self.token, self.user, self.password, self.url, self.verbose)

    def uploadInternal(self, identifier, title, desciption, issueType, decomposedBy, decomposes):
        """Uploads the specified requirement to the server."""
        if issueType == 'Requirement':
            requirement = Requirement(identifier, title, desciption, self.project, decomposedBy, decomposes)
            requirement.create(self.getClient())
            return requirement
        elif issueType == 'RequirementCollection':
            requirementCollection = RequirementCollection(identifier, title, desciption, self.project, decomposedBy, decomposes)
            requirementCollection.create(self.getClient())
            return requirementCollection
        else:
            Log.Error('Unknown issue type: ' + issueType, 1)

    def resolveLink(self, links):
        """Resolves the specified links."""
        result = []
        for link in links:
            target = list(filter(lambda x: x.identifier == link, self.requirements))[0]
            if target is None:
                Log.Error('Target not found: ' + link, 1)
            else:
                if target.spec_object_type == self.config['requirementSpecType']:
                    result.append(self.url + '/jira/services/requirement/Requirement/' + target.identifier)
                elif target.spec_object_type == self.config['requirementCollectionSpecType']:
                    result.append(self.url + '/jira/services/requirementCollection/RequirementCollection/' + target.identifier)
                else:
                    Log.Error('Unknown issue type: ' + target.spec_object_type, 1)
        return result

    def upload(self, requirement, links):
        """Uploads the specified requirement to the server."""
        identifier = requirement.identifier
        title = requirement.long_name
        descriptionAtributeType = self.config['descriptionAtributeType']
        description = requirement.attribute_map.get(descriptionAtributeType)
        decomposedBy = list(map(lambda x: x.target, list(filter(lambda x: x.relation_type_ref == self.config['decomposedBySpecType'], links))))
        decomposes = list(map(lambda x: x.target, list(filter(lambda x: x.relation_type_ref == self.config['decomposesSpecType'], links))))

        decomposedBy = self.resolveLink(decomposedBy)
        decomposes = self.resolveLink(decomposes)
        
        if description:
            description = description.value
        else:
            description = ""
            Log.Warning('Description not found for requirement: ' + identifier)

        if self.ignore:
            return self.upload(identifier, title, description, "Requirement", decomposedBy, decomposes)
        else:
            issueType = requirement.spec_object_type

            if issueType == self.config['requirementSpecType']:
                return self.uploadInternal(identifier, title, description, "Requirement", decomposedBy, decomposes)

            elif issueType == self.config['requirementCollectionSpecType']:
                return self.uploadInternal(identifier, title, description, "RequirementCollection", decomposedBy, decomposes)

            else:
                Log.Warning('Unknown issue type: ' + issueType + ", skipping requirement: " + identifier)

    def update(self, requirement):
        """Updates created requirement with links."""
        for link in requirement.decomposedBy:
            requirement.updateWithLinks(self.getClient())
      
    def download(self, builder):
        """Downloads the requirements from the server."""
        client = self.getClient()
        requirements = Requirement.GetAll(client, self.project)
        requirementCollections = RequirementCollection.GetAll(client, self.project)
        
        builder.addRequirements(requirements)
        builder.addRequirementCollections(requirementCollections)

def parseReqIF(sourceFilePath):
    """Parses the specified ReqIF file and returns the parsed data."""
    return ReqIFParser.parse(sourceFilePath)

def generateUniqueIds(requirements, relations):
    """Generates unique identifiers for the specified requirements."""
    updatedRequirements = {}
    updatedRelations = []

    for requirement in requirements:
        oldIdentifier = requirement.identifier
        requirement.identifier = str(uuid.uuid4())
        updatedRequirements[oldIdentifier] = requirement

    for relation in relations:
        updatedSource = updatedRequirements.get(relation.source)
        updatedTarget = updatedRequirements.get(relation.target)

        if updatedSource is None:
            Log.Error('Source not found: ' + relation.source, 1)
        if updatedTarget is None:
            Log.Error('Target not found: ' + relation.target, 1)

        relation.source = updatedSource.identifier
        relation.target = updatedTarget.identifier
        updatedRelations.append(relation)

    return list(updatedRequirements.values()), updatedRelations

def upload(sourceFilePath, server):
    """Uploads the specified file to the server."""
    parsed = parseReqIF(sourceFilePath) 
    server.requirements = parsed.core_content.req_if_content.spec_objects
    relations = parsed.core_content.req_if_content.spec_relations
    created = []

    if server.generate:
        updatedRequirements, relations = generateUniqueIds(server.requirements, relations)
        server.requirements = updatedRequirements

    for requirement in server.requirements:
        identifier = requirement.identifier
        links = list(filter(lambda x: x.source == identifier, relations))
        created.append(server.upload(requirement, links))

    for requirement in created:
        server.update(requirement)


def download(server):
    """Downloads the requirements from the server."""
    builder = ReqIFBuilder(server.project, server.config)
    server.download(builder)
    
    print(builder.build())

def printHelp():
    """Prints the help message for the ReqIF utility."""
    print('Usage: reqif.py [options]')
    print('Options:')
    print('  -s, --source <path>       The source file path.')
    print('  -m, --mode <mode>         The mode. Either "upload" or "download".')
    print('  -h, --help                Prints this help message.')
    print('  -T, --target <url>        The target server URL.')
    print('  -a, --authType <type>     The authentication type. Either "basic" or "token".')
    print('  -t, --token <token>       The authentication token.')
    print('  -u, --user <user>         The user name.')
    print('  -p, --password <password> The password.')
    print('  -P, --project <project>   The project ID.')
    print('  -i, --ignore              Ignores SPEC-OBJECT-TYPEs in ReqIF file and uploads everything as Requirement.')
    print('  -g, --generate            Generates unique identifiers for the requirements.')
    print('  -c, --config <path>       The configuration file path.')
    print('  -v, --verbose             Prints verbose output.')

def main():
    argParser = argparse.ArgumentParser(add_help=False)
    argParser.add_argument('-s', '--source', nargs=1, type=str)
    argParser.add_argument('-m', '--mode', nargs=1, type=str)
    argParser.add_argument('-h', '--help', action='store_true')
    argParser.add_argument('-T', '--target', nargs=1, type=str)
    argParser.add_argument('-a', '--authType', nargs=1, type=str)
    argParser.add_argument('-t', '--token', nargs=1, type=str)
    argParser.add_argument('-u', '--user', nargs=1, type=str)
    argParser.add_argument('-p', '--password', nargs=1, type=str)
    argParser.add_argument('-P', '--project', nargs=1, type=int)
    argParser.add_argument('-i', '--ignore', action='store_true')
    argParser.add_argument('-c', '--config', nargs=1, type=str)
    argParser.add_argument('-g', '--generate', action='store_true') 
    argParser.add_argument('-v', '--verbose', action='store_true')

    args = vars(argParser.parse_args())

    if args['help']:
        print()
        quit(0)

    if args['mode'] is None:
        Log.Error('Mode not specified', 1)

    if args['mode'][0] == 'upload':
        if args['source'] is None:
            Log.Error('Source file not specified', 1)
        elif not os.path.isfile(args['source'][0]):
            Log.Error('Source file not found', 1)

    if args['config'] is None:
        Log.Error('Config file not specified', 1)

    if not os.path.isfile(args['config'][0]):
        Log.Error('Config file not found', 1)
            
    if args['target'] is None:
        Log.Error('Target server not specified', 1)

    if args['authType'] is None:
        Log.Error('Authentication type not specified', 1)

    if args['authType'][0] == 'oauth' and args['token'] is None:
        Log.Error('OAuth specified but no token was provided', 1)

    if args['authType'][0] == 'basic' and (args['user'] is None or args['password'] is None):
        Log.Error('Basic authentication specified but no username or password was provided', 1)

    if args['project'] is None:
        Log.Error('Project id not specified', 1)

    config = json.load(open(args['config'][0]))

    server = Server(args['target'][0],
                    args['authType'][0], 
                    args['token'][0] if args['token'] else None, 
                    args['user'][0] if args['user'] else None, 
                    args['password'][0] if args['password'] else None, 
                    args['project'][0], 
                    args['ignore'],
                    args['generate'], 
                    args['verbose'],
                    config)

    if args['mode'][0] == 'upload':
        upload(args['source'][0], server)
    elif args['mode'][0] == 'download':
        download(server)
    else:
        Log.Error('Invalid mode specified', 1)

if __name__ == "__main__":
    main()
