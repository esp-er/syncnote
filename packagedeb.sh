#!/bin/bash
#Set needed environment variables for Linux-optimized package build
RELEASE=true OPTIMIZE=true CUSTOMCOMPOSE=true ./gradlew :desktop:packageDeb
