function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

const formatFromUnixTimeToDate = (unixTime) => {
    return new Date(unixTime).toLocaleDateString();
}

function formatTimestampToDate(timestamp) {
    const date = new Date(timestamp);

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();

    return `${day}/${month}/${year}`;
}

const formatFromUnixTimeToHoursMinutes = (unixTime) => {
    let date = new Date(unixTime);
    let timeString = date.toLocaleTimeString();
    let [hours, minutes] = timeString.split(':').slice(0, 2);
    return `${hours.padStart(2, '0')}:${minutes}`;
}

const getValueForInputDate = (unix) => {
    return new Date(unix).toLocaleDateString('en-CA');
}

function debounce(func, timeout = 300) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => {
            func.apply(this, args);
        }, timeout);
    };
}

const getUrlParameters = (apiURL, params) => {
    let url = apiURL + '?';
    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }
    url = url.slice(0, -1);
    return url;
}

function handleAddEvent(dom, eventType, func) {
    dom.on(eventType, debounce(func, 500));
}

const callToolTip = () => {
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))
}

function formatDateTime(date) {
    const d = new Date(Number(date));
    const day = String(d.getDate()).padStart(2, "0");
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const year = d.getFullYear();
    const hours = String(d.getHours()).padStart(2, "0");
    const minutes = String(d.getMinutes()).padStart(2, "0");
    return `${day}/${month}/${year} ${hours}:${minutes}`;
}

const showUIFilePDF = (file, pdfjsLib, pdfViewerId) => {
    const fileReader = new FileReader();

    const pdfViewer = document.getElementById(pdfViewerId);

    fileReader.onload = function () {
        const typedarray = new Uint8Array(this.result);

        pdfjsLib
            .getDocument(typedarray)
            .promise.then((pdf) => {
            pdfViewer.innerHTML = "";
            for (let pageNum = 1; pageNum <= pdf.numPages; pageNum++) {
                pdf
                    .getPage(pageNum)
                    .then((page) => {
                        const viewport = page.getViewport({scale: 1.5});

                        const canvas = document.createElement("canvas");
                        canvas.classList.add("pdf-page");
                        const context = canvas.getContext("2d");
                        canvas.height = viewport.height;
                        canvas.width = viewport.width;

                        pdfViewer.appendChild(canvas);

                        const renderContext = {
                            canvasContext: context,
                            viewport: viewport,
                        };

                        page
                            .render(renderContext)
                            .promise.then(() => {
                            context.font = "12px Arial";
                            context.fillStyle = "rgba(0, 0, 0, 0.7)";
                            context.fillText(
                                `Page ${pageNum}`,
                                canvas.width - 70,
                                canvas.height - 10
                            );
                        })
                            .catch((error) => {
                                console.error("Error rendering page:", error);
                            });
                    })
                    .catch((error) => {
                        console.error("Error getting page:", error);
                    });
            }
        })
            .catch((error) => {
                console.error("Error getting document:", error);
            });
    };

    fileReader.onerror = function (error) {
        console.error("Error reading file:", error);
    };

    fileReader.readAsArrayBuffer(file);
}