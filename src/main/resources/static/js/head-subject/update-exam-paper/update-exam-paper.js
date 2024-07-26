const editor = new Jodit("#editor", {
    zIndex: 0,
    readonly: false,
    activeButtonsInReadOnly: [
        "source",
        "fullsize",
        "print",
        "about",
        "dots",
    ],
    toolbarButtonSize: "middle",
    theme: "default",
    saveModeInCookie: false,
    spellcheck: true,
    editorCssClass: false,
    triggerChangeEvent: true,
    width: "auto",
    height: "auto",
    minHeight: 100,
    direction: "",
    language: "auto",
    debugLanguage: false,
    i18n: "en",
    tabIndex: -1,
    toolbar: true,
    enter: "P",
    defaultMode: Jodit.MODE_WYSIWYG,
    useSplitMode: false,
    colors: {
        greyscale: [
            "#000000",
            "#434343",
            "#666666",
            "#999999",
            "#B7B7B7",
            "#CCCCCC",
            "#D9D9D9",
            "#EFEFEF",
            "#F3F3F3",
            "#FFFFFF",
        ],
        palette: [
            "#980000",
            "#FF0000",
            "#FF9900",
            "#FFFF00",
            "#00F0F0",
            "#00FFFF",
            "#4A86E8",
            "#0000FF",
            "#9900FF",
            "#FF00FF",
        ],
        full: [
            "#E6B8AF",
            "#F4CCCC",
            "#FCE5CD",
            "#FFF2CC",
            "#D9EAD3",
            "#D0E0E3",
            "#C9DAF8",
            "#CFE2F3",
            "#D9D2E9",
            "#EAD1DC",
            "#DD7E6B",
            "#EA9999",
            "#F9CB9C",
            "#FFE599",
            "#B6D7A8",
            "#A2C4C9",
            "#A4C2F4",
            "#9FC5E8",
            "#B4A7D6",
            "#D5A6BD",
            "#CC4125",
            "#E06666",
            "#F6B26B",
            "#FFD966",
            "#93C47D",
            "#76A5AF",
            "#6D9EEB",
            "#6FA8DC",
            "#8E7CC3",
            "#C27BA0",
            "#A61C00",
            "#CC0000",
            "#E69138",
            "#F1C232",
            "#6AA84F",
            "#45818E",
            "#3C78D8",
            "#3D85C6",
            "#674EA7",
            "#A64D79",
            "#85200C",
            "#990000",
            "#B45F06",
            "#BF9000",
            "#38761D",
            "#134F5C",
            "#1155CC",
            "#0B5394",
            "#351C75",
            "#733554",
            "#5B0F00",
            "#660000",
            "#783F04",
            "#7F6000",
            "#274E13",
            "#0C343D",
            "#1C4587",
            "#073763",
            "#20124D",
            "#4C1130",
        ],
    },
    colorPickerDefaultTab: "background",
    imageDefaultWidth: 300,
    removeButtons: [],
    disablePlugins: [],
    extraButtons: [],
    sizeLG: 900,
    sizeMD: 700,
    sizeSM: 400,
    sizeSM: 400,
    buttons: [
        "source",
        "|",
        "bold",
        "strikethrough",
        "underline",
        "italic",
        "|",
        "ul",
        "ol",
        "|",
        "outdent",
        "indent",
        "|",
        "font",
        "fontsize",
        "brush",
        "paragraph",
        "|",
        "image",
        "video",
        "table",
        "link",
        "|",
        "align",
        "undo",
        "redo",
        "|",
        "hr",
        "eraser",
        "copyformat",
        "|",
        "symbol",
        "fullsize",
        "print",
        "about",
    ],
    buttonsXS: [
        "bold",
        "image",
        "|",
        "brush",
        "paragraph",
        "|",
        "align",
        "|",
        "undo",
        "redo",
        "|",
        "eraser",
        "dots",
    ],
    events: {},
    textIcons: false,
});

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
                const pdfFile = await convertJoditContentToPdf();

                const reader = new FileReader();
                reader.onload = (e) => {
                    console.log(e.target.result);
                };
                reader.readAsText(pdfFile);

                if (pdfFile) {

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
                const docxFile = await convertJoditContentToDocx();
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

const convertJoditContentToPdf = async () => {
    const content = editor.getEditorValue();
    const element = document.createElement('div');
    element.innerHTML = content;

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
        } else {
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
        image: {type: 'jpeg', quality: 0.98},
        html2canvas: {scale: 2},
        jsPDF: {unit: 'in', format: 'letter', orientation: 'portrait'},
    };

    try {
        const pdfBlob = await html2pdf().set(options).from(modifiedContent).outputPdf('blob');

        return new File([pdfBlob], "document.pdf", {
            type: "application/pdf",
            lastModified: new Date()
        });
    } catch (error) {
        showToastError("Convert PDF không thành công");
    }
}

const convertJoditContentToDocx = () => {
    const content = editor.getEditorValue();

    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = content;
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

    const blob = new Blob([sourceHTML], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'});

    return new File([blob], 'document.docx', {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'});
}

const handleOpenChooseFile = () => {
    $("#import-file").click();
};

document.getElementById('import-file').addEventListener('change', function (event) {

    const file = event.target.files[0];

    const reader = new FileReader();
    reader.onload = (e) => {
        console.log(e.target.result);
    };
    reader.readAsText(file);

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
                editor.value = result.value;
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

        const url = ApiConstant.API_HEAD_SUBJECT_UPDATE_EXAM_PAPER + "/pdf-to-docx";
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
    editor.value = formatTextAsHtml(docxText);
}

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

