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

(function () {

    $(document).click(function () {
        $(".dropdown").remove();
    });

    window.tableGoto = function (target, table) {
        var columns = [];
        $(table).find('th').each(function (i, th) {
            columns.push({name: $(th).text(), element: th});
        });

        // remove old goto
        $(target).find('.table-goto').remove();

        var goTo = $('<a href="javascript:void(0);" class="table-goto">GoTo</a>');
        goTo.click(function (e) {
            var ul = $('<ul class="dropdown"></ul>');
            columns.forEach(function (column) {
                var li = $('<li>' + column.name + '</li>');
                li.click(function () {
                    column.element.scrollIntoView();
                });
                ul.append(li);
            });

            ul.css('top', goTo.offset().top + goTo.height());
            ul.css('left', goTo.offset().left);

            $('body').append(ul);

            e.stopPropagation();
        });
        $(target).append(goTo);
    };
})();