$("#saveChangeEditExamPaper").on("click", () => {
    $("#modalChooseTypeFile").modal("show");
});

$("#save-exam_paper-pdf").on("click", () => {
    swal({
        title: "Xác nhận",
        text: "Bạn có chắc chắn muốn lưu chỉnh sửa này ở định dạng PDF không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Lưu chỉnh sửa",
                className: "btn btn-black",
            },
        },
    }).then((ok) => {
        if (ok) {
            saveChangeEditFileExamPaper(true);
        }
    });
});

$("#save-exam_paper-docx").on("click", () => {
    swal({
        title: "Xác nhận",
        text: "Bạn có chắc chắn muốn lưu chỉnh sửa này ở định dạng DOCX không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Lưu chỉnh sửa",
                className: "btn btn-black",
            },
        },
    }).then((ok) => {
        if (ok) {
            saveChangeEditFileExamPaper(false);
        }
    });
});

const saveChangeEditFileExamPaper = async (pdfOrDocx) => {
    showLoading();

    const data = new FormData();
    data.append("examPaperId", sessionStorage.getItem(EXAM_DISTRIBUTION_EDIT_FILE_EXAM_PAPER_ID));
    data.append("file", pdfOrDocx ? await convertJoditContentToPdf() : await convertJoditContentToDocx());

    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_UPDATE_EXAM_PAPER + "/edit-file",
        method: "PUT",
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
            if (myError.message) {
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


