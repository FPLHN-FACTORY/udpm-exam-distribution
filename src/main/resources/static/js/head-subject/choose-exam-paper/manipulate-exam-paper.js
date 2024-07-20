// START: state
const pdfjsLib = window["pdfjs-dist/build/pdf"];
let pdfDoc = null;
let pageNum = 1;
let pageRendering = false;
let pageNumPending = null;
const scale = 1.5;
const $pdfCanvas = $("#pdf-canvas")[0];
const ctx = $pdfCanvas.getContext("2d");
// END: state

//START: getter,setter
let examPaperFile = new File([], "");
let stateExamPaperId = "";
let statePostOrPutExamPaper = true; //true -> post, false -> put

const getValueMajorFacilityId = () => $("#major-facility").val();
const getValueExamPaperType = () => $("#exam-paper-type").val();
const getValueExamPaperSubjectId = () => $("#exam-paper-subject").val();
const getValueExamPaperId = () => stateExamPaperId;
const getExamPaperFile = () => examPaperFile;
const getStatePostOrPutExamPaper = () => statePostOrPutExamPaper;

const setValueExamPaperType = (value) => {
    $("#exam-paper-type").val(value);
}
const setValueMajorFacilityId = (value) => {
    $("#major-facility").val(value);
};
const setValueExamPaperSubjectId = (value) => {
    $("#exam-paper-subject").val(value);
};
const setValueExamPaperId = (value) => {
    stateExamPaperId = value;
};
const setExamPaperFile = (value) => {
    examPaperFile = value;
};
const setStatePostOrPutExamPaper = (value) => {
    statePostOrPutExamPaper = value;
};
//END: getter,setter

const clearFieldsChoose = () => {
    $("#major-facility").val("");
    $("#exam-paper-type").val("");
    $("#exam-paper-subject").val("");
};

const handleOpenChooseFilePdf = () => {
    const pdfFile = $("#file-pdf-input");
    pdfFile.click();
};

const handlePostOrPutExamPaperConfirm = () => {
    swal({
        title: getStatePostOrPutExamPaper() === false ? "Xác nhận cập nhật?" : "Xác nhận thêm",
        text: getStatePostOrPutExamPaper() === false ? "Bạn có chắc muốn cập nhật đề thi này không?" : "Bạn có chắc muốn thêm đề thi này không?",
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
            handlePostOrPutExamPaper();
        }
    });
};

const handlePostOrPutExamPaper = () => {
    const data = new FormData();
    if (getStatePostOrPutExamPaper() === false) {
        data.append("examPaperId", getValueExamPaperId());
    }
    data.append("examPaperType", getValueExamPaperType());
    data.append("majorFacilityId", getValueMajorFacilityId());
    data.append("subjectId", getValueExamPaperSubjectId())
    data.append("file", getExamPaperFile());

    handleResetFieldsError();

    showLoading();
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/exam-paper",
        method: getStatePostOrPutExamPaper() === false ? "PUT" : "POST",
        data: data,
        contentType: false,
        processData: false,
        success: function (responseBody) {
            showToastSuccess(responseBody.message);

            fetchListExamPaper();

            closeModalExamPaper();

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

const handleResetFieldsError = () => {
    $('.form-control').removeClass('is-invalid');
    $("#examPaperTypeError").text("");
    $("#majorFacilityIdError").text("");
    $("#subjectIdError").text("");
};

const closeModalExamPaper = () => {
    $("#examPaperModal").modal("hide");
};

const handleOpenModalExamPaper = (fileId, status, examPaperType, majorFacilityId, subjectId, examPaperId) => { //status: 1 -> detail, 2 -> edit, 3 -> add
    setStatePostOrPutExamPaper(status !== 2);

    showViewByStatus(status);
    handleResetFieldsError();

    if (status !== 3) {
        handleFetchExamRulePDF(fileId);
        if (status === 2) {
            setValueExamPaperType(examPaperType);
            setValueMajorFacilityId(majorFacilityId);
            setValueExamPaperSubjectId(subjectId);
            setValueExamPaperId(examPaperId);
        }
    } else {
        clearFieldsChoose();
    }

    $("#examPaperModal").modal("show");
};

const showViewAndPagingPdf = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
    $("#show-pdf").prop("hidden", false);

    $("#pdf-viewer").prop("hidden", false);
    if (totalPage > 1) {
        $("#paging-pdf").prop("hidden", false);
    }
};

const handleFetchExamRulePDF = (fileId) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/file",
        data: {
            fileId: fileId,
        },
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));

            const blob = new Blob([pdfData], {type: 'application/pdf'});
            const file = new File([blob], "exam_rule.pdf", {type: 'application/pdf'});
            setExamPaperFile(file);

            renderPdfInView(pdfData);

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

const onChangeChoosePDFFile = () => {
    $("#file-pdf-input").on("change", (e) => {
        const file = e.target.files[0];
        if (file) {
            setExamPaperFile(file);
            showLoading();
            const fileType = file.type;
            if (fileType === "application/pdf") {
                const fileReader = new FileReader();
                fileReader.onload = function () {
                    const pdfData = new Uint8Array(this.result);
                    renderPdfInView(pdfData);
                };
                fileReader.readAsArrayBuffer(file);
            } else {
                showToastError("Vui lòng chọn file định dạng PDF");
            }
            hideLoading();
        }
    });
};
onChangeChoosePDFFile();

const handleDownloadExamPaper = (fileId) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/file",
        data: {
            fileId: fileId,
        },
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));
            const blob = new Blob([pdfData], {type: 'application/pdf'});
            // Tạo đối tượng URL từ Blob
            const url = URL.createObjectURL(blob);

            // Tạo và nhấp vào liên kết để tải tệp
            const link = document.createElement('a');
            link.href = url;
            link.download = 'file.pdf'; // Đặt tên cho file tải về
            document.body.appendChild(link);
            link.click();

            // Xóa đối tượng URL sau khi tải
            URL.revokeObjectURL(url);
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

// Render the page
const renderPdfInView = (data) => {
    pdfjsLib
        .getDocument({data: data})
        .promise.then(function (pdfDoc_) {
        pdfDoc = pdfDoc_;
        $("#total-page").text(pdfDoc.numPages);

        renderPage(pageNum);
        showViewAndPagingPdf(pdfDoc.numPages);
    });
}

const renderPage = (num) => {
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
                renderPageDetail(pageNumPending);
                pageNumPending = null;
            }
        });
    });

    $("#page-num").text(num);
}

const queueRenderPage = (num) => {
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

const showViewByStatus = (status) => {
    $("#examPaperTitle").text(status === 1 ? "Chi tiết đề thi" : status === 2 ? "Cập nhật đề thi" : status === 3 ? "Thêm đề thi" : "")

    $("#paging-pdf").prop("hidden", true);

    $("#view-add-edit").prop("hidden", !(status === 2 || status === 3));
    $("#modal-footer").prop("hidden", !(status === 2 || status === 3));

    $("#show-pdf").prop("hidden", true);

    $("#file-pdf-input").val("");
    //reset lai page 1
    pageNum = 1;
    setExamPaperFile(new File([], ""));
};


