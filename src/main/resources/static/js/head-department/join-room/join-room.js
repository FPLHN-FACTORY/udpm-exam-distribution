$(document).ready(function () {
    $('#modifyExamShiftJoinButton').on('click', function () {
        joinExamShift();
    });
});

const joinExamShift = () => {
    const examShift = {
        examShiftCodeJoin: $('#modifyExamShiftCodeJoin').val(),
        passwordJoin: $('#modifyPasswordJoin').val(),
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

const openModalJoinExamShift = () => {
    resetFormJoinExamShift();
    removeFormJoinError();
    $('#examShiftJoinModal').modal('show');
};

const resetFormJoinExamShift = () => {
    $('#modifyExamShiftCodeJoin').val('');
    $('#modifyPasswordJoin').val('');
}

const removeFormJoinError = (id) => {
    $('#modifyExamShiftCodeJoin').removeClass('is-invalid');
    $('#examShiftCodeJoinError').text('');
    $('#modifyPasswordJoin').removeClass('is-invalid');
    $('#passwordJoinError').text('');
};