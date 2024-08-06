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

    fetchShifts();

    getExamShifts();

    $('#modifyExamShiftButton').on('click', function () {
        getClassSubject();
    });

    $('#modifyExamShiftJoinButton').on('click', function () {
        joinExamShift($('#modifyExamShiftCodeJoin').val());
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

    viewSelectForFirstSupervisor();

    viewSelectForSecondSupervisor();

});

$(document).ready(function () {
    let today = new Date();
    let day = String(today.getDate()).padStart(2, '0');
    let month = String(today.getMonth() + 1).padStart(2, '0');
    let year = today.getFullYear();

    today = year + '-' + month + '-' + day;
    $('#modifyExamDate').val(today);
});

const fetchSubjects = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_SUBJECT + classSubjectCodeUrl + subjectClassCodeVal,
        success: function (responseBody) {
            if (responseBody?.data) {
                console.log("Môn thi: ", responseBody?.data);
                const subjects = responseBody?.data?.map((subject, index) => {
                    return `<option value="${subject.id}">${subject.subjectCode} - ${subject.subjectName}</option>`;
                });
                subjects.unshift('<option value="">Chọn môn thi</option>');
                $('#modifySubjectId').html(subjects);
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    });
}

const fetchBlocks = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_BLOCK + classSubjectCodeUrl + subjectClassCodeVal + subjectIdUrl + subjectId,
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
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    });
}

const fetchFacilityChildren = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_CAMPUS + classSubjectCodeUrl + subjectClassCodeVal + subjectIdUrl + subjectId,
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
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    });
}

const getClassSubjectIdByRequest = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_CLASS_SUBJECT + classSubjectCodeUrl + subjectClassCodeVal
            + subjectIdUrl + subjectId + blockIdUrl + blockId + facilityChildIdUrl + facilityChildId,
        success: function (responseBody) {
            if (responseBody?.data) {
                classSubjectId = responseBody?.data;
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    })
};

const getClassSubject = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_CLASS_SUBJECT + '/get-class-subject' + classSubjectCodeUrl + subjectClassCodeVal,
        success: function (responseBody) {
            if (responseBody?.data) {
                addExamShift();
            }
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
        }
    })
}

const checkClassSubject = (id) => {
    if ($(`#${id}`).val() === '') {
        $(`#${id}`).addClass('is-invalid');
    }
}

const addExamShift = () => {
    const examShift = {
        classSubjectId: classSubjectId,
        firstSupervisorCode: $('#modifyFirstSupervisorCode').val(),
        secondSupervisorCode: $('#modifySecondSupervisorCode').val(),
        examDate: new Date($('#modifyExamDate').val()).setHours(0, 0, 0, 0),
        shift: $('#modifyShift').val(),
        room: $('#modifyRoom').val()
    }
    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_JOIN_ROOM + '/create',
        contentType: "application/json",
        data: JSON.stringify(examShift),
        success: function (responseBody) {
            if (responseBody?.data) {
                examShiftCode = responseBody?.data;
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

const viewSelectForFirstSupervisor = () => {
    $("#modifyFirstSupervisorCode").select2({
        width: '100%',
        dropdownParent: $('#examShiftModal'),
        closeOnSelect: true,
        placeholder: 'Chọn giám thị 1',
        allowClear: true,
        ajax: {
            url: `${ApiConstant.API_COMMON}/staff/search`,
            type: "GET",
            dataType: 'json',
            contentType: "application/json",
            delay: 250,
            data: function (query) {
                return {
                    q: query.term
                };
            },
            processResults: function (data) {
                console.log(data);
                let results = [];
                data.data.forEach(e => {
                    results.push({id: e.id, text: e.staffInfo});
                });

                return {
                    results: results
                };
            }
        },
        templateResult: formatResult,
        templateSelection: formatSelection
    });

    function formatResult(item) {
        if (item.loading) {
            return item.text;
        }
        return $('<span>').text(item.text);
    }

    function formatSelection(item) {
        return item.text;
    }
};

const viewSelectForSecondSupervisor = () => {
    $("#modifySecondSupervisorCode").select2({
        width: '100%',
        dropdownParent: $('#examShiftModal'),
        closeOnSelect: true,
        placeholder: 'Chọn giám thị 2',
        allowClear: true,
        ajax: {
            url: `${ApiConstant.API_COMMON}/staff/search`,
            type: "GET",
            dataType: 'json',
            contentType: "application/json",
            delay: 250,
            data: function (query) {
                return {
                    q: query.term
                };
            },
            processResults: function (data) {
                console.log(data);
                let results = [];
                data.data.forEach(e => {
                    results.push({id: e.id, text: e.staffInfo});
                });

                return {
                    results: results
                };
            }
        },
        templateResult: formatResult,
        templateSelection: formatSelection
    });

    function formatResult(item) {
        if (item.loading) {
            return item.text;
        }
        return $('<span>').text(item.text);
    }

    function formatSelection(item) {
        return item.text;
    }
};

const joinExamShift = (examShiftCode) => {
    const examShift = {
        examShiftCodeJoin: examShiftCode,
    }
    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_JOIN_ROOM,
        contentType: "application/json",
        data: JSON.stringify(examShift),
        success: function (responseBody) {
            if (responseBody?.data) {
                $('#examShiftJoinModal').modal('hide');
                localStorage.setItem('hsJoinExamShiftSuccessMessage', responseBody?.message);
                window.location.href = ApiConstant.REDIRECT_HEAD_SUBJECT_MANAGE_JOIN_ROOM + '/' + responseBody?.data;
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
                showToastError('Có lỗi xảy ra khi tham gia ca thi!');
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
    $('#modifyFirstSupervisorCode').val('');
    $('#modifySecondSupervisorCode').val('');
    $('#modifySubjectClassCode').val('');
    $('#modifySubjectId').val('');
    $('#modifyBlockId').val('');
    $('#modifyFacilityChildId').val('');
    fetchShifts();
}

const removeFormAddError = (id) => {
    $('#modifyRoom').removeClass('is-invalid');
    $('#roomError').text('');
    $('#modifyFirstSupervisorCode').removeClass('is-invalid');
    $('#firstSupervisorCodeError').text('');
    $('#modifySecondSupervisorCode').removeClass('is-invalid');
    $('#secondSupervisorCodeError').text('');
    $('#modifySubjectClassCode').removeClass('is-invalid');
    $('#classSubjectCodeError').text('');
    $('#modifySubjectId').removeClass('is-invalid');
    $('#modifyBlockId').removeClass('is-invalid');
    $('#modifyFacilityChildId').removeClass('is-invalid');
};

const openModalJoinExamShift = () => {
    resetFormJoinExamShift();
    removeFormJoinError();
    $('#examShiftJoinModal').modal('show');
};

const resetFormJoinExamShift = () => {
    $('#modifyExamShiftCodeJoin').val('');
}

const removeFormJoinError = () => {
    $('#modifyExamShiftCodeJoin').removeClass('is-invalid');
    $('#examShiftCodeJoinError').text('');
};

const getExamShifts = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_JOIN_ROOM,
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
                                        <p class="text-muted">Phòng thi: ${examShift.room} - ${examShift.shift}</p>
                                        <p class="text-muted">Lớp: ${examShift.classSubjectCode}</p>
                                        <p class="text-muted">Môn thi: ${examShift.subjectName}</p>
                                        <p class="text-muted">Giám thị 1: ${examShift.codeFirstSupervisor}</p>
                                        <p class="text-muted">Giám thị 2: ${examShift.codeSecondSupervisor}</p>
                                        <p class="text-muted">Trạng thái: ${
                        examShift.status === 'NOT_STARTED' ? 'Chưa bắt đầu' :
                            examShift.status === 'IN_PROGRESS' ? 'Đang diễn ra' :
                                examShift.status === 'FINISHED' ? 'Đã kết thúc' : 'Không xác định'
                    }
                                        </p>
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
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
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