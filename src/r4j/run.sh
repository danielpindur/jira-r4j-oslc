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

USRPATH="$PWD"                          
ROOTDIR="$(dirname "$(realpath "$0")")" 
R4J_CONFIG_FILE_PATH="$ROOTDIR/config/R4JAdaptor.properties"

if [ ! -f "$R4J_CONFIG_FILE_PATH" ]; then
    echo -e "\n${RED}ERORR${NC}: Configuration file for R4J Adaptor not found in $R4J_CONFIG_FILE_PATH\n"
    exit 1
else
    echo -e "\n${GREEN}SUCCESS${NC}: Configuration file for R4J Adaptor found in $R4J_CONFIG_FILE_PATH\n"
fi

r4j_host=$(grep -oP '(?<=adaptor_host=).*' "$R4J_CONFIG_FILE_PATH")
if [ -z "${r4j_host}" ]; then
    echo -e "\n${RED}ERORR${NC}: R4J host not found in $R4J_CONFIG_FILE_PATH\n"
    exit 1
else
    echo -e "\n${GREEN}SUCCESS${NC}: R4J host found in $R4J_CONFIG_FILE_PATH, host='$r4j_host'\n"
fi

r4j_port=$(grep -oP '(?<=adaptor_port=).*' "$R4J_CONFIG_FILE_PATH")
if [ -z "${r4j_port}" ]; then
    echo -e "\n${RED}ERORR${NC}: R4J port not found in $R4J_CONFIG_FILE_PATH\n"
    exit 1
else
    echo -e "\n${GREEN}SUCCESS${NC}: R4J port found in $R4J_CONFIG_FILE_PATH, port='$r4j_port'\n"
fi

# Check if Maven is available
if ! type "mvn" &> /dev/null; then
    echo -e "\n${RED}ERORR{NC}: Maven is not not avaible.\n"
    exit "$?"
fi

echo "Starting R4J Adaptor"
echo "R4J host: $r4j_host"
echo "R4J port: $r4j_port"

cd "$ROOTDIR"
exec mvn -Dadapter_port="$r4j_host" -Dadapter_host="$r4j_port" jetty:run
cd "$USRPATH"
