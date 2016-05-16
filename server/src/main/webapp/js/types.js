/*
 Copyright 2015 Artem Stasiuk

 Licensed under the Apache License, Version 2.0 (the 'License');
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an 'AS IS' BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

// todo if we count very long without user better to stop as now we have history
App.controller('TypesController', ['$rootScope', '$scope', '$http', '$q', '$timeout',
    function ($rootScope, $scope, $http, $q, $timeout) {
        $scope.context = $rootScope.context; // todo should be removed in future, looks like hack

        $scope.filterCounts = function (count) {
            if ($scope.context.selectedGigaspace.typesTab.hideZero && count.count == 0) return false;
            return !($scope.context.selectedGigaspace.typesTab.filter
            && count.name.toLowerCase().indexOf($scope.context.selectedGigaspace.typesTab.filter.toLowerCase()) < 0);
        };

        $scope.toggleHistory = function (count) {
            if (count.history) {
                count.history = void 0;
                notifyCountHistoryCharts();
            } else {
                $scope.resetHistory(count);
            }
        };

        $scope.resetHistory = function (count) {
            count.history = {time: ['x'], count: ['data']};
            notifyCountHistoryCharts();
        };

        function notifyCountHistoryCharts() {
            $scope.tick = !$scope.tick;
        }

        // todo duplicate from engine.js
        function findPredefinedGigaspace(name) {
            for (var m = 0; m < $scope.config.user.gigaspaces.length; m++) {
                if ($scope.config.user.gigaspaces[m].name == name) return $scope.config.user.gigaspaces[m];
            }
            return void 0;
        }

        $scope.forceStartCheckTypes = function () {
            $scope.context.selectedGigaspace.typesTab.error = undefined;
            $scope.startCheckTypes();
        };

        // todo duplicate from engine.js
        $scope.isCredentialInvalid = function () {
            $('input.source[type="password"]').removeClass('border-error');
            var gigaspace = findPredefinedGigaspace($scope.context.selectedGigaspace.name);
            if (gigaspace) {
                if (gigaspace.secure) {
                    if (!nonEmptyString($scope.context.selectedGigaspace.user)
                        || !nonEmptyString($scope.context.selectedGigaspace.password)) {
                        $('input.source[type="password"]').focus();
                        $('input.source[type="password"]').addClass('border-error');
                        return true;
                    }
                }
            }
            return false;
        };

        $scope.startCheckTypes = function () {
            if ($scope.isCredentialInvalid()) return;

            if ($scope.context.selectedGigaspace.typesTab.checking) {
                log.log('already started');
                return;
            }

            if ($scope.context.selectedGigaspace.typesTab.error) {
                log.log('we have errors no start');
                return;
            }

            $scope.context.selectedGigaspace.typesTab.error = undefined;
            $scope.context.selectedGigaspace.typesTab.status = 'Loading...';
            $scope.context.selectedGigaspace.typesTab.checking = true;
            $scope.queryCounts($scope.context.selectedGigaspace);
        };

        $scope.stopCheckTypes = function () {
            if ($scope.context.selectedGigaspace.typesTab.checking) {
                $scope.context.selectedGigaspace.typesTab.status = undefined;
                $scope.context.selectedGigaspace.typesTab.checking = false;
                log.log('checking stopped');
            }
        };

        $scope.getCountClass = function (count) {
            if (count.prevCount == undefined) return '';
            else if (count.count == count.prevCount && count.count == 0) return 'count_stable_zero';
            else if (count.count == count.prevCount) return '';
            else if (count.count > count.prevCount) return 'count_up';
            else return 'count_down';
        };

        $scope.prevCountStatus = function (count) {
            if (!count || count.prevCount == undefined || count.prevCountUpdate == undefined) return 'no update';

            var status = ((new Date().getTime() - count.prevCountUpdate) / (1000 * 60)).toFixed(0) + ' min ago';

            if (count.count > count.prevCount) status += ' +' + (count.count - count.prevCount);
            else status += ' ' + (count.count - count.prevCount);

            return status;
        };
        
        $scope.toggleByPartitions = function () {
            var typesTab = $scope.context.selectedGigaspace.typesTab;
            if (typesTab.byPartitions) {
                typesTab.byPartitions = void 0;
            } else {
                typesTab.byPartitions = true;
            }
        };

        $scope.queryCounts = function (gigaspace) {
            if (!gigaspace.typesTab.checking) return; // stopped

            var request = {
                url: gigaspace.url,
                user: gigaspace.user,
                password: gigaspace.password,
                driver: gigaspace.driver,
                appVersion: $scope.config.internal.appVersion,
                byPartitions: $scope.context.selectedGigaspace.typesTab.byPartitions
            };

            $http({
                url: 'counts',
                method: 'POST',
                data: request,
                headers: {'Content-Type': 'application/json'},
                transformResponse: transformResponse
            }).success(function (res) {
                var typesTab = gigaspace.typesTab;

                if (!typesTab.checking) return; // stopped

                function updateCount(count, newCount) {
                    var time = new Date().getTime();
                    if (count.count != newCount) {
                        count.prevCount = count.count;
                        count.prevCountUpdate = time;
                    }
                    count.count = newCount;

                    if (count.history) {
                        count.history.time.push(time);
                        count.history.count.push(newCount);
                    }
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

                // update total
                var total = 0;
                for (var n = 0; n < res.counts.length; n++) {
                    total += res.counts[n].count;
                }

                if (!typesTab.total) typesTab.total = {count: total};
                updateCount(typesTab.total, total);

                notifyCountHistoryCharts();

                $timeout(function () {
                    $scope.queryCounts(gigaspace);
                }, 5000);
            }).error(function (res) {
                gigaspace.typesTab.checking = false;
                gigaspace.typesTab.status = undefined;
                gigaspace.typesTab.error = responseToError(res);
            });
        };
    }]);