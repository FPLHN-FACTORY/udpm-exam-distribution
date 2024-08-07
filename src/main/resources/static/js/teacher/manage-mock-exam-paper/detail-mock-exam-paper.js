// START: state
const pdfjsLibDetail = window["pdfjs-dist/build/pdf"];
let pdfDocDetail = null;
let pageNumDetail = 1;
let pageRenderingDetail = false;
let pageNumPendingDetail = null;
const scaleDetail = 1.5;
const $pdfCanvasDetail = $("#pdf-canvas-detail")[0];
const ctxDetail = $pdfCanvasDetail.getContext("2d");

function queueRenderPageDetail(num) {
    if (pageRenderingDetail) {
        pageNumPendingDetail = num;
    } else {
        renderPageDetail(num);
    }
}

$(document).ready(function () {

    $("#prev-page-detail").on("click", function () {
        if (pageNumDetail <= 1) {
            return;
        }
        pageNumDetail--;
        queueRenderPageDetail(pageNumDetail);
    });

    $("#next-page-detail").on("click", function () {
        if (pageNumDetail >= pdfDocDetail.numPages) {
            return;
        }
        pageNumDetail++;
        queueRenderPageDetail(pageNumDetail);
    });
    //add event search
    $('#staff-upload-find').on('keyup', debounce(changeFormSearch))
    $("#detailMockExamModal").on('hidden.bs.modal', function (e) {
        $("#mockExamPaperModal").modal("show");
    });
})


function changeFormSearch() {
    const startEndDate = $('#startEndDate').val().trim();
    let startDateString = startEndDate.substring(0, 10);
    let endDateString = startEndDate.substring(12);
    let startDate = null;
    let endDate = null;
    if (startEndDate !== '') {
        startDate = moment(startDateString, "DD/MM/YYYY").toDate().getTime();
        endDate = moment(endDateString, "DD/MM/YYYY").toDate().getTime();
    }
    fetchMockExamPaper(idSubject, blockId, $('#staff-upload-find').val().trim(), startDate, endDate);
}

let idSubject = '';
let blockId = '';

function getStatusTypeExam(type) {
    switch (type) {
        case 'MOCK_EXAM_PAPER':
            return '<span class="tag tag-success">Đề thi thử</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}

const setupDate = () => {
    const time = $('#startEndDate');
    time.daterangepicker({
        opens: 'center',
        locale: {
            format: 'DD/MM/YYYY'
        },
        autoUpdateInput: false
    });

    time.on('apply.daterangepicker', (ev, picker) => {
        time.val(picker.startDate.format('DD/MM/YYYY') + ' ⇀ ' + picker.endDate.format('DD/MM/YYYY'));
        changeFormSearch(); // Call your custom function here
    });

    time.on('cancel.daterangepicker', (ev, picker) => {
        time.val('');
        changeFormSearch();
    });

    // If you want to handle change event separately
    time.on('change', function (e) {
        e.preventDefault();
        changeFormSearch(); // Call your custom function here
    });
};

const clearValueInput = () => {
    $("#staff-upload-find").val('');
    $("#startEndDate").val('');
};

const handleSolveViewWhenOpenModalDetail = () => { // ẩn đi view pdf và paging của nó
    $("#paging-pdf-detail").prop("hidden", true);
    $("#pdf-viewer-detail").prop("hidden", true);
};

// Render the page
function renderPageDetail(num) {
    pageRenderingDetail = true;
    pdfDocDetail.getPage(num).then(function (page) {
        const viewport = page.getViewport({scale: scaleDetail});
        $pdfCanvasDetail.height = viewport.height;
        $pdfCanvasDetail.width = viewport.width;

        const renderContext = {
            canvasContext: ctxDetail,
            viewport: viewport,
        };
        const renderTask = page.render(renderContext);

        renderTask.promise.then(function () {
            pageRenderingDetail = false;
            if (pageNumPendingDetail !== null) {
                renderPageDetail(pageNumPendingDetail);
                pageNumPendingDetail = null;
            }
        });
    });

    $("#page-num-detail").text(num);
}

const showViewAndPagingPdfDetail = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
    $("#pdf-viewer-detail").prop("hidden", false);
    if (totalPage > 1) {
        $("#paging-pdf-detail").prop("hidden", false);
    }
};

const handleFetchMockExamPaper = (idMockExamPaper) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_MOCK_EXAM_PAPER + "/file?idMockExamPaper=" + idMockExamPaper,
        success: function (responseBody) {
            const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));
            pdfjsLibDetail
                .getDocument({data: pdfData})
                .promise.then(function (pdfDoc_) {
                pdfDocDetail = pdfDoc_;
                $("#total-page-detail").text(pdfDocDetail.numPages);
                renderPageDetail(pageNumDetail);
                showViewAndPagingPdfDetail(pdfDocDetail.numPages);
            });
            hideLoading();
            $("#detailMockExamModal").modal("show");
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

function handleDetailMockExamPaper(idMockExamPaper) {
    showLoading();
    handleSolveViewWhenOpenModalDetail();

    //reset laij page 1
    pageNumDetail = 1;
    handleFetchMockExamPaper(idMockExamPaper);
    $("#mockExamPaperModal").modal("hide");
}

const handleDownloadExamPaper = (fileId) => {
    $('#mockExamPaperModal').modal('hide');
    swal({
        title: "Xác nhận tải đề thi thử?",
        text: "Bạn chắc chắn muốn tải đề thi thử không?",
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
    }).then((confirm) => {
        if (confirm) {
            showLoading();
            $.ajax({
                type: "GET",
                url: ApiConstant.API_TEACHER_MOCK_EXAM_PAPER + "/download",
                data: {
                    fileId: fileId,
                },
                success: function (responseBody) {
                    const pdfData = Uint8Array.from(atob(responseBody), c => c.charCodeAt(0));
                    const blob = new Blob([pdfData], {type: 'application/pdf'});
                    // Tạo đối tượng URL từ Blob
                    const url = URL.createObjectURL(blob);

                    // Tạo và nhấp vào liên kết để tải tệp
                    const link = document.createElement('a');
                    link.href = url;
                    link.download = 'file.pdf'; // Đặt tên cho file tải về
                    document.body.appendChild(link);
                    link.click();

                    // Xóa đối tượng URL sau khi tải
                    URL.revokeObjectURL(url);
                    hideLoading();
                    $('#mockExamPaperModal').modal('show');
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
        } else {
            $('#mockExamPaperModal').modal('show');
        }
    });
};

const fetchMockExamPaper = (subjectId,
                            blockId,
                            codeAndTeacher = null,
                            startDate = null,
                            endDate = null) => {
    const params = {
        idSubject: subjectId,
        blockId: blockId,
        codeAndTeacher: codeAndTeacher,
        startDate: startDate,
        endDate: endDate
    };

    let url = ApiConstant.API_TEACHER_MOCK_EXAM_PAPER + "?";

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const responseData = responseBody?.data;
            if (responseData.length === 0) {
                $('#mockExamTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const mockExamPapers = responseData.map(function (mockExamPaper, index) {
                return `<tr>
                            <td>${index + 1}</td>
                            <td><a target="_blank" href="https://drive.google.com/file/d/${mockExamPaper.path}/view">${mockExamPaper.examPaperCode}</a></td>
                            <td>${getStatusTypeExam(mockExamPaper.examPaperType)}</td>
                            <td>${mockExamPaper.staffUpload}</td>
                            <td>${formatDateTime(mockExamPaper.createdExamPaperDate)}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span onclick="handleDetailMockExamPaper('${mockExamPaper.id}')" 
                                data-bs-toggle="tooltip" 
                                    data-bs-title="Chi tiết đề"
                                    class="fs-4">
                                <i class="fas fas fa-eye" 
                                   style="cursor: pointer; margin-left: 10px;"></i>
                                     
                                </span>
                                 <span onclick="handleDownloadExamPaper('${mockExamPaper.path}')" 
                                     data-bs-toggle="tooltip" 
                                    data-bs-title="Tải xuống"
                                    class="fs-4">
                                <i class="fa-solid fa-download" 
                                   style="cursor: pointer; margin-left: 10px;"></i>
                                </span> 
                            </td>
                        </tr>`;
            });
            $('#mockExamTableBody').html(mockExamPapers)
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy danh sách đề thi');
        }
    });
}

const handleOpenModalMockExam = (subjectId, subjectName, idBlock) => {
    idSubject = subjectId;
    blockId = idBlock;
    setupDate();
    clearValueInput();
    fetchMockExamPaper(subjectId, idBlock);
    $('#labelMockExamPaper').text("Đề thi thử môn " + subjectName);
    $("#mockExamPaperModal").modal("show");
};