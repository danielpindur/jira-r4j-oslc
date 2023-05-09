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
R4J_CONFIG_FILE_PATH="$ROOTDIR/src/r4j/config/R4JAdaptor.properties"

# Import utils
source "$ROOTDIR/dev_tools/utils.sh"

unset KILL_JIRA_TAIL KILL_R4J_TAIL
killTailTerminals(){
    if [ -n "$KILL_JIRA_TAIL" ] || [ $1 -eq 1 ]; then
        kill "$(ps -ef | grep "tail -f .*/logs/../logs/../logs/jira_" | head -1 | awk '{ print $2 }')" &> /dev/null
    fi

    if [ -n "$KILL_R4J_TAIL" ] || [ $1 -eq 1 ]; then
        kill "$(ps -ef | grep "tail -f .*/logs/../logs/../logs/r4j_" | head -1 | awk '{ print $2 }')" &> /dev/null
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

    # Check if CURL is available
    if ! type "curl" &> /dev/null; then
        echo -e "\n${RED}ERORR${NC}: Curl is not not avaible.\n"
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

    # Core config file
    if [ ! -f "$CONFIG_FILE_PATH" ]; then
        echo -e "\n${RED}ERORR${NC}: Configuration file not found in $CONFIG_FILE_PATH\n"
        exit 1
    else
        echo -e "\n${GREEN}SUCCESS${NC}: Configuration file found in $CONFIG_FILE_PATH\n"
    fi

    # Jira config file
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

    # R4J config file
    if [ ! -f "$R4J_CONFIG_FILE_PATH" ]; then
        echo -e "\n${RED}ERORR${NC}: Configuration file for R4J Adaptor not found in $R4J_CONFIG_FILE_PATH\n"
        exit 1
    else
        echo -e "\n${GREEN}SUCCESS${NC}: Configuration file for R4J Adaptor found in $R4J_CONFIG_FILE_PATH\n"
    fi

    r4j_host=$(grep -oP '(?<=adaptor_host=).*' "$R4J_CONFIG_FILE_PATH")
    if [ -z "${jira_host}" ]; then
        echo -e "\n${RED}ERORR${NC}: R4J host not found in $R4J_CONFIG_FILE_PATH\n"
        exit 1
    else
        echo -e "\n${GREEN}SUCCESS${NC}: R4J host found in $R4J_CONFIG_FILE_PATH, host='$r4j_host'\n"
    fi

    r4j_port=$(grep -oP '(?<=adaptor_port=).*' "$R4J_CONFIG_FILE_PATH")
    if [ -z "${jira_port}" ]; then
        echo -e "\n${RED}ERORR${NC}: R4J port not found in $R4J_CONFIG_FILE_PATH\n"
        exit 1
    else
        echo -e "\n${GREEN}SUCCESS${NC}: R4J port found in $R4J_CONFIG_FILE_PATH, port='$r4j_port'\n"
    fi

    r4j_url="$r4j_host:$r4j_port"

    # Create log files and append headings
    mkdir "$ROOTDIR/logs" &> /dev/null
    CURTIME="$(date +%F_%T)"

    echo -e "########################################################\n   Started at: $CURTIME\n########################################################\n" > "$ROOTDIR/logs/jira_$CURTIME.log"
    echo -e "########################################################\n   Started at: $CURTIME\n########################################################\n" > "$ROOTDIR/logs/r4j_$CURTIME.log"

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
    echo -e "Jira Adaptor starting at $jira_url"
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

    ############################################################################################################
    #  R4J
    ############################################################################################################
    gnome-terminal --title="tail: R4J Log" -- /bin/bash -c "tail -f \"$ROOTDIR/logs/../logs/../logs/r4j_$CURTIME.log\"" 

    echo -n "Starting R4J Adaptor"
    "$ROOTDIR/src/r4j/run.sh" &> "$ROOTDIR/logs/r4j_$CURTIME.log" &
    PROCESS_PID=$!
    echo " (PID: $PROCESS_PID)"
    echo -e "R4J Adaptor starting at $r4j_url"
    echo -e "Waiting for the R4J Adaptor to finish startup..."
    waitForUrlOnline "$r4j_url" "$PROCESS_PID" "$SLEEP" 0
    ret="$?"
    if [ "$ret" -eq 0 ]; then
        echo -e "R4J Adaptor ${GREEN}running${NC} at $r4j_url" 
        KILL_R4J_TAIL=true
    else
        echo -e "R4J Adaptor ${RED}failed${NC} to start."
        echo -e "Check the log file for more information: $ROOTDIR/logs/r4j_$CURTIME.log"
        abortAll # exit
    fi
    
    echo
    echo -e "${GREEN}Ready to go!${NC}"
    echo "Use ctrl+c to exit..."
    wait 
    killall
}


main "$@"