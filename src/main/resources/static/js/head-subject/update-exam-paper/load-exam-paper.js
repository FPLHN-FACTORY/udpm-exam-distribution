$(document).ready(() => {
    tinymce.init({
        selector: "textarea#tiny-editor",
        plugins:
            "anchor autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount checklist mediaembed casechange export formatpainter pageembed linkchecker a11ychecker tinymcespellchecker permanentpen powerpaste advtable advcode editimage advtemplate ai mentions tinycomments tableofcontents footnotes mergetags autocorrect typography inlinecss markdown",
        toolbar:
            "undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat",
        tinycomments_mode: "embedded",
        tinycomments_author: "Author name",
        mergetags_list: [
            {value: "First.Name", title: "First Name"},
            {value: "Email", title: "Email"},
        ],
        height: "1000px",
        toolbar_sticky: true,
        icons: "thin",
        autosave_restore_when_empty: true,
        ai_request: (request, respondWith) =>
            respondWith.string(() =>
                Promise.reject("See docs to implement AI Assistant")
            ),
        setup: (editor) => {
            editor.on('init', () => {
                fetchFileExamPaper();
            });
        }
    });
});

const getTinyContent = () => tinymce.get("tiny-editor").getContent();

const setTinyContent = (value) => {
    tinymce.get("tiny-editor").setContent(value);
};

const fetchFileExamPaper = async () => {
    showLoading();

    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_UPDATE_EXAM_PAPER + "/file/" + sessionStorage.getItem(EXAM_DISTRIBUTION_EDIT_FILE_EXAM_PAPER_ID),
        method: "GET",
        success: (responseBody) => {
            console.log(responseBody.data);
            setTinyContent(responseBody.data);
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

    const file = new File([pdfData], "exam_rule.pdf", {
        type: 'application/pdf',
        lastModified: new Date()
    });
    showFilePdf(file);
}