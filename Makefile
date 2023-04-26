include .makefile.inc

.PHONY: venv build test clean all docs help
.DEFAULT_GOAL := help

SHELL = /bin/sh
PROJECT_NAME := $(SHELl basename $(CURDIR))

# Define access to common tools 
GRADLE = ./gradlew

# Python
VENV_NAME ?= .venv
VENV_ACTIVATE = . $(VENV_NAME)/bin/activate
PYTHON = ${VENV_NAME}/bin/python3
PIP = ${VENV_NAME}/bin/pip
MKDOCS = ${VENV_NAME}/bin/mkdocs

venv: $(VENV_NAME)/bin/activate 

$(VENV_NAME)/bin/activate: requirements.txt
	test -d $(VENV_NAME) || virtualenv -p python3 $(VENV_NAME)
	${PYTHON} -m pip install -Ur requirements.txt
	touch $(VENV_NAME)/bin/activate

build: ##  builds the project.
	$(GRADLE) build

test: ## runs all tests.
	$(GRADLE) testAll

clean:  ## Cleans the project.
	$(GRADLE) clean

site-build: venv ## Builds the doc site.
	$(MKDOCS) build

site-serve: venv ## Serves the doc site locally
	$(MKDOCS) serve

site-deploy: site-build ## Deploys the site to Github pages.
	$(MKDOCS) gh-deploy

site-clean: venv ## Removes the site directory
	rm -rf site


.PHONY: help
help: ##  List all available commands.
	${SHOW_IDENTITY}
	@echo 'Usage:'
	@echo '${BLUE}make${RESET} ${GREEN}<target>${RESET}'
	@echo ''
	@echo 'Targets:'
	@perl -ne "$${HELP_SCRIPT}" $(MAKEFILE_LIST)
