#!/bin/bash
##########################
# Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
#
# This program and the accompanying materials are made available under
# the terms of the Eclipse Public License 2.0 which is available at
# https://www.eclipse.org/legal/epl-2.0
#
# SPDX-License-Identifier: EPL-2.0
##########################

HELP="
   Checks configuration files and starts the Jira adapter.
"
USAGE="   Usage: $0 [-t|-h|-b]
      -b ... build - Runs build script before launching
      -h ... help
"

SLEEP=3
ROOTDIR="$PWD"
CONFIG_FILE_PATH="$ROOTDIR/config/configuration.json"
JIRA_CONFIG_FILE_PATH="$ROOTDIR/src/jira/config/JiraAdaptor.properties"

# Import utils
source "$ROOTDIR/dev_tools/utils.sh"

unset KILL_JIRA_TAIL 
killTailTerminals(){
    if [ -n "$KILL_JIRA_TAIL" ] || [ $1 -eq 1 ]; then
        kill "$(ps -ef | grep "tail -f .*/logs/../logs/../logs/jira_" | head -1 | awk '{ print $2 }')" &> /dev/null
    fi
}

trap 'killall' INT
killall() {
    trap '' INT TERM     # ignore INT and TERM while shutting down
    echo -e "\nShutting down..."
    kill -TERM 0
    killTailTerminals 1
    wait
    echo "All done."

    exit 0
}

abortAll() {
    trap '' INT TERM     # ignore INT and TERM while shutting down
    echo -e "\nShutting down..."
    kill -TERM 0
    killTailTerminals 0
    wait
    echo "All done."

    exit 0
}

main () {
    # Process command line arguments
    unset ARG_BUILD
    for arg in "$@"
    do
        case "$arg" in
            -b) ARG_BUILD=true ; shift ;;
            -h) print_help "$HELP" "$USAGE"; shift ;;
            *) invalid_arg "$arg" "$USAGE" ;;
        esac
    done

    # Check if Maven is available
    if ! type "mvn" &> /dev/null; then
        echo -e "\n${RED}ERORR${NC}: Maven is not not avaible.\n"
        exit "$?"
    fi

    # Build if requested
    if [ -n "$ARG_BUILD" ]; then
        echo -e "\nRunning build.sh...\n"
        "$ROOTDIR/build.sh"
        if [ "$?" -ne 0 ]; then
            echo -e "\n${RED}Build failed${NC} - Aborting start.\n"
            exit "$?"
        fi
    fi

    echo
    echo "############################################################"
    echo "    Check for configuration files"
    echo "############################################################"
    echo
    if [ ! -f "$CONFIG_FILE_PATH" ]; then
        echo -e "\n${RED}ERORR${NC}: Configuration file not found in $CONFIG_FILE_PATH\n"
        exit 1
    else
        echo -e "\n${GREEN}SUCCESS${NC}: Configuration file found in $CONFIG_FILE_PATH\n"
    fi

    if [ ! -f "$JIRA_CONFIG_FILE_PATH" ]; then
        echo -e "\n${RED}ERORR${NC}: Configuration file for Jira Adaptor not found in $JIRA_CONFIG_FILE_PATH\n"
        exit 1
    else
        echo -e "\n${GREEN}SUCCESS${NC}: Configuration file for Jira Adaptor found in $JIRA_CONFIG_FILE_PATH\n"
    fi

    jira_host=$(grep -oP '(?<=adaptor_host=).*' "$JIRA_CONFIG_FILE_PATH")
    if [ -z "${jira_host}" ]; then
        echo -e "\n${RED}ERORR${NC}: Jira host not found in $JIRA_CONFIG_FILE_PATH\n"
        exit 1
    else
        echo -e "\n${GREEN}SUCCESS${NC}: Jira host found in $JIRA_CONFIG_FILE_PATH, host='$jira_host'\n"
    fi

    jira_port=$(grep -oP '(?<=adaptor_port=).*' "$JIRA_CONFIG_FILE_PATH")
    if [ -z "${jira_port}" ]; then
        echo -e "\n${RED}ERORR${NC}: Jira port not found in $JIRA_CONFIG_FILE_PATH\n"
        exit 1
    else
        echo -e "\n${GREEN}SUCCESS${NC}: Jira port found in $JIRA_CONFIG_FILE_PATH, port='$jira_port'\n"
    fi

    jira_url="$jira_host:$jira_port"

    # Create log files and append headings
    mkdir "$ROOTDIR/logs" &> /dev/null
    CURTIME="$(date +%F_%T)"

    echo -e "########################################################\n   Started at: $CURTIME\n########################################################\n" > "$ROOTDIR/logs/jira_$CURTIME.log"

    echo
    echo "############################################################"
    echo "    Starting adaptors"
    echo "############################################################"
    echo

    ############################################################################################################
    #  Jira
    ############################################################################################################
    gnome-terminal --title="tail: Jira Log" -- /bin/bash -c "tail -f \"$ROOTDIR/logs/../logs/../logs/jira_$CURTIME.log\"" 

    echo -n "Starting Jira Adaptor"
    "$ROOTDIR/src/jira/run.sh" &> "$ROOTDIR/logs/jira_$CURTIME.log" &
    PROCESS_PID=$!
    echo " (PID: $PROCESS_PID)"
    echo -e "Waiting for the Jira Adaptor to finish startup..."
    waitForUrlOnline "$jira_url" "$PROCESS_PID" "$SLEEP" 0
    ret="$?"
    if [ "$ret" -eq 0 ]; then
        echo -e "Jira Adaptor ${GREEN}running${NC} at $jira_url" 
        KILL_JIRA_TAIL=true
    else
        echo -e "Jira Adaptor ${RED}failed${NC} to start."
        echo -e "Check the log file for more information: $ROOTDIR/logs/jira_$CURTIME.log"
        abortAll # exit
    fi
    
    echo
    echo -e "${GREEN}Ready to go!${NC}"
    echo "Use ctrl+c to exit..."
    wait 
    killall
}


main "$@"