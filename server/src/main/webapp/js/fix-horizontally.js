(function () {

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