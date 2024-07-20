$(document).ready(function () {

    getExamShiftByCode();

    getFirstSupervisorId();

    getSecondSupervisorId();

    getStudents();

    const hsJoinExamShiftSuccessMessage = localStorage.getItem('hsJoinExamShiftSuccessMessage');
    if (hsJoinExamShiftSuccessMessage) {
        showToastSuccess(hsJoinExamShiftSuccessMessage);
        localStorage.removeItem('hsJoinExamShiftSuccessMessage');
    }

    connect();

    countStudentInExamShift();

});

let examShiftCode = $('#examShiftCodeCtl').text();
let currentStudentViolationId = "";
let stompClient = null;

const getExamShiftByCode = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM + '/' + examShiftCode,
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
            // showToastSuccess(JSON.parse(response.body).message);
            getSecondSupervisorId();
        });
        stompClient.subscribe("/topic/student-exam-shift", function (response) {
            // showToastSuccess(JSON.parse(response.body).message);
            countStudentInExamShift();
            getStudents();
        });
        stompClient.subscribe("/topic/student-exam-shift-kick", function (response) {
            // showToastSuccess(JSON.parse(response.body).message);
            countStudentInExamShift();
            getStudents();
        });
        stompClient.subscribe("/topic/head-department-exam-shift-join", function () {
            countStudentInExamShift();
            getStudents();
        });
        stompClient.subscribe("/topic/track-student", function (response) {
            const responseBody = JSON.parse(response.body);
            showToastSuccess(responseBody.message);
            getStudents();
        });
    });
}

const getFirstSupervisorId = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_STAFF + '/first-supervisor/' + examShiftCode,
        success: function (responseBody) {
            if (responseBody?.data) {
                const firstSupervisor = responseBody?.data;
                console.log(firstSupervisor);
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
        url: ApiConstant.API_HEAD_DEPARTMENT_STAFF + '/second-supervisor/' + examShiftCode,
        success: function (responseBody) {
            if (responseBody?.data) {
                const secondSupervisor = responseBody?.data;
                console.log(secondSupervisor);
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

const countStudentInExamShift = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM + '/' + examShiftCode + '/count-student',
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
        url: ApiConstant.API_HEAD_DEPARTMENT_STUDENT + '/' + examShiftCode,
        success: function (responseBody) {
            console.log(responseBody);
            if (responseBody?.data) {
                const students = responseBody?.data;
                $('#studentsContainer').empty();
                let row = $('<div class="row mt-3"></div>');
                let colCounter = 0;
                students.forEach((student, index) => {
                    const col = $(`
                        <div class="col-3">
                            <div class="${student.isViolation === 0 ? "bg-warning" : "bg-white"} p-4 shadow rounded min-vh-30 w-30 position-relative">
                                <div class="user-box">
                                    <div class="avatar-lg">
                                        <img src="${student.picture}"
                                         alt="image profile"
                                         class="avatar-img rounded"/>
                                    </div>
                                    <div class="u-text">
                                        <h4>${student.name}</h4>
                                        <p class="text-muted">${student.email}</p>
                                        <p class="text-muted">
                                        Join time: ${formatFromUnixTimeToHoursMinutes(student.joinTime)}</p>
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
            showToastError('Có lỗi xảy ra khi lấy thông tin ca thi');
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

const formatDateTime = (date) => {
    const d = new Date(Number(date));
    const day = String(d.getDate()).padStart(2, "0");
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const year = d.getFullYear();
    const hours = String(d.getHours()).padStart(2, "0");
    const minutes = String(d.getMinutes()).padStart(2, "0");
    return `${day}/${month}/${year} ${hours}:${minutes}`;
}

