// todo skip empty line when send to execute
// todo add support column name with "-"

var App = angular.module('App', ['ui.codemirror']);

App.controller('GigaSpaceBrowserController', ['$scope', '$http', '$q', '$timeout', function ($scope, $http, $q, $timeout) {
    $scope.request = {};
    $scope.results = [];

    $scope.history = {

        HISTORY_SIZE: 20,
        URL_HISTORY_SIZE: 20,
        LOCAL_STORAGE_KEY: "history",

        data: [],

        init: function () {
            this.restore();
        },

        restore: function () {
            this.data = angular.fromJson(window.localStorage.getItem(this.LOCAL_STORAGE_KEY));
            if (!this.data) this.data = [];
        },

        store: function () {
            window.localStorage.setItem(this.LOCAL_STORAGE_KEY, angular.toJson(this.data));
        },

        editorByUrl: function (url) {
            var urlHistory = this.findUrlHistory(url);
            return (urlHistory && urlHistory.editor) ? urlHistory.editor : "";
        },

        setEditor: function (request) {
            var urlHistory = this.findUrlHistoryOrCreate(request);
            urlHistory.editor = request.editor;
            this.store();
            console.log("set editor for url: " + request.url + " to: " + request.editor);
        },

        findUrlHistory: function (url) {
            for (var i = 0; i < this.data.length; i++) {
                var item = this.data[i];
                if (item.url == url) return item;
            }
            return undefined;
        },

        findUrlHistoryOrCreate: function (request) {
            var urlHistory = this.findUrlHistory(request.url);
            if (urlHistory == undefined) {
                urlHistory = {
                    url: request.url,
                    user: request.user,
                    password: request.password,
                    editor: undefined,
                    items: []
                };

                this.data.push(urlHistory);
                console.log("add new url history: " + urlHistory);
            }
            return urlHistory;
        },

        add: function (request) {
            var urlHistory = this.findUrlHistoryOrCreate(request);

            // add only if last not same
            if (urlHistory.items.length == 0 || urlHistory.items[urlHistory.items.length - 1] != request.sql) {
                console.log("add new sql: " + request.sql);
                urlHistory.items.push(request.sql);
            }

            urlHistory.updated = new Date().getTime();

            this.store();
        },

        getLatestEditor: function () {
            var data = this.data;

            function findLatest() {
                var latest = undefined;
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    if (latest == undefined || latest.updated < item.updated) {
                        latest = item;
                    }
                }
                return latest;
            }

            var latest = findLatest();

            if (latest == undefined) return {};
            else return {
                url: latest.url,
                user: latest.user,
                password: latest.password,
                editor: latest.editor
            };
        }

    };

    $scope.history.init();

    $scope.tab = "query";

    $scope.counts = {
        check: false,
        hideZero: false,
        status: undefined,
        error: undefined,
        prevData: undefined,
        data: undefined,
        selectedCount: undefined
    };

    $scope.selectGigaspace = function (gigaspace) {
        $scope.request.url = gigaspace.url;
        $scope.request.user = gigaspace.user;
        $scope.request.password = gigaspace.password;
        $scope.request.sql = $scope.history.editorByUrl($scope.request.url);
        resetResult();
    };

    $('input').keydown(function (e) {
        if (e.keyCode == 13) {
            $scope.executeQuery()
        }
    });

    $('ui-codemirror').keydown(function (e) {
        if (e.ctrlKey && e.keyCode == 13) {
            $scope.executeQuery()
        }
    });

    function resetResult() {
        $scope.results = [];
    }

    resetResult();

    var latest = $scope.history.getLatestEditor();
    $scope.request = {
        url: latest.url,
        user: latest.user,
        password: latest.password,
        sql: latest.editor
    };

    $scope.textLengthLimit = 50;

    $scope.toggleShowAllText = function () {
        $scope.showAllText = !$scope.showAllText;
        $scope.textLengthLimit = $scope.showAllText ? 100000 : 50;
    };

    $scope.selectRecent = function (recentItem, sql) {
        $scope.showRecent = false;
        $scope.request.url = recentItem.url;
        $scope.request.user = recentItem.user;
        $scope.request.password = recentItem.password;

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

        $scope.results = [];

        var content = $scope.codeMirrorEditor.getSelection() || $scope.request.sql;

        $scope.history.setEditor({
            url: $scope.request.url,
            user: $scope.request.user,
            password: $scope.request.password,
            editor: content
        });

        var sqlList = filterCommentedAndEmpty(getLines(content));
        for (var j = 0; j < sqlList.length; j++) {
            var sql = sqlList[j];
            console.log("start sql: " + sql);
            executeOneQuery({sql: sql});
        }

        if (sqlList.length == 0) {
            console.log("nothing to execute");
            $scope.status = "Nothing to execute";
        } else {
            $scope.status = undefined;
        }
    };

    function executeOneQuery(result) {
        result.status = "Executing...";
        $scope.results.push(result);

        var executionCanceller = $q.defer();
        executionCancellerList.push(executionCanceller);

        var request = {
            url: $scope.request.url,
            user: $scope.request.user,
            password: $scope.request.password,
            sql: result.sql,
            appVersion: $scope.config.internal.appVersion
        };

        $scope.history.add(request);

        $scope.maxCellLength = 100;

        $scope.error = undefined;
        $scope.result = undefined;
        $scope.status = "Executing...";

        $http({
            url: "execute",
            method: "POST",
            data: request,
            timeout: executionCanceller.promise,
            headers: {'Content-Type': "application/json"}
        }).success(function (res) {
            result.status = undefined;
            result.data = res;
        }).error(function (res) {
            result.status = undefined;
            if (res) result.error = res;
            else result.error = {"message": "Can't connect to server!"};
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
            appVersion: $scope.config.internal.appVersion
        };

        $http({
            url: "counts",
            method: "POST",
            data: request,
            headers: {'Content-Type': "application/json"}
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
            if (res) $scope.counts.error = res;
            else $scope.counts.error = {"message": "Can't connect to server!"};
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
        mode: 'text/x-sql',
        content: 'SQL, could be list, use # for comments',
        indentWithTabs: true,
        smartIndent: true,
        lineWrapping: true,
        lineNumbers: true,
        matchBrackets: true,
        autofocus: true,
        extraKeys: {"Ctrl-Space": "autocomplete"},
        hint: CodeMirror.hint.sql,
        hintOptions: {
            tables: {//todo load automatically based on real GigaSpaces; Issue #23
                "table1": ["col_A", "col_B", "col_C"],
                "table2": ["other_columns1", "other_columns2"]
            }
        },
        onLoad: function (cm) {
            $scope.codeMirrorEditor = cm;
            $scope.codeMirrorEditor.setSize(null, 95);
        }
    };

    $scope.loadConfig();
}]);


