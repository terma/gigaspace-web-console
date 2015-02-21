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
            //                cursor: 1, // line number
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

                    // check if restored predefinedGigaspace present in config other skip restore
                    if (findPredefinedGigaspace(fromStoreGigaspace.name)) {
                        this.gigaspaces.push(fromStoreGigaspace);

                        // restore link on selected predefinedGigaspace
                        if (fromStore.selectedGigaspace == i) this.selectedGigaspace = fromStoreGigaspace;

                        // restore link on selected editor
                        this.gigaspaces[i].queryTab.selectedEditor =
                            fromStore.gigaspaces[i].queryTab.editors[fromStore.gigaspaces[i].queryTab.selectedEditor];
                    }
                }
            } else {
                var oldFromStore = angular.fromJson(window.localStorage.getItem(this.LOCAL_STORAGE_OLD_KEY));
                if (oldFromStore) {
                    console.log("old context found, migrating...");
                    for (var i = 0; i < oldFromStore.length; i++) {
                        var url = oldFromStore[i].url;
                        var oldEditor = oldFromStore[i].editor;

                        var predefinedGigaspace = findPredefinedGigaspaceByUrl(url);
                        if (predefinedGigaspace) {
                            var gigaspace = {
                                name: predefinedGigaspace.name,
                                url: predefinedGigaspace.url,
                                user: predefinedGigaspace.user,
                                password: predefinedGigaspace.password,
                                selectedTab: "query",
                                typesTab: {},
                                queryTab: {
                                    editors: [
                                        {
                                            name: undefined,
                                            content: oldEditor
                                        }
                                    ]
                                }
                            };

                            gigaspace.queryTab.selectedEditor = gigaspace.queryTab.editors[0];
                            this.gigaspaces.push(gigaspace);
                        }
                    }
                    //window.localStorage.removeItem(this.LOCAL_STORAGE_OLD_KEY);
                }
            }

            console.log("context restored");
            console.log(this.gigaspaces);
        },

        store: function () {
            console.log("starting store context");

            keepSelectedEditorCursor(); // as no direct update to model we should do this manually

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

                    copyTab: {
                        targetUrl: this.gigaspaces[i].copyTab ? this.gigaspaces[i].copyTab.targetUrl : undefined,
                        targetUser: this.gigaspaces[i].copyTab ? this.gigaspaces[i].copyTab.targetUser : undefined,
                        content: this.gigaspaces[i].copyTab ? this.gigaspaces[i].copyTab.content : undefined
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
                        content: this.gigaspaces[i].queryTab.editors[j].content,
                        cursor: this.gigaspaces[i].queryTab.editors[j].cursor
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

    function findPredefinedGigaspaceByUrl(url) {
        for (var i = 0; i < $scope.config.user.gigaspaces.length; i++) {
            if ($scope.config.user.gigaspaces[i].url === url) {
                return $scope.config.user.gigaspaces[i];
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

    function keepSelectedEditorCursor() {
        // todo keep not only line but selection as well
        $scope.context.selectedGigaspace.queryTab.selectedEditor.cursor = $scope.codeMirrorEditor.getCursor().line;
        console.log("store cursor in " + $scope.context.selectedGigaspace.queryTab.cursor);
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

        if (!gigaspace.copyTab) {
            gigaspace.copyTab = {};
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

        // finish work with current gigaspace
        if ($scope.context.selectedGigaspace) {
            // stop checking types for current selected
            $scope.stopCheckTypes();
            keepSelectedEditorCursor();
        }

        // select gigaspace
        console.log("select gigaspace:");
        console.log(gigaspace);
        $scope.context.selectedGigaspace = gigaspace;

        // restore cursor position
        asyncGoToAndFocus($scope.context.selectedGigaspace.queryTab.selectedEditor.cursor);
    };

    $("input.connection").keydown(function (e) {
        if (e.keyCode == 13) $scope.executeQuery();
        else $scope.stopCheckTypes();
    });

    $("select").change(function (e) {
        $scope.stopCheckTypes();
    });

    $("ui-codemirror").keydown(function (e) {
        if (e.ctrlKey && e.keyCode == 13) $scope.executeQuery();
        else $scope.stopCheckTypes();
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
    var copyingDefers = [];

    function cancelDefers(defers) {
        for (var i = 0; i < defers.length; i++) {
            defers[i].resolve("cancelled by user!");
        }
        defers.length = 0;
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

    $scope.executeToCsv = function () {
        executeQueryToSmt(function (sqlList) {
            var sqlToExecute = sqlList[sqlList.length - 1];

            if (sqlList.length > 0) {
                $scope.context.selectedGigaspace.queryTab.selectedEditor.status =
                    "Only last SQL will be exported to CSV " + sqlToExecute;
            }

            var request = {
                url: $scope.context.selectedGigaspace.url,
                user: $scope.context.selectedGigaspace.user,
                password: $scope.context.selectedGigaspace.password,
                gs: $scope.context.selectedGigaspace.gs,
                sql: sqlToExecute,
                appVersion: $scope.config.internal.appVersion
            };

            var form = $("<form></form>").attr("action", "execute-to-csv").attr("method", "post");
            form.append($("<input></input>").attr("type", "hidden").attr("name", "json").attr("value", angular.toJson(request)));
            form.appendTo("body").submit().remove();
        });
    };

    $scope.executeQuery = function () {
        executeQueryToSmt(function (sqlList) {
            for (var j = 0; j < sqlList.length; j++) {
                var sql = sqlList[j];
                console.log("start execute sql:");
                console.log(sql);
                executeOneQuery({sql: sql});
            }
        });
    };

    function executeQueryToSmt(executeQueries) {
        cancelDefers(executionCancellerList);

        $scope.context.selectedGigaspace.queryTab.selectedEditor.status = undefined;
        $scope.context.selectedGigaspace.queryTab.selectedEditor.queries = [];

        var content = $scope.codeMirrorEditor.getSelection()
            || $scope.context.selectedGigaspace.queryTab.selectedEditor.content;
        console.log("content to execute:");
        console.log(content);

        var sqlList = filterCommentedAndEmpty(getLines(content));
        if (sqlList.length > 0) executeQueries(sqlList);

        if (sqlList.length == 0) {
            console.log("nothing to execute");
            $scope.context.selectedGigaspace.queryTab.selectedEditor.status = "Nothing to execute";
        }
    }

    function responseToError(response) {
        if (!response) return {"message": "Can't connect to server!"};
        else if (typeof response == "string") return {
            "exceptionClass": "Unknown Error! Please, please ping us with that!",
            "message": response
        };
        else return response;
    }

    function transformResponse(data, headers, status) {
        if (status == 200)  return angular.fromJson(data);
        else {
            var errorPrefix = "/* --- JSON STREAM --- ERROR DELIMITER --- */";
            var errorBegin = data.indexOf(errorPrefix);
            if (errorBegin < 0) return data; // as is

            try {
                return angular.fromJson(data.substring(errorBegin + errorPrefix.length));
            } catch (e) {
                return data; // as is
            }
        }
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
            headers: {'Content-Type': "application/json"},
            transformResponse: transformResponse
        }).success(function (res) {
            query.status = undefined;
            query.data = res;
            query.data.textLengthLimit = $scope.textLengthLimit;
        }).error(function (res) {
            console.log(res);
            query.status = undefined;
            query.error = responseToError(res);
        });
    }

    $scope.filterCounts = function (count) {
        if ($scope.context.selectedGigaspace.typesTab.hideZero && count.count == 0) return false;
        if ($scope.context.selectedGigaspace.typesTab.filter &&
            count.name.toLowerCase().indexOf($scope.context.selectedGigaspace.typesTab.filter.toLowerCase()) < 0) return false;
        return true;
    };

    $scope.forceStartCheckTypes = function () {
        $scope.context.selectedGigaspace.typesTab.error = undefined;
        $scope.startCheckTypes();
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
        if ($scope.context.selectedGigaspace.typesTab.checking) {
            $scope.context.selectedGigaspace.typesTab.status = undefined;
            $scope.context.selectedGigaspace.typesTab.checking = false;
            console.log("checking stopped");
        }
    };

    $scope.closeTabs = function () {
        $scope.stopCheckTypes();
    };

    $scope.openTypesTab = function () {
        $scope.closeTabs();
        $scope.context.selectedGigaspace.selectedTab = "types";
    };

    $scope.openQueryTab = function () {
        $scope.closeTabs();
        $scope.context.selectedGigaspace.selectedTab = "query";
        asyncFocus();
    };

    $scope.openCopyTab = function () {
        $scope.closeTabs();
        $scope.context.selectedGigaspace.selectedTab = "copy";
    };

    function openQueryTabWith(sql) {
        $scope.openQueryTab();
        var content = $scope.context.selectedGigaspace.queryTab.selectedEditor.content;
        var updatedContent = content ? content + "\n" + sql : sql;
        asyncUpdateAndGoToEndAndFocus(updatedContent);
    }

    function asyncUpdateAndGoToEndAndFocus(content) {
        // timeout here is fix of issues that codemirror doesn't reflect new value when hidden
        $timeout(function () {
            $scope.codeMirrorEditor.setValue(content);
            $scope.codeMirrorEditor.setCursor($scope.codeMirrorEditor.lineCount(), 0);
            $scope.codeMirrorEditor.focus();
        });
    }

    function asyncGoToAndFocus(line) {
        $timeout(function () {
            $scope.codeMirrorEditor.setCursor(line ? line : $scope.codeMirrorEditor.lineCount(), 0);
            $scope.codeMirrorEditor.focus();
        });
    }

    function asyncFocus() {
        $timeout(function () {
            $scope.codeMirrorEditor.focus();
        });
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
            headers: {"Content-Type": "application/json"},
            transformResponse: transformResponse
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

    $scope.executeCopy = function () {
        cancelDefers(copyingDefers);

        $scope.context.selectedGigaspace.copyTab.status = undefined;
        $scope.context.selectedGigaspace.copyTab.queries = [];

        var content = $scope.context.selectedGigaspace.copyTab.content;
        console.log("content to copy:");
        console.log(content);

        var sqlList = filterCommentedAndEmpty(getLines(content));
        for (var j = 0; j < sqlList.length; j++) {
            var sql = sqlList[j];
            console.log("start copy for sql:");
            console.log(sql);
            copyOne({sql: sql});
        }

        if (sqlList.length == 0) {
            console.log("nothing to copy");
            $scope.context.selectedGigaspace.copyTab.status = "Nothing to copy";
        }
    };

    $scope.queryColumnIsTimestamp = function (data, columnIndex) {
        var firstValue = data.data[0][columnIndex];
        return /^\d{9,}$/.test(firstValue);
    };

    $scope.queryColumnToTimestamp = function (data, columnIndex) {
        for (var i = 0; i < data.data.length; i++) {
            var value = data.data[i][columnIndex];
            data.data[i][columnIndex] = value + " > " + new Date(parseInt(value)).toUTCString();
        }
    };

    $scope.selectTargetGigaspace = function (gigaspace) {
        console.log("select target g");
        console.log($scope.context.selectedGigaspace.copyTab);
        $scope.context.selectedGigaspace.copyTab.targetUrl = gigaspace.url;
        $scope.context.selectedGigaspace.copyTab.targetUser = gigaspace.user;
        $scope.context.selectedGigaspace.copyTab.targetPassword = gigaspace.password;
    };

    function copyOne(query) {
        $scope.context.selectedGigaspace.copyTab.queries.push(query);

        query.status = "Copying...";

        var copyDefer = $q.defer();
        copyingDefers.push(copyDefer);

        var request = {
            url: $scope.context.selectedGigaspace.url,
            user: $scope.context.selectedGigaspace.user,
            password: $scope.context.selectedGigaspace.password,
            gs: $scope.context.selectedGigaspace.gs,
            targetUrl: $scope.context.selectedGigaspace.copyTab.targetUrl,
            targetUser: $scope.context.selectedGigaspace.copyTab.targetUser,
            targetPassword: $scope.context.selectedGigaspace.copyTab.targetPassword,
            sql: query.sql,
            appVersion: $scope.config.internal.appVersion
        };

        $http({
            url: "copy",
            method: "POST",
            data: request,
            timeout: copyDefer.promise,
            headers: {'Content-Type': "application/json"},
            transformResponse: transformResponse
        }).success(function (res) {
            query.status = undefined;
            query.count = res.count;
            console.log(query);
        }).error(function (res) {
            query.status = undefined;
            query.error = responseToError(res);
        });
    }

    $scope.loadConfig = function () {
        $http({
            url: "config",
            method: "POST",
            headers: {"Content-Type": "application/json"},
            transformResponse: transformResponse
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
            } else {
                var predefinedGigaspace = findPredefinedGigaspace($scope.context.selectedGigaspace.name);
                if (predefinedGigaspace) $scope.selectGigaspace(predefinedGigaspace);
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
}]);

