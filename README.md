# GigaSpaces SQL Console

[![Build Status](https://travis-ci.org/terma/gigaspace-web-console.svg)](https://travis-ci.org/terma/gigaspace-web-console) [![Coverage Status](https://coveralls.io/repos/terma/gigaspace-web-console/badge.svg?branch=master&service=github)](https://coveralls.io/github/terma/gigaspace-web-console?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.terma.gigaspace-web-console/server/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.terma.gigaspace-web-console/server/)

Powerful alternative for GigaSpace Management Console when you have a lot of work with spaces data, especially for Quality Assurance Team. Open Source Apache 2.0

* [42](#42)
 * [License](#license)
 * [Contacts](#contacts)
 * [Features](#key-features)
* [How to run](#how-to-run)
* [How to use](#how-to-use)
 * [Execute SQL Queries](#execute-sql-queries)
 * [Registered Types and Counts](#registered-types-and-counts)
   * [Counts History](#counts-history) 
   * [Counts By Partitions](#counts-by-partitions) 
 * [Execute Groovy](#work-with-groovy)
 * [Copy data between spaces](#copy-data-between-spaces)
 * [Export / Import data](#export--import-data)

# 42

## License

[Apache 2.0](https://github.com/terma/gigaspace-web-console/blob/master/LICENSE)

## Contacts 

_Thx a lot for any feedback_

* Convert you point to feature or defect [here](https://github.com/terma/gigaspace-web-console/issues)
* artem.stasuk@gmail.com
* Skype artem.stasuk

## Key Features 

_Almost everything what you want but can't find in GigaSpace Management Console for data quering_

* Supports all versions of GigaSpace starts from 9.X (never tested with previous) from one page
* One click switch between different instances, versions and spaces
* Supports detailed view for embedded data, so no more ```my.package.Object@12223``` in results
* Super view for types counts with filtering by name, non zero and date and count of last update
* Supports multiqueries and comments when you have complex work with data
* All settings store cross session, so you don't need to retype your queries again
* Copy data between spaces
* Export query result to CSV
* Support Groovy language in console
* Export / Import data between spaces

# How to Run

## Just Run and Use

```console
$ mvn com.github.terma.gigaspace-web-console:plugin:console
```

Useful parameters:

* ```-DgsVersion=<GS_VERSION>``` - to specify version of Gigaspace (will takes it from your maven repository)
* ```-Dport=<PORT>``` - specify port to run by default ```7777```
* ```-DconfigPath=file:<FILE_PATH>``` - if need to provide custom config

## Integrate in other app

```xml
<dependency>
    <groupId>com.github.terma.gigaspace-web-console</groupId>
    <artifactId>server</artifactId>
</dependency>
```

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
1. Pass JVM option ```-DgigaspacewebconsoleConfig=<classpath:X> or <file:X>``` to your Web Container
1. Start within any Web Container like Jetty, Tomcat etc.
1. Open web app in browser

# How to use

When you start your console first time after configuration you will see:

![Start](https://raw.githubusercontent.com/terma/gigaspace-web-console/master/img/start.png)

## Execute SQL queries

Select preconfigured space or type url details and click on -Query- tab

* You can enter more than one query when you run them they will be execute in independenly so you will get all results as on example below
* To disable execution of some query without remove just comment it by ```#``` or ```//``` or ```--```
* Supports ```select```, ```update```, ```delete```
* For ```delete``` and ```update``` result will be count of modified records
* For ```select``` you will get result table and count of records
* When value size for one cell more ```50``` result will be truncated, so ```result``` plus ```...``` to show full result just click on ```Show/Hide all text``` under each result table
* For columns which looks like timestamp you can click on ```T?``` after column name so console shows result in date format for example ```1424054208000``` will be show as ```1424054208000 = Mon, 16 Feb 2015 02:36:48 GMT```

![Execute SQL Queries](https://raw.githubusercontent.com/terma/gigaspace-web-console/master/img/execute.png)

### Work with timestamps

That's general case when you have data stored with timestamp and you need to query by it. Instead of typing exactly count of millis from 1970 like:
```select * from Book where created < 1435028867000```
you can:
```select * from Book where created < TODAY-1d```
or
```select * from Book where created < NOW-1d```
You can use ```-``` or ```+``` with ```d|w|h``` and ```TODAY``` means start of day when ```NOW``` means current millis

### Search documents with properties like 

Sometimes you need to find documents with specific property. As example: application stores documents with property specified for each date like P-2016-05-11=true etc. How to find documents for 2016? 

_This type of query required full scan so be careful on PROD =)_

```select * from MyDocs where property 'P-2016%'```

That's all

## Registered types and counts

![Registered Types and Counts](https://raw.githubusercontent.com/terma/gigaspace-web-console/master/img/types.png)

### Counts History

On Types Tab you for any type you can click History to track dynamic of count

![Counts History](https://raw.githubusercontent.com/terma/gigaspace-web-console/master/img/counts-history.png)

### Counts By Partitions

By switching Partitions/Space on Types Tab you can see count for space or partitions. That helps a lot 
when you need to how good your routing keys

![Counts By Partitions](https://raw.githubusercontent.com/terma/gigaspace-web-console/master/img/counts-by-partitions.png)

## Work with Groovy

Sometimes you have complex queries which depends on each other or complex calculation. For example I want to collect all values from select to one string. How I can do that?

First of all enable groovy by adding ```groovy``` word in first line of editor.
Second, create simple [Groovy](http://groovy.codehaus.org/) script and run it in console!

![Execute Groovy Script](https://raw.githubusercontent.com/terma/gigaspace-web-console/master/img/groovy.png)

A few additional words about Groovy in console. Withing your script you can use all groovy features (don't forget about proper import) plus a few additional: 

* ```java SqlResult sql(sql: java.lang.String)``` - function which can execute any SQL in GigaSpace and return result

```java 
public interface SqlResult {
    boolean next() throws SQLException;
    List<String> getColumns() throws SQLException;
    List<String> getRow() throws SQLException;
    String getSql();
}
```

* ```java void out(message: java.lang.Object)``` - print any result to console output
* ```java gs``` - ref on GigaSpace instance

### Useful fast scripts

Show space type description:
```java  
groovy
gs.typeManager.getTypeDescriptor("typeName")  
```

*Clean all data from space! Think twice!*
```java  
groovy
gs.clear(null)  
```

## Copy data between spaces

![Copy data between spaces](https://raw.githubusercontent.com/terma/gigaspace-web-console/master/img/copy.png)

### Copy part of data

If you don't have criteria to limit you dataset for copy by business field. You can use ```from X only Y``` notation in copy queries for example to copy from 1k set 200 documents start from 160 you need:

```copy MyDocs from 160 only 200```

## Export / Import data

TBD =)
