/*
 Copyright 2015 Artem Stasiuk

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

// todo add support column name with "-"

App.filter('nullAsString', function () {
    return function (value) {
        return value === null ? 'null' : value;
    };
});

App.controller("controller", [
    "$scope", "$http", "$q", "$timeout", "$filter",
    function ($scope, $http, $q, $timeout, $filter) {

        /*
         How many symbols we need to show in result for long text fields without checking showAllText?
         */
        $scope.textLengthLimit = 50;

        /*
         Object to get user settings for everything
         */
        $scope.context = {

            selectedGigaspace: undefined,

            gigaspaces: [],

            LOCAL_STORAGE_KEY: "settings",
            LOCAL_STORAGE_OLD_KEY: "history",

            restore: function () {
                var _this = $scope.context;

                log.log("start restoring context...");
                this.gigaspaces = [];

                function unsafeRestore() {
                    var fromStore = angular.fromJson(window.localStorage.getItem(_this.LOCAL_STORAGE_KEY));
                    if (fromStore) {
                        log.log("stored context found, restoring...");
                        for (var i = 0; i < fromStore.gigaspaces.length; i++) {
                            var fromStoreGigaspace = fromStore.gigaspaces[i];

                            // check if restored predefinedGigaspace present in config other skip restore
                            if (findPredefinedGigaspace(fromStoreGigaspace.name)) {
                                _this.gigaspaces.push(fromStoreGigaspace);

                                // restore link on selected predefinedGigaspace
                                if (fromStore.selectedGigaspace == i) _this.selectedGigaspace = fromStoreGigaspace;

                                // restore link on selected editor
                                _this.gigaspaces[i].queryTab.selectedEditor =
                                    fromStore.gigaspaces[i].queryTab.editors[fromStore.gigaspaces[i].queryTab.selectedEditor];

                                // create export if not present
                                if (!_this.gigaspaces[i].exportImportTab) _this.gigaspaces[i].exportImportTab = {};
                            }
                        }
                    }
                }

                try {
                    unsafeRestore();

                    log.log('context restored');
                    log.log(this.gigaspaces);
                } catch (e) {
                    log.log('cant restore context, go with empty');
                    log.log(e);
                }
            },

            store: function () {
                var _this = $scope.context;

                log.log("starting store context");

                keepSelectedEditorCursor(); // as no direct update to model we should do this manually

                var toStore = {
                    gigaspaces: []
                };

                for (var i = 0; i < this.gigaspaces.length; i++) {
                    var toStoreGigaspace = {
                        name: _this.gigaspaces[i].name,
                        user: _this.gigaspaces[i].user,
                        url: _this.gigaspaces[i].url,
                        driver: _this.gigaspaces[i].driver,
                        selectedTab: _this.gigaspaces[i].selectedTab,

                        typesTab: {
                            hideZero: _this.gigaspaces[i].typesTab.hideZero,
                            filter: _this.gigaspaces[i].typesTab.filter,
                            selectedCount: _this.gigaspaces[i].typesTab.selectedCount
                        },

                        copyTab: {
                            targetUrl: _this.gigaspaces[i].copyTab ? _this.gigaspaces[i].copyTab.targetUrl : undefined,
                            targetUser: _this.gigaspaces[i].copyTab ? _this.gigaspaces[i].copyTab.targetUser : undefined,
                            content: _this.gigaspaces[i].copyTab ? _this.gigaspaces[i].copyTab.content : undefined
                        },

                        queryTab: {
                            editors: []
                        }
                    };
                    toStore.gigaspaces.push(toStoreGigaspace);

                    // if selected store index
                    if (_this.selectedGigaspace == _this.gigaspaces[i]) toStore.selectedGigaspace = i;

                    // copy editors
                    for (var j = 0; j < _this.gigaspaces[i].queryTab.editors.length; j++) {
                        // if selected store index
                        if (_this.gigaspaces[i].queryTab.selectedEditor == _this.gigaspaces[i].queryTab.editors[j])
                            toStoreGigaspace.queryTab.selectedEditor = j;

                        var toStoreEditor = {
                            name: _this.gigaspaces[i].queryTab.editors[j].name,
                            content: _this.gigaspaces[i].queryTab.editors[j].content,
                            cursor: _this.gigaspaces[i].queryTab.editors[j].cursor
                        };

                        toStoreGigaspace.queryTab.editors.push(toStoreEditor);
                    }
                }

                window.localStorage.setItem(_this.LOCAL_STORAGE_KEY, angular.toJson(toStore));
                log.log("context was stored");
            }

        };

        function findPredefinedGigaspace(name) {
            for (var m = 0; m < $scope.config.user.gigaspaces.length; m++) {
                if ($scope.config.user.gigaspaces[m].name == name) return $scope.config.user.gigaspaces[m];
            }
            return void 0;
        }

        function findGigaspace(name) {
            for (var i = 0; i < $scope.context.gigaspaces.length; i++) {
                if ($scope.context.gigaspaces[i].name === name) {
                    return $scope.context.gigaspaces[i];
                }
            }
            return void 0;
        }

        function keepSelectedEditorCursor() {
            // todo keep not only line but selection as well
            $scope.context.selectedGigaspace.queryTab.selectedEditor.cursor = $scope.codeMirrorEditor.getCursor().line;
            log.log("store cursor in " + $scope.context.selectedGigaspace.queryTab.cursor);
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
                log.log("Add new gigaspace:");
                log.log(gigaspace);
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
            gigaspace.driver = predefinedGigaspace.driver;
            if (predefinedGigaspace.user) gigaspace.user = predefinedGigaspace.user;
            if (predefinedGigaspace.password) gigaspace.password = predefinedGigaspace.password;

            // finish work with current gigaspace
            if ($scope.context.selectedGigaspace) {
                // stop checking types for current selected
                $scope.stopCheckTypes();
                keepSelectedEditorCursor();
            }

            // select gigaspace
            log.log("select gigaspace:");
            log.log(gigaspace);
            $scope.context.selectedGigaspace = gigaspace;

            // restore cursor position
            asyncGoToAndFocus($scope.context.selectedGigaspace.queryTab.selectedEditor.cursor);
        };

        $("input.connection").keydown(function (e) {
            if (e.keyCode == 13) $scope.executeQuery();
            else $scope.stopCheckTypes();
        });

        $("select").change(function () {
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
                    driver: $scope.context.selectedGigaspace.driver,
                    sql: sqlToExecute,
                    appVersion: $scope.config.internal.appVersion
                };

                var form = $("<form></form>").attr("action", "execute-to-csv").attr("method", "post");
                form.append($("<input></input>").attr("type", "hidden").attr("name", "json").attr("value", angular.toJson(request)));
                form.appendTo("body").submit().remove();
            });
        };

        function groovyScript(lines) {
            return lines.length > 0 && lines[0] == "groovy";
        }

        $scope.executeQuery = function () {
            executeQueryToSmt(function (lines) {
                if (groovyScript(lines)) { // groovy script
                    lines.splice(0, 1); // remove groovy line

                    var editor = $scope.context.selectedGigaspace.queryTab.selectedEditor;
                    editor.status = "Executing...";

                    var request = {
                        url: $scope.context.selectedGigaspace.url,
                        user: $scope.context.selectedGigaspace.user,
                        password: $scope.context.selectedGigaspace.password,
                        driver: $scope.context.selectedGigaspace.driver,
                        sql: lines.mkString("\n"),
                        appVersion: $scope.config.internal.appVersion
                    };

                    $http({
                        url: "groovy-execute",
                        method: "POST",
                        data: request,
                        headers: {'Content-Type': "application/json"},
                        transformResponse: transformResponse
                    }).success(function (res) { // array of result
                        for (var i = 0; i < res.length; i++) {
                            var query = {
                                sql: res[i].header,
                                data: res[i]
                            };
                            query.data.textLengthLimit = 50;
                            editor.queries.push(query);
                        }
                        editor.status = undefined;
                        $scope.successefullyConnected();
                    }).error(function (res) {
                        log.log(res);
                        editor.status = undefined;
                        var errorQuery = {error: responseToError(res)};
                        editor.queries.push(errorQuery);
                    });
                } else { // real SQL lines
                    for (var j = 0; j < lines.length; j++) {
                        var sql = lines[j];
                        log.log("start execute sql:");
                        log.log(sql);
                        executeOneQuery({sql: sql});
                    }
                }
            });
        };

        function executeQueryToSmt(executeQueries) {
            cancelDefers(executionCancellerList);

            $scope.context.selectedGigaspace.queryTab.selectedEditor.status = undefined;
            $scope.context.selectedGigaspace.queryTab.selectedEditor.queries = [];

            var content = $scope.codeMirrorEditor.getSelection()
                || $scope.context.selectedGigaspace.queryTab.selectedEditor.content;
            log.log("content to execute:");
            log.log(content);

            var sqlList = filterCommentedAndEmpty(getLines(content));
            if (sqlList.length > 0) executeQueries(sqlList);

            if (sqlList.length == 0) {
                log.log("nothing to execute");
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
                driver: $scope.context.selectedGigaspace.driver,
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
                $scope.successefullyConnected();
            }).error(function (res) {
                log.log(res);
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
                log.log("already started");
                return;
            }

            if ($scope.context.selectedGigaspace.typesTab.error) {
                log.log("we have errors no start");
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
                log.log("checking stopped");
            }
        };

        $scope.closeTabs = function () {
            $scope.stopCheckTypes();
        };

        $scope.openTypesTab = function () {
            $scope.closeTabs();
            $scope.context.selectedGigaspace.selectedTab = "types";
        };

        $scope.openAdminTab = function () {
            $scope.closeTabs();
            $scope.context.selectedGigaspace.selectedTab = "admin";
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

        $scope.openExportImportTab = function () {
            $scope.closeTabs();
            $scope.context.selectedGigaspace.selectedTab = "exportImport";
        };

        $scope.export = function () {
            var types = [];


            //var typesString = $scope.context.selectedGigaspace.exportImportTab.types;
            //if (typesString) {
            // todo parse types
            //typesString.splite(",")
            //}

            var request = {
                url: $scope.context.selectedGigaspace.url,
                user: $scope.context.selectedGigaspace.user,
                password: $scope.context.selectedGigaspace.password,
                driver: $scope.context.selectedGigaspace.driver,
                types: types,
                appVersion: $scope.config.internal.appVersion
            };

            var form = $("<form></form>").attr("action", "export").attr("method", "post");
            form.append($("<input></input>").attr("type", "hidden").attr("name", "json").attr("value", angular.toJson(request)));
            form.appendTo("body").submit().remove();
        };

        $scope.startImport = function () {
            log.log("enrich import form with json");
            $("#import-json").val(angular.toJson({
                url: $scope.context.selectedGigaspace.url,
                user: $scope.context.selectedGigaspace.user,
                password: $scope.context.selectedGigaspace.password,
                driver: $scope.context.selectedGigaspace.driver,
                appVersion: $scope.config.internal.appVersion
            }));
        };

        function openQueryTabWith(sql, replaceContent) {
            $scope.openQueryTab();
            var content = $scope.context.selectedGigaspace.queryTab.selectedEditor.content;
            var updatedContent = (!replaceContent && content) ? content + "\n" + sql : sql;
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

        $scope.openQueryTabWithTypeFor = function (typeName) {
            openQueryTabWith("groovy\nout gs.typeManager.getTypeDescriptor('" + typeName + "')", true);
        };

        $scope.toggleTemplates = function () {
            $scope.showTemplates = !$scope.showTemplates;
        };

        $scope.useTemplate = function (template) {
            $scope.showTemplates = undefined;
            asyncUpdateAndGoToEndAndFocus(template.sql);
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
                driver: gigaspace.driver,
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
            log.log("content to copy:");
            log.log(content);

            var sqlList = filterCommentedAndEmpty(getLines(content));
            for (var j = 0; j < sqlList.length; j++) {
                var sql = sqlList[j];
                log.log("start copy for sql:");
                log.log(sql);
                copyOne({sql: sql});
            }

            if (sqlList.length == 0) {
                log.log("nothing to copy");
                $scope.context.selectedGigaspace.copyTab.status = "Nothing to copy";
            }
        };

        $scope.queryColumnIsTimestamp = function (data, columnIndex) {
            if (data.length < 1) return false;
            var firstValue = data[0][columnIndex];
            return /^\d{9,}$/.test(firstValue);
        };

        $scope.queryColumnToTimestamp = function (data, columnIndex) {
            // <= 10 - seconds
            // <= 13 - ms
            // > 13 - reduce to ms

            function secTimestampToTime(string) {
                return msTimestampToTime(string + "000");
            }

            function msTimestampToTime(string) {
                return new Date(parseInt(string)).toUTCString();
            }

            function moreThanMsTimestampToTime(string) {
                return msTimestampToTime(string.substring(0, 13));
            }

            var stringToTime = undefined;
            var testValueLength = data[0][columnIndex].length;
            if (testValueLength <= 10) stringToTime = secTimestampToTime;
            else if (testValueLength <= 13) stringToTime = msTimestampToTime;
            else stringToTime = moreThanMsTimestampToTime;

            for (var i = 0; i < data.length; i++) {
                var value = data[i][columnIndex];
                data[i][columnIndex] = value + " > " + stringToTime(value);
            }
        };

        $scope.selectTargetGigaspace = function (gigaspace) {
            log.log("select target g");
            log.log($scope.context.selectedGigaspace.copyTab);
            $scope.context.selectedGigaspace.copyTab.targetUrl = gigaspace.url;
            $scope.context.selectedGigaspace.copyTab.targetUser = gigaspace.user;
            $scope.context.selectedGigaspace.copyTab.targetPassword = gigaspace.password;
        };

        $scope.togglePUs = function () {
            var selected = $scope.admin.selected;
            var filteredPUs = $filter("filter")($scope.pus, $scope.puFilter);
            for (var i = 0; i < filteredPUs.length; i++) {
                filteredPUs[i].selected = selected;
            }
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
                driver: $scope.context.selectedGigaspace.driver,
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
                log.log(query);
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
                log.log(res);
                alert("Oops... \n" + res);
            });
        };

        var autocompleteCancel = void 0;

        $scope.successefullyConnected = function () {
            $scope.loadStructureOnlyIfError();
        };

        $scope.loadStructureOnlyIfError = function () {
            var structure = $scope.context.selectedGigaspace.structure;
            if (!structure || structure.loadError) {
                $scope.context.selectedGigaspace.structure = undefined;
                $scope.loadStructure(function () {
                });
            }
        };

        $scope.injectAutocomplete = function (text) {
            var cursor = $scope.codeMirrorEditor.getCursor();

            var currentLine = $scope.codeMirrorEditor.lineInfo(cursor.line).text;
            var firstPartOfLine = currentLine.substring(0, cursor.ch);
            var lastSpaceIndex = Math.max(firstPartOfLine.lastIndexOf(' '), firstPartOfLine.lastIndexOf(','));
            if (lastSpaceIndex > -1) {
                $scope.codeMirrorEditor.replaceRange(
                    text,
                    {line: cursor.line, ch: lastSpaceIndex + 1},
                    {line: cursor.line, ch: cursor.ch});
            } else {
                $scope.codeMirrorEditor.replaceRange(text, cursor);
            }
            $scope.codeMirrorEditor.focus();
        };

        $scope.isCredentialForSelectedGigaspaceCorrect = function () {
            var gigaspace = findPredefinedGigaspace($scope.context.selectedGigaspace.name);
            if (gigaspace) {
                if (gigaspace.secure)
                    return nonEmptyString($scope.context.selectedGigaspace.user)
                        && nonEmptyString($scope.context.selectedGigaspace.password);
                else return true;
            } else return false;
        };

        $scope.loadStructure = function (callback) {
            if ($scope.context.selectedGigaspace.structure) {
                callback($scope.context.selectedGigaspace.structure);
            } else {
                if (!$scope.isCredentialForSelectedGigaspaceCorrect()) return;

                if (autocompleteCancel) autocompleteCancel.resolve("cancelled by user!");
                autocompleteCancel = $q.defer();

                var request = {
                    url: $scope.context.selectedGigaspace.url,
                    user: $scope.context.selectedGigaspace.user,
                    password: $scope.context.selectedGigaspace.password,
                    driver: $scope.context.selectedGigaspace.driver,
                    appVersion: $scope.config.internal.appVersion
                };

                $http({
                    url: 'explore',
                    method: 'POST',
                    data: request,
                    timeout: autocompleteCancel.promise,
                    headers: {'Content-Type': 'application/json'},
                    transformResponse: transformResponse
                }).success(function (res) {
                    $scope.context.selectedGigaspace.structure = res;
                    callback($scope.context.selectedGigaspace.structure);
                }).error(function () {
                    $scope.context.selectedGigaspace.structure = {loadError: true};
                });
            }
        };

        $scope.editorOptions = {
            mode: "text/x-sql",
            indentWithTabs: true,
            smartIndent: true,
            lineWrapping: true,
            lineNumbers: false,
            matchBrackets: true,
            autofocus: true,
            placeholder: "SQL to execute, support lists, use #, // or -- for comment",
            hint: CodeMirror.hint.sql,
            onLoad: function (cm) {
                $scope.codeMirrorEditor = cm;
                $scope.codeMirrorEditor.setSize(null, 95);
                cm.on('cursorActivity', function (cm) {
                    $timeout(function () {
                        var cursor = cm.getCursor();
                        var currentLine = cm.lineInfo(cursor.line).text;

                        function showTablesAutocomplete(searchPattern) {
                            $scope.loadStructure(function (structure) {
                                $scope.autocompletePattern = searchPattern;
                                $scope.autocompleteType = 'tables';
                                $scope.autocomplete = [];

                                angular.forEach(structure.tables, function (table) {
                                    $scope.autocomplete.push(table.name);
                                });
                            });
                        }

                        function showColumnsAutocomplete(tableName) {
                            $scope.loadStructure(function (structure) {


                                $scope.autocompletePattern = void 0;
                                $scope.autocompleteType = 'columns';
                                $scope.autocomplete = [];

                                function findTableByName(tableName) {
                                    for (var i = 0; i < structure.tables.length; i++) {
                                        if (structure.tables[i].name === tableName) return structure.tables[i];
                                    }
                                    return void 0;
                                }

                                var table = findTableByName(tableName);
                                if (table) $scope.autocomplete = table.columns;
                            });
                        }

                        var closerIndexColumns = currentLine.closerIndexFromLeft(['select', 'where', 'order', 'set', 'setProperty', 'group'], cursor.ch);
                        var closerIndexTables = currentLine.closerIndexFromLeft(['from', 'update'], cursor.ch);

                        if (closerIndexColumns > -1 && closerIndexColumns > closerIndexTables) {
                            var match = currentLine.match(/(from|update)[ ]+([a-zA-Z\._0-9-]+)/);
                            if (match) showColumnsAutocomplete(match[2]);
                            else $scope.autocomplete = [];
                        } else if (closerIndexTables > -1) {
                            var lastSpaceIndex = currentLine.substring(0, cursor.ch).lastIndexOf(' ');
                            if (lastSpaceIndex > -1) {
                                var alreadyEnteredTableName = currentLine.substring(lastSpaceIndex + 1, cursor.ch);
                                showTablesAutocomplete(alreadyEnteredTableName);
                            } else {
                                showTablesAutocomplete('');
                            }
                        } else {
                            $scope.autocomplete = [];
                        }
                    }, 0);
                });
            }
        };

        $scope.puFilter = "";
        $scope.components = [];
        $scope.pus = [{
            name: "my-space",
            space: true,
            expectedInstances: 40,
            instances: 39,
            gscs: "9000123, 12323, 112324",
            mem: {
                max: 9000,
                used: 8000
            }
        }, {
            name: "listener-1",
            expectedInstances: 1,
            instances: 1
        }, {
            name: "processor-my-super",
            expectedInstances: 1,
            instances: 0
        }, {
            name: "processor-my-super",
            expectedInstances: 1,
            instances: 0
        }, {
            name: "processor-my-super",
            expectedInstances: 1,
            instances: 0
        }, {
            name: "processor-my-super",
            expectedInstances: 1,
            instances: 0
        }, {
            name: "processor-my-a",
            expectedInstances: 1,
            instances: 0
        }, {
            name: "processor-my-b",
            expectedInstances: 1,
            instances: 1,
            status: "DEPLOYED"
        }, {
            name: "processor-my-c",
            expectedInstances: 1,
            instances: 1
        }, {
            name: "another-big-space-app",
            space: true,
            expectedInstances: 10,
            instances: 10
        }];


        $scope.loadConfig();
    }]);

function nonEmptyString(string) {
    return string && string.length > 0;
}

Array.prototype.mkString = function (delimiter) {
    var result = "";
    for (var i = 0; i < this.length; i++) {
        if (i > 0) result += delimiter;
        result += this[i];
    }
    return result;
};

if (!String.prototype.endsWith) {
    String.prototype.endsWith = function (searchString, position) {
        var subjectString = this.toString();
        if (position === undefined || position > subjectString.length) {
            position = subjectString.length;
        }
        position -= searchString.length;
        var lastIndex = subjectString.indexOf(searchString, position);
        return lastIndex !== -1 && lastIndex === position;
    };
}

String.prototype.closerIndexFromLeft = function (searchStrings, lastPosition) {
    var closerIndex = -1;
    for (var i = 0; i < searchStrings.length; i++) {
        var index = this.indexOf(searchStrings[i]);
        if (index + searchStrings[i].length < lastPosition && closerIndex < index) closerIndex = index;
    }
    return closerIndex;
};