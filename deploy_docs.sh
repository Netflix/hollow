#!/bin/bash

# Simple script to deploy the doc site. Note that this will run the mkdocs generator
# and then directly push to github.
if ! type virtualenv &> /dev/null; then
  echo "virtualenv is required: easy_install virtualenv"
  exit 0
fi
if ! type pip &> /dev/null; then
  echo "pip is required: easy_install pip"
  exit 0
fi
if [ ! -f 'mkdocs.yml' ]; then
  echo "Please run this command from the repository root"
  exit 0
fi
virtualenv venv
source venv/bin/activate
pip install -r requirements.txt
mkdocs gh-deploy
rm -r site
