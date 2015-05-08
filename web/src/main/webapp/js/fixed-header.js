(function ($) {
    $.fn.fixMe = function () {
        return this.each(function () {
            var $this = $(this),
                $t_fixed;

            function init() {
                $this.wrap('<div class="container" />');
                $t_fixed = $this.clone(); // todo bad approach to copy entire table
                $t_fixed.find("tbody").remove().end().addClass("fixed").insertBefore($this);
                resizeFixed();
            }

            function resizeFixed() {
                $t_fixed.find("th").each(function (index) {
                    $(this).css("width", ($this.find("th").eq(index).width() + 1) + "px");
                });
            }

            function scrollFixed() {
                $t_fixed.css("left", -$this.scrollLeft());
                console.log($this.scrollLeft());

                var offset = $(this).scrollTop(),
                    tableOffsetTop = $this.offset().top,
                    tableOffsetBottom = tableOffsetTop + $this.height() - $this.find("thead").height();

                if (offset < tableOffsetTop || offset > tableOffsetBottom)
                    $t_fixed.hide();
                else if (offset >= tableOffsetTop && offset <= tableOffsetBottom && $t_fixed.is(":hidden"))
                    $t_fixed.show();
            }

            if (!$this.parent().hasClass("container")) {
                $(window).resize(resizeFixed);
                $(window).scroll(scrollFixed);
                init();
                console.log("attached");
            } else {
                console.log("skip");
            }
        });
    };
})(jQuery);