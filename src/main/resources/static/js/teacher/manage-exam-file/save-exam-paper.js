const handleResetFieldsError = () => {
    $('.form-control').removeClass('is-invalid');
    $("#examPaperTypeError").text("");
    $("#subjectIdError").text("");
};

//button save exam_paper pdf
$("#save-exam_paper").on("click", () => {
    handleSaveExamPaperConfirm();
});

const handleSaveExamPaperConfirm = async () => {
    if (getTinyContentTeacher() && (getTinyContentTeacher() + '').trim().length === 0) {
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
            handleSaveExamPaperTeacher();
        }
    });
};

const handleSaveExamPaperTeacher = async () => {
    showLoading();
    const data = new FormData();
    data.append("file", await convertTinyContentToPdfTeacher());
    data.append("contentFile", getTinyContentTeacher());
    let url = window.location.href;
    let subjectId = url.split('/').pop()?.trim();
    $.ajax({
        type: "POST",
        url: ApiConstant.API_TEACHER_EXAM_FILE + "/upload/" + subjectId,
        data: data,
        contentType: false, // Không đặt kiểu nội dung vì nó sẽ được thiết lập tự động
        processData: false, // Không xử lý dữ liệu, để nguyên dạng `FormData`
        success: function (responseBody) {
            showToastSuccess(responseBody?.message);
            hideLoading();
            localStorage.setItem('messageCreateExamPaper', responseBody?.message);
            redirectUploadExamPaperTeacher(subjectId);
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            } else {
                showToastError('Có lỗi xảy ra');
            }
            hideLoading();
        },
    });
};

const redirectUploadExamPaperTeacher = (subjectId) => {
    window.location.href = ApiConstant.REDIRECT_TEACHER_EXAM_FILE + "/subject/" + subjectId;
}

$(document).ready(function () {
    detailSubject();
})

function detailSubject() {
    let url = window.location.href;
    let subjectId = url.split('/').pop()?.trim();

    let api = ApiConstant.API_TEACHER_EXAM_FILE + '/subject/' + subjectId;

    $.ajax({
        type: "GET",
        url: api,
        success: function (responseBody) {
            const detail = responseBody?.data;
            $('#subjectCode').text(detail?.subjectCode);
            $('#subjectName').text(detail?.subjectName);
            $('#subjectDepartment').text(detail?.departmentName);
            $('#subjectType').text(detail?.subjectType);
            $('#subjectUploaded').text(detail?.uploaded + "/" + detail?.maxUpload);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học!');
        }
    });
}