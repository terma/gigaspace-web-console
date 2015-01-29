// todo skip empty line when send to execute
// todo add support column name with "-"

var App = angular.module("App", ["ui.codemirror"]);

App.controller("GigaSpaceBrowserController", ["$scope", "$http", "$q", "$timeout", function ($scope, $http, $q, $timeout) {

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
            //{
            //    name: "GS-9.5",
            //    custom: undefined, // for future usage
            //
            //    url: "URL",
            //    gs: "GS-9.5",
            //    user: "USER",
            //    password: "XXX", // transient
            //
            //    selectedTab: "query",
            //
            //    typesTab: {
            //        hideZero: undefined,
            //        filter: undefined,
            //        selectedCount: undefined,
            //
            //        // transient
            //        checking: false,
            //        status: undefined,
            //        error: undefined,
            //        // or
            //        data: undefined
            //    },
            //
            //    queryTab: {
            //        selectedEditor: undefined,
            //
            //        editors: [
            //            {
            //                name: undefined, // default
            //                content: "SQLs",
            //
            //                // transient
            //                status: undefined
            //
            //                queries: [
            //                    //    {
            //                    //query: "SQL",
            //
            //                    //status: undefined,
            //
            //                    //error: {
            //                    //    exceptionClass: undefined,
            //                    //    message: undefined,
            //                    //    stacktrace: undefined
            //                    //}
            //
            //                    // or
            //
            //                    //data: {
            //                    //    showAllText: false,
            //                    //    selectedRecord: undefined,
            //                    //    textLengthLimit: $scope.textLengthLimit
            //                    //}
            //                    //}
            //                ]
            //            },
            //            {
            //                name: "Custom1",
            //                content: "SQLs"
            //            }
            //        ]
            //    }
            //}
        ],

        LOCAL_STORAGE_KEY: "settings",
        LOCAL_STORAGE_OLD_KEY: "history",

        //init: function () {
        //    this.restore();
        //    // old version so just free storage
        //    window.localStorage.setItem(this.LOCAL_STORAGE_OLD_KEY, null);
        //},

        restore: function () {
            console.log("start restoring context...");
            this.gigaspaces = [];

            var fromStore = angular.fromJson(window.localStorage.getItem(this.LOCAL_STORAGE_KEY));

            if (fromStore) {
                console.log("stored context found, restoring...");
                for (var i = 0; i < fromStore.gigaspaces.length; i++) {
                    var fromStoreGigaspace = fromStore.gigaspaces[i];

                    // check if restored gigaspace present in config other skip restore
                    if (findPredefinedGigaspace(fromStoreGigaspace.name)) {
                        this.gigaspaces.push(fromStoreGigaspace);

                        // restore link on selected gigaspace
                        if (fromStore.selectedGigaspace == i) this.selectedGigaspace = fromStoreGigaspace;

                        // restore link on selected editor
                        this.gigaspaces[i].queryTab.selectedEditor =
                            fromStore.gigaspaces[i].queryTab.editors[fromStore.gigaspaces[i].queryTab.selectedEditor];
                    }
                }
            }

            var oldFromStore = angular.fromJson(window.localStorage.getItem(this.LOCAL_STORAGE_OLD_KEY));
            if (oldFromStore) {
                console.log("old context found, migrating...");
                for (var i = 0; i < oldFromStore.length; i++) {
                    var url = oldFromStore[i].url;
                    var oldEditor = oldFromStore[i].editor;

                    var gigaspace = findGigaspaceByUrl(url);
                    if (gigaspace) {
                        gigaspace.selectedTab = "query";
                        var editor = {content: oldEditor};
                        gigaspace.queryTab.editors = [editor];
                        gigaspace.queryTab.selectedEditor = editor;
                    }
                }
                window.localStorage.removeItem(this.LOCAL_STORAGE_OLD_KEY);
            }

            console.log("context restored");
        },

        store: function () {
            console.log("starting store context");

            var toStore = {
                gigaspaces: []
            };

            for (var i = 0; i < this.gigaspaces.length; i++) {
                var toStoreGigaspace = {
                    name: this.gigaspaces[i].name,
                    user: this.gigaspaces[i].user,
                    url: this.gigaspaces[i].url,
                    gs: this.gigaspaces[i].gs,
                    selectedTab: this.gigaspaces[i].selectedTab,

                    typesTab: {
                        hideZero: this.gigaspaces[i].typesTab.hideZero,
                        filter: this.gigaspaces[i].typesTab.filter,
                        selectedCount: this.gigaspaces[i].typesTab.selectedCount
                    },

                    queryTab: {
                        editors: []
                    }
                };
                toStore.gigaspaces.push(toStoreGigaspace);

                // if selected store index
                if (this.selectedGigaspace == this.gigaspaces[i]) toStore.selectedGigaspace = i;

                // copy editors
                for (var j = 0; j < this.gigaspaces[i].queryTab.editors.length; j++) {
                    // if selected store index
                    if (this.gigaspaces[i].queryTab.selectedEditor == this.gigaspaces[i].queryTab.editors[j])
                        toStoreGigaspace.queryTab.selectedEditor = j;

                    var toStoreEditor = {
                        name: this.gigaspaces[i].queryTab.editors[j].name,
                        content: this.gigaspaces[i].queryTab.editors[j].content
                    };

                    toStoreGigaspace.queryTab.editors.push(toStoreEditor);
                }
            }

            window.localStorage.setItem(this.LOCAL_STORAGE_KEY, angular.toJson(toStore));
            console.log("context was stored");
        }

    };

    function findPredefinedGigaspace(name) {
        for (var m = 0; m < $scope.config.user.gigaspaces.length; m++) {
            if ($scope.config.user.gigaspaces[m].name == name) return $scope.config.user.gigaspaces[m];
        }
        return undefined;
    }

    function findGigaspaceByUrl(url) {
        for (var i = 0; i < $scope.context.gigaspaces.length; i++) {
            if ($scope.context.gigaspaces[i].url === url) {
                return $scope.context.gigaspaces[i];
            }
        }
        return undefined;
    }

    function findGigaspace(name) {
        for (var i = 0; i < $scope.context.gigaspaces.length; i++) {
            if ($scope.context.gigaspaces[i].name === name) {
                return $scope.context.gigaspaces[i];
            }
        }
        return undefined;
    }

    $scope.selectGigaspace = function (predefinedGigaspace) {
        var gigaspace = findGigaspace(predefinedGigaspace.name);
        if (!gigaspace) {
            gigaspace = {
                name: predefinedGigaspace.name,
                selectedTab: "query",
                typesTab: {},
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
            $scope.context.selectedGigaspace.typesTab.checking = false;

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

        $scope.context.selectedGigaspace.queryTab.selectedEditor.status = undefined;
        $scope.context.selectedGigaspace.queryTab.selectedEditor.queries = [];

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
            $scope.context.selectedGigaspace.queryTab.selectedEditor.status = "Nothing to execute";
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
        $scope.context.selectedGigaspace.queryTab.selectedEditor.queries.push(query);

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
        if ($scope.context.selectedGigaspace.typesTab.checking) {
            console.log("already started");
            return;
        }

        if ($scope.context.selectedGigaspace.typesTab.error) {
            console.log("we have errors no start");
            return;
        }

        $scope.context.selectedGigaspace.typesTab.error = undefined;
        $scope.context.selectedGigaspace.typesTab.status = "Loading...";
        $scope.context.selectedGigaspace.typesTab.checking = true;
        $scope.queryCounts($scope.context.selectedGigaspace);
    };

    $scope.stopCheckTypes = function () {
        $scope.context.selectedGigaspace.typesTab.checking = false;
    };

    $scope.openTypesTab = function () {
        $scope.context.selectedGigaspace.selectedTab = "types";
        $scope.startCheckTypes();
    };

    $scope.openQueryTab = function () {
        $scope.context.selectedGigaspace.selectedTab = "query";
        $scope.stopCheckTypes();
    };

    function openQueryTabWith(sql) {
        $scope.openQueryTab();
        $scope.context.selectedGigaspace.queryTab.selectedEditor.content += "\n" + sql;
        // todo add scroll down editor
    }

    $scope.openQueryTabWithSelectFor = function (typeName) {
        openQueryTabWith("select * from " + typeName + " where rownum < 50");
    };

    $scope.openQueryTabWithUpdateFor = function (typeName) {
        openQueryTabWith("update " + typeName + " set ? where ?");
    };

    $scope.openQueryTabWithDeleteFor = function (typeName) {
        openQueryTabWith("delete from " + typeName + " where ?");
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

    $scope.queryCounts = function (gigaspace) {
        if (!gigaspace.typesTab.checking) return; // stopped

        var request = {
            url: gigaspace.url,
            user: gigaspace.user,
            password: gigaspace.password,
            gs: gigaspace.gs,
            appVersion: $scope.config.internal.appVersion
        };

        $http({
            url: "counts",
            method: "POST",
            data: request,
            headers: {"Content-Type": "application/json"}
        }).success(function (res) {
            var typesTab = gigaspace.typesTab;

            if (!typesTab.checking) return; // stopped

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

            typesTab.error = undefined;
            typesTab.status = undefined;
            if (!typesTab.data) typesTab.data = [];

            // update existent counters and remove old
            for (var i = 0; i < typesTab.data.length;) {
                var count = typesTab.data[i];
                var newCount = findCount(res.counts, count.name);
                if (newCount) {
                    // get updates
                    updateCount(count, newCount.count);
                    i++;
                } else {
                    // old just remove
                    typesTab.data.splice(i, 1);
                }
            }

            // add new counters
            for (var j = 0; j < res.counts.length; j++) {
                var newCount = res.counts[j];
                var count = findCount(typesTab.data, newCount.name);
                if (!count) {
                    typesTab.data.push(newCount);
                }
            }

            var total = 0;
            for (var n = 0; n < res.counts.length; n++) {
                total += res.counts[n].count;
            }

            if (!typesTab.total) typesTab.total = {count: total};
            updateCount(typesTab.total, total);

            $timeout(function () {
                $scope.queryCounts(gigaspace);
            }, 5000);
        }).error(function (res) {
            gigaspace.typesTab.checking = false;
            gigaspace.typesTab.status = undefined;
            gigaspace.typesTab.error = responseToError(res);
        });
    };

    $scope.loadConfig = function () {
        $http({
            url: "config",
            method: "POST",
            headers: {"Content-Type": "application/json"}
        }).success(function (res) {
            $scope.config = res;

            $scope.context.restore();

            // register event to store context before unload
            window.addEventListener("beforeunload", function () {
                $scope.context.store();
            });

            // restore selected gigaspace, first or default
            if (!$scope.context.selectedGigaspace) {
                if ($scope.config.user.gigaspaces.length > 0) $scope.selectGigaspace($scope.config.user.gigaspaces[0]);
                else $scope.selectGigaspace({name: "New"});
            }
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


