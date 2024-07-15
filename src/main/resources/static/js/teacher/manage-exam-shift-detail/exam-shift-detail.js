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

    getFirstSupervisorId();

    getSecondSupervisorId();

    getStudents();

    getStudentRejoin();

    const addExamShiftSuccessMessage = localStorage.getItem('addExamShiftSuccessMessage');
    if (addExamShiftSuccessMessage) {
        showToastSuccess(addExamShiftSuccessMessage);
        localStorage.removeItem('addExamShiftSuccessMessage');
    }
    const joinExamShiftSuccessMessage = localStorage.getItem('joinExamShiftSuccessMessage');
    if (joinExamShiftSuccessMessage) {
        showToastSuccess(joinExamShiftSuccessMessage);
        localStorage.removeItem('joinExamShiftSuccessMessage');
    }

    connect();

    removeStudentSubmit();

    countStudentInExamShift();

    getPathFilePDFExamPaper(examShiftCode);

    $('#examShiftStart').click(function () {
        openModalStartExamShift();
    });

    // examShiftStartSubmit();

    $('#modifyExamShiftStartButton').click(function () {
        examShiftStartSubmit();
    });

});

let examShiftCode = $('#examShiftCodeCtl').text();
let stompClient = null;

const getExamShiftByCode = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode,
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


const connect = () => {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/topic/exam-shift", function (response) {
            showToastSuccess(JSON.parse(response.body).message);
            getSecondSupervisorId();
        });
        stompClient.subscribe("/topic/student-exam-shift", function (response) {
            showToastSuccess(JSON.parse(response.body).message);
            countStudentInExamShift();
            getStudents();
            getStudentRejoin();
        });
        stompClient.subscribe("/topic/student-exam-shift-kick", function (response) {
            showToastSuccess(JSON.parse(response.body).message);
            countStudentInExamShift();
            getStudents();
            getStudentRejoin();
        });
        stompClient.subscribe("/topic/student-exam-shift-rejoin", function (response) {
            showToastSuccess(JSON.parse(response.body).message);
            getStudentRejoin();
        });
        stompClient.subscribe("/topic/student-exam-shift-approve", function (response) {
            showToastSuccess(JSON.parse(response.body).message);
            getStudentRejoin();
            getStudents();
            countStudentInExamShift();
        });
        stompClient.subscribe("/topic/student-exam-shift-refuse", function (response) {
            showToastError(JSON.parse(response.body).message);
            getStudentRejoin();
        });
        stompClient.subscribe("/topic/exam-shift-start", function (response) {
            const responseBody = JSON.parse(response.body);
            showToastSuccess(responseBody.message);
        });
    });
}

const getFirstSupervisorId = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_STAFF + '/first-supervisor/' + examShiftCode,
        success: function (responseBody) {
            if (responseBody?.data) {
                const firstSupervisor = responseBody?.data;
                $('#first-supervisor-info-name').text(firstSupervisor.name + ' - ' + firstSupervisor.staffCode);
                $('#first-supervisor-info-email').text(firstSupervisor.accountFe);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin ca thi');
        }
    })
}

const getSecondSupervisorId = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_STAFF + '/second-supervisor/' + examShiftCode,
        success: function (responseBody) {
            if (responseBody?.data) {
                const secondSupervisor = responseBody?.data;
                $('#second-supervisor-info-name').text(secondSupervisor.name + ' - ' + secondSupervisor.staffCode);
                $('#second-supervisor-info-email').text(secondSupervisor.accountFe);
                $('#secondSupervisorColumn').removeAttr('hidden');
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin ca thi');
        }
    })
}

const removeStudent = (studentId) => {
    let reason = $('#modifyReason').val();
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/remove-student/' + studentId,
        data: JSON.stringify(reason),
        contentType: 'application/json',
        success: function (responseBody) {
            if (responseBody?.message) {
                const message = responseBody?.message;
                showToastSuccess(message);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi xóa sinh viên');
        }
    });
}

const approveStudent = (studentId) => {
    let reason = $('#modifyReason').val();
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/approve-student/' + studentId,
        data: JSON.stringify(reason),
        contentType: 'application/json',
        success: function (responseBody) {
            if (responseBody?.message) {
                const message = responseBody?.message;
                showToastSuccess(message);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi xóa sinh viên');
        }
    });
}

const refuseStudent = (studentId) => {
    let reason = $('#modifyReason').val();
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/refuse-student/' + studentId,
        data: JSON.stringify(reason),
        contentType: 'application/json',
        success: function (responseBody) {
            if (responseBody?.message) {
                const message = responseBody?.message;
                showToastSuccess(message);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi xóa sinh viên');
        }
    });
}

const countStudentInExamShift = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/count-student',
        success: function (responseBody) {
            if (responseBody?.data) {
                const students = responseBody?.data;
                $('#studentCount').text("Tổng Số Sinh Viên: " + students);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy số sinh viên tham gia');
        }
    });
}

const getStudents = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_STUDENT_JOIN_EXAM_SHIFT + '/' + examShiftCode,
        success: function (responseBody) {
            if (responseBody?.data) {
                const students = responseBody?.data;
                $('#studentsContainer').empty();
                let row = $('<div class="row mt-3"></div>');
                let colCounter = 0;
                students.forEach((student, index) => {
                    const col = $(`
                        <div class="col-3">
                            <div class="bg-white p-4 shadow rounded min-vh-30 w-30 position-relative">
                                <div class="user-box">
                                    <div class="avatar-lg">
                                        <img src="https://img.freepik.com/premium-photo/graphic-designer-digital-avatar-generative-ai_934475-9193.jpg"
                                         alt="image profile"
                                         class="avatar-img rounded"/>
                                    </div>
                                    <div class="u-text">
                                        <h4>${student.name}</h4>
                                        <p class="text-muted">${student.email}</p>
                                        <p class="text-muted">
                                        Join time: ${formatFromUnixTimeToHoursMinutes(student.joinTime)}</p>
                                        <button class="btn position-absolute top-0 end-0 p-2 fs-3" 
                                        onclick="openModalRemoveStudent('${student.id}')">&times;</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `);
                    row.append(col);
                    colCounter++;
                    if (colCounter === 4) {
                        $('#studentsContainer').append(row);
                        row = $('<div class="row mt-3"></div>');
                        colCounter = 0;
                    }
                });
                if (colCounter !== 0) {
                    $('#studentsContainer').append(row);
                }
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin ca thi');
        }
    });
}

const getStudentRejoin = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_STUDENT_JOIN_EXAM_SHIFT + '/rejoin/' + examShiftCode,
        success: function (responseBody) {
            if (responseBody?.data) {
                const students = responseBody?.data;
                $('#studentsRejoinContainer').empty();
                let row = $('<div class="row mt-3"></div>');
                let colCounter = 0;
                students.forEach((student, index) => {
                    const col = $(`
                        <div class="col-3">
                            <div class="bg-white p-2 shadow rounded min-vh-30">
                                <div class="user-box">
                                    <div class="avatar-lg">
                                        <img src="https://img.freepik.com/premium-photo/graphic-designer-digital-avatar-generative-ai_934475-9193.jpg"
                                         alt="image profile"
                                         class="avatar-img rounded"/>
                                    </div>
                                    <div class="u-text">
                                        <h4>${student.name}</h4>
                                        <p class="text-muted">${student.email}</p>
                                        <p class="text-muted">
                                        Join time: ${formatFromUnixTimeToHoursMinutes(student.joinTime)}</p>
                                        <button class="btn-label-secondary" 
                                                onclick="approveStudentSubmit('${student.id}')">
                                                Phê duyệt
                                        </button>
                                        <button class="btn-label-danger" 
                                                onclick="refuseStudentSubmit('${student.id}')">
                                                Từ chối
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `);
                    row.append(col);
                    colCounter++;
                    if (colCounter === 4) {
                        $('#studentsRejoinContainer').append(row);
                        row = $('<div class="row mt-3"></div>');
                        colCounter = 0;
                    }
                });
                if (colCounter !== 0) {
                    $('#studentsRejoinContainer').append(row);
                }
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin ca thi');
        }
    });
}

const examShiftStart = () => {
    const passwordStart = $('#modifyPasswordStart').val()
    $.ajax({
        type: "PUT",
        contentType: 'text/plain',
        data: passwordStart,
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/start',
        success: function (responseBody) {
            fetchFilePDFExamPaper(responseBody?.data?.fileId);
            startCountdown(responseBody?.data?.startTime, responseBody?.data?.endTime);
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

const updateExamPaperShiftStatus = () => {
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_PAPER_SHIFT + '/' + examShiftCode,
        success: function (responseBody) {
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi cập nhật trạng thái ca thi');
        }
    });

}

const updateExamShiftStatus = () => {
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/update-status',
        success: function (responseBody) {
            showToastSuccess('Đã hết giờ làm bài thi!')
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi cập nhật trạng thái ca thi');
        }
    });

}

const getPathFilePDFExamPaper = (examShiftCode) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + "/path",
        data: {
            examShiftCode: examShiftCode
        },
        success: function (responseBody) {
            if (responseBody?.data) {
                const fileId = responseBody?.data;
                fetchFilePDFExamPaper(fileId);
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
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + "/file",
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

const examShiftStartSubmit = () => {
    swal({
        title: "Xác nhận bắt đầu ca thi",
        text: "Phát đề thi?",
        icon: "info",
        buttons: true,
        dangerMode: false,
    }).then((willApprove) => {
        if (willApprove) {
            examShiftStart();
            $('#examShiftStartModal').modal('hide');
        }
    });
};

const openModalStartExamShift = () => {
    $('#modifyPasswordStart').val('');
    $(`#passwordStartError`).text('');
    $(`#modifyPasswordStart`).removeClass('is-invalid');
    $('#examShiftStartModal').modal('show');
}

const openModalRemoveStudent = (studentId) => {
    $('#studentIdRemove').val(studentId);
    $('#modifyReason').val('');
    $(`#examReasonError`).text('');
    $(`#modifyReason`).removeClass('is-invalid');
    $('#removeStudentModal').modal('show');
}

const removeStudentSubmit = () => {
    $('#modifyRemoveStudentButton').click(function () {
        const studentId = $('#studentIdRemove').val();
        if ($('#modifyReason').val() === '') {
            $(`#modifyReason`).addClass('is-invalid');
            $(`#examReasonError`).text('Lý do không được để trống');
        } else {
            removeStudent(studentId);
            $('#removeStudentModal').modal('hide');
        }
    });
};

const approveStudentSubmit = (studentId) => {
    if (studentId !== null) {
        swal({
            title: "Xác nhận phê duyệt sinh viên",
            text: "Phê duyệt sinh viên vào phòng thi?",
            icon: "info",
            buttons: true,
            dangerMode: false,
        }).then((willApprove) => {
            if (willApprove) {
                approveStudent(studentId);
            }
        });
    }
};

const refuseStudentSubmit = (studentId) => {
    if (studentId !== null) {
        swal({
            title: "Xác nhận từ chối sinh viên",
            text: "Từ chối sinh viên vào phòng thi?",
            icon: "info",
            buttons: true,
            dangerMode: false,
        }).then((willApprove) => {
            if (willApprove) {
                refuseStudent(studentId);
            }
        });
    }
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
            updateExamPaperShiftStatus();
            updateExamShiftStatus();
            $('#countdown').text("Đã kết thúc!");
        }
    }, 1000);
}