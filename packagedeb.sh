#!/bin/bash
RELEASE=true OPTIMIZE=true CUSTOMCOMPOSE=true ./gradlew :desktop:packageDeb
#Set needed environment variables for building for Linux
