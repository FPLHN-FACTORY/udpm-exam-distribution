//START: getter
const getValueExamPaperType = () => $("#exam-paper-type").val();
const getValueExamPaperSubjectId = () => $("#exam-paper-subject").val();
//END: getter


const handleOpenModalSaveExamPaper = () => {
    fetchListSubject();

    handleResetFieldsError();
    clearFieldsChoose();
    $("#saveExamPaperModal").modal("show");
};

const handleResetFieldsError = () => {
    $('.form-control').removeClass('is-invalid');
    $("#examPaperTypeError").text("");
    $("#subjectIdError").text("");
};

const clearFieldsChoose = () => {
    $("#exam-paper-type").val("");
    $("#exam-paper-subject").val("");
};

//button save exam_paper pdf
$("#save-exam_paper").on("click", () => {
    handleSaveExamPaperConfirm();
});

const handleSaveExamPaperConfirm = async () => {
    if (getTinyContentTeacher() && (getTinyContentTeacher()+'').trim().length === 0){
        showToastError("Đề không được trống");
    }
    swal({
        title: "Xác nhận",
        text: "Bạn có chắc muốn lưu đề thi này không?",
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
    }).then((willDelete) => {
        if (willDelete) {
            handleSaveExamPaper();
        }
    });
};

const handleSaveExamPaper = async () => {
    showLoading();

    const data = new FormData();

    data.append("examPaperType", getValueExamPaperType());
    data.append("subjectId", getValueExamPaperSubjectId())
    data.append("file", await convertTinyContentToPdf());
    data.append("contentFile", getTinyContent());

    handleResetFieldsError();

    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_CREATE_EXAM_PAPER + "/exam-paper",
        method: "POST",
        data: data,
        contentType: false,
        processData: false,
        success: function (responseBody) {
            showToastSuccess(responseBody.message);
            redirectChooseExamPaper();
            hideLoading();
        },
        error: function (error) {
            const myError = error?.responseJSON;
            $('.form-control').removeClass('is-invalid');

            if (myError?.length > 0) {
                myError.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}`).addClass('is-invalid');
                });
            } else if (myError.message) {
                showToastError(myError.message);
            } else {
                showToastError("Có lỗi xảy ra");
            }
            hideLoading();
        }
    });
};

const redirectChooseExamPaper = () => {
    window.location.href = "/head-subject/choose-exam-paper";
}