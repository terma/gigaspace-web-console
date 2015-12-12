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
            var selectedIndex = void 0;

            function renderModel(restoreSelection) {
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

                // restore selection
                log.log('selectedIndex');
                log.log(selectedIndex);
                if (selectedIndex !== void 0) angular.element(tbody.children()[selectedIndex]).addClass('selected');
                else selectedIndex = void 0;


                function clickHandler(event) {
                    tbody.children().removeClass('selected');
                    var selectedTr = angular.element(event.target).parent();
                    selectedTr.addClass('selected');
                    selectedIndex = selectedTr.data('index');
                }

                angular.forEach(tbody.children(), function (rawTd, index) {
                    var tr = angular.element(rawTd);
                    tr.on('click', clickHandler);
                    tr.data('index', index);
                });

                element.append(table);

                window.tableGoto(element.parent().parent().find('.table-goto-target'), table);
            }

            $scope.$watch(attrs.qrtModel, function (newValue) {
                model = newValue;
                showAllText = false;
                renderModel(false);
            });

            $scope.$watch(attrs.qrtShowAllText, function (newValue) {
                showAllText = newValue;
                renderModel(true);
            });
        }
    }
}]);

var htmlEscaper = /[&<>"'\/]/g;
var htmlEscapes = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#x27;',
    '/': '&#x2F;'
};

String.prototype.escapeHtml = function () {
    return this.replace(htmlEscaper, function (match) {
        return htmlEscapes[match];
    });
};