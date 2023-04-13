#!/bin/bash

# Simple script to deploy the doc site. Note that this will run the mkdocs generator
# and then directly push to github.
make site-deploy
# Clean the site. 
make site-clean
