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
                const pdfFile = await convertTinyContentToPdf();

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

const convertTinyContentToPdf = async () => {
    const element = document.createElement('div');
    element.innerHTML = getTinyContent();

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

const convertTinyContentToDocx = () => {
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = getTinyContent();
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
    setTinyContent(formatTextAsHtml(docxText));
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

