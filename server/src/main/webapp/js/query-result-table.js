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

var App = angular.module("App", ["ui.codemirror"]);

App.directive('queryResultTable', ['$rootScope', '$filter', function ($rootScope, $filter) {
    log.log('directive called');

    return {
        restrict: 'E',

        link: function ($scope, element, attrs) {
            var nullAsString = $filter('nullAsString');
            var limitTo = $filter('limitTo');

            var columnIsTimestamp = $scope[attrs.qrtColumnIsTimestamp];
            var columnToTimestamp = $scope[attrs.qrtColumnToTimestamp];
            if (!columnIsTimestamp) columnIsTimestamp = function () {
                return false;
            };

            var model = void 0;
            var showAllText = false;

            function renderModel() {
                element.empty();
                if (!model) return;

                var columns = model.columns;
                var data = model.data;

                var table = angular.element('<table class="result fixMe" style="table-layout: fixed;"></table>');
                var thead = angular.element('<thead></thead>');
                table.append(thead);

                var columnTr = angular.element('<tr></tr>');
                thead.append(columnTr);

                angular.forEach(columns, function (column, columnIndex) {
                    var th = angular.element('<th valign="top" class="mono-text">' + column.escapeHtml() + '</th>');
                    columnTr.append(th);

                    if (columnIsTimestamp(model.data, columnIndex)) {
                        var timestampA = angular.element('<a href="javascript:void(0);">T?</a>');
                        timestampA.on('click', function () {
                            columnToTimestamp(data, columnIndex);
                            renderModel();
                            // todo refactor to show info with timestamp and keep original data immutable

                        });
                        th.append('&nbsp;');
                        th.append(timestampA);
                    }
                });

                var tbody = angular.element('<tbody></tbody>');
                table.append(tbody);

                var tbodyHtml = '';
                angular.forEach(data, function (row) {
                    var trHtml = '';
                    angular.forEach(row, function (value) {
                        var renderValue = value;

                        renderValue = nullAsString(value);
                        if (!showAllText) {
                            renderValue = limitTo(renderValue, 50);
                            if (value && value.length > 50) renderValue += '...';
                        }

                        var additionalClass = '';
                        if (!value) additionalClass = 'null-text';

                        trHtml += '<td class="result-value ' + additionalClass + '" valign="top">'
                            + renderValue.escapeHtml() + '</td>';
                    });
                    tbodyHtml += '<tr>' + trHtml + '</tr>';
                });
                tbody.append(tbodyHtml);

                tbody.children().on('click', function (event) {
                    tbody.children().removeClass('selected');
                    angular.element(event.target).parent().addClass('selected');
                });

                log.debug('replace with:');
                log.debug(table);
                element.append(table);
            }

            $scope.$watch(attrs.qrtModel, function (newValue) {
                model = newValue;
                showAllText = false;
                renderModel();
            });

            $scope.$watch(attrs.qrtShowAllText, function (newValue) {
                showAllText = newValue;
                renderModel();
            });
        }
    }
}]);

String.prototype.escapeHtml = function () {
    return this.replace(/</g, '&lt;').replace(/>/g, '&gt;');
};