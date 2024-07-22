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

    const images = element.getElementsByTagName("img");
    for (let img of images) {
        if (img.width > 700) {
            img.width = 700;
        }
    }

    const tables = element.getElementsByTagName("table");
    for (let table of tables) {
        if (table.style.width && parseInt(table.style.width) > 700) {
            table.style.width = '700px';
        }
        else {
            const computedWidth = window.getComputedStyle(table).width;
            if (parseInt(computedWidth) > 700) {
                table.style.width = '700px';
            }
        }
    }

    const modifiedContent = element.innerHTML;

    const options = {
        margin: 0.5,
        filename: 'document.pdf',
        image: {type: 'jpeg', quality: 1},
        html2canvas: {scale: 2},
        jsPDF: {unit: 'in', format: 'a4', orientation: 'portrait'},
    };

    try {
        const pdfBlob = await html2pdf().set(options).from(modifiedContent).outputPdf('blob');

        return new File([pdfBlob], "document.pdf", {type: "application/pdf"});

    } catch (error) {
        showToastError("Convert PDF không thành công");
    }
}

const convertJoditContentToDocx = () => {
    const content = editor.getEditorValue();

    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = content;
    const images = tempDiv.getElementsByTagName("img");
    for (let img of images) {
        if (img.width > 700) {
            img.width = 700;
        }
    }
    const modifiedContent = tempDiv.innerHTML;

    const header = "<html xmlns:o='urn:schemas-microsoft-com:office:office' " +
        "xmlns:w='urn:schemas-microsoft-com:office:word' " +
        "xmlns='http://www.w3.org/TR/REC-html40'>" +
        "<head><meta charset='utf-8'><title>Export HTML to Word Document with JavaScript</title></head><body>";
    const footer = "</body></html>";
    const sourceHTML = header + modifiedContent + footer;

    const blob = new Blob([sourceHTML], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });

    return new File([blob], 'document.docx', { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
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
    showLoading();

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