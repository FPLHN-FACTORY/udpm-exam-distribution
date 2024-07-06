//START: swiper
const progressCircle = document.querySelector(".autoplay-progress svg");
const progressContent = document.querySelector(".autoplay-progress span");
var swiper = new Swiper(".mySwiper", {
    slidesPerView: 4,
    slidesPerGroup: 4,
    grid: {
        rows: 2,
    },
    grabCursor: true,
    keyboard: {
        enabled: true,
    },
    spaceBetween: 30,
    scrollbar: {
        el: ".swiper-scrollbar",
    },
    navigation: {
        nextEl: ".swiper-button-next",
        prevEl: ".swiper-button-prev",
    },
    autoplay: {
        delay: 10000,
        disableOnInteraction: false,
    },
    on: {
        autoplayTimeLeft(s, time, progress) {
            progressCircle.style.setProperty("--progress", 1 - progress);
            progressContent.textContent = `${Math.ceil(time / 1000)}s`;
        },
    },
});
//END: swiper

$(document).ready(function () {
    fetchListSubject();
});

const fetchListSubject = () => {
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/subject",
        method: "GET",
        success: function (responseData) {
            console.log(responseData);
            const select = document.getElementById('subjectId');
            responseData?.data.forEach(item => {
                const option = document.createElement('option');
                option.value = item.id;
                option.text = item.name;
                select.appendChild(option);
            });
            $('#subjectId').val(select);
        },
        error: function (error) {
            const messageErr = error?.responseJSON?.message;
            if(messageErr){
                showToastError(messageErr);
            }else{
                showToastError("Có lỗi xảy ra");
            }
        }
    });
};