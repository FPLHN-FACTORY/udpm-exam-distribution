// START: state
const pdfjsLibDetail = window["pdfjs-dist/build/pdf"];
let pdfDocDetail = null;
let pageNumDetail = 1;
let pageRenderingDetail = false;
let pageNumPendingDetail = null;
const scaleDetail = 1.5;
const $pdfCanvasDetail = $("#pdf-canvas-detail")[0];
const ctxDetail = $pdfCanvasDetail.getContext("2d");

let stateSubjectIdDetail = "";
// END: state

//START: getter
const getStateSubjectIdDetail = () => stateSubjectIdDetail;
//END: getter

//START: setter
const setStateSubjectIdDetail = (value) => {
    stateSubjectIdDetail = value;
}

//END: setter

function handleDetail(subjectId) {
    handleSolveViewWhenOpenModalDetail();

    //reset laij page 1
    pageNumDetail = 1;
    handleFetchSampleExam(subjectId);

    $("#detailSampleExamModal").modal("show");
}

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

const handleFetchSampleExam = (subjectId) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_EXAM_FILE + "/sample-exam-paper/" + subjectId,
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));
            pdfjsLibDetail
                .getDocument({data: pdfData})
                .promise.then(function (pdfDoc_) {
                pdfDocDetail = pdfDoc_;
                $("#total-page-detail").text(pdfDocDetail.numPages);

                renderPageDetail(pageNumDetail);
                showViewAndPagingPdfDetail(pdfDocDetail.numPages);

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

const handleDownloadExamPaper = (subjectId) => {
    swal({
        title: "Xác nhận download đề thi mẫu?",
        text: "Bạn chắc chắn muốn download đề thi mẫu không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Download",
                className: "btn btn-black",
            },
        },
    }).then((ok) => {
        if (ok) {
            showLoading();
            $.ajax({
                type: "GET",
                url: ApiConstant.API_TEACHER_EXAM_FILE + "/sample-exam-paper/" + subjectId,
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
        }
    });
};

// Render the page
function renderPageDetail(num) {
    pageRenderingDetail = true;
    pdfDocDetail.getPage(num).then(function (page) {
        const viewport = page.getViewport({scale: scaleDetail});
        $pdfCanvasDetail.height = viewport.height;
        $pdfCanvasDetail.width = viewport.width;

        const renderContext = {
            canvasContext: ctxDetail,
            viewport: viewport,
        };
        const renderTask = page.render(renderContext);

        renderTask.promise.then(function () {
            pageRenderingDetail = false;
            if (pageNumPendingDetail !== null) {
                renderPageDetail(pageNumPendingDetail);
                pageNumPendingDetail = null;
            }
        });
    });

    $("#page-num-detail").text(num);
}

function queueRenderPageDetail(num) {
    if (pageRenderingDetail) {
        pageNumPendingDetail = num;
    } else {
        renderPageDetail(num);
    }
}

$("#prev-page-detail").on("click", function () {
    if (pageNumDetail <= 1) {
        return;
    }
    pageNumDetail--;
    queueRenderPageDetail(pageNumDetail);
});

$("#next-page-detail").on("click", function () {
    if (pageNumDetail >= pdfDocDetail.numPages) {
        return;
    }
    pageNumDetail++;
    queueRenderPageDetail(pageNumDetail);
});