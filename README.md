# GigaSpaces SQL Console

Free lightweight java web application. Powerful alternative for GigaSpace Management Console when you have a lot of work with spaces data, especially for Quality Assurance Team.

* [Features](#key-features)
* [How to install](#how-to-install)
* [How to use](#how-to-use)
 * [Execute SQL Queries](#execute-sql-queries)
 * [Registered Types and Counts](#registered-types-and-counts)
 * [Copy data between spaces](#copy-data-between-spaces)
 * [Execute Groovy](#work-with-groovy)

## Key Features 

_All of them you don't find in GigaSpace Management Console_

* Supports all versions of GigaSpace starts from 9.X (never tested with previous) from one page
* One click switch between different instances, versions and spaces
* Supports custom render for user types in spaces, so no more ```my.package.Object@12223``` in results
* Super view for types counts with filtering by name, non zero and date and count of last update
* Supports multiqueries and comments when you have complex work with data
* All settings store cross session, so you don't need to retype your queries again
* Copy data between spaces
* Export query result to CSV
* Support Groovy language in console

## How install

1. Download (WAR file) latest release [from](https://github.com/terma/gigaspaces-sql-console/releases)
1. Prepare config file (JSON format) 
```json
{
  "comment": "list of all versions of GigaSpace which you want to use",
  "gs": [
    {
      "comment": "any name just for user",
      "name": "GS-10",
      "comment": "list of GigaSpace libraries (you can use that as minimun)",
      "libs": [ 
        "/my-path/gs-runtime-10.0.1-11800-RELEASE.jar",
        "/my-path/gs-openspaces-10.0.1-11800-RELEASE.jar",
        "/my-path/spring-beans-3.2.4.RELEASE.jar",
        "/my-path/commons-logging-1.1.1.jar",
      ]
    }
  ],
  "links": [
    {
      "name": "Name just for user",
      "url": "any link"
    }
  ],
  "comment": "List of converters which will be used to render your custom embedded types in space, could be empty so we will reference name",
  "converters": [
    "my.package.CustomTypeConverter"
  ],
  "comment": "predefined list of GigaSpace spaces which you have, so you don't need to enter all details manualy on page. Any way you can customize them from UI too",
  "gigaspaces": [
    {
      "comment": "Name for user",
      "name": "GS-10",
      "url": "jini:/*/*/gs10?locators=localhost:4700",
      "user": "",
      "password": "",
      "comment": "name from section gs which just show what version of GigaSpace use for that instance, if empty will use first from list or if empty list will take default from app classpath",
      "gs": ""
    }
  ]
}
```
1. Pass JVM option ```-DgigaspaceSqlConsoleConfig=<classpath:X> or <file:X>``` to your Web Container
1. Start within any Web Container like Jetty, Tomcat etc.
1. Open web app in browser

## How to use

When you start your console first time after configuration you will see:

![Start](https://raw.githubusercontent.com/terma/gigaspace-sql-console/master/img/start.png)

### Execute SQL queries

![Execute SQL Queries](https://raw.githubusercontent.com/terma/gigaspace-sql-console/master/img/execute.png)

To execute SQL queries in selected space you just need to select -Query- and SQL query

* You can enter more than one query when you run them they will be execute in independenly so you will get all results as on example below
* To disable execution of some query without remove just comment it by ```#``` or ```//``` or ```--```
* Supports ```select```, ```update```, ```delete```
* For ```delete``` and ```update``` result will be count of modified records
* For ```select``` you will get result table and count of records
* When value size for one cell more ```50``` result will be truncated, so ```result``` plus ```...``` to show full result just click on ```Show/Hide all text``` under each result table
* For columns which looks like timestamp you can click on ```T?``` after column name so console shows result in date format for example ```1424054208000``` will be show as ```1424054208000 = Mon, 16 Feb 2015 02:36:48 GMT```

### Registered types and counts

![Registered Types and Counts](https://raw.githubusercontent.com/terma/gigaspace-sql-console/master/img/types.png)

### Copy data between spaces

![Copy data between spaces](https://raw.githubusercontent.com/terma/gigaspace-sql-console/master/img/copy.png)

### Work with Groovy

Sometimes you have complex queries which depends on each other or you need to have spacial calculation on data in space. For example I want to collect all values from select to one string. how I can do that?

First of all enable groovy by adding ```groovy``` word in first line of editor.
Second, create simple [Groovy](http://groovy.codehaus.org/) script and run it in console!

![Execute Groovy Script](https://raw.githubusercontent.com/terma/gigaspace-sql-console/master/img/groovy.png)

A few additional words about Groovy in console. Withing your script you can use all groovy features plus a few additional: 

* sql(string) - function which can execute any SQL in GigaSpace and return result
* out(string) - print any result to console output
* gs - ref on GigaSpace instance
