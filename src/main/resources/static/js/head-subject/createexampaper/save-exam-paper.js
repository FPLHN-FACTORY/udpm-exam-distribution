//START: getter
const getValueMajorFacilityId = () => $("#major-facility").val();
const getValueExamPaperType = () => $("#exam-paper-type").val();
const getValueExamPaperSubjectId = () => $("#exam-paper-subject").val();
//END: getter


const handleOpenModalSaveExamPaper = () => {
    handleResetFieldsError();
    clearFieldsChoose();
    $("#saveExamPaperModal").modal("show");
};

const convertJoditContentToPdf = async () => {
    const content = editor.getEditorValue();
    const element = document.createElement('div');
    element.innerHTML = content;

    // Sử dụng html2pdf để chuyển đổi nội dung HTML thành Blob
    const opt = {
        margin: 0.5,
        filename: 'document.pdf',
        image: {type: 'png', quality: 1},
        html2canvas: {scale: 2},
        jsPDF: {unit: 'in', format: 'a4', orientation: 'portrait'},
    };

    try {
        const pdfBlob = await html2pdf().set(opt).from(element).outputPdf('blob');

        return new File([pdfBlob], "document.pdf", {type: "application/pdf"});

    } catch (error) {
        showToastError("Convert PDF không thành công");
    }
}

const convertJoditContentToDocx = () => {
    const content = editor.getEditorValue();
    const converted = htmlDocx.asBlob(content);
    // Create a File object from the Blob
    return new File([converted], "document.docx", {type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"});
    // For demonstration purposes, log the file object to the console
}

const handleResetFieldsError = () => {
    $('.form-control').removeClass('is-invalid');
    $("#examPaperTypeError").text("");
    $("#majorFacilityIdError").text("");
    $("#subjectIdError").text("");
};

const clearFieldsChoose = () => {
    $("#major-facility").val("");
    $("#exam-paper-type").val("");
    $("#exam-paper-subject").val("");
};

//button save exam_paper pdf
$("#save-exam_paper-pdf").on("click", () => {
    handleSaveExamPaperConfirm(0);
});

//button save exam_paper docx
$("#save-exam_paper-docx").on("click", () => {
    handleSaveExamPaperConfirm(1);
});

const handleSaveExamPaperConfirm = async (status) => {
    swal({
        title: "Xác nhận",
        text: "Bạn có chắc muốn lưu đề thi thi này không?",
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
            handleSaveExamPaper(status);
        }
    });
};

const handleSaveExamPaper = async (status) => {
    const data = new FormData();

    data.append("examPaperType", getValueExamPaperType());
    data.append("majorFacilityId", getValueMajorFacilityId());
    data.append("subjectId", getValueExamPaperSubjectId())
    if (status === 0) {
        data.append("file", await convertJoditContentToPdf());
    } else {
        data.append("file", await convertJoditContentToDocx());
    }

    handleResetFieldsError();

    showLoading();
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