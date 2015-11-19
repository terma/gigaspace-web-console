(function () {
    var fixedHeaders = {

        removeFixed: function (table) {
            var fixedHeader = table.parent().children(".fixed");
            fixedHeader.remove();
        },

        createFixed: function (table) {
            var fixedHeader = $(table[0].cloneNode(false));

            fixedHeader.append(table.children("thead").clone());

            fixedHeader.addClass("fixed").removeClass("fixMe").insertBefore(table);
            fixedHeader.css("width", table.width());

            fixedHeader.find("th").each(function (index) {
                var width = table.find("th").eq(index).width();
                $(this).css("width", (width + 1));
            });

            fixedHeader.show();

            return fixedHeader;
        },

        showFixed: function (table, offsetLeft, tableOffset) {
            var fixedHeader = table.parent().children(".fixed");

            if (fixedHeader.length == 0) {
                fixedHeader = fixedHeaders.createFixed(table);
            }

            fixedHeader.css("left", tableOffset.left - offsetLeft);
        },

        onScroll: function () {
            var offsetTop = $(document).scrollTop();
            var offsetLeft = $(document).scrollLeft();

            $(".fixMe").each(function () {
                var table = $(this);
                var tableOffset = table.offset();

                var tableOffsetTop = tableOffset.top;
                var tableOffsetBottom = tableOffsetTop + table.height() - table.children("thead").height();

                if (offsetTop < tableOffsetTop || offsetTop > tableOffsetBottom) {
                    fixedHeaders.removeFixed(table);
                } else if (offsetTop >= tableOffsetTop && offsetTop <= tableOffsetBottom) {
                    fixedHeaders.showFixed(table, offsetLeft, tableOffset);
                }
            });
        }

    };

    $(window).on("scroll", fixedHeaders.onScroll);

})();