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
        examShiftStartSubmit();
    });

    $('#examShiftStartTime').click(function () {
        examShiftStartTimeSubmit();
    });

    $('#completeExamShift').click(function () {
        completeExamShift();
    });

    $('#modifyExamTimeButton').click(function () {
        approveStudentWhenStartTimeSubmit($('#studentIdSetExamTime').val());
    });

    onChangePageSize();

});

let examShiftCode = $('#examShiftCodeCtl').text();
let currentStudentViolationId = "";
let stompClient = null;

const getExamShiftByCode = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode,
        success: function (responseBody) {
            if (responseBody?.data) {
                const examShift = responseBody?.data;
                $('#examShift').text(
                    "Phòng thi: " + examShift.subjectName
                    + " - Lớp: " + examShift.classSubjectCode
                );
                $('#examShiftCodePassword').text(
                    "Mã phòng: " + examShift.examShiftCode
                    + " - Mật khẩu: " + examShift.password
                );
                if (examShift.examPaperPassword !== null) {
                    $('#examPaperPassword').prop('hidden', false);
                    $('#examPaperPassword').text("Mật khẩu mở đề: " + examShift.examPaperPassword);
                }
                fetchFilePDFExamRule(examShift.pathExamRule);
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
        }
    })
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
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
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
                secondSupervisorId = secondSupervisor.id;
                $('#second-supervisor-info-name').text(secondSupervisor.name + ' - ' + secondSupervisor.staffCode);
                $('#second-supervisor-info-email').text(secondSupervisor.accountFe);
                $('#secondSupervisorColumn').removeAttr('hidden');
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
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
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
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
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    });
}

const approveStudentWhenStartTime = (studentId) => {
    let examTime = $('#modifyExamTime').val();
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/approve-student-when-start-time/' + studentId,
        data: JSON.stringify({examTime: examTime}),
        contentType: 'application/json',
        success: function (responseBody) {
            if (responseBody?.message) {
                const message = responseBody?.message;
                showToastSuccess(message);
                $('#examTimeModal').modal('hide');
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
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
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
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
                $('#studentCount').text("Tổng số: " + students);
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
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
                            <div class="${student.checkLogin === false ? "bg-secondary-subtle" : "bg-white"} p-4 shadow rounded min-vh-30 w-30 position-relative">
                                <div class="user-box">
                                    <div class="avatar-lg">
                                        <img src="${student.picture}"
                                         alt="image profile"
                                         class="avatar-img rounded"/>
                                    </div>
                                    <div class="u-text" style="max-height: 76px; overflow-y: scroll; scrollbar-width: none;">
                                        <h4>${student.name}</h4>
                                        <p class="text-muted">${student.email}</p>
                                        <p class="text-muted">
                                        Thời gian tham gia: ${formatFromUnixTimeToHoursMinutes(student.joinTime)}</p>
                                        ${student.checkLogin === false ?
                                            `<p class="text-muted">
                                                Thời gian thoát: ${formatFromUnixTimeToHoursMinutes(student.leaveTime)}</p>` :
                                            ""
                                        }
                                        <button class="btn-label-danger position-absolute top-0 end-0 p-1 fs-3 lh-1" 
                                        style="border-bottom-left-radius: 5px; border-top-right-radius: 5px;"
                                        onclick="openModalRemoveStudent('${student.id}')">&times;</button>
                                        ${student.isViolation === 0 ?
                        `<button onclick="handleOpenModalStudentViolation('${student.id}')" class="btn position-absolute bottom-0 end-0 p-2 fs-3">
                                                <i class="fa-regular fa-rectangle-list"></i>
                                             </button>` :
                        ""
                    }
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
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
        }
    });
}

const handleOpenModalStudentViolation = (studentId) => {
    currentStudentViolationId = studentId;
    fetchListViolationStudent();
    $("#violationModal").modal("show");
};

const fetchListViolationStudent = (
    page = 1,
) => {
    const params = {
        page: page,
        size: $('#pageSizeViolation').val(),
        studentId: currentStudentViolationId,
        examShiftCode: examShiftCode
    };

    let url = ApiConstant.API_TEACHER_TRACK_HISTORY + '?';

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            console.log(responseBody);
            const responseData = responseBody?.data?.data;
            if (responseData.length === 0) {
                $('#violationTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const violations = responseData.map((violation, index) => {
                return `<tr>
                            <td style="width: 5%">${violation.orderNumber}</td>
                            <td style="width: 80%">${violation.url}</td>
                            <td style="width: 15%">${formatDateTime(violation.timeViolation)}</td>
                        </tr>`;
            });
            $('#violationTableBody').html(violations);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu bộ môn');
        }
    });
};

const createPagination = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePage(${currentPage - 1})">
                            Trước
                        </a>
                     </li>`;
    } else {
        paginationHtml += `
                <li class="page-item disabled">
                    <a class="page-link" href="#">
                        Trước
                    </a>
                </li>
        `;
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<li class="page-item active"><a class="page-link text-white" href="#">${i}</a></li>`;
        } else {
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePage(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePage(${currentPage + 1})">
                          Sau
                    </a>
                </li>
`;
    } else {
        paginationHtml += `
            <li class="page-item disabled">
                <a class="page-link" href="#">
                    Sau
                </a>
            </li>
`;
    }

    $('#paginationViolation').html(paginationHtml);
};

const changePage = (page) => {
    fetchListViolationStudent(page);
};

const onChangePageSize = () => {
    $("#pageSizeViolation").on('change', () => {
        fetchListViolationStudent();
    });
};

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
                                        <img src="${student.picture}"
                                         alt="image profile"
                                         class="avatar-img rounded"/>
                                    </div>
                                    <div class="u-text" style="max-height: 76px; overflow-y: scroll; scrollbar-width: none;">
                                        <h4>${student.name}</h4>
                                        <p class="text-muted">${student.email}</p>
                                        <p class="text-muted">
                                        Join time: ${formatFromUnixTimeToHoursMinutes(student.joinTime)}</p>
                                        <button style="border-radius: 5px;"
                                                class="btn-label-warning" 
                                                onclick="${student.startTime == null && student.endTime == null
                    && student.examPaperShiftStartTime != null && student.examPaperShiftEndTime != null
                        ? 'openModalSetExamTime'
                        : 'approveStudentSubmit'}('${student.id}')">
                                                Phê duyệt
                                        </button>
                                        <button style="border-radius: 5px;"
                                                class="btn-label-danger" 
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
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
        }
    });
}

const examShiftStart = () => {
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/start',
        success: function (responseBody) {
            const examPaper = responseBody?.data;
            if (examPaper.password !== null) {
                $('#examPaperPassword').text('Mật khẩu mở đề: ' + examPaper.password);
                $('#examPaperPassword').prop('hidden', false);
                downloadExamPaper(examPaper.fileId);
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
        }
    });
};

const examShiftStartTime = () => {
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/start-time',
        success: function (responseBody) {
            $('#displayCountdown').prop('hidden', false);
            $('#displayCountdown').addClass('d-flex');
            startCountdown(responseBody?.data?.startTime, responseBody?.data?.endTime);
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
        }
    });
};

const connect = () => {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe(TopicConstant.TOPIC_EXAM_SHIFT, function (response) {
            const responseMessage = JSON.parse(response.body).message;
            if (responseMessage.split(' - ')[1] === examShiftCode) {
                showToastSuccess(responseMessage.split(' - ')[0]);
                getSecondSupervisorId();
            }
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT, function (response) {
            const responseMessage = JSON.parse(response.body).message;
            if (responseMessage.split(' - ')[1] === examShiftCode) {
                showToastSuccess(responseMessage.split(' - ')[0]);
                countStudentInExamShift();
                getStudents();
                getStudentRejoin();
            }
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_KICK, function (response) {
            const responseMessage = JSON.parse(response.body).message;
            if (responseMessage.split(' - ')[2] === examShiftCode) {
                showToastSuccess(JSON.parse(response.body).message.split(' - ')[0]);
                countStudentInExamShift();
                getStudents();
                getStudentRejoin();
            }
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REJOIN, function (response) {
            const responseMessage = JSON.parse(response.body).message;
            if (responseMessage.split(' - ')[1] === examShiftCode) {
                showToastSuccess(responseMessage.split(' - ')[0]);
                getStudentRejoin();
            }
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_APPROVE, function (response) {
            const responseMessage = JSON.parse(response.body).message;
            if (responseMessage.split(' - ')[2] === examShiftCode) {
                showToastSuccess(responseMessage.split(' - ')[0]);
                getStudentRejoin();
                getStudents();
                countStudentInExamShift();
            }
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REFUSE, function (response) {
            const responseMessage = JSON.parse(response.body).message;
            if (responseMessage.split(' - ')[2] === examShiftCode) {
                showToastSuccess(responseMessage.split(' - ')[0]);
                getStudentRejoin();
            }
        });
        stompClient.subscribe(TopicConstant.TOPIC_EXAM_SHIFT_START, function (response) {
            const responseMessage = JSON.parse(response.body).message;
            if (responseMessage.split(' - ')[1] === examShiftCode) {
                showToastSuccess(responseMessage.split(' - ')[0]);
                getPathFilePDFExamPaper(examShiftCode);
            }
        });
        stompClient.subscribe(TopicConstant.TOPIC_EXAM_SHIFT_START_TIME, function (response) {
            const responseMessage = JSON.parse(response.body).message;
            if (responseMessage.split(' - ')[1] === examShiftCode) {
                showToastSuccess(responseMessage.split(' - ')[0]);
            }
        });
        // stompClient.subscribe(TopicConstant.TOPIC_TRACK_STUDENT, function (response) {
        //     const responseBody = JSON.parse(response.body);
        //     showToastError(responseBody.message);
        //     getStudents();
        // });
        // stompClient.subscribe(TopicConstant.TOPIC_STUDENT_REMOVE_TAB, function (response) {
        //     const responseBody = JSON.parse(response.body);
        //     showToastError(responseBody.message);
        //     getStudents();
        // });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_REMOVE_TAB, function (response) {
            const responseBody = JSON.parse(response.body);
            if (responseBody.message === examShiftCode) {
                getStudents();
            }
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_REMOVE_TAB_REJOIN, function (response) {
            const responseBody = JSON.parse(response.body);
            if (responseBody.message === examShiftCode) {
                getStudents();
            }
        });
    });
}

const updateExamPaperShiftStatus = () => {
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_PAPER_SHIFT + '/' + examShiftCode,
        success: function (responseBody) {
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
        }
    });
}

const updateExamShiftStatus = () => {
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/' + examShiftCode + '/update-status',
        success: function (responseBody) {
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
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
                const fileId = responseBody?.data?.path;
                const startTime = responseBody?.data?.startTime;
                const endTime = responseBody?.data?.endTime;
                const examShiftStatus = responseBody?.data?.examShiftStatus;
                if (fileId !== null && examShiftStatus === 1) {
                    fetchFilePDFExamPaper(fileId);
                }
                if (startTime !== null && endTime !== null) {
                    startCountdown(startTime, endTime);
                }
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
        }
    });
}

const downloadExamPaper = (fileId) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + "/file",
        data: {
            fileId: fileId
        },
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));
            const blob = new Blob([pdfData], {type: 'application/pdf'});
            const url = URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = 'exam-paper.pdf';
            link.style.display = 'none';

            document.body.appendChild(link);

            link.click();

            document.body.removeChild(link);
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            }
        }
    });
}

// Exam paper
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

                renderPage(pdfDoc, pageNum, '#page-num', pageRendering, pageNumPending, scale, $pdfCanvas, ctx);
                showViewAndPagingPdf(pdfDoc.numPages, '#pdf-viewer', '#paging-pdf');

                $('#examShiftStart').prop('hidden', true);
                $('#examShiftStartTime').prop('hidden', false);

                hideLoading();
            });
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
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

//Exam rule
const fetchFilePDFExamRule = (fileId) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + "/file-exam-rule",
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
                showToastError(error?.responseJSON?.message);
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
            $('#examShiftStart').prop('hidden', true);
        }
    });
};

const examShiftStartTimeSubmit = () => {
    swal({
        title: "Xác nhận bắt đầu ca thi",
        text: "Phát đề thi?",
        icon: "info",
        buttons: true,
        dangerMode: false,
    }).then((willApprove) => {
        if (willApprove) {
            examShiftStartTime();
            $('#examShiftStartTime').prop('hidden', true);
        }
    });
};

const openModalSetExamTime = (studentId) => {
    $('#studentIdSetExamTime').val(studentId);
    $('#modifyExamTime').val('');
    $(`#examTimeError`).text('');
    $(`#modifyExamTime`).removeClass('is-invalid');
    $('#examTimeModal').modal('show');
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

const approveStudentWhenStartTimeSubmit = (studentId) => {
    if (studentId !== null) {
        swal({
            title: "Xác nhận phê duyệt sinh viên",
            text: "Phê duyệt sinh viên vào phòng thi?",
            icon: "info",
            buttons: true,
            dangerMode: false,
        }).then((willApprove) => {
            if (willApprove) {
                approveStudentWhenStartTime(studentId);
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
            $('#examShiftStart').prop('hidden', true);
            $('#examShiftStartTime').prop('hidden', true);
            $('#countdown').text(minutesToEnd + " phút " + secondsToEnd + " giây");
        } else {
            clearInterval(countdown);
            $('#examShiftStart').prop('hidden', true);
            $('#examShiftStartTime').prop('hidden', true);
            $('#completeExamShift').prop('hidden', false);
            $('#countdown').text("Đã kết thúc!");
            showToastSuccess('Đã hết giờ làm bài thi!')
        }
    }, 1000);
}

const completeExamShift = () => {
    swal({
        title: "Xác nhận hoàn thành ca thi",
        text: "Hoàn thành ca thi?",
        icon: "info",
        buttons: true,
        dangerMode: false,
    }).then((willComplete) => {
        if (willComplete) {
            window.location.href = ApiConstant.REDIRECT_TEACHER_EXAM_SHIFT;
            updateExamPaperShiftStatus();
            updateExamShiftStatus();
        }
    });
};