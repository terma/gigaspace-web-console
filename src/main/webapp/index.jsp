<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>GigaSpace Browser</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/angular.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.11.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/engine.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div ng-app="App" ng-controller="GigaSpaceBrowserController" class="container">

    <ul id="gigaspaces">
        <li ng-repeat="gigaspace in gigaspaces">
            <a ng-click="selectGigaspace(gigaspace)" href="javascript:void(0);">{{ gigaspace.name }}</a>
        </li>
        <li>
            <a ng-click="toggleRecent()" href="javascript:void(0);">Recent</a>
        </li>
    </ul>

    <div id="recent" ng-show="showRecent">
        <div ng-repeat="urlHistory in history.data track by $index">
            <p>{{ urlHistory.url }}</p>

            <ul>
                <li ng-repeat="sql in urlHistory.items track by $index">
                    <a ng-click="selectRecent(urlHistory, sql)" href="javascript:void(0);">{{ sql }}</a>
                </li>
            </ul>
        </div>
    </div>

    <p/>
    <label for="url">Url</label>
    <br>
    <input id="url" name="url" ng-model="request.url" style="width: 100%"/>

    <p/>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td style="padding-right: 10px;">
                <label for="user">User</label>
                <br>
                <input id="user" name="user" ng-model="request.user"/>
            </td>

            <td>
                <label for="password">Password</label>
                <br>
                <input id="password" name="password" type="password" ng-model="request.password"/>
            </td>
        </tr>
    </table>

    <p/>

    <a href="javascript:void(0);" ng-click="openTypesTab()">Types</a>
    <a href="javascript:void(0);" ng-click="openQueryTab()">Query</a>

    <div ng-show="tab == 'types'">
        <p ng-show="counts.status">{{ counts.status }}</p>

        <p ng-show="counts.error"/>
        <a href="javascript:void(0);" ng-click="startCheckTypes()">Try Again</a>
        <pre style="color: red;">{{ counts.error.exceptionClass }}</pre>
        <pre style="color: red;">{{ counts.error.message }}</pre>
        <pre style="color: red;">{{ counts.error.stacktrace }}</pre>
        </p>

        <p/>
        <table ng-show="counts.data">
            <tbody>
            <tr ng-repeat="count in counts.data" class="{{ getCountClass(count) }}">
                <td>{{ count.name }}</td>
                <td align="right">{{ count.count }}</td>
                <td>{{ countStatus(count) }}</td>
            </tr>
            </tbody>
        </table>
    </div>

    <div ng-show="tab == 'query'">
        <p/>
        <label for="sql">SQL Query</label>
        <textarea id="sql" name="sql" ng-model="request.sql" style="width: 100%" rows="6"></textarea>

        <p/>
        <a ng-click="executeQuery()" href="javascript:void(0);">Execute</a>

        <p ng-show="status">{{ status }}</p>

        <div ng-repeat="result in results">

            <h3>Results {{ result.data.data.length }} for {{ result.sql }}</h3>

            <p ng-show="result.error">
            <pre style="color: red;">{{ result.error.exceptionClass }}</pre>
            <pre style="color: red;">{{ result.error.message }}</pre>
            <pre style="color: red;">{{ result.error.stacktrace }}</pre>
            </p>

            <p ng-show="result.status">{{ result.status }}</p>

            <a ng-show="result.data" href="javascript:void(0);" ng-click="toggleShowAllText()">Show/Hide all text</a>

            <p/>

            <table ng-show="result.data" style="border-collapse: collapse;" class="result">
                <thead>
                <tr>
                    <th ng-repeat="value in result.data.columns track by $index" valign="top" style="border: 1px solid
                    #C0C0C0; padding: 8px">{{ value }}
                    </th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="record in result.data.data track by $index"
                    ng-click="selectResultRecord(result.data.data, record)" ng-class="record.selectedClass">
                    <td ng-repeat="value in record track by $index" valign="top"
                        style="border: 1px solid #C0C0C0; padding: 8px">
                        <pre style="margin: 0; padding: 0;">{{ value | limitTo: textLengthLimit }}</pre>
                        <span ng-if="value.length > 50" ng-show="!showAllText">...</span>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>

</div>

</body>
</html>

