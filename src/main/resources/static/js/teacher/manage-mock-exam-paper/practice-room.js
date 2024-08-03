let stateSubjectId = '';

//start setter
const setStateSubjectId = (value) => {
    stateSubjectId = value;
}
//end setter

//start getter
const getStateSubjectId = () => stateSubjectId;

//end getter

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