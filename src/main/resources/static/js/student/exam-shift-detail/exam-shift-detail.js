const userInfo = getExamDistributionInfo();

const pdfjsLib = window["pdfjs-dist/build/pdf"];
let pdfDoc = null;
let pageNum = 1;
let pageRendering = false;
let pageNumPending = null;
const scale = 1.5;
const $pdfCanvas = $("#pdf-canvas")[0];
const ctx = $pdfCanvas.getContext("2d");

let pdfDocExamRule = null;
let pageNumExamRule = 1;
let pageRenderingExamRule = false;
let pageNumPendingExamRule = null;
const scaleExamRule = 1.5;
const $pdfCanvasExamRule = $("#pdf-canvas-exam-rule")[0];
const ctxExamRule = $pdfCanvasExamRule.getContext("2d");

document.addEventListener('contextmenu', (e) => {
    e.preventDefault()
});

document.onkeydown = (e) => {
    const blockedKeys = ['F12', 'KeyI', 'KeyJ', 'KeyC', 'KeyU'];

    if (blockedKeys.includes(e.code) && (e.ctrlKey && e.shiftKey) || (e.ctrlKey && e.code === 'KeyU')) {
        e.preventDefault();
        return false;
    }
}

$(document).ready(function () {

    if (devtools.isOpen) {
        while (true) {
            console.log("Access denied")
        }
    }

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

    $('#examRuleOpen').click(function () {
        openModalExamRule();
    });

});

const openModalExamRule = () => {
    $('#examRuleOpenModal').modal('show');
}

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
                const fileId = examShift.pathExamRule;
                $('#examShift').text(
                    "Phòng thi: " + examShift.subjectName
                    + " - Lớp: " + examShift.classSubjectCode
                );
                fetchFilePDFExamRule(fileId);
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    });
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
                const allowOnline = responseBody?.data?.allowOnline;
                if (fileId !== null) {
                    fetchFilePDFExamPaper(fileId);
                }
                if (startTime !== null && endTime !== null && allowOnline !== null) {
                    startCountdown(startTime, endTime, allowOnline);
                }
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    });
}

// Exam paper
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
            localStorage.setItem('pdfData', pdfData)
            pdfjsLib
                .getDocument({data: pdfData})
                .promise.then(function (pdfDoc_) {
                pdfDoc = pdfDoc_;
                $("#total-page").text(pdfDoc.numPages);

                renderPage(pdfDoc, pageNum, '#page-num', pageRendering, pageNumPending, scale, $pdfCanvas, ctx);
                showViewAndPagingPdf(pdfDoc.numPages, '#pdf-viewer', '#paging-pdf');

                hideLoading();
            });
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
            hideLoading();
        }
    });
};

$("#prev-page").on("click", function () {
    if (pageNum <= 1) {
        return;
    }
    pageNum--;
    queueRenderPage(-1, pdfDoc, pageNum, '#page-num', pageRendering, pageNumPending, scale, $pdfCanvas, ctx);
});

$("#next-page").on("click", function () {
    if (pageNum >= pdfDoc.numPages) {
        return;
    }
    pageNum++;
    queueRenderPage(1, pdfDoc, pageNum, '#page-num', pageRendering, pageNumPending, scale, $pdfCanvas, ctx);
});

const getStartTimeEndTimeExamPaper = (examShiftCode) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + "/start-time-end-time",
        data: {
            examShiftCode: examShiftCode
        },
        success: function (responseBody) {
            if (responseBody?.data) {
                const startTime = responseBody?.data?.startTime;
                const endTime = responseBody?.data?.endTime;
                const allowOnline = responseBody?.data?.allowOnline;
                if (startTime !== null && endTime !== null && allowOnline !== null) {
                    startCountdown(startTime, endTime, allowOnline);
                }
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    });
}

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
            }
        }
    });
};

//Exam rule
const fetchFilePDFExamRule = (fileId) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + "/file-exam-rule",
        data: {
            fileId: fileId
        },
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));
            pdfjsLib
                .getDocument({data: pdfData})
                .promise.then(function (pdfDoc_) {
                pdfDocExamRule = pdfDoc_;
                $("#total-page-exam-rule").text(pdfDocExamRule.numPages);

                renderPage(pdfDocExamRule, pageNumExamRule, '#page-num-exam-rule', pageRenderingExamRule,
                    pageNumPendingExamRule, scaleExamRule, $pdfCanvasExamRule, ctxExamRule);
                showViewAndPagingPdf(pdfDocExamRule.numPages, '#pdf-viewer-exam-rule', '#paging-pdf-exam-rule');
            });
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    });
};

$("#prev-page-exam-rule").on("click", function () {
    if (pageNumExamRule <= 1) {
        return;
    }
    pageNumExamRule--;
    queueRenderPage(-1, pdfDocExamRule, pageNumExamRule, '#page-num-exam-rule', pageRenderingExamRule,
        pageNumPendingExamRule, scaleExamRule, $pdfCanvasExamRule, ctxExamRule);
});

$("#next-page-exam-rule").on("click", function () {
    if (pageNumExamRule >= pdfDocExamRule.numPages) {
        return;
    }
    pageNumExamRule++;
    queueRenderPage(1, pdfDocExamRule, pageNumExamRule, '#page-num-exam-rule', pageRenderingExamRule,
        pageNumPendingExamRule, scaleExamRule, $pdfCanvasExamRule, ctxExamRule);
});

const connect = () => {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT, function (response) {
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_KICK, function (response) {
            const responseBody = JSON.parse(response.body);
            const studentId = responseBody.message.split(' - ')[1];
            console.log(studentId)
            console.log(userInfo.userId)
            if (studentId === userInfo.userId) {
                localStorage.setItem('kickExamShiftStudentSuccessMessage', 'Bạn đã bị kick ra khỏi ca thi!');
                window.location.href = ApiConstant.REDIRECT_STUDENT_EXAM_SHIFT;
            }
        });
        stompClient.subscribe(TopicConstant.TOPIC_EXAM_SHIFT_START, function (response) {
            const responseBody = JSON.parse(response.body);
            showToastSuccess(responseBody.message);
            getPathFilePDFExamPaper(examShiftCode);
        });
        stompClient.subscribe(TopicConstant.TOPIC_EXAM_SHIFT_START_TIME, function (response) {
            const responseBody = JSON.parse(response.body);
            showToastSuccess(responseBody.message);
            getStartTimeEndTimeExamPaper(examShiftCode);
        });
    });
};

let isTabClosing = false;

document.addEventListener("visibilitychange", function () {
    if (document.visibilityState === "hidden") {
        isTabClosing = true;
    }
});

window.addEventListener("beforeunload", function (event) {
    if (isTabClosing) {
        refreshJoinRoom();
        isTabClosing = false;
    }
});

const refreshJoinRoom = () => {
    $.ajax({
        type: "PUT",
        contentType: "application/json",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + "/refresh-join-room",
        data: JSON.stringify({
            studentId: userInfo.userId,
            examShiftCode: examShiftCode
        }),
        success: function (responseBody) {},
        error: function (error) {}
    });
}

let checkInternetInterval;

const startCountdown = (startTime, endTime, allowOnline) => {
    let endTimeDate = new Date(endTime).getTime();
    let checkTime = new Date(startTime).getTime() + 60 * 1000;
    let hasChecked = false;

    let countdown = setInterval(function () {
        let now = new Date().getTime();
        let distanceToEnd = endTimeDate - now;

        if (now >= checkTime && !hasChecked) {
            hasChecked = true;
            if (!allowOnline) {
                checkInternetInterval = setInterval(checkInternetConnection, 1000);
            }
        }

        if (distanceToEnd > 0) {
            let minutesToEnd = Math.floor((distanceToEnd % (1000 * 60 * 60)) / (1000 * 60));
            let secondsToEnd = Math.floor((distanceToEnd % (1000 * 60)) / 1000);
            $('#countdown').text(minutesToEnd + " phút " + secondsToEnd + " giây");
        } else {
            if (checkInternetInterval) {
                clearInterval(checkInternetInterval);
                $('#overlay').hide();
            }
            clearInterval(countdown);
            showToastSuccess('Đã hết giờ làm bài thi!')
            $('#openExamPaper').prop('hidden', false);
            $('#countdown').prop('hidden', true);
            $('#examShiftPaper').prop('hidden', true);
            updateExamPaperShiftStatus();
        }
    }, 1000);
}

const updateExamPaperShiftStatus = () => {
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + '/' + examShiftCode + '/update-exam-student-status',
        success: function (responseBody) {
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
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
            window.location.href = ApiConstant.REDIRECT_STUDENT_EXAM_SHIFT;
        }
    });
};

const openModalOpenExamPaper = () => {
    $('#modifyPasswordOpen').val('');
    $(`#modifyPasswordOpenError`).text('');
    $(`#modifyPasswordOpen`).removeClass('is-invalid');
    $('#examPaperOpenModal').modal('show');
}

function checkInternetConnection() {
    fetch('https://www.google.com', {mode: 'no-cors'})
        .then(response => {
            if (!response.ok) {
                // $('body *').css('background-color', 'red');
                $('#overlay').show();
            } else {
                // $('body *').css('background-color', '');
                $('#overlay').hide();
            }
        })
        .catch(() => {
            // $('body *').css('background-color', '');
            $('#overlay').hide();
        })
}

