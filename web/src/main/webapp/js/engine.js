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
                    selectedCount: undefined,

                    result: { // transient
                        checking: false,

                        status: undefined,

                        error: undefined,

                        // or

                        data: undefined
                    }
                },

                queryTab: {
                    selectedEditor: undefined,

                    editors: [
                        {
                            name: undefined, // default
                            content: "SQLs",

                            result: { // transient
                                status: undefined

                                //queries: [
                                //    {
                                        //query: "SQL",

                                        //status: undefined,

                                        //error: {
                                        //    exceptionClass: undefined,
                                        //    message: undefined,
                                        //    stacktrace: undefined
                                        //}

                                        // or

                                        //data: {
                                        //    showAllText: false,
                                        //    selectedRecord: undefined,
                                        //    textLengthLimit: $scope.textLengthLimit
                                        //}
                                    //}
                                //]
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
        LOCAL_STORAGE_OLD_KEY: "history"

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
                typesTab: {
                    result: {}
                },
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
        gigaspace.gs = predefinedGigaspace.gs;
        if (predefinedGigaspace.user) gigaspace.user = predefinedGigaspace.user;
        if (predefinedGigaspace.password) gigaspace.password = predefinedGigaspace.password;

        // stop checking types for current selected
        if ($scope.context.selectedGigaspace)
            $scope.context.selectedGigaspace.typesTab.result.checking = false;

        // select gigaspace
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

    $scope.toggleShowAllText = function (query) {
        query.data.showAllText = !query.data.showAllText;
        query.data.textLengthLimit = query.data.showAllText ? 100000 : 50;
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
            gs: $scope.context.selectedGigaspace.gs,
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
            query.data.textLengthLimit = $scope.textLengthLimit;
        }).error(function (res) {
            query.status = undefined;
            query.error = responseToError(res);
        });
    }

    $scope.filterCounts = function (count) {
        if ($scope.context.selectedGigaspace.typesTab.hideZero && count.count == 0) return false;
        if ($scope.context.selectedGigaspace.typesTab.filter && count.name.indexOf($scope.context.selectedGigaspace.typesTab.filter) < 0) return false;
        return true;
    };

    $scope.startCheckTypes = function () {
        if ($scope.context.selectedGigaspace.typesTab.result.checking) {
            console.log("already started");
            return;
        }

        if ($scope.context.selectedGigaspace.typesTab.result.error) {
            console.log("we have errors no start");
            return;
        }

        $scope.context.selectedGigaspace.typesTab.result.status = "Loading...";
        $scope.context.selectedGigaspace.typesTab.result.checking = true;
        $scope.queryCounts();
    };

    $scope.stopCheckTypes = function () {
        $scope.context.selectedGigaspace.typesTab.result.checking = false;
    };

    $scope.openTypesTab = function () {
        $scope.context.selectedGigaspace.selectedTab = "types";
        $scope.startCheckTypes();
    };

    $scope.openQueryTab = function () {
        $scope.context.selectedGigaspace.selectedTab = "query";
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
        if (!$scope.context.selectedGigaspace.typesTab.result.checking) return; // stopped

        var request = {
            url: $scope.context.selectedGigaspace.url,
            user: $scope.context.selectedGigaspace.user,
            password: $scope.context.selectedGigaspace.password,
            gs: $scope.context.selectedGigaspace.gs,
            appVersion: $scope.config.internal.appVersion
        };

        $http({
            url: "counts",
            method: "POST",
            data: request,
            headers: {"Content-Type": "application/json"}
        }).success(function (res) {
            var typesTab = $scope.context.selectedGigaspace.typesTab;
            var result = typesTab.result;

            if (!result.checking) return; // stopped

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

            result.error = undefined;
            result.status = undefined;
            if (!result.data) result.data = [];

            // update existent counters and remove old
            for (var i = 0; i < result.data.length;) {
                var count = result.data[i];
                var newCount = findCount(res.counts, count.name);
                if (newCount) {
                    // get updates
                    updateCount(count, newCount.count);
                    i++;
                } else {
                    // old just remove
                    result.data.splice(i, 1);
                }
            }

            // add new counters
            for (var j = 0; j < res.counts.length; j++) {
                var newCount = res.counts[j];
                var count = findCount(result.data, newCount.name);
                if (!count) {
                    result.data.push(newCount);
                }
            }

            var total = 0;
            for (var n = 0; n < res.counts.length; n++) {
                total += res.counts[n].count;
            }

            if (!result.total) result.total = {count: total};
            updateCount(result.total, total);

            $timeout(function () {
                $scope.queryCounts();
            }, 5000);
        }).error(function (res) {
            $scope.context.selectedGigaspace.typesTab.result.checking = false;
            $scope.context.selectedGigaspace.typesTab.result.status = undefined;
            $scope.context.selectedGigaspace.typesTab.result.error = responseToError(res);
        });
    };

    $scope.loadConfig = function () {
        $http({
            url: "config",
            method: "POST",
            headers: {"Content-Type": "application/json"}
        }).success(function (res) {
            $scope.config = res;
            // todo select gigaspace
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


