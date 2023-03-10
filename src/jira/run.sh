USRPATH="$PWD"                          
ROOTDIR="$(dirname "$(realpath "$0")")" 
JIRA_CONFIG_FILE_PATH="$ROOTDIR/config/JiraAdaptor.properties"

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

# Check if Maven is available
if ! type "mvn" &> /dev/null; then
    echo -e "\n${RED}ERORR{NC}: Maven is not not avaible.\n"
    exit "$?"
fi

echo "Starting Jira Adaptor"
echo "Jira host: $jira_host"
echo "Jira port: $jira_port"

cd "$ROOTDIR"
exec mvn -Dadapter_port="$jira_host" -Dadapter_host="$jira_port" jetty:run
cd "$USRPATH"
