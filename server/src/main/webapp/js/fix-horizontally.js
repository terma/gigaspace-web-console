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

    window.fixHorizontally = {
        reset: resetScroll
    };

    function resetScroll() {
        $(document).scrollLeft(0);
    }

    function onScroll() {
        var scrollLeft = $(document).scrollLeft();

        $('.fix-horizontally').each(function () {
            if (scrollLeft > 0) {
                $(this).css('width', '100%');
                $(this).css('margin-left', scrollLeft);
            } else {
                $(this).css('width', '');
                $(this).css('margin-left', '');
            }
        });
    }

    $(window).on('scroll', onScroll);

})();