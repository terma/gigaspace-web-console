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

App.directive('seasonA', ['$interval', function ($interval) {
    var events = [{
        name: 'New Year',
        month: 11,
        day: 31,
        tolerance: 7
    }];

    return {
        restrict: 'E',
        replace: true,
        template: '<a href="javascript:void(0);"></a>',
        link: function ($scope, element, attrs) {
            function updateTitle() {
                var currentMonth = new Date().getMonth(); // 0-11
                var currentDay = new Date().getDate(); // 1-31

                var currentEvent = void 0;
                for (var i = 0; i < events.length; i++) {
                    var tolerance = events[i].tolerance ? events[i].tolerance : 2;
                    if (events[i].month == currentMonth && (events[i].day - currentDay) <= tolerance) {
                        currentEvent = events[i];
                        break;
                    }
                }

                var title = attrs.saTitle ? attrs.saTitle : 'Click';
                if (currentEvent) title = currentEvent.name;

                angular.element(element).text(title);
            }

            if (attrs.saClick) {
                angular.element(element).click(function () {
                    $scope.$apply(function () {
                        $scope.$eval(attrs.saClick);
                    });
                });
            }

            $interval(function () {
                updateTitle();
            }, 60 * 60 * 1000); // 1h

            updateTitle();
        }
    }
}]);
