# Requierement Management OSLC Adaptors for Jira and R4J

This work has been created as a part of my [Bachelor's thesis](https://www.vut.cz/studenti/zav-prace/detail/148230) at the Brno University of Technology.

## Summary
This repository contains OSLC adaptors for Jira and Requirements Management for Jira (R4J) based on the Core and Requirement Management OSLC specifications. The adaptors have been created using the OSLC4J framework and Eclipse Lyo.  

# Configuration
The adaptors are configurable using the ```configuration.json``` file in the ```config``` folder. Detailed information about the configuration options can be found in the ```config/README.md``` file.

## Usage
The adaptors can be started using the provided build and run scripts - ```run_all.sh``` and ```run_jira.sh```. The ```run_all.sh``` script starts both the Jira and R4J adaptor, while the ```run_jira.sh``` script starts only the Jira adaptor. Both scripts support the ```-b``` flag, which can be used to build the adaptors before running them.

## Testing
Both adaptors have been end-to-end tested using a Postman collection, which can be found in the ```test/postman``` folder.

## Example usage
The example API usage can be found in the ```example``` folder in the shape of a Postman collection. The collection contains examples of all the supported API calls and authentication methods.

## ReqIF python client
The ```test/reqif``` folder contains a simple python client for the Jira adaptor, which can be used to import and export requirements from Jira using the ReqIF format. The details about the usage of the client can be found in the ```test/reqif/README.md``` file.
