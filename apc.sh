#!/usr/bin/env bash

# Join arguments and pass to Gradle
function join { local IFS="$1"; shift; echo "$*"; }
args=$(join , $@)
gradle run -PappArgs=${args} --quiet