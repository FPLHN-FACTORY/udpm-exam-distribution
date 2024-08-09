// START: state
const pdfjsLibSEP = window["pdfjs-dist/build/pdf"];

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

    $("#pdf-viewer-sep").attr("hidden", true);

    if (status === 2) {
        if (fileId !== "null") {
            if (status === 2) {
                handleFetchSampleExamRulePDF(fileId);
            }

            showViewSEPByStatus(status);
            $("#sampleExamPaperFooter").attr("hidden", true);
            $("#sampleExamPaperModal").modal("show");
        } else {
            showToastError("Môn học này chưa có đề thi mẫu");
        }
    } else {
        $("#sampleExamPaperFooter").attr("hidden", false);
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
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0))

            const file = new File([pdfData], "exam_rule.pdf", {
                type: 'application/pdf',
                lastModified: new Date()
            });

            showUIFilePDF(file, pdfjsLibSEP, "pdf-viewer-sep");

            $("#pdf-viewer-sep").attr("hidden", false);

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
                showUIFilePDF(file, pdfjsLibSEP, "pdf-viewer-sep");
                $("#pdf-viewer-sep").attr("hidden", false);
            } else {
                showToastError("Vui lòng chọn file định dạng PDF");
            }
            hideLoading();
        }
    });
};
onChangeChoosePDFFileSEP();

const showViewAndPagingPdfSEP = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
    $("#pdf-viewer-sep").prop("hidden", false);

    $("#paging-pdf-sep").prop("hidden", totalPage <= 1);
};

const showViewSEPByStatus = (status) => {
    $("#button-upload-sep").prop("hidden", status === 2);

    $("#sampleExamPaperTitle").text(status === 1 ? "Tải đề thi mẫu" : "Chi tiết đề thi mẫu");
};

const handleUploadSampleExamPaperConfirm = () => {
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
};

const handleUploadSampleExamPaper = () => {
    const data = new FormData();
    data.append("subjectId", getStateValueCurrentSubjectId())
    data.append("file", getStateValueInputSampleExamPaper());

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