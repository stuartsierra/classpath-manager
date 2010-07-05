classpath-manager: Nailgun add-on to run Java classes with configurable classpath

by Stuart Sierra, http://stuartsierra.com/

Copyright (C) 2010 Stuart Sierra.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Step One: Compile classpath-manager
==================================================

You will need [Maven](http://maven.apache.org/) version 2.0 or later.

Run `mvn package` in this directory.

The JAR file will be created at `target/classpath-manager-$VERSION.jar` 
where $VERSION is the current version of classpath-manager.


Step Two: Install Nailgun
=========================================

Download [Nailgun](http://martiansoftware.com/nailgun/).

Compile the Nailgun command-line client.

Copy the `ng` executable to your PATH.


Getting Stated Part Three: Run Nailgun
======================================

Run a Nailgun server like this:

    java -jar /your/path/to/nailgun-0.7.1.jar

Add the classpath-manager JAR to the Nailgun classpath:

    ng ng-cp /your/path/to/classpath-manager-$VERSION.jar

where $VERSION is the current version of classpath-manager. The path
to the JAR *must* be an absolute path.

Add a short alias `cm` for the ClasspathManager main class:

    ng ng-alias cm com.stuartsierra.ClasspathManager


Step Four: Create a classpath File
==================================================

Create a text file named `classpath` in a project directory.

On each line of the file, put the name of a directory or JAR file to
be included in the classpath for that project.  For example:

    src
    test
    classes
    lib/library.jar

Paths will be interpreted as relative to the project directory unless
they begin with a "/".

Shell wildcards are not supported.


Step Five: Run classpath-manager
================================================

Run `ng cm your-main-class` in your project directory to run
your-main-class with the configured classpath.

Example:

     ng cm clojure.main


Caveats
=======

* Your main class runs in the same working directory as the Nailgun server process.

* Your main class runs as the same user as the Nailgun server process.

* The Java System properties `user.dir` and `java.class.path` are not useful.
