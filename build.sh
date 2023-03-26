#!/bin/bash

HELP="
   Builds and installs the adapter and its dependencies.
"
USAGE="   Usage: $0 [-h]
      -h ... help
"

ROOTDIR="$PWD"

# Import utils
source "$ROOTDIR/dev_tools/utils.sh"

main () {
    # Process command line arguments
    for arg in "$@"
    do
        case "$arg" in
            -h) print_help "$HELP" "$USAGE"; shift ;;
            *) invalid_arg "$arg" "$USAGE" ;;
        esac
    done

    # Check if Maven is available
    if ! type "mvn" &> /dev/null; then
        echo -e "\n${RED}ERORR{NC}: Maven is not not avaible.\n"
        exit "$?"
    fi


    echo
    echo "############################################################"
    echo "    Build and Install shared resources"
    echo "############################################################"
    echo

    mvn -f "$ROOTDIR/src/shared/pom.xml" clean install || exit "$?"
    mvn -f "$ROOTDIR/src/oauth/pom.xml" clean install || exit "$?"


    echo
    echo "############################################################"
    echo "    Build and Install the Jira adapter"
    echo "############################################################"
    echo
    mvn -f "$ROOTDIR/src/jira/pom.xml" clean install || exit "$?"

    echo
    echo "############################################################"
    echo "    Build and Install the R4J adapter"
    echo "############################################################"
    echo
    mvn -f "$ROOTDIR/src/r4j/pom.xml" clean install || exit "$?"

    echo
    echo "############################################################"
    echo -e "    ${GREEN}BUILD DONE${NC}"
    echo "############################################################"
    echo


    exit 0
}

main "$@"