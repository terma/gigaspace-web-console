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

App.directive('countHistoryChart', ['$interval', function ($interval) {
    log.log('directive called');

    return {
        restrict: 'E',
        scope: {
            count: '=chcCount'
        },
        link: function ($scope, element) {
            var chartDiv = element.append('<div></div>');

            var chart = c3.generate({
                bindto: chartDiv[0],
                data: {x: 'x', columns: []},
                axis: {x: {type: 'timeseries', tick: {format: '%HH:%MM:%SS'}}},
                size: {height: 200, width: 900}
            });

            update();

            var interval = $interval(function () {
                update();
            }, 5000);

            $scope.$on('$destroy', function () {
                $interval.cancel(interval);
            });

            function update() {
                if (!$scope.count) return;
                var history = $scope.count.history;
                if (!history) return;

                chart.load({columns: [history.time, history.count]});
            }
        }
    }
}]);