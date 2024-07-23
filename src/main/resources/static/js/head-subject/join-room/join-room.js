$(document).ready(function () {

    getExamShifts();

    $('#modifyExamShiftJoinButton').on('click', function () {
        joinExamShift($('#modifyExamShiftCodeJoin').val());
    });

    connect();
});

const connect = () => {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe(TopicConstant.TOPIC_EXAM_SHIFT_CREATE, function (response) {
            getExamShifts();
        });
    });
}

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
    // $('#modifyPasswordJoin').val('');
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