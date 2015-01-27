// todo skip empty line when send to execute
// todo add support column name with "-"

var App = angular.module('App', ['ui.codemirror']);

App.controller('GigaSpaceBrowserController', ['$scope', '$http', '$q', '$timeout', function ($scope, $http, $q, $timeout) {

    /*
     How many symbols we need to show in result for long text fields without checking showAllText?
     */
    $scope.textLengthLimit = 50;

    /*
     Object to get user settings for everything
     */
    // todo implement storing part of that object to localStorage and restore from it
    $scope.context = {

        selectedGigaspace: undefined,

        gigaspaces: [
            {
                name: "GS-9.5",
                custom: undefined, // for future usage

                url: "URL",
                gs: "GS-9.5",
                user: "USER",
                password: "XXX", // transient

                selectedTab: "query",

                typesTab: {
                    hideZero: undefined,
                    filter: undefined,

                    result: { // transient
                        status: undefined,

                        error: undefined,

                        // or

                        data: {
                            prev: undefined,
                            current: undefined,
                            selectedCount: undefined
                        }
                    }
                },

                queryTab: {
                    selectedEditor: undefined,

                    editors: [
                        {
                            name: undefined, // default
                            content: "SQLs",

                            result: { // transient
                                status: undefined,

                                queries: [
                                    {
                                        status: undefined,

                                        error: {
                                            exceptionClass: undefined,
                                            message: undefined,
                                            stacktrace: undefined
                                        },

                                        // or

                                        data: [
                                            {
                                                showAllText: false,
                                                query: "SQL",
                                                records: []
                                            }
                                        ]
                                    }
                                ]
                            }
                        },
                        {
                            name: "Custom1",
                            content: "SQLs"
                        }
                    ]
                }
            }
        ],

        LOCAL_STORAGE_KEY: "settings",
        LOCAL_STORAGE_OLD_KEY: "history",

        //init: function () {
        //    this.restore();
        //    // old version so just free storage
        //    window.localStorage.setItem(this.LOCAL_STORAGE_OLD_KEY, null);
        //},

        //restore: function () {
        //    this.temporary = [];
        //    this.permanent = angular.fromJson(window.localStorage.getItem(this.LOCAL_STORAGE_KEY));
        //    if (!this.permanent) this.permanent = [];
        //},

        //store: function () {
        //    window.localStorage.setItem(this.LOCAL_STORAGE_KEY, angular.toJson(this.permanent));
        //}

    };

    function findGigaspace(name) {
        for (var i = 0; i < $scope.context.gigaspaces.length; i++) {
            if ($scope.context.gigaspaces[i].name === name) {
                return $scope.context.gigaspaces[i];
            }
        }
        return undefined;
    }

    $scope.selectGigaspace = function (predefinedGigaspace) {
        // todo stop work with current selected gigaspace if needed

        var gigaspace = findGigaspace(predefinedGigaspace.name);
        if (!gigaspace) {
            gigaspace = {
                name: predefinedGigaspace.name,
                selectedTab: "query",
                queryTab: {
                    editors: []
                }
            };
            console.log("Add new gigaspace:");
            console.log(gigaspace);
            $scope.context.gigaspaces.push(gigaspace);
        }

        if (!gigaspace.queryTab.selectedEditor) {
            if (gigaspace.queryTab.editors.length > 0) gigaspace.queryTab.selectedEditor = gigaspace.queryTab.editors[0];
            else {
                var editor = {};
                gigaspace.queryTab.editors.push(editor);
                gigaspace.queryTab.selectedEditor = editor;
            }
        }

        gigaspace.url = predefinedGigaspace.url;
        gigaspace.user = predefinedGigaspace.user;
        gigaspace.gs = predefinedGigaspace.gs;
        gigaspace.password = predefinedGigaspace.password;

        console.log("select gigaspace:");
        console.log(gigaspace);
        $scope.context.selectedGigaspace = gigaspace;
    };

    $("input,select").keydown(function (e) {
        if (e.keyCode == 13) $scope.executeQuery();
    });

    $("ui-codemirror").keydown(function (e) {
        if (e.ctrlKey && e.keyCode == 13) $scope.executeQuery();
    });

    function resetResult() {
        $scope.queries = [];
    }

    $scope.toggleShowAllText = function () {
        $scope.showAllText = !$scope.showAllText;
        $scope.textLengthLimit = $scope.showAllText ? 100000 : 50;
    };

    $scope.selectRecent = function (recentItem, sql) {
        $scope.showRecent = false;
        $scope.request.url = recentItem.url;
        $scope.request.user = recentItem.user;
        $scope.request.password = recentItem.password;
        $scope.request.gs = recentItem.gs;

        var editor = $scope.history.editorByUrl(recentItem.url);
        $scope.request.sql = (editor.length > 0 ? editor + "\n" : "") + sql;
        resetResult();
    };

    $scope.showRecent = false;
    $scope.toggleRecent = function () {
        $scope.showRecent = !$scope.showRecent;
    };

    function getLines(text) {
        return text == undefined ? [] : text.split("\n");
    }

    function isCommentedOrEmpty(string) {
        return string.trim().length == 0 || string.indexOf("//") == 0 || string.indexOf("#") == 0 || string.indexOf("--") == 0;
    }

    var executionCancellerList = [];

    function stopExecuteSqls() {
        for (var i = 0; i < executionCancellerList.length; i++) {
            executionCancellerList[i].resolve("cancelled by user!");
        }
        executionCancellerList = [];
    }

    function filterCommentedAndEmpty(lines) {
        var filtered = [];
        for (var j = 0; j < lines.length; j++) {
            if (!isCommentedOrEmpty(lines[j])) {
                filtered.push(lines[j]);
            }
        }
        return filtered;
    }

    $scope.executeQuery = function () {
        stopExecuteSqls();

        $scope.context.selectedGigaspace.queryTab.selectedEditor.result = {
            queries: []
        };

        var content = $scope.codeMirrorEditor.getSelection()
            || $scope.context.selectedGigaspace.queryTab.selectedEditor.content;
        console.log("content to execute:");
        console.log(content);

        var sqlList = filterCommentedAndEmpty(getLines(content));
        for (var j = 0; j < sqlList.length; j++) {
            var sql = sqlList[j];
            console.log("start execute sql:");
            console.log(sql);
            executeOneQuery({sql: sql});
        }

        if (sqlList.length == 0) {
            console.log("nothing to execute");
            $scope.context.selectedGigaspace.queryTab.selectedEditor.result.status = "Nothing to execute";
        }
    };

    function responseToError(response) {
        if (!response) return {"message": "Can't connect to server!"};
        else if (typeof response == "string") return {
            "exceptionClass": "Unknown Error! Please, please ping us with that!",
            "message": response
        };
        else return response;
    }

    function executeOneQuery(query) {
        $scope.context.selectedGigaspace.queryTab.selectedEditor.result.queries.push(query);

        query.status = "Executing...";

        var executionCanceller = $q.defer();
        executionCancellerList.push(executionCanceller);

        var request = {
            url: $scope.context.selectedGigaspace.url,
            user: $scope.context.selectedGigaspace.user,
            password: $scope.context.selectedGigaspace.password,
            gsVersion: $scope.context.selectedGigaspace.gs,
            sql: query.sql,
            appVersion: $scope.config.internal.appVersion
        };

        query.status = "Executing...";
        $http({
            url: "execute",
            method: "POST",
            data: request,
            timeout: executionCanceller.promise,
            headers: {'Content-Type': "application/json"}
        }).success(function (res) {
            query.status = undefined;
            query.data = res;
        }).error(function (res) {
            query.status = undefined;
            query.error = responseToError(res);
        });
    }

    $scope.filterCounts = function (count) {
        if ($scope.counts.hideZero && count.count == 0) return false;
        if ($scope.counts.filter && count.name.indexOf($scope.counts.filter) < 0) return false;
        return true;
    };

    $scope.startCheckTypes = function () {
        if ($scope.counts.check) return; // already started

        $scope.counts.status = "Loading...";
        $scope.counts.error = undefined;
        $scope.counts.data = undefined;
        $scope.counts.check = true;
        $scope.queryCounts();
    };

    $scope.stopCheckTypes = function () {
        $scope.counts.check = false;
    };

    $scope.openTypesTab = function () {
        $scope.tab = "types";
        $scope.startCheckTypes();
    };

    $scope.openQueryTab = function () {
        $scope.tab = "query";
        $scope.stopCheckTypes();
    };

    $scope.openQueryTabWithSelectFor = function (typeName) {
        $scope.openQueryTab();
        $scope.request.sql += "\nselect * from " + typeName + " where rownum < 50";
    };

    $scope.openQueryTabWithUpdateFor = function (typeName) {
        $scope.openQueryTab();
        $scope.request.sql += "\nupdate " + typeName + " set ? where ?";
    };

    $scope.openQueryTabWithDeleteFor = function (typeName) {
        $scope.openQueryTab();
        $scope.request.sql += "\ndelete from " + typeName + " where ?";
    };

    $scope.getCountClass = function (count) {
        if (count.prevCount == undefined) return "";
        else if (count.count == count.prevCount && count.count == 0) return "count_stable_zero";
        else if (count.count == count.prevCount) return "";
        else if (count.count > count.prevCount) return "count_up";
        else return "count_down";
    };

    $scope.prevCountStatus = function (count) {
        if (!count || count.prevCount == undefined || count.prevCountUpdate == undefined) return "no update";

        var status = ((new Date().getTime() - count.prevCountUpdate) / (1000 * 60)).toFixed(0) + " min ago";

        if (count.count > count.prevCount) status += " +" + (count.count - count.prevCount);
        else status += " " + (count.count - count.prevCount);

        return status;
    };

    $scope.queryCounts = function () {
        if (!$scope.counts.check) return; // stopped

        var request = {
            url: $scope.request.url,
            user: $scope.request.user,
            password: $scope.request.password,
            gs: $scope.request.gs,
            appVersion: $scope.config.internal.appVersion
        };

        $http({
            url: "counts",
            method: "POST",
            data: request,
            headers: {"Content-Type": "application/json"}
        }).success(function (res) {
            if (!$scope.counts.check) return; // stopped

            $scope.counts.error = undefined;
            $scope.counts.status = undefined;

            function updateCount(count, newCount) {
                if (count.count != newCount) {
                    count.prevCount = count.count;
                    count.prevCountUpdate = new Date().getTime();
                }
                count.count = newCount;
            }

            function findCount(counts, name) {
                for (var j = 0; j < counts.length; j++) {
                    if (counts[j].name == name) {
                        return counts[j];
                    }
                }
                return undefined;
            }

            if (!$scope.counts.data) $scope.counts.data = [];

            // update existent counters and remove old
            for (var i = 0; i < $scope.counts.data.length;) {
                var count = $scope.counts.data[i];
                var newCount = findCount(res.counts, count.name);
                if (newCount) {
                    // get updates
                    updateCount(count, newCount.count);
                    i++;
                } else {
                    // old just remove
                    $scope.counts.data.splice(i, 1);
                }
            }

            // add new counters
            for (var j = 0; j < res.counts.length; j++) {
                var newCount = res.counts[j];
                var count = findCount($scope.counts.data, newCount.name);
                if (!count) {
                    $scope.counts.data.push(newCount);
                }
            }

            var total = 0;
            for (var n = 0; n < res.counts.length; n++) {
                total += res.counts[n].count;
            }

            if (!$scope.counts.total) $scope.counts.total = {count: total};
            updateCount($scope.counts.total, total);

            $timeout(function () {
                $scope.queryCounts();
            }, 5000);
        }).error(function (res) {
            $scope.counts.check = false;
            $scope.counts.status = undefined;
            $scope.counts.error = responseToError(res);
        });
    };

    $scope.loadConfig = function () {
        $http({
            url: "config",
            method: "POST",
            headers: {'Content-Type': "application/json"}
        }).success(function (res) {
            $scope.config = res;


        }).error(function (res) {
            // todo show error if can't load config, as temporary solution
            console.log(res);
            alert("Oops... \n" + res);
        });
    };

    $scope.editorOptions = {
        mode: "text/x-sql",
        indentWithTabs: true,
        smartIndent: true,
        lineWrapping: true,
        lineNumbers: false,
        matchBrackets: true,
        autofocus: true,
        extraKeys: {"Ctrl-Space": "autocomplete"},
        placeholder: "SQL to execute, support lists, use #, // or -- for comment",
        hint: CodeMirror.hint.sql,
        hintOptions: {
            tables: {//todo load automatically based on real GigaSpaces; Issue #23
            }
        },
        onLoad: function (cm) {
            $scope.codeMirrorEditor = cm;
            $scope.codeMirrorEditor.setSize(null, 95);
        }
    };

    $scope.loadConfig();
}])
;


