// START: state
const pdfjsLib = window["pdfjs-dist/build/pdf"];
// END: state

//START: getter,setter
let examPaperFile = [];
let stateExamPaperId = "";
let statePostOrPutExamPaper = true; //true -> post, false -> put

const getValueExamPaperType = () => $("#exam-paper-type").val();
const getValueExamPaperSubjectId = () => $("#exam-paper-subject").val();
const getValueExamPaperId = () => stateExamPaperId;
const getExamPaperFile = () => examPaperFile;
const getStatePostOrPutExamPaper = () => statePostOrPutExamPaper;

const setValueExamPaperType = (value) => {
    $("#exam-paper-type").val(value);
}
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
    $("#exam-paper-type").val("");
    $("#exam-paper-subject").val("");
};

const handleOpenChooseFilePdf = () => {
    $("#file-pdf-input").click();
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
    data.append("subjectId", getValueExamPaperSubjectId())
    for (let i = 0; i < getExamPaperFile().length; i++) {
        data.append('file', getExamPaperFile()[i]);
    }

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

    $("#fileNames").empty();

    if (status !== 3) {
        handleFetchExamPaperPDF(fileId);
        if (status === 2) {
            setValueExamPaperType(examPaperType);
            setValueExamPaperSubjectId(subjectId);
            setValueExamPaperId(examPaperId);
        }
    } else {
        clearFieldsChoose();
    }

    $("#examPaperModal").modal("show");
};

const handleRedirectUpdateContentFile = (id) => {
    sessionStorage.setItem(EXAM_DISTRIBUTION_EDIT_FILE_EXAM_PAPER_ID, id);
    window.location.href = "/head-subject/update-exam-paper";
}

const handleRedirectCreateExamPaper = () => {
    window.location.href = "/head-subject/create-exam-paper";
}

const handleFetchExamPaperPDF = (fileId) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/file",
        data: {
            fileId: fileId
        },
        success: function (responseBody) {
            const data = responseBody?.data?.data;

            convertAndShowPdf(data);

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
    setExamPaperFile([
        ...getExamPaperFile(),
        file
    ]);

    showUIFilePDF(file, pdfjsLib, "pdf-viewer");

    $("#pdf-viewer").attr("hidden", false);
};


const onChangeChoosePDFFile = () => {
    $("#file-pdf-input").on("change", function (e) {
        let files = this.files;

        if (files.length === 0) {
            return;
        }

        let fileNames = $("#fileNames");
        fileNames.empty();

        setExamPaperFile(files);

        if (files.length > 3) {
            showToastError("Chỉ có thể chọn tối đa 3 file PDF.");
            this.value = "";
            return;
        }

        $("#pdf-viewer").attr("hidden", false);
        showUIFilePDF(files[0], pdfjsLib, "pdf-viewer");

        for (let i = 0; i < files.length; i++) {
            let file = files[i];
            if (file.type === "application/pdf") {
                let fileSize = (file.size / 1024 / 1024).toFixed(2);
                let fileElement = $(
                    "<p style='margin-top: 10px; margin-bottom: 10px;cursor: pointer; text-decoration-line: underline'>" + file.name + " (" + fileSize + " MB)" + "</p>"
                );

                fileElement.on("click", function () {
                    showUIFilePDF(file, pdfjsLib, "pdf-viewer");
                });

                fileNames.append(fileElement);
            } else {
                showToastError("Vui lòng chọn file định dạng PDF");
                this.value = ""; // Reset file input
                fileNames.empty();
                setExamPaperFile([]);
                $("#pdf-viewer").attr("hidden", true);
                $("#paging-pdf").attr("hidden", true);
                break;
            }
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
                url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/file",
                data: {
                    fileId: fileId
                },
                success: function (responseBody) {
                    const pdfData = Uint8Array.from(atob(responseBody.data.data), c => c.charCodeAt(0));
                    const blob = new Blob([pdfData], {type: 'application/pdf'});
                    const url = URL.createObjectURL(blob);

                    const link = document.createElement('a');
                    link.href = url;
                    link.download = 'file.pdf';
                    document.body.appendChild(link);
                    link.click();

                    URL.revokeObjectURL(url);

                    $("#confirmDownloadModal").modal("hide");
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

const showViewByStatus = (status) => {
    $("#examPaperTitle").text(status === 1 ? "Chi tiết đề thi" : status === 2 ? "Cập nhật đề thi" : status === 3 ? "Thêm đề thi" : "")

    $("#view-add-edit").prop("hidden", !(status === 2 || status === 3));
    $("#modal-footer").prop("hidden", !(status === 2 || status === 3));

    $("#file-pdf-input").val("");

    $("#pdf-viewer").attr("hidden", true);

    $("#file-pdf-input").attr("multiple", status !== 2);

    setExamPaperFile([]);
};


