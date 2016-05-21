/*
 Copyright 2015-2016 Artem Stasiuk

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

App.directive('countHistoryChart', [function () {
    return {
        restrict: 'E',
        link: function ($scope, element, attrs) {
            var count = $scope.$eval(attrs.chcCount);
            var chartDiv = element.append('<div></div>');

            var chart = c3.generate({
                bindto: chartDiv[0],
                data: {x: 'x', columns: []},
                axis: {x: {type: 'timeseries', tick: {format: '%HH:%MM:%SS'}}},
                size: {height: 200, width: 900}
            });

            $scope.$watch(attrs.chcTrigger, function () {
                update();
            });

            function update() {
                if (!count || !count.history) return;
                chart.load({columns: [count.history.time, count.history.count]});
            }
        }
    }
}]);