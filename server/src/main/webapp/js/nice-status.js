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

App.directive('niceStatus', [function () {
    return {
        restrict: 'A',
        link: function ($scope, element, attrs) {
            $scope.$watch(attrs.nsMessage, function (newMessage, oldMessage) {
                if (newMessage || oldMessage) {
                    if (newMessage && newMessage.endsWith('...')) {
                        newMessage = newMessage.substring(0, newMessage.length - 3);
                        newMessage += '<span class="jumping-dots">' +
                            '<span class="dot-1">.</span>' +
                            '<span class="dot-2">.</span>' +
                            '<span class="dot-3">.</span>' +
                            '</span>';
                    }
                    angular.element(element).html(newMessage);
                }
            });
        }
    }
}]);