//-------------------------------------------------Upload ExamRule------------------------------------------------------

// START: state
const pdfjsLib = window["pdfjs-dist/build/pdf"];
let pdfDoc = null;
let pageNum = 1;
let pageRendering = false;
let pageNumPending = null;
const scale = 1.5;
const $pdfCanvas = $("#pdf-canvas")[0];
const ctx = $pdfCanvas.getContext("2d");

let fileExamRule = new File([], "emptyFile");
let stateAddOrUpdateExamRule;
let stateExamRuleId;
// END: state

// START: getter
const getValueFileInput = () => fileExamRule;
const getExamRuleName = () => $("#exam-rule-name").val();
const getStateAddOrUpdateExamRule = () => stateAddOrUpdateExamRule;
const getStateExamRuleId = () => stateExamRuleId;
// END: getter

// START: setter
const setValueFileInput = (value) => {
    fileExamRule = value;
};

const setExamRuleName = (value) => {
    $("#exam-rule-name").val(value);
};

const setStateAddOrUpdateExamRule = (value) => {
    stateAddOrUpdateExamRule = value;
};

const setStateExamRuleId = (value) => {
    stateExamRuleId = value;
};

const setStateExamRuleName = (value) => {
    $("#exam-rule-name").val(value);
}
// END: setter

const clearValueFileInput = () => {
    $("#file-pdf-input").val("");
    fileExamRule = new File([], "emptyFile");
};

const handleOpenModalExamRule = (status, examRuleId, examRuleName) => {
    clearValueFileInput();
    setExamRuleName("");
    handleResetFieldsError();

    setStateWhenOpenModal(status, examRuleId, examRuleName);
    if(status === 2){
        handleFetchExamRulePDF(examRuleId);
    }

    //reset lai page 1
    pageNum = 1;
    // ẩn đi view pdf và paging của nó
    handleSolveViewWhenOpenModal();
    $("#examRuleModal").modal("show");
};

const setStateWhenOpenModal = (status, examRuleId, examRuleName) => {
    $("#examRuleModalTitle").text(status === 1 ? "Thêm quy định thi" : "Cập nhật quy định thi");
    setStateAddOrUpdateExamRule(status);
    setStateExamRuleId(examRuleId);
    setStateExamRuleName(examRuleName);
};

const handleFetchExamRulePDF = (id) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_OFFICE_EXAM_RULE + "/file/" + id,
        success: function (responseBody) {
            convertAndShowPdf(responseBody);

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

const convertAndShowPdf = (data) => {
    const pdfData = Uint8Array.from(atob(data), c => c.charCodeAt(0));

    const blob = new Blob([pdfData], {type: 'application/pdf'});
    const file = new File([blob], "exam_rule.pdf", {type: 'application/pdf'});
    setValueFileInput(file);

    renderPdfInView(pdfData);
};

const renderPdfInView = (pdfData) => {
    pdfjsLib
        .getDocument({data: pdfData})
        .promise.then(function (pdfDoc_) {
        pdfDoc = pdfDoc_;

        $("#total-page").text(pdfDoc.numPages);

        renderPage(pdfDoc, pageNum, "#page-num", pageRendering, pageNumPending, scale, $pdfCanvas, ctx);

        showViewAndPagingPdf(pdfDoc.numPages, "#pdf-viewer", "#paging-pdf");
    });
};

const handleResetFieldsError = () => {
    $('.form-control').removeClass('is-invalid');
    $("#nameError").text("");
};

const handleSolveViewWhenOpenModal = () => { // ẩn đi view pdf và paging của nó
    $("#paging-pdf").prop("hidden", true);
    $("#pdf-viewer").prop("hidden", true);
};

const handleOpenChooseFilePdf = () => {
    const pdfFile = $("#file-pdf-input");
    pdfFile.click();
};

const handleConfirmUploadExamRule = () => {
    if (getValueFileInput().size === 0 || getValueFileInput().name === "emptyFile") {
        showToastError("Quy định phòng thi chưa được tải");
    } else {
        swal({
            title: "Xác nhận",
            text: getStateAddOrUpdateExamRule() === 1 ? "Bạn có chắc muốn tải đề thi này không?" :
                "Bạn có chắc muốn cập nhật đề thi này không?",
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
                handleUploadExamRule();
            }
        });
    }
};

const handleUploadExamRule = () => {
    handleResetFieldsError();

    showLoading();

    const data = new FormData();
    data.append("file", getValueFileInput());
    data.append("name", getExamRuleName());
    if (getStateAddOrUpdateExamRule() === 2) {
        data.append("id", getStateExamRuleId());
    }

    $.ajax({
        type: getStateAddOrUpdateExamRule() === 1 ? "POST" : "PUT",
        url: ApiConstant.API_HEAD_OFFICE_EXAM_RULE + "/exam-rule",
        data: data,
        contentType: false,
        processData: false,
        success: function (responseBody) {
            showToastSuccess(responseBody.message);

            fetchListExamRule();

            $("#examRuleModal").modal("hide");

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
        },
    });
};
//----------------------------------------------------------------------------------------------------------------------

$(document).ready(() => {
    $("#file-pdf-input").on("change", (e) => {
        const file = e.target.files[0];
        if (file) {
            showLoading();
            const fileType = file.type;
            setValueFileInput(file);
            if (fileType === "application/pdf") {
                const fileReader = new FileReader();
                fileReader.onload = function () {
                    const pdfData = new Uint8Array(this.result);
                    pdfjsLib
                        .getDocument({data: pdfData})
                        .promise.then(function (pdfDoc_) {
                        pdfDoc = pdfDoc_;

                        $("#total-page").text(pdfDoc.numPages);

                        renderPage(pdfDoc, pageNum, "#page-num", pageRendering, pageNumPending, scale, $pdfCanvas, ctx);

                        showViewAndPagingPdf(pdfDoc.numPages, "#pdf-viewer", "#paging-pdf");
                    });
                };
                fileReader.readAsArrayBuffer(file);
                hideLoading();
            } else {
                showToastError("Vui lòng chọn file định dạng PDF");
            }
        }
    });
});

$("#prev-page").on("click", function () {
    if (pageNum <= 1) {
        return;
    }
    pageNum--;
    queueRenderPage(1, pdfDoc, pageNum, "#page-num", pageRendering, pageNumPending, scale, $pdfCanvas, ctx);
});

$("#next-page").on("click", function () {
    if (pageNum >= pdfDoc.numPages) {
        return;
    }
    pageNum++;
    queueRenderPage(1, pdfDoc, pageNum, "#page-num", pageRendering, pageNumPending, scale, $pdfCanvas, ctx);
});