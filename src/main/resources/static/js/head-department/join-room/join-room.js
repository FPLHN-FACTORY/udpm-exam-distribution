const classSubjectCodeUrl = "?classSubjectCode=";
const subjectIdUrl = "&subjectId=";
const blockIdUrl = "&blockId=";
const facilityChildIdUrl = "&facilityChildId=";

let subjectId = null;
let blockId = null;
let facilityChildId = null;
let classSubjectId = null;
let examShiftCode = null;

$(document).ready(function () {

    getExamShifts();

    $('#modifyExamShiftButton').on('click', function () {
        addExamShift();
    });

    $('#modifyExamShiftJoinButton').on('click', function () {
        joinExamShiftSubmit($('#modifyExamShiftCodeJoin').val());
    });

    let timeout = null;

    getExamDate();

    $('#modifySubjectClassCode').on('input', function () {
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            fetchSubjects();
        }, 1200);
        $('#modifySubjectId').html('<option value="">Chọn môn thi</option>');
        $('#modifyBlockId').html('<option value="">Chọn block</option>');
        $('#modifyFacilityChildId').html('<option value="">Chọn campus</option>');
    });

    $('#modifySubjectId').on('change', function () {
        subjectId = $(this).val();
        if (subjectId != null) {
            fetchBlocks();
            fetchFacilityChildren();
        }
    });

    $('#modifyBlockId').on('change', function () {
        blockId = $(this).val();
        if (subjectId != null && blockId != null && facilityChildId != null) {
            getClassSubjectIdByRequest();
        }
    });

    $('#modifyFacilityChildId').on('change', function () {
        facilityChildId = $(this).val();
        if (subjectId != null && blockId != null && facilityChildId != null) {
            getClassSubjectIdByRequest();
        }
    });
});

const fetchSubjects = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_SUBJECT + classSubjectCodeUrl + subjectClassCodeVal,
        success: function (responseBody) {
            if (responseBody?.data) {
                const subjects = responseBody?.data?.map((subject, index) => {
                    return `<option value="${subject.id}">${subject.subjectCode} - ${subject.subjectName}</option>`;
                });
                subjects.unshift('<option value="">Chọn môn thi</option>');
                $('#modifySubjectId').html(subjects);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin môn học');
        }
    });
}

const fetchBlocks = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_BLOCK + classSubjectCodeUrl + subjectClassCodeVal + subjectIdUrl + subjectId,
        success: function (responseBody) {
            if (responseBody?.data) {
                const blocks = responseBody?.data?.map((block, index) => {
                    return `<option value="${block.id}">${block.blockName}</option>`;
                });
                blocks.unshift('<option value="">Chọn block</option>');
                $('#modifyBlockId').html(blocks);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin block');
        }
    });
}

const fetchFacilityChildren = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_CAMPUS + classSubjectCodeUrl + subjectClassCodeVal + subjectIdUrl + subjectId,
        success: function (responseBody) {
            if (responseBody?.data) {
                const facilityChildren = responseBody?.data?.map((facilityChild, index) => {
                    return `<option value="${facilityChild.id}">${facilityChild.facilityChildName}</option>`;
                });
                facilityChildren.unshift('<option value="">Chọn campus</option>');
                $('#modifyFacilityChildId').html(facilityChildren);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin campus');
        }
    });
}

const getClassSubjectIdByRequest = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_CLASS_SUBJECT + classSubjectCodeUrl + subjectClassCodeVal
            + subjectIdUrl + subjectId + blockIdUrl + blockId + facilityChildIdUrl + facilityChildId,
        success: function (responseBody) {
            if (responseBody?.data) {
                classSubjectId = responseBody?.data;
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin lớp môn');
        }
    })
};

const checkClassSubject = (id) => {
    if ($(`#${id}`).val() === '') {
        $(`#${id}`).addClass('is-invalid');
    }
}

const addExamShift = () => {
    const examShift = {
        classSubjectId: classSubjectId,
        firstSupervisorCode: $('#modifyFirstSupervisor').val(),
        secondSupervisorCode: $('#modifySecondSupervisor').val(),
        examDate: new Date($('#modifyExamDate').val()).getTime(),
        shift: $('#modifyShift').val(),
        room: $('#modifyRoom').val(),
        password: $('#modifyPassword').val()
    }
    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM + '/create',
        contentType: "application/json",
        data: JSON.stringify(examShift),
        success: function (responseBody) {
            if (responseBody?.data) {
                examShiftCode = responseBody?.data;
                console.log(responseBody)
                $('#examShiftModal').modal('hide');
                getExamShifts();
                showToastSuccess(responseBody?.message);
            }
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    if (err.fieldError === 'classSubjectId') {
                        showToastError(err.message);
                    }
                    checkClassSubject('modifySubjectClassCode');
                    checkClassSubject('modifySubjectId');
                    checkClassSubject('modifyBlockId');
                    checkClassSubject('modifyFacilityChildId');
                    $(`#${err.fieldError}Error`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}`).addClass('is-invalid');
                });
            } else if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message)
            } else {
                showToastError('Có lỗi xảy ra khi thêm ca thi!');
            }
        }
    });
}

const joinExamShift = (examShiftCode) => {
    const examShift = {
        examShiftCodeJoin: examShiftCode
    }
    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM,
        contentType: "application/json",
        data: JSON.stringify(examShift),
        success: function (responseBody) {
            if (responseBody?.data) {
                $('#examShiftJoinModal').modal('hide');
                localStorage.setItem('hsJoinExamShiftSuccessMessage', responseBody?.message);
                window.location.href = ApiConstant.REDIRECT_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM + '/' + responseBody?.data;
            }
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
            } else {
                showToastError('Có lỗi xảy ra khi thêm ca thi!');
            }
        }
    });
}

const openModalAddExamShift = () => {
    resetFormAddExamShift();
    removeFormAddError();
    $('#examShiftModal').modal('show');
};

const resetFormAddExamShift = () => {
    $('#modifyRoom').val('');
    $('#modifyFirstSupervisor').val('');
    $('#modifySecondSupervisor').val('');
    $('#modifySubjectClassCode').val('');
    $('#modifySubjectId').val('');
    $('#modifyBlockId').val('');
    $('#modifyFacilityChildId').val('');
    $('#modifyPassword').val('');
}

const removeFormAddError = (id) => {
    $('#modifyRoom').removeClass('is-invalid');
    $('#roomError').text('');
    $('#modifyFirstSupervisor').removeClass('is-invalid');
    $('#firstSupervisorError').text('');
    $('#modifySecondSupervisor').removeClass('is-invalid');
    $('#secondSupervisorError').text('');
    $('#modifySubjectClassCode').removeClass('is-invalid');
    $('#classSubjectCodeError').text('');
    $('#modifySubjectId').removeClass('is-invalid');
    $('#modifyBlockId').removeClass('is-invalid');
    $('#modifyFacilityChildId').removeClass('is-invalid');
    $('#modifyPassword').removeClass('is-invalid');
    $('#passwordError').text('');
};

const openModalJoinExamShift = () => {
    resetFormJoinExamShift();
    removeFormJoinError();
    $('#examShiftJoinModal').modal('show');
};

const resetFormJoinExamShift = () => {
    $('#modifyExamShiftCodeJoin').val('');
    // $('#modifyPasswordJoin').val('');
}

const removeFormJoinError = () => {
    $('#modifyExamShiftCodeJoin').removeClass('is-invalid');
    $('#examShiftCodeJoinError').text('');
};

const getExamShifts = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM,
        success: function (responseBody) {
            if (responseBody?.data) {
                const examShifts = responseBody?.data;
                $('#examShiftsContainer').empty();
                let row = $('<div class="row mt-3"></div>');
                let colCounter = 0;
                examShifts.forEach((examShift, index) => {
                    const col = $(`
                        <div class="col-3" onclick="joinExamShiftSubmit('${examShift.examShiftCode}')">
                            <div class="btn-label-warning p-2 shadow rounded min-vh-30">
                                <div class="user-box">
                                    <div class="u-text">
                                        <h3>${examShift.examShiftCode}</h3>
                                        <p class="text-muted">Phòng thi: ${examShift.room}</p>
                                        <p class="text-muted">Người tạo: ${examShift.codeFirstSupervisor}</p>
                                        <p class="text-muted">Trạng thái: ${examShift.status}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `);
                    row.append(col);
                    colCounter++;
                    if (colCounter === 4) {
                        $('#examShiftsContainer').append(row);
                        row = $('<div class="row mt-3"></div>');
                        colCounter = 0;
                    }
                });
                if (colCounter !== 0) {
                    $('#examShiftsContainer').append(row);
                }
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin ca thi');
        }
    });
}

const joinExamShiftSubmit = (examShiftCode) => {
    if (examShiftCode !== null) {
        swal({
            title: "Xác nhận tham gia phòng thi",
            text: `Tham gia phòng thi ${examShiftCode}?`,
            icon: "info",
            buttons: true,
            dangerMode: false,
        }).then((willJoin) => {
            if (willJoin) {
                joinExamShift(examShiftCode);
            }
        });
    }
};