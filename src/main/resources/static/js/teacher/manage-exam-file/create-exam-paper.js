tinymce.init({
    selector: "textarea#tiny-editor",
    plugins:
        "anchor autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount checklist mediaembed casechange export formatpainter pageembed linkchecker a11ychecker tinymcespellchecker permanentpen powerpaste advtable advcode editimage advtemplate ai mentions tinycomments tableofcontents footnotes mergetags autocorrect typography inlinecss markdown",
    toolbar:
        "undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat",
    tinycomments_mode: "embedded",
    tinycomments_author: "Author name",
    mergetags_list: [
        { value: "First.Name", title: "First Name" },
        { value: "Email", title: "Email" },
    ],
    height: "1000px",
    toolbar_sticky: true,
    icons: "thin",
    autosave_restore_when_empty: true,
    ai_request: (request, respondWith) =>
        respondWith.string(() =>
            Promise.reject("See docs to implement AI Assistant")
        ),
});

const getTinyContentTeacher = () => tinymce.get("tiny-editor").getContent();

const setTinyContent = (value) => {
    tinymce.get("tiny-editor").setContent(value);
};

//export pdf
document.getElementById('export-pdf').addEventListener('click', () => {
    swal({
        title: "Xác nhận",
        text: "Bạn chắc muốn xuất đề thi này ở định dạng PDF",
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
    }).then((ok) => {
        if (ok) {
            showLoading();
            setTimeout(async () => {
                const pdfFile = await convertTinyContentToPdfTeacher();
                if (pdfFile) {
                    // Ví dụ: Bạn có thể tải file xuống
                    const url = URL.createObjectURL(pdfFile);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = pdfFile.name;
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    URL.revokeObjectURL(url);
                }
                hideLoading();
            }, 0);
        }
    });
});

//export docx
document.getElementById('export-docx').addEventListener('click', () => {
    swal({
        title: "Xác nhận",
        text: "Bạn chắc muốn xuất đề thi này ở định dạng DOCX",
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
    }).then((ok) => {
        if (ok) {
            showLoading();
            setTimeout(async () => {
                const docxFile = await convertTinyContentToDocx();
                if (docxFile) {
                    // Ví dụ: Bạn có thể tải file xuống
                    const url = URL.createObjectURL(docxFile);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = docxFile.name;
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    URL.revokeObjectURL(url);
                }
                hideLoading();
            }, 0);
            hideLoading();
        }
    });
});

const handleOpenChooseFile = () => {
    $("#import-file").click();
};

const convertTinyContentToPdfTeacher = async () => {
    const element = document.createElement('div');
    element.innerHTML = getTinyContentTeacher();

    const images = element.getElementsByTagName("img");
    for (let img of images) {
        if (img.width > 700) {
            img.width = 700;
        }
    }

    const tables = element.getElementsByTagName("table");
    for (let table of tables) {
        if (table.style.width && parseInt(table.style.width) > 700) {
            table.style.width = '700px';
        }
        else {
            const computedWidth = window.getComputedStyle(table).width;
            if (parseInt(computedWidth) > 700) {
                table.style.width = '700px';
            }
        }
    }

    const modifiedContent = element.innerHTML;

    const options = {
        margin: 0.5,
        filename: 'document.pdf',
        image: {type: 'jpeg', quality: 1},
        html2canvas: {scale: 2},
        jsPDF: {unit: 'in', format: 'a4', orientation: 'portrait'},
    };

    try {
        const pdfBlob = await html2pdf().set(options).from(modifiedContent).outputPdf('blob');

        return new File([pdfBlob], "document.pdf", {type: "application/pdf"});

    } catch (error) {
        showToastError("Convert PDF không thành công");
    }
}

const convertTinyContentToDocx = () => {
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = getTinyContentTeacher();
    const images = tempDiv.getElementsByTagName("img");
    for (let img of images) {
        if (img.width > 700) {
            img.width = 700;
        }
    }
    const modifiedContent = tempDiv.innerHTML;

    const header = "<html xmlns:o='urn:schemas-microsoft-com:office:office' " +
        "xmlns:w='urn:schemas-microsoft-com:office:word' " +
        "xmlns='http://www.w3.org/TR/REC-html40'>" +
        "<head><meta charset='utf-8'><title>Export HTML to Word Document with JavaScript</title></head><body>";
    const footer = "</body></html>";
    const sourceHTML = header + modifiedContent + footer;

    const blob = new Blob([sourceHTML], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });

    return new File([blob], 'document.docx', { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
}

document.getElementById('import-file').addEventListener('change', function (event) {

    const file = event.target.files[0];

    if (file) {
        const fileName = file.name.toLowerCase();
        if (fileName.endsWith('.pdf')) {
            showFilePdf(file);
        } else if (fileName.endsWith('.docx')) {
            showFileDocx(file);
        } else {
            showToastError("Vui lòng chọn tệp PDF hoặc DOCX");
        }
        event.target.value = '';
    }
});

const showFileDocx = (file) => {
    showLoading();
    const reader = new FileReader();
    reader.onload = function (event) {
        mammoth.convertToHtml({arrayBuffer: event.target.result})
            .then((result) => {
                setTinyContent(result.value);
            })
            .catch((err) => {
                showToastError("Import file Docx không thành công");
            }).finally(hideLoading)
    };
    reader.readAsArrayBuffer(file);
}

const showFilePdf = async (file) => {
    try {
        showLoading();

        const formData = new FormData();
        formData.append('file', file);

        const url = ApiConstant.API_HEAD_SUBJECT_CREATE_EXAM_PAPER + "/pdf-to-docx";
        const response = await fetch(url, {
            body: formData,
            method: "POST",
        });
        if (response.ok) {
            await convertBlobAndShow(response);
        } else {
            showToastError("Import file PDF không thành công");
        }
    } catch (err) {
        showToastError("Import file PDF không thành công");
    } finally {
        hideLoading();
    }
}

const convertBlobAndShow = async (response) => {
    const blob = await response.blob();
    const docxText = await readDocxBlob(blob);
    setTinyContent(formatTextAsHtml(docxText));
};

const readDocxBlob = async (blob) => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = function (event) {
            mammoth.extractRawText({arrayBuffer: event.target.result})
                .then(result => {
                    resolve(result.value);
                })
                .catch(reject);
        };
        reader.readAsArrayBuffer(blob);
    });
}

const formatTextAsHtml = (text) => {
    const paragraphs = text.split('\n');
    let html = '';
    paragraphs.forEach(paragraph => {
        if (paragraph.trim() !== '') {
            html += `<p>${paragraph.trim()}</p>`;
        }
    });
    return html;
}


