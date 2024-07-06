const examDistributionInfo = getExamDistributionInfo();
const classSubjectCodeUrl = "?classSubjectCode=";
const subjectIdUrl = "&subjectId=";
const blockIdUrl = "&blockId=";
const facilityChildIdUrl = "&facilityChildId=";
const examShiftDateUrl = "&examShiftDate=";

let subjectId = null;
let blockId = null;
let facilityChildId = null;
let classSubjectId = null;
let examShiftCode = null;

$(document).ready(function () {

    $('#modifyExamShiftButton').on('click', function () {
        addExamShift();
    });

    $('#modifyExamShiftJoinButton').on('click', function () {
        joinExamShift();
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

    // $('#modifyBlockId').on('change', function () {
    //     blockId = $(this).val();
    //     if (subjectId != null && blockId != null && facilityChildId != null) {
    //         getClassSubjectIdByRequest();
    //     }
    // });

    $('#modifyFacilityChildId').on('change', function () {
        facilityChildId = $(this).val();
        if (subjectId != null && blockId != null && facilityChildId != null) {
            getClassSubjectIdByRequest();
        }
    });

});

const getExamDate = () => {
    let today = new Date();
    let dd = String(today.getDate()).padStart(2, '0');
    let mm = String(today.getMonth() + 1).padStart(2, '0');
    let yyyy = today.getFullYear();
    today = mm + '/' + dd + '/' + yyyy;
    $('#modifyExamDate').val(today);
};

const fetchSubjects = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_SUBJECT + classSubjectCodeUrl + subjectClassCodeVal,
        success: function (responseBody) {
            if (responseBody?.data) {
                console.log("subjects", responseBody?.data);
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

// const fetchBlocks = () => {
//     let subjectClassCodeVal = $('#modifySubjectClassCode').val();
//     $.ajax({
//         type: "GET",
//         url: ApiConstant.API_TEACHER_BLOCK + classSubjectCodeUrl + subjectClassCodeVal + subjectIdUrl + subjectId,
//         success: function (responseBody) {
//             if (responseBody?.data) {
//                 console.log("blocks", responseBody?.data)
//                 const blocks = responseBody?.data?.map((block, index) => {
//                     return `<option value="${block.id}">${block.blockName}</option>`;
//                 });
//                 blocks.unshift('<option value="">Chọn block</option>');
//                 $('#modifyBlockId').html(blocks);
//             }
//         },
//         error: function (error) {
//             showToastError('Có lỗi xảy ra khi lấy thông tin block');
//         }
//     });
// }

const fetchBlocks = () => {
    let subjectClassCodeVal = $('#modifySubjectClassCode').val();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_BLOCK + '/block-id' + classSubjectCodeUrl + subjectClassCodeVal
            + subjectIdUrl + subjectId + examShiftDateUrl + new Date($('#modifyExamDate').val()).getTime(),
        success: function (responseBody) {
            if (responseBody?.data) {
                console.log("blocks", responseBody?.data)
                blockId = responseBody?.data;
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
        url: ApiConstant.API_TEACHER_CAMPUS + classSubjectCodeUrl + subjectClassCodeVal + subjectIdUrl + subjectId,
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
        url: ApiConstant.API_TEACHER_CLASS_SUBJECT + classSubjectCodeUrl + subjectClassCodeVal
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

const addExamShift = () => {
    const examShift = {
        classSubjectId: classSubjectId,
        firstSupervisorId: examDistributionInfo.userId,
        examDate: new Date($('#modifyExamDate').val()).getTime(),
        shift: $('#modifyShift').val(),
        room: $('#modifyRoom').val(),
        password: $('#modifyPassword').val()
    }
    $.ajax({
        type: "POST",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT,
        contentType: "application/json",
        data: JSON.stringify(examShift),
        success: function (responseBody) {
            if (responseBody?.data) {
                examShiftCode = responseBody?.data;
                console.log("examShiftCode", examShiftCode)
                $('#examShiftModal').modal('hide');
                localStorage.setItem('addExamShiftSuccessMessage', responseBody?.message);
                window.location.href = ApiConstant.REDIRECT_TEACHER_EXAM_SHIFT + '/' + examShiftCode;
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

const joinExamShift = () => {
    const examShift = {
        examShiftCodeJoin: $('#modifyExamShiftCodeJoin').val(),
        passwordJoin: $('#modifyPasswordJoin').val(),
        secondSupervisorId: examDistributionInfo.userId
    }
    $.ajax({
        type: "POST",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT + '/join',
        contentType: "application/json",
        data: JSON.stringify(examShift),
        success: function (responseBody) {
            if (responseBody?.data) {
                $('#examShiftJoinModal').modal('hide');
                localStorage.setItem('joinExamShiftSuccessMessage', responseBody?.message);
                window.location.href = ApiConstant.REDIRECT_TEACHER_EXAM_SHIFT + '/' + responseBody?.data;
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
    $('#examShiftModal').modal('show');
};

const openModalJoinExamShift = () => {
    $('#examShiftJoinModal').modal('show');
};



