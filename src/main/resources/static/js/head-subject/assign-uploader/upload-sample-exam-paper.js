// START: state
const pdfjsLibSEP = window["pdfjs-dist/build/pdf"];
let pdfDocSEP = null;
let pageNumSEP = 1;
let pageRenderingSEP = false;
let pageNumPendingSEP = null;
const scaleSEP = 1.5;
const $pdfCanvasSEP = $("#pdf-canvas-sep")[0];
const ctxSEP = $pdfCanvasSEP.getContext("2d");

let stateValueInputSampleExamPaper = new File([], "");
let stateValueCurrentSubjectId = "";
// END: state

// START: getter
const getStateValueInputSampleExamPaper = () => stateValueInputSampleExamPaper;
const getStateValueCurrentSubjectId = () => stateValueCurrentSubjectId;
// END: getter

// START: setter
const setStateValueInputSampleExamPaper = (value) => {
    stateValueInputSampleExamPaper = value;
};
const setStateValueCurrentSubjectId = (value) => {
    stateValueCurrentSubjectId = value;
};
const clearValueFileInput = () => {
    $("#file-pdf-input-sep").val("");
};
// END: setter

const handleOpenModalSampleExamPaper = (status, subjectId, fileId) => { // 1 -> edit , 2 -> detail
    setStateValueInputSampleExamPaper(new File([], ""));
    setStateValueCurrentSubjectId(subjectId);
    clearValueFileInput();

    if (status === 2) {
        if (fileId !== "null") {
            if (status === 2) {
                handleFetchSampleExamRulePDF(fileId);
            }

            showViewSEPByStatus(status);
            $("#sampleExamPaperModal").modal("show");
        } else {
            showToastError("Môn học này chưa có đề thi mẫu");
        }
    } else {
        showViewSEPByStatus(status);
        $("#sampleExamPaperModal").modal("show");
    }

};

const handleFetchSampleExamRulePDF = (fileId) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER + "/file",
        data: {
            fileId: fileId,
        },
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));

            renderPdfInViewSEP(pdfData);

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

const openInputChooseFileFdf = () => {
    $("#file-pdf-input-sep").click();
};

const onChangeChoosePDFFileSEP = () => {
    $("#file-pdf-input-sep").on("change", (e) => {
        const file = e.target.files[0];
        if (file) {
            showLoading();
            const fileType = file.type;
            if (fileType === "application/pdf") {
                setStateValueInputSampleExamPaper(file);

                const fileReader = new FileReader();
                fileReader.onload = function () {
                    const pdfData = new Uint8Array(this.result);
                    renderPdfInViewSEP(pdfData);
                };
                fileReader.readAsArrayBuffer(file);
            } else {
                showToastError("Vui lòng chọn file định dạng PDF");
            }
            hideLoading();
        }
    });
};
onChangeChoosePDFFileSEP();

// Render the page
const renderPdfInViewSEP = (data) => {
    pdfjsLibSEP
        .getDocument({data: data})
        .promise.then(function (pdfDoc_) {
        pdfDocSEP = pdfDoc_;
        $("#total-page-sep").text(pdfDocSEP.numPages);

        renderPageSEP(pageNumSEP);
        showViewAndPagingPdfSEP(pdfDocSEP.numPages);
    });
}

const showViewAndPagingPdfSEP = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
    $("#pdf-viewer-sep").prop("hidden", false);

    $("#paging-pdf-sep").prop("hidden", totalPage <= 1);
};

const renderPageSEP = (num) => {
    pageRenderingSEP = true;
    pdfDocSEP.getPage(num).then(function (page) {
        const viewport = page.getViewport({scale: scaleSEP});
        $pdfCanvasSEP.height = viewport.height;
        $pdfCanvasSEP.width = viewport.width;

        const renderContext = {
            canvasContext: ctxSEP,
            viewport: viewport,
        };
        const renderTask = page.render(renderContext);

        renderTask.promise.then(function () {
            pageRenderingSEP = false;
            if (pageNumPendingSEP !== null) {
                renderPageDetail(pageNumPendingSEP);
                pageNumPendingSEP = null;
            }
        });
    });

    $("#page-num-sep").text(num);
}

const queueRenderPagesEP = (num) => {
    if (pageRenderingSEP) {
        pageNumPendingSEP = num;
    } else {
        renderPageSEP(num);
    }
}

$("#prev-page-sep").on("click", function () {
    if (pageNumSEP <= 1) {
        return;
    }
    pageNumSEP--;
    queueRenderPagesEP(pageNumSEP);
});

$("#next-page-sep").on("click", function () {
    if (pageNumSEP >= pdfDocSEP.numPages) {
        return;
    }
    pageNumSEP++;
    queueRenderPagesEP(pageNumSEP);
});

const showViewSEPByStatus = (status) => {
    pageNumSEP = 1;

    $("#button-upload-sep").prop("hidden", status === 2);

    $("#pdf-viewer-sep").prop("hidden", true);
    $("#paging-pdf-sep").prop("hidden", true);

    $("#sampleExamPaperTitle").text(status === 1 ? "Tải đề thi mẫu" : "Chi tiết đề thi mẫu");
    if (status === 1) {
        if ($('#major-facility-container').length === 0) {  // Kiểm tra nếu phần tử chưa tồn tại
            let majorFacilityUpload = startMajorFacilityUpload+getMiddleMajorFacilityUpload()+endMajorFacilityUpload;
            $('#modal-body-upload').prepend(majorFacilityUpload);
        }
    } else {
        $('#major-facility-container').remove();
    }
};

const handleUploadSampleExamPaperConfirm = () => {
    let check = true;
    if ($('#major-facility-upload').val()?.trim().length === 0) {
        check = false;
        $('#major-facility-upload-error').text("Chọn chuyên ngành - cơ sở");
    } else {
        $('#major-facility-upload-error').text('');
    }
    if (check) {
        swal({
            title: "Xác nhận",
            text: "Bạn có chắc muốn tải lên đề thi mẫu cho môn học này không?",
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
                handleUploadSampleExamPaper();
            }
        });
    }
};

const handleUploadSampleExamPaper = () => {
    const data = new FormData();
    data.append("subjectId", getStateValueCurrentSubjectId())
    data.append("file", getStateValueInputSampleExamPaper());
    data.append("majorFacilityId", $('#major-facility-upload').val());

    showLoading();
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER + "/sample-exam-paper",
        method: "POST",
        data: data,
        contentType: false,
        processData: false,
        success: function (responseBody) {
            showToastSuccess(responseBody.message);

            fetchSearchSubject();

            $("#sampleExamPaperModal").modal("hide");

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
}