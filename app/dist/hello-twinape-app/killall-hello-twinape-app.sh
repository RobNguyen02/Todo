#!/bin/bash
kill -9 $(jps -mvl | grep 'hello-twinape-app-jar-with-dependencies.jar' | grep -v grep | awk '{print $1}') || true