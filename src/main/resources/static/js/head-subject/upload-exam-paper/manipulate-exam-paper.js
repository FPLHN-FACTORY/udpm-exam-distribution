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

//END: getter,setter

const handleOpenChooseFilePdf = () => {
    const pdfFile = $("#file-pdf-input");
    pdfFile.click();
};

const handleOpenModalExamPaper = (fileId) => {
    showViewByStatus();

    handleFetchExamPaperPDF(fileId);

    $("#examPaperModal").modal("show");
};

const handleFetchExamPaperPDF = (fileId) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/file",
        data: {
            fileId: fileId,
        },
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));

            const blob = new Blob([pdfData], {type: 'application/pdf'});
            const file = new File([blob], "exam_rule.pdf", {type: 'application/pdf'});

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
    swal({
        title: "Xác nhận",
        text: "Bạn có chắc muốn tải đề thi này với dạng PDF không?",
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
            showLoading();
            $.ajax({
                type: "GET",
                url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/file",
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
        }
    });
};

const handleSendEmailPublicExamPaper = (examPaperId) => {
    swal({
        title: "Xác nhận",
        text: "Bạn có chắc muốn công khai đề thi thử này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Công khai",
                className: "btn btn-black",
            },
        },
    }).then((willDelete) => {
        if (willDelete) {
            showLoading();
            $.ajax({
                type: "POST",
                url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/send-email-public-exam-paper/" + examPaperId,
                success: function (responseBody) {
                    showToastSuccess(responseBody?.message);
                    fetchListExamPaper();
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
const renderPdfInView = (data) => {
    pdfjsLib
        .getDocument({data: data})
        .promise.then(function (pdfDoc_) {
        pdfDoc = pdfDoc_;
        $("#total-page").text(pdfDoc.numPages);

        renderPageC(pageNum);
        showViewAndPagingPdfC(pdfDoc.numPages);
    });
}

const showViewAndPagingPdfC = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
    $("#show-pdf").prop("hidden", false);

    $("#pdf-viewer").prop("hidden", false);
    if (totalPage > 1) {
        $("#paging-pdf").prop("hidden", false);
    }
};

const renderPageC = (num) => {
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

const queueRenderPageC = (num) => {
    if (pageRendering) {
        pageNumPending = num;
    } else {
        renderPageC(num);
    }
}

$("#prev-page").on("click", function () {
    if (pageNum <= 1) {
        return;
    }
    pageNum--;
    queueRenderPageC(pageNum);
});

$("#next-page").on("click", function () {
    if (pageNum >= pdfDoc.numPages) {
        return;
    }
    pageNum++;
    queueRenderPageC(pageNum);
});

const showViewByStatus = () => {
    $("#examPaperTitle").text("Chi tiết đề thi");

    $("#paging-pdf").prop("hidden", true);

    $("#modal-footer").prop("hidden", true);

    $("#show-pdf").prop("hidden", true);

    $("#file-pdf-input").val("");

    pageNum = 1;
};


