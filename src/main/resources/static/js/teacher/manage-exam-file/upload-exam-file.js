
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
let stateMajorFacilityId = "";
// END: state

// START: getter
const getStateSubjectId = () => stateSubjectId;
const getStateSubjectCode = () => stateSubjectCode;
const getStateMajorFacilityId = () => stateMajorFacilityId;
// END: getter

// START: setter
const setStateSubjectId = (subjectId) => {
    stateSubjectId = subjectId;
};
const setStateSubjectCode = (subjectCode) => {
    stateSubjectCode = subjectCode
};
const setStateMajorFacilityId = (majorFacilityId) => {
    stateMajorFacilityId = majorFacilityId
};
// END: setter

const getValueFileInput = () => examFile;

const setValueFileInput = (value) => {
    examFile = value;
};

const clearValueFileInput = () => {
    $("#file-pdf-input").val('');
    $('#major-facility').html('<option value="">Chọn chuyên ngành - cơ sở</option>');
    examFile = new File([], "emptyFile");
};

function loadMajorFacility(){
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_TEACHER_EXAM_FILE + "/major-facility?majorFacilityId=" + examDistributionInfor.departmentFacilityId,
        success: function (responseBody) {
            const majorFacility = responseBody?.data?.map((mf) => {

                return `<option value="${mf.majorFacilityId}">${mf.majorFacilityName}</option>`
            });
            majorFacility.unshift('<option value="">Chọn chuyên ngành - cơ sở</option>');
            $('#major-facility').html(majorFacility);
        },
        error: function (error) {
            console.log(error)
            showToastError("Có lỗi xảy ra khi lấy danh sách chuyên ngành theo cơ sở!")
        },
    });
    hideLoading();
}

const handleOpenModalExamFile= (subjectId,subjectCode) => {

    loadMajorFacility();

    setStateSubjectId(subjectId);

    setStateSubjectCode(subjectCode);

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

const showViewAndPagingPdf = (totalPage) => { // hiển thị view và paging khi đã chọn xong file
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
    let check = 0 ;

    if ($("#major-facility").val()?.length === 0 ){
        $("#major-facility-error").text('Hãy chọn chuyên ngành - cơ sở.');
        check = 1;
    }else {
        $("#major-facility-error").text('');
    }

    return check === 0;

}

const handleUploadExamFile = () => {

    if (validateInput()){
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
                data.append("majorFacilityId", getStateMajorFacilityId());

                $.ajax({
                    type: "POST",
                    url: ApiConstant.API_TEACHER_EXAM_FILE + "/upload/" + getStateSubjectId(),
                    data: data,
                    contentType: false, // Không đặt kiểu nội dung vì nó sẽ được thiết lập tự động
                    processData: false, // Không xử lý dữ liệu, để nguyên dạng `FormData`
                    success: function (responseBody) {
                        showToastSuccess(responseBody.message);

                        fetchSearchSubject();

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

$(document).ready(function () {
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
                        renderPage(pageNum, true);
                        showViewAndPagingPdf(pdfDoc.numPages);
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
    // Event listener for major facility select
    $("#major-facility").on("change", function (e) {
        setStateMajorFacilityId($("#major-facility").val())
    });
});

function renderPage(num) {
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
                renderPage(pageNumPending);
                pageNumPending = null;
            }
        });
    });

    $("#page-num").text(num);
}

function queueRenderPage(num) {
    if (pageRendering) {
        pageNumPending = num;
    } else {
        renderPage(num);
    }
}

$("#prev-page").on("click", function () {
    if (pageNum <= 1) {
        return;
    }
    pageNum--;
    queueRenderPage(pageNum);
});

$("#next-page").on("click", function () {
    if (pageNum >= pdfDoc.numPages) {
        return;
    }
    pageNum++;
    queueRenderPage(pageNum);
});