$(document).ready(function () {
    let currentUrl = window.location.pathname;

    $('.nav-item a').each(function () {
        let itemUrl = $(this).attr('href');
        if (currentUrl.includes(itemUrl) && itemUrl !== '/') {
            $(this).parent().addClass('active');
        } else {
            $(this).parent().removeClass('active');
        }
    });
});