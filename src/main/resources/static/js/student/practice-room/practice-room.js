// START: state
const pdfjsLibDetail = window["pdfjs-dist/build/pdf"];
let pdfDocDetail = null;
let pageNumDetail = 1;
let pageRenderingDetail = false;
let pageNumPendingDetail = null;
const scaleDetail = 2.0;
const $pdfCanvasDetail = $("#pdf-canvas-detail")[0];
const ctxDetail = $pdfCanvasDetail.getContext("2d");

let interval = null;

const examDistributionInfo = getExamDistributionInfo();

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

    resetFormJoinPracticeRoom();

    removeFormJoinError();

    $("#prev-page-detail").on("click", function () {
        if (pageNumDetail <= 1) {
            return;
        }
        pageNumDetail--;
        queueRenderPageDetail(pageNumDetail);
    });

    $("#next-page-detail").on("click", function () {
        if (pageNumDetail >= pdfDocDetail.numPages) {
            return;
        }
        pageNumDetail++;
        queueRenderPageDetail(pageNumDetail);
    });

    $('#modifyPracticeRoomJoinButton').on('click', function () {
        joinPracticeRoom();
    });

    let startTime = localStorage.getItem('startTime');
    let mockExamPaper = localStorage.getItem('mockExamPaper');
    let practiceRoom = getDataLocalStorage();
    if (startTime && mockExamPaper && practiceRoom) {
        const sixtyMinutes = 60 * 60 * 1000;
        if (new Date().getTime() - startTime > sixtyMinutes) {
            joinPracticeRoom(true, practiceRoom?.practiceRoomCodeJoin, practiceRoom?.passwordJoin);
        } else {
            showTimeMockExam(startTime);
            handleShowMockContainer();
            showPDF(mockExamPaper.toString());
        }
    } else {
        handleShowSectionContainer();
    }
});

function getDataLocalStorage() {
    const myObjectString = localStorage.getItem('practiceRoom');
    if (myObjectString) {
        let res = JSON.parse(myObjectString);
        return res;
    }
    return '';
}

const joinPracticeRoom = (isTakeNewMockExamPaper = true,
                          practiceRoomCode = '',
                          password = '') => {

    const practiceRoom = {
        studentId: examDistributionInfo.userId,
        practiceRoomCodeJoin: practiceRoomCode ? practiceRoomCode : $('#modifyPracticeRoomCodeJoin').val()?.trim(),
        passwordJoin: password ? password : $('#modifyPasswordJoin').val()?.trim(),
        isTakeNewMockExamPaper: isTakeNewMockExamPaper,
        key: localStorage.getItem('key')
    }
    showLoading();
    $.ajax({
        type: "POST",
        url: ApiConstant.API_STUDENT_PRACTICE_ROOM,
        contentType: "application/json",
        data: JSON.stringify(practiceRoom),
        success: function (responseBody) {
            if (responseBody?.data && responseBody?.status === 'OK') {
                const data = responseBody?.data;
                if (data?.isTakeNewMockExamPaper) {
                    const startTime = new Date().getTime();
                    localStorage.setItem('startTime', startTime.toString());
                    showTimeMockExam(startTime);
                    localStorage.setItem('mockExamPaper', data?.file?.data.toString())
                }
                const practiceRoom = {
                    practiceRoomCodeJoin: data.practiceRoomCode,
                    passwordJoin: data.password,
                    isTakeNewMockExamPaper: data.isTakeNewMockExamPaper,
                    key: data.key
                };
                const practiceRoomString = JSON.stringify(practiceRoom);
                localStorage.setItem('practiceRoom', practiceRoomString);

                //show mock exam paper
                handleShowMockContainer();
                showPDF(data?.file?.data);
            }
            hideLoading();
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}`).addClass('is-invalid');
                });
            } else if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message)
            }
            handleLeftRoom();
            hideLoading();
        }
    });
}

function showTimeMockExam(startTime) {
    clearInterval(interval);
    const sixtyMinutes = 60 * 60 * 1000;
    interval = setInterval(function () {
        const currentTime = new Date().getTime();
        const timeElapsed = currentTime - startTime;
        const timeRemaining = sixtyMinutes - timeElapsed;
        if (timeRemaining <= 0 || formatTimeMockExam(timeRemaining) === '00:00') {
            let practiceRoom = getDataLocalStorage();
            if (practiceRoom) {
                joinPracticeRoom(true, practiceRoom?.practiceRoomCodeJoin, practiceRoom?.passwordJoin);
            } else {
                handleShowSectionContainer();
            }
            clearInterval(this);
        } else {
            $('#timeMockExam').text(formatTimeMockExam(timeRemaining));
        }
    }, 500);
}

function formatTimeMockExam(time) {
    const d = new Date(Number(time));
    const minutes = String(d.getMinutes()).padStart(2, "0");
    const sec = String(d.getSeconds()).padStart(2, "0");
    return `${minutes}:${sec}`;
}

function showPDF(data) {
    const pdfData = Uint8Array.from(atob(data), c => c.charCodeAt(0));
    pdfjsLibDetail
        .getDocument({data: pdfData})
        .promise.then(function (pdfDoc_) {
        pdfDocDetail = pdfDoc_;
        $("#total-page-detail").text(pdfDocDetail.numPages);

        renderPageDetail(pageNumDetail);
        showViewAndPagingPdfDetail(pdfDocDetail.numPages);

        hideLoading();
    });
}

const showViewAndPagingPdfDetail = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
    $("#pdf-viewer-detail").prop("hidden", false);
    if (totalPage > 1) {
        $("#paging-pdf-detail").prop("hidden", false);
    }
};

// Render the page
function renderPageDetail(num) {
    pageRenderingDetail = true;
    pdfDocDetail.getPage(num).then(function (page) {
        const viewport = page.getViewport({scale: scaleDetail});
        $pdfCanvasDetail.height = viewport.height;
        $pdfCanvasDetail.width = viewport.width;

        const renderContext = {
            canvasContext: ctxDetail,
            viewport: viewport,
        };
        const renderTask = page.render(renderContext);

        renderTask.promise.then(function () {
            pageRenderingDetail = false;
            if (pageNumPendingDetail !== null) {
                renderPageDetail(pageNumPendingDetail);
                pageNumPendingDetail = null;
            }
        });
    });

    $("#page-num-detail").text(num);
}

function handleShowMockContainer() {
    let mockContainer = $('#mockExamPaperContainer');
    let sectionContainer = $('#sectionContainer');
    if (mockContainer.hasClass('d-none')) {
        mockContainer.removeClass('d-none');
        mockContainer.addClass('d-flex');
        sectionContainer.addClass('d-none');
    }
}

function handleShowSectionContainer() {
    $('#modifyPracticeRoomCodeJoin').val('');
    $('#modifyPasswordJoin').val('');
    let mockContainer = $('#mockExamPaperContainer');
    let sectionContainer = $('#sectionContainer');
    if (sectionContainer.hasClass('d-none')) {
        mockContainer.addClass('d-none');
        sectionContainer.removeClass('d-none');
    }
}

const resetFormJoinPracticeRoom = () => {
    $('#modifyPracticeRoomCodeJoin').val('');
    $('#modifyPasswordJoin').val('');
}

const removeFormJoinError = (id) => {
    $('#modifyPracticeRoomCodeJoin').removeClass('is-invalid');
    $('#practiceRoomCodeJoinError').text('');
    $('#modifyPasswordJoin').removeClass('is-invalid');
    $('#passwordJoinError').text('');
};

// Render the page
function renderPageDetail(num) {
    pageRenderingDetail = true;
    pdfDocDetail.getPage(num).then(function (page) {
        const viewport = page.getViewport({scale: scaleDetail});
        $pdfCanvasDetail.height = viewport.height;
        $pdfCanvasDetail.width = viewport.width;

        const renderContext = {
            canvasContext: ctxDetail,
            viewport: viewport,
        };
        const renderTask = page.render(renderContext);

        renderTask.promise.then(function () {
            pageRenderingDetail = false;
            if (pageNumPendingDetail !== null) {
                renderPageDetail(pageNumPendingDetail);
                pageNumPendingDetail = null;
            }
        });
    });

    $("#page-num-detail").text(num);
}

function queueRenderPageDetail(num) {
    if (pageRenderingDetail) {
        pageNumPendingDetail = num;
    } else {
        renderPageDetail(num);
    }
}

function handleLeftRoom() {
    localStorage.removeItem('startTime');
    localStorage.removeItem('mockExamPaper');
    localStorage.removeItem('practiceRoom');
    handleShowSectionContainer();
}