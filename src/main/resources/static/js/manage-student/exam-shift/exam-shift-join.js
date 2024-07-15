const pdfjsLib = window["pdfjs-dist/build/pdf"];
let pdfDoc = null;
let pageNum = 1;
let pageRendering = false;
let pageNumPending = null;
const scale = 1.5;
const $pdfCanvas = $("#pdf-canvas")[0];
const ctx = $pdfCanvas.getContext("2d");

$(document).ready(function () {

    getExamShiftByCode();

    const joinExamShiftSuccessMessage = localStorage.getItem('joinExamShiftStudentSuccessMessage');
    if (joinExamShiftSuccessMessage) {
        showToastSuccess(joinExamShiftSuccessMessage);
        localStorage.removeItem('joinExamShiftStudentSuccessMessage');
    }

    getPathFilePDFExamPaper(examShiftCode);

    connect();

    $('#openExamPaper').click(function () {
        openModalOpenExamPaper();
    });

    $('#modifyExamPaperOpenButton').click(function () {
        examPaperOpenSubmit();
    });

    $('#completeExamShift').click(function () {
        completeExamShift();
    });

    // checkOnline();

});

let examShiftCode = $('#examShiftCodeCtl').text();
let examPaperId = $('#examPaperId').text();
let stompClient = null;

const getExamShiftByCode = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + '/' + examShiftCode,
        success: function (responseBody) {
            if (responseBody?.data) {
                const examShift = responseBody?.data;
                $('#examShiftCode').text("Phòng thi - Mã tham gia: " + examShift.examShiftCode);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin ca thi');
        }
    })
}

const getPathFilePDFExamPaper = (examShiftCode) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + "/path",
        data: {
            examShiftCode: examShiftCode
        },
        success: function (responseBody) {
            if (responseBody?.data) {
                examPaperId = responseBody?.data?.id;
                const fileId = responseBody?.data?.path;
                const startTime = responseBody?.data?.startTime;
                const endTime = responseBody?.data?.endTime;
                fetchFilePDFExamPaper(fileId);
                startCountdown(startTime, endTime);
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            } else {
                showToastError('Có lỗi xảy ra');
            }
        }
    });
}

const fetchFilePDFExamPaper = (fileId) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + "/file",
        data: {
            fileId: fileId
        },
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));
            pdfjsLib
                .getDocument({data: pdfData})
                .promise.then(function (pdfDoc_) {
                pdfDoc = pdfDoc_;
                $("#total-page").text(pdfDoc.numPages);

                renderPage(pageNum);
                showViewAndPagingPdf(pdfDoc.numPages);

                hideLoading();
            });
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            } else {
                showToastError('Có lỗi xảy ra');
            }
            hideLoading();
        }
    });
};

const openExamPaper = () => {
    const openExamPaper = {
        examPaperShiftId: examPaperId,
        passwordOpen: $('#modifyPasswordOpen').val()
    };
    $.ajax({
        type: "POST",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + "/open",
        contentType: "application/json",
        data: JSON.stringify(openExamPaper),
        success: function (responseBody) {
            if (responseBody?.status === "OK") {
                $('#examPaperOpenModal').modal('hide');
                $('#examShiftPaper').prop('hidden', false);
                $('#openExamPaper').prop('hidden', true);
                $('#completeExamShift').prop('hidden', false);
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            } else {
                showToastError('Có lỗi xảy ra');
            }
        }
    });
};

// Render the page
function renderPage(num) {
    pageRendering = true;
    pdfDoc.getPage(num).then(function (page) {
        const viewport = page.getViewport({scale: scale});
        $pdfCanvas.height = viewport.height;
        $pdfCanvas.width = viewport.width;

        const renderContext = {
            canvasContext: ctx,
            viewport: viewport,
        };
        const renderTask = page.render(renderContext);

        renderTask.promise.then(function () {
            pageRendering = false;
            if (pageNumPending !== null) {
                renderPage(pageNumPending);
                pageNumPending = null;
            }
        });
    });

    $("#page-num").text(num);
}

const showViewAndPagingPdf = (totalPage) => {
    console.log(totalPage);
    $("#pdf-viewer").prop("hidden", false);
    if (totalPage > 1) {
        $("#paging-pdf").prop("hidden", false);
    }
};

function queueRenderPage(num) {
    if (pageRendering) {
        pageNumPending = num;
    } else {
        renderPage(num);
    }
}

$("#prev-page").on("click", function () {
    if (pageNum <= 1) {
        return;
    }
    pageNum--;
    queueRenderPage(pageNum);
});

$("#next-page").on("click", function () {
    if (pageNum >= pdfDoc.numPages) {
        return;
    }
    pageNum++;
    queueRenderPage(pageNum);
});

const connect = () => {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/topic/student-exam-shift", function (response) {
            const responseBody = JSON.parse(response.body);
            showToastSuccess(responseBody.message);
        });
        stompClient.subscribe("/topic/student-exam-shift-kick", function (response) {
            localStorage.setItem('kickExamShiftStudentSuccessMessage', 'Bạn đã bị kick ra khỏi phòng thi!');
            window.location.href = ApiConstant.REDIRECT_STUDENT_HOME;
        });
        stompClient.subscribe("/topic/exam-shift-start", function (response) {
            const responseBody = JSON.parse(response.body);
            showToastSuccess(responseBody.message);
            getPathFilePDFExamPaper(examShiftCode);
            handleSendMessageStartToExt();
        });
    });
}

const startCountdown = (startTime, endTime) => {
    let endTimeDate = new Date(endTime).getTime();

    let countdown = setInterval(function () {
        let now = new Date().getTime();
        let distanceToEnd = endTimeDate - now;

        if (distanceToEnd > 0) {
            let minutesToEnd = Math.floor((distanceToEnd % (1000 * 60 * 60)) / (1000 * 60));
            let secondsToEnd = Math.floor((distanceToEnd % (1000 * 60)) / 1000);
            $('#countdown').text(minutesToEnd + "m " + secondsToEnd + "s ");
        } else {
            handleSendMessageEndTimeToExt();
            clearInterval(countdown);
            showToastSuccess('Đã hết giờ làm bài thi!')
            $('#openExamPaper').prop('hidden', false);
            $('#countdown').prop('hidden', true);
            $('#examShiftPaper').prop('hidden', true);
        }
    }, 1000);
}

const handleSendMessageStartToExt = () => {
    const editorExtensionId = "dmdccbaohooloinlamfebaijhhpeegne";
    chrome.runtime.sendMessage(
        editorExtensionId,
        { active: "onTracking" },
        function (response) {
            console.log(response);
        }
    );
};

const handleSendMessageEndTimeToExt = () => {
    const editorExtensionId = "dmdccbaohooloinlamfebaijhhpeegne";
    chrome.runtime.sendMessage(
        editorExtensionId,
        { active: "stopTracking" },
        function (response) {
            console.log(response);
        }
    );
};

const updateExamPaperShiftStatus = () => {
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + '/' + examShiftCode + '/update-exam-student-status',
        success: function (responseBody) {
            window.location.href = ApiConstant.REDIRECT_STUDENT_HOME;
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi cập nhật trạng thái ca thi');
        }
    });
}

const examPaperOpenSubmit = () => {
    swal({
        title: "Xác nhận mở đề thi",
        text: "Mở đề thi?",
        icon: "info",
        buttons: true,
        dangerMode: false,
    }).then((willOpen) => {
        if (willOpen) {
            openExamPaper();
        }
    });
};

const completeExamShift = () => {
    swal({
        title: "Xác nhận hoàn thành ca thi",
        text: "Hoàn thành ca thi?",
        icon: "info",
        buttons: true,
        dangerMode: false,
    }).then((willComplete) => {
        if (willComplete) {
            updateExamPaperShiftStatus();
        }
    });
};

const openModalOpenExamPaper = () => {
    $('#modifyPasswordOpen').val('');
    $(`#modifyPasswordOpenError`).text('');
    $(`#modifyPasswordOpen`).removeClass('is-invalid');
    $('#examPaperOpenModal').modal('show');
}

// const checkOnline = () => {
//     setTimeout(doOnlineCheck, 1000);
// };
//
// const doOnlineCheck = () => {
//     console.log('onl', window.navigator.onLine)
//     if (window.navigator.onLine) {
//         showToastSuccess('Đã kết nối mạng');
//     } else {
//         showToastError('Mất kết nối mạng');
//     }
//     checkOnline();
// };

