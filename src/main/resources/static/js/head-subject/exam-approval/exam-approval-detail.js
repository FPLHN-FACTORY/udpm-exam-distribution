// START: state
const pdfjsLib = window["pdfjs-dist/build/pdf"];

// END: state

function handleDetail(path) {
    $("#pdf-viewer").attr("hidden", true);

    handleFetchExamRule(path);

    $("#detailExamApprovalModal").modal("show");
}

const handleFetchExamRule = (path) => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL + "/file?path=" + path,
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