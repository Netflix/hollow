![Hollow Logo](logo.png)

# Hollow

[![Build Status](https://travis-ci.com/Netflix/hollow.svg?branch=master)](https://travis-ci.com/Netflix/hollow)
[![Join the chat at https://gitter.im/Netflix/hollow](https://badges.gitter.im/Netflix/hollow.svg)](https://gitter.im/Netflix/hollow?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![NetflixOSS Lifecycle](https://img.shields.io/osslifecycle/Netflix/hollow.svg)](#)
[ ![Download](https://api.bintray.com/packages/netflixoss/maven/hollow/images/download.svg) ](https://bintray.com/netflixoss/maven/hollow/_latestVersion)

Hollow is a java library and toolset for disseminating in-memory datasets from a single producer to many consumers for high performance read-only access. [Read more](http://techblog.netflix.com/2016/12/netflixoss-announcing-hollow.html).

Documentation is available at [http://hollow.how](http://hollow.how).  

## Getting Started

We recommend jumping into the [quick start guide](http://hollow.how/quick-start) — you'll have a demo up and running in minutes, and a fully production-scalable implementation of Hollow at your fingertips in about an hour.  From there, you can plug in your data model and it's off to the races.

## Get Hollow

Release binaries are available from Maven Central and jCenter.

|GroupID/Org|ArtifactID/Name|Latest Stable Version|
|-----------|---------------|---------------------|
|com.netflix.hollow|hollow|5.1.3|

In a Maven `.pom` file:
```xml
        ...
        <dependency>
                <groupId>com.netflix.hollow</groupId>
                <artifactId>hollow</artifactId>
                <version>5.1.3</version>
        </dependency>
        ...
```

In a Gradle `build.gradle` file:
```gradle
        ...
        compile 'com.netflix.hollow:hollow:5.1.3'
        ...
```
        
Release candidate binaries, matching the `-rc\.*` pattern for an artifact's version, are available from the jCenter [oss-candidate](https://dl.bintray.com/netflixoss/oss-candidate/) repository, which may be declared in a `build.gradle` file:

```gradle
        ...
        repositories {
            maven {
                url 'https://dl.bintray.com/netflixoss/oss-candidate/'
            }
        }
        ...
```

## Get Support

Hollow is maintained by the Platform Data Technologies team at Netflix.  Support can be obtained directly from us or from fellow users through [Gitter](https://gitter.im/Netflix/hollow) or by opening an issue in this project.

## Generating the Docs

To view the docs locally you can just `make site-serve`, this will start the MkDocs server at `http://127.0.0.1:8000/`.
You can also run `make site-build` to build the site locally and `make site-deploy` to deploy it to Github.

MkDocs runs with python, the Makefile via the `venv` task should take care of setting the Python's _virtualenv_ for the site tasks.
It does assume that `virtualenv` is available as a command and it also assume that we are targeting _python3_.
Installing Python3 is out of the scope, check your OS package manager. For example, in Mac you can use [homebrew] to install `python3` or `anaconda3`.


[homebrew]: https://brew.sh/


## LICENSE

Copyright (c) 2016 Netflix, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

