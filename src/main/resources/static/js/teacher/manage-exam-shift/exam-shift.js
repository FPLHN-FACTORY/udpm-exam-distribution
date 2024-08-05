$(document).ready(function () {

    getExamShifts();

});

const getExamShifts = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_EXAM_SHIFT,
        success: function (responseBody) {
            if (responseBody?.data) {
                const examShifts = responseBody?.data;
                console.log(examShifts)
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

const joinExamShift = (examShiftCode) => {
    const examShift = {
        examShiftCodeJoin: examShiftCode
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
                showToastError('Có lỗi xảy ra khi tham gia ca thi!');
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



