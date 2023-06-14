#!/bin/bash
##########################
# Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
# 
# Based on build scripts from Unite https://pajda.fit.vutbr.cz/verifit/unite
#
# This program and the accompanying materials are made available under
# the terms of the Eclipse Public License 2.0 which is available at
# https://www.eclipse.org/legal/epl-2.0
#
# SPDX-License-Identifier: EPL-2.0
##########################

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