$(document).ready(() => {
    fetchFileExamPaper();
});

const fetchFileExamPaper = async () => {
    showLoading();

    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_UPDATE_EXAM_PAPER + "/file/" + sessionStorage.getItem(EXAM_DISTRIBUTION_EDIT_FILE_EXAM_PAPER_ID),
        method: "GET",
        success: (responseBody) => {
            const fileName = responseBody?.data?.isPdf;
            if(fileName.endsWith('.docx')){
                convertAndShowDocx(responseBody.data.data);
            }else{
                convertAndShowPdf(responseBody.data.data);
            }
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

const convertAndShowDocx = (base64) => {
    const binaryString = atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
        bytes[i] = binaryString.charCodeAt(i);
    }

    const decoder = new TextDecoder('utf-8');
    editor.value = decoder.decode(bytes);
};

const convertAndShowPdf = (base64) => {
    const pdfData = Uint8Array.from(atob(base64), c => c.charCodeAt(0));

    const blob = new Blob([pdfData], {type: 'application/pdf'});
    const file = new File([blob], "exam_rule.pdf", {type: 'application/pdf'});
    showFilePdf(file);
}