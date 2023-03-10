# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

# Prints help and usage to stdout and exits with 0.
# $1 ... help
# $2 ... usage
print_help()
{
    echo "$1"
    echo "$2"
    exit 0
}

# Waits for an URL to go online (curl) while checking if a process is still running (the server that is supposed to startup).
# If the server stops running, then it had encounter an error. 
# $1 ... URL for curl to poll
# $2 ... pid of the associated process
# $3 ... sleep in seconds for each poll cycle
# $4 ... timeout in number of cycles (0 means no timeout)
# return ... 0 ok URL running, 1 process stopped or timeout
waitForUrlOnline()
{
    # loop until URL online
    CURL_RET=42
    COUNTER=0
    while [ "$CURL_RET" != 0 ]
    do
        sleep "$3"
        echo -n "."

        # try polling the URL (once)
        curl "$1" &> /dev/null
        CURL_RET="$?"

        # check that the process is still running
        ps -p $2 > /dev/null
        if [ "$?" == 1 ]; then
            # not running --> error
            echo -e "\n${RED}ERROR${NC}: Process $2 stopped running."
            return 1
        fi

        # check for timeout in number of poll cycles
        if [ "$4" -ne "0" ] && [ "$COUNTER" -ge "$4" ]; then # only check timout if there is one
            echo -e "\n${RED}ERROR${NC}: Timeout while waiting for $1 to go online."
            return 1
        fi
        COUNTER="$((COUNTER+1))"
    done

    echo -e "\n${GREEN}SUCCESS${NC}: $1 is online."
    return 0
}