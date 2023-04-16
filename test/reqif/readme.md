# Install
pip install reqif
pip install rdflib

# Download with Basic Auth
python3 reqif_utility.py -T localhost:8081 -m download -P 10000 -a basic -u test_user -p Tester99 -c config.json > test.reqif

# Download with OAuth
python3 reqif_utility.py -T localhost:8081 -m download -P 10000 -a oauth -t eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjJhMDQxMmFjNDZmMWFkMGMxZGUyZTUxNDExYTJiOGM4In0.yA9ceDzNDTyXUXC-ARDgfYbmq-5J88f_NiOoqXzJp_E -c config.json > test.reqif

# Upload with Basic Auth
python3 reqif_utility.py -T localhost:8081 -m upload -P 10200 -a basic -u test_user -p Tester99 -c config.json -s test.reqif

# Upload with OAuth
python3 reqif_utility.py -T localhost:8081 -m upload -P 10200 -a oauth -t eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjJhMDQxMmFjNDZmMWFkMGMxZGUyZTUxNDExYTJiOGM4In0.yA9ceDzNDTyXUXC-ARDgfYbmq-5J88f_NiOoqXzJp_E -c config.json -s test.reqif
