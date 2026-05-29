#!/bin/bash
set -e

if [ ! -d "$HOME/sqlcl" ]; then
    echo "Downloading SQLcl..."
    curl -L https://download.oracle.com/otn_software/java/sqldeveloper/sqlcl-latest.zip -o sqlcl-latest.zip
    unzip -q sqlcl-latest.zip -d $HOME
    rm sqlcl-latest.zip
fi

echo "SQLcl installed in $HOME/sqlcl"
# Add SQLcl to PATH for the rest of the script
export PATH=$HOME/sqlcl/bin:$PATH
sql -V
