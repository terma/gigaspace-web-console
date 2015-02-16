# GigaSpaces SQL Console

Free lightweight java web application. Powerful alternative for GigaSpace Management Console when you have a lot of work with spaces data, especially for Quality Assurance Team.

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

Start page



```
// todo
```
