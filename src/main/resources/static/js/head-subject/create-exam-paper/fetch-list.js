const fetchListSubject = () => {
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_CREATE_EXAM_PAPER + "/subject",
        method: "GET",
        success: function (responseBody) {
            const subjects = responseBody?.data?.map((item) => {
                return `<option value="${item.id}">${item.name}</option>`
            });
            subjects.unshift('<option value="">--Chọn môn học--</option>');
            $('#exam-paper-subject').html(subjects);
        },
        error: function (error) {
            const messageErr = error?.responseJSON?.message;
            if (messageErr) {
                showToastError(messageErr);
            } else {
                showToastError("Có lỗi xảy ra");
            }
        }
    });
};

