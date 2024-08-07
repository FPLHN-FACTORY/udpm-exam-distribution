let stateETSubjectId = "";

const getStateERSubjectId = () => stateETSubjectId;

const setStateERSubjectId = (value) => {
    stateETSubjectId = value;
}

const getFieldExamTime = () => $("#examTime").val();

const setFieldExamTime = (value) => {
    $("#examTime").val(value);
};

const handleOpenModalExamTime = (subjectId) => {
    fetchDetailExamTime(subjectId);
    setStateERSubjectId(subjectId);

    $("#examTimeModal").modal("show");
};

const fetchDetailExamTime = (subjectId) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE + "/exam-time",
        contentType: "application/json",
        data: {
            subjectId: subjectId
        },
        success: function (responseBody) {
            setFieldExamTime(responseBody?.data?.examTime);
            hideLoading();
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            } else {
                showToastError('Có lỗi xảy ra');
            }
            hideLoading();
        }
    });
};

$("#button-update-exam-time").on("click", () => {
    showLoading();
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE + "/exam-time",
        contentType: "application/json",
        data: JSON.stringify({
            subjectId: getStateERSubjectId(),
            examTime: getFieldExamTime()
        }),
        success: function (responseBody) {
            showToastSuccess(responseBody?.message);

            $("#examTimeModal").modal("hide");
            fetchSearchSubject();

            hideLoading();
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            } else {
                showToastError('Có lỗi xảy ra');
            }
            hideLoading();
        }
    });
});