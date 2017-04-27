/*
 Copyright 2015-2017 Artem Stasiuk

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

App.directive('erError', [function () {
    return {
        restrict: 'A',
        link: function ($scope, $element, $attrs) {
            $scope.$watch($attrs.erError, function (error) {
                if (error) {
                    $element.html(
                        '<p/>' +
                        '<pre class="mono-error">' + error.exceptionClass + '</pre>' +
                        '<pre class="mono-error">' + error.message + '</pre>' +
                        '<pre class="mono-error">' + error.stacktrace + '</pre>'
                    );
                    $element.css('display', 'block');
                } else {
                    $element.css('display', 'none');
                }
            });
        }
    }
}
]);
