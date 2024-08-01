// START: state
const pdfjsLibDetail = window["pdfjs-dist/build/pdf"];
let pdfDocDetail = null;
let pageNumDetail = 1;
let pageRenderingDetail = false;
let pageNumPendingDetail = null;
const scaleDetail = 1.5;
const $pdfCanvasDetail = $("#pdf-canvas-detail")[0];
const ctxDetail = $pdfCanvasDetail.getContext("2d");

// END: state


const handleOpenModalDetailExamRule = (examRuleId) => {

    handleSolveViewWhenOpenModalDetail();

    //reset lai page 1
    pageNumDetail = 1;
    handleFetchExamRule(examRuleId);

    $("#detailExamRuleModal").modal("show");
};

const handleSolveViewWhenOpenModalDetail = () => { // ẩn đi view pdf và paging của nó
    $("#paging-pdf-detail").prop("hidden", true);
    $("#pdf-viewer-detail").prop("hidden", true);
};

const showViewAndPagingPdfDetail = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
    $("#pdf-viewer-detail").prop("hidden", false);
    if (totalPage > 1) {
        $("#paging-pdf-detail").prop("hidden", false);
    }
};

const handleFetchExamRule = (examRuleId) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_EXAM_RULE + "/file/" + examRuleId,
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));
            pdfjsLibDetail
                .getDocument({data: pdfData})
                .promise.then(function (pdfDoc_) {
                pdfDocDetail = pdfDoc_;
                $("#total-page-detail").text(pdfDocDetail.numPages);

                renderPage(pdfDocDetail, pageNumDetail, "#page-num-detail", pageRenderingDetail, pageNumPendingDetail, scaleDetail, $pdfCanvasDetail, ctxDetail);

                showViewAndPagingPdf(pdfDocDetail.numPages, "#pdf-viewer-detail", "#paging-pdf-detail");

                hideLoading();
            });
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

$("#prev-page-detail").on("click", function () {
    if (pageNumDetail <= 1) {
        return;
    }
    pageNumDetail--;
    queueRenderPage(1,pdfDocDetail, pageNumDetail, "#page-num-detail", pageRenderingDetail, pageNumPendingDetail, scaleDetail, $pdfCanvasDetail, ctxDetail);
});

$("#next-page-detail").on("click", function () {
    if (pageNumDetail >= pdfDocDetail.numPages) {
        return;
    }
    pageNumDetail++;
    queueRenderPage(1,pdfDocDetail, pageNumDetail, "#page-num-detail", pageRenderingDetail, pageNumPendingDetail, scaleDetail, $pdfCanvasDetail, ctxDetail);
});