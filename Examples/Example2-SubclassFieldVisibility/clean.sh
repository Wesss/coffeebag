#!/bin/bash

function recurse {
    local d="$1"
    if [ -d "$d" ]; then
        cd $d &> /dev/null
        scan *
        cd .. &> /dev/null
    fi
}

function scan {
    workUnit
    for x in $*; do
        recurse "$x"
    done
}

function workUnit {
    rm *.jar &> /dev/null
    rm *.class &> /dev/null
}

rm sources &> /dev/null
scan *