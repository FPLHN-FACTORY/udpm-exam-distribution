$(document).ready(() => {
    fetchFileExamPaper();
});

const fetchFileExamPaper = async () => {
    showLoading();

    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_UPDATE_EXAM_PAPER + "/file/" + sessionStorage.getItem(EXAM_DISTRIBUTION_EDIT_FILE_EXAM_PAPER_ID),
        method: "GET",
        success: (responseBody) => {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));

            const blob = new Blob([pdfData], {type: 'application/pdf'});
            const file = new File([blob], "exam_rule.pdf", {type: 'application/pdf'});

            showFilePdf(file);

            hideLoading();
        },
        error: (error) => {
            const myError = error?.responseJSON;
            if (myError.message) {
                showToastError(myError.message);
            } else {
                showToastError("Có lỗi xảy ra");
            }
            hideLoading();
        }
    });
};

