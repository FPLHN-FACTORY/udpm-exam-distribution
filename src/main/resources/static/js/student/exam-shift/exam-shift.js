const examDistributionInfo = getExamDistributionInfo();

$(document).ready(function () {
    $('#modifyExamShiftJoinButton').on('click', function () {
        joinExamShift();
    });

    const kickExamShiftStudentSuccessMessage = localStorage.getItem('kickExamShiftStudentSuccessMessage');
    if (kickExamShiftStudentSuccessMessage) {
        showToastError(kickExamShiftStudentSuccessMessage);
        localStorage.removeItem('kickExamShiftStudentSuccessMessage');
    }

    connect();
});

let messageType = null;

const connect = () => {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REJOIN, function (response) {
            messageType = 'rejoin';
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REFUSE, function (response) {
            showToastError('Bạn đã bị từ chối tham gia ca thi!');
        });
        stompClient.subscribe(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_APPROVE, function (response) {
            let examShiftCodeRejoin = localStorage.getItem('rejoinExamShiftCode');
            if (examShiftCodeRejoin) {
                window.location.href = ApiConstant.REDIRECT_STUDENT_EXAM_SHIFT + '/' + examShiftCodeRejoin;
                localStorage.removeItem('rejoinExamShiftCode');
            }
        });
    });
}

const joinExamShift = () => {
    const examShift = {
        studentId: examDistributionInfo.userId,
        examShiftCodeJoin: $('#modifyExamShiftCodeJoin').val(),
        passwordJoin: $('#modifyPasswordJoin').val(),
        joinTime: new Date().getTime()
    }
    localStorage.setItem('rejoinExamShiftCode', examShift.examShiftCodeJoin);
    $.ajax({
        type: "POST",
        url: ApiConstant.API_STUDENT_EXAM_SHIFT + '/join',
        contentType: "application/json",
        data: JSON.stringify(examShift),
        success: function (responseBody) {
            if (responseBody?.data) {
                $('#examShiftJoinModal').modal('hide');
                if (messageType === 'rejoin') {
                    showToastSuccess(responseBody?.message);
                } else {
                    localStorage.setItem('joinExamShiftStudentSuccessMessage', responseBody?.message);
                    window.location.href = ApiConstant.REDIRECT_STUDENT_EXAM_SHIFT + '/' + responseBody?.data;
                }
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