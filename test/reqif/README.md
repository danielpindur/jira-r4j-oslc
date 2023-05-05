# Jira adaptor ReqIF client
This folder contains a simple python client for the Jira adaptor, which can be used to import and export requirements from Jira using the ReqIF format.

## Required packages
The client requires the following packages to be installed:

- ```reqif``` - ReqIF file parser
- ```rdflib``` - RDF file parser

## Usage
The client can be used from the command line using the ```reqif_utility.py``` script. The script supports the following arguments:

- ```-h``` - Prints the help message.

- ```-s``` - The source file path. **Required** for the ```upload``` mode.

- ```-m``` - The mode. Either ```upload``` or ```download```. **Required**.

- ```-T``` - The target Jira adaptor URL. **Required**.

- ```-a``` - The authentication type. Either ```basic``` or ```token```. **Required**.

- ```-t``` - The authentication token. Required for the ```token``` authentication type.

- ```-u``` - The user name. Required for the ```basic``` authentication type.

- ```-p``` - The password. Required for the ```basic``` authentication type.

- ```-P``` - The project ID. **Required**.

- ```-i``` - Ignores SPEC-OBJECT-TYPEs in ReqIF file and uploads everything as ```oslc_rm:Requirement```.

- ```-g``` - Generates unique identifiers for the requirements instead of using the ones from the ReqIF file.

- ```-c``` - The configuration file path. **Required**.

- ```-v``` - Prints verbose output.

## Example usage
### Download with Basic Auth
```
python3 reqif_utility.py -T localhost:8081 -m download -P 10000 -a basic -u test_user -p Tester99 -c config.json > test.reqif
```

### Download with OAuth
```
python3 reqif_utility.py -T localhost:8081 -m download -P 10000 -a oauth -t eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjJhMDQxMmFjNDZmMWFkMGMxZGUyZTUxNDExYTJiOGM4In0.yA9ceDzNDTyXUXC-ARDgfYbmq-5J88f_NiOoqXzJp_E -c config.json > test.reqif
```

### Upload with Basic Auth
```
python3 reqif_utility.py -T localhost:8081 -m upload -P 10200 -a basic -u test_user -p Tester99 -c config.json -s test.reqif
```

### Upload with OAuth
```
python3 reqif_utility.py -T localhost:8081 -m upload -P 10200 -a oauth -t eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjJhMDQxMmFjNDZmMWFkMGMxZGUyZTUxNDExYTJiOGM4In0.yA9ceDzNDTyXUXC-ARDgfYbmq-5J88f_NiOoqXzJp_E -c config.json -s test.reqif
```
