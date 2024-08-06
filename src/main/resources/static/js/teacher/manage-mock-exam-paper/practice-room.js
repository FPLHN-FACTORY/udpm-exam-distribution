let stateSubjectId = '';
let statePracticeRoomId = '';

//start setter
const setStateSubjectId = (value) => {
    stateSubjectId = value;
}
const setStatePracticeRoomId = (value) => {
    statePracticeRoomId = value;
}
//end setter

//start getter
const getStateSubjectId = () => stateSubjectId;
const getStatePracticeRoomId = () => statePracticeRoomId;

//end getter

$(document).ready(function() {
    $('#listStudentModal').on('hidden.bs.modal', function () {
        $('#practiceRoomDetailModal').modal('show');
    });
    $('#keyWordStudent').on('keyup', debounce(function () {
        openModalListStudent();
    }));
    $('#pageSizeListStudent').on('change',function () {
        openModalListStudent();
    });
});

function handleOpenModalPracticeRoom(subjectId) {
    setStateSubjectId(subjectId);
    setupDatePracticeRoom();
    $('#practiceRoomModal').modal('show');
}

function handleSubmitPracticeRoom() {
    $('#practiceRoomModal').modal('hide');
    swal({
        title: "Xác nhận tạo phòng luyện tập?",
        text: "Bạn chắc chắn muốn tạo phòng luyện tập không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Xác nhận",
                className: "btn btn-black",
            },
        },
    }).then((confirm) => {
        if (confirm) {
            const startEndDate = $('#startEndDatePracticeRoom').val().trim();
            let startDateString = startEndDate.substring(0, 10);
            let endDateString = startEndDate.substring(12);
            let startDate = null;
            let endDate = null;
            if (startEndDate !== '') {
                startDate = moment(startDateString, "DD/MM/YYYY").toDate().getTime();
                endDate = moment(endDateString, "DD/MM/YYYY").toDate().getTime();
            }
            let data = {
                subjectId: getStateSubjectId(),
                startDate: startDate,
                endDate: endDate
            }
            let url = ApiConstant.API_TEACHER_PRACTICE_ROOM;
            showLoading();
            $.ajax({
                type: 'post',
                url: url,
                data: JSON.stringify(data),
                contentType: "application/json",
                success: function (responseBody) {
                    if (responseBody?.status === 'OK') {
                        showToastSuccess(responseBody.message);
                        handleChange();
                    }
                    hideLoading();
                },
                error: function (error) {
                    hideLoading();
                    if (error?.responseJSON?.length > 0) {
                        error.responseJSON.forEach(err => {
                            $(`#${err.fieldError}Error`).text(err.message);
                        });
                    } else {
                        let mess = error?.responseJSON?.message
                            ? error?.responseJSON?.message : 'Có lỗi xảy ra khi tạo phòng luyện tập';
                        showToastError(mess);
                    }
                }
            });
        } else {
            $('#practiceRoomModal').modal('show');
        }
    });
}

const setupDatePracticeRoom = () => {
    const time = $('#startEndDatePracticeRoom');
    time.val('');
    time.daterangepicker({
        opens: 'center',
        locale: {
            format: 'DD/MM/YYYY'
        },
        autoUpdateInput: false
    });

    time.on('apply.daterangepicker', (ev, picker) => {
        time.val(picker.startDate.format('DD/MM/YYYY') + ' ⇀ ' + picker.endDate.format('DD/MM/YYYY'));
    });

    time.on('cancel.daterangepicker', (ev, picker) => {
        time.val('');
    });
};

function handleOpenModalPracticeRoomDetail(practiceRoomId) {
    setStatePracticeRoomId(practiceRoomId);
    let url = ApiConstant.API_TEACHER_PRACTICE_ROOM + "/" + practiceRoomId;
    showLoading();
    $.ajax({
        type: 'get',
        url: url,
        success: function (responseBody) {
            if (responseBody?.status === 'OK') {
                const data = responseBody?.data;
                $('#subjectDetail').val(data.subjectName);
                $('#openTimeDetail').val(formatDateTime(data.startDate) + ' -> ' + formatDateTime(data.endDate));
                $('#practiceRoomCodeDetail').val(data.practiceRoomCode);
                $('#practiceRoomCodeTitle').val(data.practiceRoomCode);
                $('#practiceRoomPasswordDetail').val(data.password);
                $('#practiceRoomDetailModal').modal('show');
            }
            hideLoading();
        },
        error: function (error) {
            hideLoading();
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                });
            } else {
                let mess = error?.responseJSON?.message
                    ? error?.responseJSON?.message : 'Có lỗi xảy ra khi lấy thông tin phòng luyện tập';
                showToastError(mess);
            }
        }
    });
}

function openModalListStudent(page = 1) {
    const params = {
        page: page,
        size: $('#pageSizeListStudent').val(),
        keyWord: $('#keyWordStudent').val()?.trim(),
        practiceRoomId:getStatePracticeRoomId()?.trim()
    };

    let url = ApiConstant.API_TEACHER_PRACTICE_ROOM + "/student?";

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);
    showLoading();
    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const responseData = responseBody?.data?.content;
            hideLoading();
            if (responseData.length === 0) {
                $('#studentTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const students = responseData.map(function (student, index) {
                return `<tr>
                <td>${index + 1 + responseBody?.data?.pageable?.offset}</td>
                <td>${student.studentCode}</td>
                <td>${student.studentName}</td>
                <td>${student.studentEmail}</td>
                <td>${formatDateTime(student.joinedAt)}</td>
            </tr>`;
            });
            $('#studentTableBody').html(students);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationStudent(totalPages, page);
            $('#practiceRoomDetailModal').modal('hide');
            $('#listStudentModal').modal('show');
        },
        error: function (error) {
            hideLoading();
            let mess = error?.responseJSON?.message
                ? error?.responseJSON?.message : 'Có lỗi xảy ra khi lấy dữ liệu sinh viên';
            showToastError(mess);
        }
    });
}

const createPaginationStudent = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="openModalListStudent(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="openModalListStudent(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="openModalListStudent(${currentPage + 1})">
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
    $('#paginationListStudent').html(paginationHtml);
};