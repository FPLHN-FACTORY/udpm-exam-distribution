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
let idRunJobCheckExtEnable = "";

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

window.addEventListener('offline', function () {
    showLoading();
    showToastDisConnectNetwork('Mất kết nối mạng!');
})

window.addEventListener('online', function () {
    hideLoading();
})

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

    if (getEnableExtLocalStorage() !== "false") {
        getPathFilePDFExamPaper(examShiftCode);
    } else {
        $('#openExamPaperTabTracker').prop('hidden', false);

        $('#countdown').prop('hidden', true);
        $('#examShiftPaper').prop('hidden', true);
    }

    connect();

    $('#openExamPaper').click(function () {
        openModalOpenExamPaper(0);
    });

    $('#openExamPaperTabTracker').click(function () {
        openModalOpenExamPaper(1);
    });

    $('#modifyExamPaperOpenButton').click(function () {
        examPaperOpenSubmit();
    });

    $('#modifyExamPaperOpenButtonTabTracker').click(function () {
        examPaperOpenSubmitTabTracker();
    });

    $('#completeExamShift').click(function () {
        completeExamShift();
    });

    $('#examRuleOpen').click(function () {
        openModalExamRule();
    });

    // fetchExamShiftValidAndRunJob();
});

const fetchExamShiftValidAndRunJob = () => {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: ApiConstant.API_STUDENT_TRACKER + '/check-room-is-valid',
        data: JSON.stringify({
            email: getExamDistributionInfo().userEmail,
            roomCode: examShiftCode
        }),
        success: (responseBody) => {
            runJobCheckExtEnable();
        },
        error: (error) => {
        }
    })
};

const getEnableExtLocalStorage = () => localStorage.getItem(EXAM_DISTRIBUTION_IS_ENABLE_EXT);

const setEnableExtLocalStorage = (value) => {
    localStorage.setItem(EXAM_DISTRIBUTION_IS_ENABLE_EXT, value);
}

const runJobCheckExtEnable = async () => {
    idRunJobCheckExtEnable = setInterval(async () => {
        if (!await isExtEnable()) {
            if (getEnableExtLocalStorage() !== "false") {
                $('#openExamPaperTabTracker').prop('hidden', false);

                $('#countdown').prop('hidden', true);
                $('#examShiftPaper').prop('hidden', true);

                fetchSaveTrackUrl();

                stopRunJobCheckExtEnable();
            }
        }
    }, 5000);
};

const fetchSaveTrackUrl = () => {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: ApiConstant.API_STUDENT_TRACKER + '/save-track-url',
        data: JSON.stringify({
            email: getExamDistributionInfo().userEmail,
            roomCode: examShiftCode,
            url: "Sinh Viên đã tắt extension Tab Tracker",
            isDisable: true
        }),
        success: (responseBody) => {
        },
        error: (error) => {
        }
    })
}

const stopRunJobCheckExtEnable = () => {
    clearInterval(idRunJobCheckExtEnable);
    handleSendMessageEndTimeToExt();
    setEnableExtLocalStorage("false");
}

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
                $('#examShiftCode').text("Phòng thi - Mã tham gia: " + examShift.examShiftCode);
                fetchFilePDFExamRule(examShift.pathExamRule);
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
                fetchFilePDFExamPaper(fileId);
                startCountdown(startTime, endTime);
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

const openExamPaperTabTracker = () => {
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
                setEnableExtLocalStorage("true");
                handleSendMessageStartToExt();

                $('#openExamPaperTabTracker').prop('hidden', true);
                $('#countdown').prop('hidden', false);
                $('#examShiftPaper').prop('hidden', false);

                runJobCheckExtEnable();

                $('#examPaperOpenModal').modal('hide');
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
            const responseBody = JSON.parse(response.body);
            showToastSuccess(responseBody.message);
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_KICK, function (response) {
            localStorage.setItem('kickExamShiftStudentSuccessMessage', 'Bạn đã bị kick ra khỏi phòng thi!');
            window.location.href = ApiConstant.REDIRECT_STUDENT_EXAM_SHIFT;
        });
        stompClient.subscribe(TopicConstant.TOPIC_EXAM_SHIFT_START, async function (response) {
            if (!await isExtEnable()) {
                showToastError("Bạn chưa cài đặt Extension Tab-Tracker");
                kickStudentUnInstallExtension();
            } else {
                const responseBody = JSON.parse(response.body);
                showToastSuccess(responseBody.message);
                getPathFilePDFExamPaper(examShiftCode);
                handleSendMessageStartToExt();
                runJobCheckExtEnable();
            }
        });
    });
};

const isExtEnable = async () => {
    return await checkExtensionInstalled();
};

const kickStudentUnInstallExtension = () => {
    $.ajax({
        type: "POST",
        url: ApiConstant.API_STUDENT_JOIN_EXAM_SHIFT + "/kick-uninstall-ext/" + examShiftCode,
        success: function (responseBody) {
            window.location.href = ApiConstant.REDIRECT_STUDENT_EXAM_SHIFT;
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi cập nhật trạng thái ca thi');
        }
    });
};

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
            clearInterval(countdown);
            showToastSuccess('Đã hết giờ làm bài thi!')
            $('#openExamPaper').prop('hidden', false);
            $('#countdown').prop('hidden', true);
            $('#examShiftPaper').prop('hidden', true);
            handleSendMessageEndTimeToExt();
            stopRunJobCheckExtEnable();
        }
    }, 1000);
}

const updateExamPaperShiftStatus = () => {
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + '/' + examShiftCode + '/update-exam-student-status',
        success: function (responseBody) {
            window.location.href = ApiConstant.REDIRECT_STUDENT_EXAM_SHIFT;
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

const examPaperOpenSubmitTabTracker = () => {
    swal({
        title: "Xác nhận mở đề thi",
        text: "Mở đề thi?",
        icon: "info",
        buttons: true,
        dangerMode: false,
    }).then((willOpen) => {
        if (willOpen) {
            openExamPaperTabTracker();
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

const openModalOpenExamPaper = async (status) => {
    if (!await isExtEnable()) {
        showToastError("Bạn chưa cài đặt Extension Tab-Tracker");
    } else {
        $("#modifyExamPaperOpenButton").prop("hidden", status !== 0);
        $("#modifyExamPaperOpenButtonTabTracker").prop("hidden", status !== 1);

        $('#modifyPasswordOpen').val('');
        $(`#modifyPasswordOpenError`).text('');
        $(`#modifyPasswordOpen`).removeClass('is-invalid');
        $('#examPaperOpenModal').modal('show');
    }
}

