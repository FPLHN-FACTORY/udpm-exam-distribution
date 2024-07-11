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
let stateSubjectId = "";
// END: state

// START: getter
const getStateSubjectId = () => stateSubjectId;
// END: getter

// START: setter
const setStateSubjectId = (subjectId) => {
    stateSubjectId = subjectId;
};
// END: setter

const getValueFileInput = () => fileExamRule;

const setValueFileInput = (value) => {
    fileExamRule = value;
};

const clearValueFileInput = () => {
    $("#file-pdf-input").val("");
    fileExamRule = new File([], "emptyFile");
};

const handleOpenModalExamRule = (subjectId) => {
    setStateSubjectId(subjectId);

    clearValueFileInput();

    //reset lai page 1
    pageNum = 1;
    // ẩn đi view pdf và paging của nó
    handleSolveViewWhenOpenModal();
    $("#examRuleModal").modal("show");
};

const handleSolveViewWhenOpenModal = () => { // ẩn đi view pdf và paging của nó
    $("#paging-pdf").prop("hidden", true);
    $("#pdf-viewer").prop("hidden", true);

    $("#word-container").prop("hidden", true);
};

const showViewAndPagingPdf = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
    $("#pdf-viewer").prop("hidden", false);
    if (totalPage > 1) {
        $("#paging-pdf").prop("hidden", false);
    }
};

const handleOpenChooseFilePdf = () => {
    const pdfFile = $("#file-pdf-input");
    pdfFile.click();
};

const handleConfirmUploadExamRule = () => {
    console.log(getValueFileInput());
    if (getValueFileInput().size === 0 || getValueFileInput().name === "emptyFile") {
        showToastError("Nội quy thi chưa được tải");
    } else {
        swal({
            title: "Xác nhận",
            text: "Bạn có chắc muốn tải đề thi này không?",
            type: "warning",
            buttons: {
                cancel: {
                    visible: true,
                    text: "Hủy",
                    className: "btn btn-black",
                },
                confirm: {
                    text: "Xác nhận",
                    className: "btn btn-secondary",
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
    showLoading();

    const data = new FormData();
    data.append("file", getValueFileInput());

    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_EXAM_RULE + "/upload/" + getStateSubjectId(),
        data: data,
        contentType: false,
        processData: false,
        success: function (responseBody) {
            showToastSuccess(responseBody.message);

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
                        renderPage(pageNum, true);

                        showViewAndPagingPdf(pdfDoc.numPages);
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

function renderPage(num) {
    pageRendering = true;
    pdfDoc.getPage(num).then(function (page) {
        const viewport = page.getViewport({scale: scale});
        $pdfCanvas.height = viewport.height;
        $pdfCanvas.width = viewport.width;

        const renderContext = {
            canvasContext: ctx,
            viewport: viewport,
        };
        const renderTask = page.render(renderContext);

        renderTask.promise.then(function () {
            pageRendering = false;
            if (pageNumPending !== null) {
                renderPage(pageNumPending);
                pageNumPending = null;
            }
        });
    });

    $("#page-num").text(num);
}

function queueRenderPage(num) {
    if (pageRendering) {
        pageNumPending = num;
    } else {
        renderPage(num);
    }
}

$("#prev-page").on("click", function () {
    if (pageNum <= 1) {
        return;
    }
    pageNum--;
    queueRenderPage(pageNum);
});

$("#next-page").on("click", function () {
    if (pageNum >= pdfDoc.numPages) {
        return;
    }
    pageNum++;
    queueRenderPage(pageNum);
});