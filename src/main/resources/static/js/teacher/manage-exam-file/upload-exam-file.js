// START: state
const pdfjsLib = window["pdfjs-dist/build/pdf"];
let pdfDoc = null;
let pageNum = 1;
let pageRendering = false;
let pageNumPending = null;
const scale = 1.5;
const $pdfCanvas = $("#pdf-canvas")[0];
const ctx = $pdfCanvas.getContext("2d");

let examFile = new File([], "emptyFile");
let stateSubjectId = "";
let stateSubjectCode = "";
let stateExamPaperStatus = "";
// END: state

// START: getter
const getStateSubjectId = () => stateSubjectId;
const getValueFileInput = () => examFile;
const getStateSubjectCode = () => stateSubjectCode;
const getStateExamPaperStatus = () => stateExamPaperStatus;
// END: getter

// START: setter
const setStateSubjectId = (subjectId) => {
    stateSubjectId = subjectId;
};
const setStateSubjectCode = (subjectCode) => {
    stateSubjectCode = subjectCode
};
const setValueFileInput = (value) => {
    examFile = value;
};
const setStateExamPaperStatus = (value) => {
    stateExamPaperStatus = value;
};
// END: setter

$(document).ready(function () {

    detailSubject();

    fetchExamPapers(1, $('#pageSize').val(), $('#staff-upload-find').val()?.trim());

    handleAddClassHover('');

    setupDate();

    handleGetCountExamPaper();

    //add event
    handleAddEvent($('#staff-upload-find'), 'keyup', function () {
        submitFetchExamPaper();
    })
    $("#btnAll").on('click', function () {
        handleSearchExamPaper('');
    });
    $("#btnWaitingApproval").on('click', function () {
        handleSearchExamPaper('WAITING_APPROVAL');
    });
    $("#btnInUse").on('click', function () {
        handleSearchExamPaper('IN_USE');
    });
    $("#btnRejected").on('click', function () {
        handleSearchExamPaper('REJECTED');
    });

    // Event listener for file input
    $("#file-pdf-input").on("change", function (e) {
        showLoading();
        const file = e.target.files[0];
        if (file) {
            const fileType = file.type;
            setValueFileInput(file);
            if (fileType === "application/pdf" || fileType === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
                const fileReader = new FileReader();
                fileReader.onload = function () {
                    const pdfData = new Uint8Array(this.result);
                    pdfjsLib
                        .getDocument({data: pdfData})
                        .promise.then(function (pdfDoc_) {
                        pdfDoc = pdfDoc_;
                        $("#total-page").text(pdfDoc.numPages);
                        renderPageUploadExamFile(pageNum, true);
                        showViewAndPagingPdfUploadExamFile(pdfDoc.numPages);
                    });
                };
                fileReader.readAsArrayBuffer(file);
                $('#btn-upload-exam-file').removeClass('disabled');
            } else {
                showToastError("Vui lòng chọn file PDF hoặc file DOCX");
                $('#btn-upload-exam-file').addClass('disabled');
            }
        }
        hideLoading();
    });
    $("#prev-page").on("click", function () {
        if (pageNum <= 1) {
            return;
        }
        pageNum--;
        queueRenderPageUploadExamFile(pageNum);
    });

    $("#next-page").on("click", function () {
        if (pageNum >= pdfDoc.numPages) {
            return;
        }
        pageNum++;
        queueRenderPageUploadExamFile(pageNum);
    });
    $('#pageSize').on("change", debounce(() => {
        fetchExamPapers(1,
            $('#pageSize').val(),
            $('#staff-upload-find').val()?.trim());
    }));
});

function handleSearchExamPaper(status) {
    handleAddClassHover(status);
    setStateExamPaperStatus(status);
    submitFetchExamPaper();
}

function handleAddClassHover(examPaperStatus) {
    switch (examPaperStatus) {
        case 'WAITING_APPROVAL':
            $('#btnWaitingApproval').addClass('btn-selected');
            handleClearClassHover([$('#btnInUse'), $('#btnRejected'), $('#btnAll')]);
            break;
        case 'IN_USE':
            $('#btnInUse').addClass('btn-selected');
            handleClearClassHover([$('#btnWaitingApproval'), $('#btnRejected'), $('#btnAll')]);
            break;
        case 'REJECTED':
            $('#btnRejected').addClass('btn-selected');
            handleClearClassHover([$('#btnWaitingApproval'), $('#btnInUse'), $('#btnAll')]);
            break;
        default:
            $('#btnAll').addClass('btn-selected');
            handleClearClassHover([$('#btnWaitingApproval'), $('#btnInUse'), $('#btnRejected')]);
            break;
    }
}

function getExamStatusTag(status) {
    switch (status) {
        case 'WAITING_APPROVAL':
            return '<span class="tag tag-warning">Chờ phê duyệt</span>';
        case 'IN_USE':
            return '<span class="tag tag-success">Đã phê duyệt</span>';
        case 'REJECTED':
            return '<span class="tag tag-danger">Bị từ chối</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}

function getExamTypeTag(type) {
    switch (type) {
        case 'OFFICIAL_EXAM_PAPER':
            return '<span class="tag tag-warning">Đề thi thật</span>';
        case 'MOCK_EXAM_PAPER':
            return '<span class="tag tag-warning">Đề thi thử</span>';
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
        submitFetchExamPaper(); // Call your custom function here
    });

    time.on('cancel.daterangepicker', (ev, picker) => {
        time.val('');
        submitFetchExamPaper();
    });
    time.on('change', function (e) {
        e.preventDefault();
        submitFetchExamPaper(); // Call your custom function here
    });
};

function handleClearClassHover(listDom) {
    for (let i = 0; i < listDom.length; i++) {
        listDom[i].removeClass('btn-selected');
    }
}

function submitFetchExamPaper() {
    const startEndDate = $('#startEndDate').val().trim();
    let startDateString = startEndDate.substring(0, 10);
    let endDateString = startEndDate.substring(12);
    let startDate = null;
    let endDate = null;
    if (startEndDate !== '') {
        startDate = moment(startDateString, "DD/MM/YYYY").toDate().getTime();
        endDate = moment(endDateString, "DD/MM/YYYY").toDate().getTime();
    }
    fetchExamPapers(1, $('#pageSize').val(), $('#staff-upload-find').val()?.trim(), startDate, endDate);
}

const fetchExamPapers = async (page,
                               size,
                               codeAndTeacher = null,
                               startDate = null,
                               endDate = null) => {
    let url = window.location.href;

    let idSubject = url.substring(url.lastIndexOf('/') + 1);

    idSubject = idSubject.split('#')[0];

    const params = {
        page: page,
        size: size,
        idSubject: idSubject,
        examPaperStatus: getStateExamPaperStatus(),
        codeAndTeacher: codeAndTeacher,
        startDate: startDate,
        endDate: endDate
    };

    let api = ApiConstant.API_TEACHER_EXAM_FILE + "?";

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            api += `${key}=${value}&`;
        }
    }

    api = api.slice(0, -1);
    await $.ajax({
        type: "GET",
        url: api,
        success: function (responseBody) {
            const responseData = responseBody?.data?.content;
            if (responseData.length === 0) {
                $('#examTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            if (getStateExamPaperStatus() === 'IN_USE') {
                const tableHead = `<tr>
                    <td>STT</td>
                    <td>Mã đề</td>
                    <td>Trạng thái</td>
                    <td>Loại đề</td>
                    <td>Giảng viên upload</td>
                    <td>Ngày upload</td>
                    <td
                        style="width: 1px; text-wrap: nowrap; padding: 0 10px;">Hành động
                    </td>
                </tr>`
                $('#examTableHead').html(tableHead);
            } else {
                const tableHead = `<tr>
                    <td>STT</td>
                    <td>Mã đề</td>
                    <td>Trạng thái</td>
                    <td>Giảng viên upload</td>
                    <td>Ngày upload</td>
                    <td
                        style="width: 1px; text-wrap: nowrap; padding: 0 10px;">Hành động
                    </td>
                </tr>`
                $('#examTableHead').html(tableHead);
            }
            const examPapers = responseData.map(function (examPaper, index) {
                return `<tr>
                            <td>${index + 1 + responseBody?.data?.pageable?.offset}</td>
                            <td><a target="_blank" href="https://drive.google.com/file/d/${examPaper.path}/view">${examPaper.examPaperCode}</a></td>
                            <td>${getExamStatusTag(examPaper.examPaperStatus)}</td>
                            ${getStateExamPaperStatus() === 'IN_USE' ? `<td>${getExamTypeTag(examPaper.examPaperType)}</td>` : null}
                            <td>${examPaper.staffUpload}</td>
                            <td>${formatDateTime(examPaper.createdExamPaperDate)}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                 <span onClick="handleDetailExamPaper('${examPaper.id}')"
                                       data-bs-toggle="tooltip"
                                       data-bs-title="Chi tiết đề"
                                       class="fs-4">
                                 <i class="fas fas fa-eye"
                                    style="cursor: pointer; margin-left: 10px;"></i>

                                 </span>
                                  <span onClick="handleDownloadExamPaperById('${examPaper.id}')"
                                        data-bs-toggle="tooltip"
                                        ata-bs-title="Tải xuống"
                                        class="fs-4">
                                 <i class="fa-solid fa-download"
                                    style="cursor: pointer; margin-left: 10px;"></i>
                                 </span>
                            </td>
                        </tr>`;
            });
            $('#examTableBody').html(examPapers);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy danh sách đề thi');
        }
    });
    await handleGetCountExamPaper();
}

function detailSubject() {
    let url = window.location.href;

    let idSubject = url.substring(url.lastIndexOf('/') + 1);

    idSubject = idSubject.split('#')[0];

    let api = ApiConstant.API_TEACHER_EXAM_FILE + '/subject/' + idSubject;

    $.ajax({
        type: "GET",
        url: api,
        success: function (responseBody) {
            const detail = responseBody?.data;
            setStateSubjectId(detail.subjectId)
            setStateSubjectCode(detail.subjectCode)
            $('#subjectCode').text(detail?.subjectCode);
            $('#subjectName').text(detail?.subjectName);
            $('#subjectDepartment').text(detail?.departmentName);
            $('#subjectType').text(detail?.subjectType);
            $('#subjectUploaded').text(detail?.uploaded + "/" + detail?.maxUpload);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học!');
        }
    });
}

const clearValueFileInput = () => {
    $("#file-pdf-input").val('');
    $('#major-facility').html('<option value="">Chọn chuyên ngành - cơ sở</option>');
    examFile = new File([], "emptyFile");
};

const handleOpenModalExamFile = () => {

    clearValueFileInput();

    $('#btn-upload-exam-file').addClass('disabled');

    //reset lai page 1
    pageNum = 1;
    // ẩn đi view pdf và paging của nó
    handleSolveViewWhenOpenModal();
    $("#examFileModal").modal("show");
};

const handleSolveViewWhenOpenModal = () => { // ẩn đi view pdf và paging của nó
    $("#paging-pdf").prop("hidden", true);
    $("#pdf-viewer").prop("hidden", true);
};

const showViewAndPagingPdfUploadExamFile = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
    $("#pdf-viewer").prop("hidden", false);
    if (totalPage > 1) {
        $("#paging-pdf").prop("hidden", false);
    }
};

const handleOpenChooseFilePdf = () => {
    const pdfFile = $("#file-pdf-input");
    pdfFile.click();
};

function validateInput() {
    let check = 0;

    if ($("#major-facility").val()?.length === 0) {
        $("#major-facility-error").text('Hãy chọn chuyên ngành - cơ sở.');
        check = 1;
    } else {
        $("#major-facility-error").text('');
    }

    return check === 0;

}

const handleUploadExamFile = () => {

    if (validateInput()) {
        $("#examFileModal").modal("hide");
        swal({
            title: "Xác nhận tải đề lên?",
            text: "Bạn chắc chắn muốn tải đề này lên không?",
            type: "warning",
            buttons: {
                cancel: {
                    visible: true,
                    text: "Hủy",
                    className: "btn btn-black",
                },
                confirm: {
                    text: "Tải lên",
                    className: "btn btn-black",
                },
            },
        }).then((ok) => {
            if (ok) {
                showLoading();
                const data = new FormData();
                data.append("folderName", getStateSubjectCode());
                data.append("file", getValueFileInput());

                $.ajax({
                    type: "POST",
                    url: ApiConstant.API_TEACHER_EXAM_FILE + "/upload/" + getStateSubjectId(),
                    data: data,
                    contentType: false, // Không đặt kiểu nội dung vì nó sẽ được thiết lập tự động
                    processData: false, // Không xử lý dữ liệu, để nguyên dạng `FormData`
                    success: function (responseBody) {
                        showToastSuccess(responseBody.message);
                        hideLoading();
                    },
                    error: function (error) {
                        if (error?.responseJSON?.message) {
                            showToastError(error?.responseJSON?.message);
                        } else {
                            showToastError('Có lỗi xảy ra');
                        }
                        hideLoading();
                    },
                });
            } else {
                $("#examFileModal").modal("show");
            }
        });
    }

};

function renderPageUploadExamFile(num) {
    pageRendering = true;
    pdfDoc.getPage(num).then(function (page) {
        const viewport = page.getViewport({scale: scale});
        $pdfCanvas.height = viewport.height;
        $pdfCanvas.width = viewport.width;

        const renderContext = {
            canvasContext: ctx,
            viewport: viewport,
        };
        const renderTask = page.render(renderContext);

        renderTask.promise.then(function () {
            pageRendering = false;
            if (pageNumPending !== null) {
                renderPageUploadExamFile(pageNumPending);
                pageNumPending = null;
            }
        });
    });

    $("#page-num").text(num);
}

function queueRenderPageUploadExamFile(num) {
    if (pageRendering) {
        pageNumPending = num;
    } else {
        renderPageUploadExamFile(num);
    }
}

function handleGetCountExamPaper() {

    let url = window.location.href;

    let idSubject = url.substring(url.lastIndexOf('/') + 1);

    idSubject = idSubject.split('#')[0];

    let api = ApiConstant.API_TEACHER_EXAM_FILE + '/count/' + idSubject;
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: api,
            success: function (responseBody) {
                const detail = responseBody?.data;
                $('#sumAll').text(Number(detail?.inUse) + Number(detail?.rejected) + Number(detail?.waitingApproval));
                $('#sumWaitingApproval').text(detail?.waitingApproval ? detail?.waitingApproval : 0);
                $('#sumInUse').text(detail?.inUse ? detail?.inUse : 0);
                $('#sumRejected').text(detail?.rejected ? detail?.rejected : 0);
                resolve();
            },
            error: function (error) {
                reject();
                showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học!');
            }
        });
    })
}

const changePage = (page) => {
    const startEndDate = $('#startEndDate').val().trim();
    let startDateString = startEndDate.substring(0, 10);
    let endDateString = startEndDate.substring(12);
    let startDate = null;
    let endDate = null;
    if (startEndDate !== '') {
        startDate = moment(startDateString, "DD/MM/YYYY").toDate().getTime();
        endDate = moment(endDateString, "DD/MM/YYYY").toDate().getTime();
    }
    fetchExamPapers(page, $('#pageSize').val(), $('#staff-upload-find').val()?.trim(), startDate, endDate);
};

const createPagination = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePage(${currentPage - 1})">
                            Trước
                        </a>
                     </li>`;
    } else {
        paginationHtml += `
                <li class="page-item disabled">
                    <a class="page-link" href="#">
                        Trước
                    </a>
                </li>
        `;
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<li class="page-item active"><a class="page-link text-white" href="#">${i}</a></li>`;
        } else {
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePage(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePage(${currentPage + 1})">
                          Sau
                    </a>
                </li>
`;
    } else {
        paginationHtml += `
            <li class="page-item disabled">
                <a class="page-link" href="#">
                    Sau
                </a>
            </li>
`;
    }
    $('#pagination').html(paginationHtml);
};
