// START: state
const pdfjsLib = window["pdfjs-dist/build/pdf"];
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

            showUIFilePDF(blob, pdfjsLib, "pdf-viewer");

            $("#pdf-viewer").attr("hidden", false);

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
        $("#pdf-viewer").attr("hidden", false);
        if (file) {
            setExamPaperFile(file);
            showLoading();
            const fileType = file.type;
            if (fileType === "application/pdf") {
                showUIFilePDF(file, pdfjsLib, "pdf-viewer");
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

const showViewByStatus = () => {
    $("#examPaperTitle").text("Chi tiết đề thi");

    $("#modal-footer").prop("hidden", true);

    $("#file-pdf-input").val("");

    $("#pdf-viewer").attr("hidden", true);

    pageNum = 1;
};


