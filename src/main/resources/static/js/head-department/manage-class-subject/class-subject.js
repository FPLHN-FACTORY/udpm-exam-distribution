$(document).ready(() => {

    getClassSubjects();

    fetchBlocks();

    fetchFacilityChildes();

    setupDate();

    change();

    //add event
    handleAddEvent($('#facilityChildId'), 'change', function () {
        submitFormFilter();
    });
    handleAddEvent($('#subjectName'), 'keyup', function () {
        submitFormFilter();
    });
    handleAddEvent($('#staffName'), 'keyup', function () {
        submitFormFilter();
    });
    handleAddEvent($('#shift'), 'change', function () {
        submitFormFilter();
    });
    handleAddEvent($('#classSubjectCode'), 'keyup', function () {
        submitFormFilter();
    });

    $('#modifySubjectButton').on('click', function () {
        submitSubjectForm();
    });

    $('#detailSubjectBtn').on('click', function () {
        const classSubjectId = $(this).closest('tr').find('td').eq(1).text();
        getDetailSubject(classSubjectId);
    });

    $('#pageSize').on('change', function () {
        getClassSubjects();
    });

});

const getClassSubjects = (
    page = 1,
    size = $('#pageSize').val(),
    facilityChildId = null,
    subjectName = null,
    staffName = null,
    startDate = null,
    endDate = null,
    shift = null,
    classSubjectCode = null
) => {

    const params = {
        page: page,
        size: size,
        facilityChildId: facilityChildId,
        subjectName: subjectName,
        staffName: staffName,
        shift: shift,
        classSubjectCode: classSubjectCode,
        startDate: new Date(startDate).getTime(),
        endDate: new Date(endDate).getTime()
    };

    let url = ApiConstant.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT + '?';

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    $('#loading-ele').show();
    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const ClassSubjectTableBody = $('#classSubjectTableBody');
            if (responseBody?.data?.data?.length === 0) {
                ClassSubjectTableBody.html(`
                    <tr>
                         <td colspan="9" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                $('#loading-ele').hide();
                return;
            }
            const classSubjects = responseBody?.data?.data?.map((classSubject, index) => {
                return `<tr>
                            <td>${classSubject.orderNumber}</td>
                            <td>${classSubject.classSubjectCode}</td>
                            <td>${classSubject.subjectName}</td>
                            <td>${classSubject.shift}</td>
                            <td>${classSubject.day ? formatDate(classSubject.day) : 'Chưa xác định'}</td>
                            <td>${classSubject.staffName}</td>
                            <td>${classSubject.blockName}</td>
                            <td>${classSubject.facilityChildName}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            <a onclick="getDetailSubject('${classSubject.id}')"
                               data-bs-toggle="tooltip" 
                               data-bs-title="Cập nhật">
                                <i 
                                    class="fas fa-pen-nib"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                            </td>
                        </tr>`;
            });
            ClassSubjectTableBody.html(classSubjects);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
            callToolTip();
            $('#loading-ele').hide();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu lớp môn');
            $('#loading-ele').hide();
        }
    });
}

// UI
const setupDate = () => {
    const time = $('#startEndDate');
    time.daterangepicker({
        opens: 'center', locale: {
            format: 'DD/MM/YYYY'
        }, autoUpdateInput: false
    }, function (start, end, label) {
    });

    time.on('apply.daterangepicker', (ev, picker) => {
        time.val(picker.startDate.format('DD/MM/YYYY') + ' ⇀ ' + picker.endDate.format('DD/MM/YYYY'));
        submitFormFilter();
    });

    time.on('cancel.daterangepicker', (ev, picker) => {
        time.val('');
        submitFormFilter();
    });
    // If you want to handle change event separately
    time.on('change', function (e) {
        e.preventDefault();
        submitFormFilter();
    });
}

const changePage = (page) => {
    getClassSubjects(page, $('#pageSize').val());
}

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
}

// const getSemester
let time;
const debounceSemester = (fun, delay) => {
    return (...args) => {
        if (time) {
            clearTimeout(time);
            time = null;
        }
        time = setTimeout(() => {
            fun(...args);
        }, delay)
    }
}

const change = () => {
    $('#modifySemesterId').change(() => {
        const semester = $('#modifySemesterId').val();
        const yearId = $('#modifyYearId').val();
        let semesterRequest = {
            semesterName: semester,
            year: yearId
        }
        if (semester !== "" && yearId !== "") {
            getSemester(semesterRequest);
            return;
        }
        $("#block").addClass("d-none")
    })
    $('#modifyYearId').on("input", () => {
        const semester = $('#modifySemesterId').val();
        const yearId = $('#modifyYearId').val();
        let semesterRequest = {
            semesterName: semester,
            year: yearId
        }
        if (semester !== "" && yearId !== "") {
            debounceSemester(getSemester, 1000)(semesterRequest);
            return;
        }
        $("#block").addClass("d-none")

    })
}

const openModalAddClassSubject = () => {
    closeModal();
    $('#classSubjectModal').modal('show');
}

// CALL API
const createClassSubject = () => {
    const modifyClassSubjectCode = $("#modifyClassSubjectCode").val().trim();
    const modifySubjectCode = $("#modifySubjectCode").val().trim();
    const modifyShift = $("#modifyShift").val();
    const modifyStaffCode = $("#modifyStaffCode").val().trim();
    const modifyFacilityChildId = $("#modifyFacilityChildId").val();
    const modifyBlockId = $("#modifyBlockId").val();
    const modifyDay = $("#modifyDay").val();

    $('#loading-ele').show();
    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT,
        data: JSON.stringify({
            classSubjectCode: modifyClassSubjectCode,
            subjectCode: modifySubjectCode,
            shift: modifyShift,
            staffCode: modifyStaffCode,
            facilityChildId: modifyFacilityChildId,
            blockId: modifyBlockId,
            day: new Date(modifyDay).getTime()
        }),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody) {
                showToastSuccess('Thêm lớp môn thành công');
                getClassSubjects();
                closeModal();
            }
            $('#loading-ele').hide();
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}`).addClass('is-invalid');
                });
            } else {
                let mess = error?.responseJSON?.message
                    ? error?.responseJSON?.message : 'Có lỗi xảy ra khi thêm môn học';
                showToastError(mess);
            }
            $('#loading-ele').hide();
        }
    });
}

const updateClassSubject = () => {
    const classSubjectId = $("#classSubjectId").val().trim();
    const modifyClassSubjectCode = $("#modifyClassSubjectCode").val().trim();
    const modifySubjectCode = $("#modifySubjectCode").val().trim();
    const modifyShift = $("#modifyShift").val();
    const modifyStaffCode = $("#modifyStaffCode").val().trim();
    const modifyFacilityChildId = $("#modifyFacilityChildId").val();
    const modifyBlockId = $("#modifyBlockId").val();
    const modifyDay = $("#modifyDay").val();

    $('#loading-ele').show();
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT + `/${classSubjectId}`,
        data: JSON.stringify({
            classSubjectCode: modifyClassSubjectCode,
            subjectCode: modifySubjectCode,
            shift: modifyShift,
            staffCode: modifyStaffCode,
            facilityChildId: modifyFacilityChildId,
            blockId: modifyBlockId,
            day: new Date(modifyDay).getTime()
        }),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody) {
                showToastSuccess('Cập nhật lớp môn học thành công');
                getClassSubjects();
                closeModal();
            }
            $('#loading-ele').hide();
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}`).addClass('is-invalid');
                });
            } else {
                let mess = error?.responseJSON?.message
                    ? error?.responseJSON?.message : 'Có lỗi xảy ra khi cập nhật môn học';
                showToastError(mess);
            }
            $('#loading-ele').hide();
        }
    });
}

const getDetailSubject = (id) => {
    $('#classSubjectModalLabel').text('Cập nhật lớp môn');
    $('#loading-ele').show();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT + `/${id}`,
        success: function (responseBody) {
            if (responseBody?.data) {
                $("#block").removeClass("d-none")
                const classSubject = responseBody?.data;
                $("#modifyClassSubjectCode").val(classSubject.classSubjectCode);
                $("#modifySubjectCode").val(classSubject.subjectCode);
                $("#modifyShift").val(classSubject.shift);
                $("#modifyStaffCode").val(classSubject.staffCode);
                $("#modifyFacilityChildId").val(classSubject.facilityChildId);
                $("#modifyBlockId").val(classSubject.blockId);
                $("#modifyDay").val(getValueForInputDate(classSubject.day));
                $("#modifySemesterId").val(classSubject.semesterName);
                $("#modifyYearId").val(classSubject.year);
                $('#classSubjectId').val(classSubject.id);
                $('#classSubjectModal').modal('show');
            }
            $('#loading-ele').hide();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin lớp môn học');
            $('#loading-ele').hide();
        }
    });
}

const fetchBlocks = (blockRequest) => {
    $('#loading-ele').show();
    $('#modifyBlockId').empty();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT + '/block-by-year',
        data: blockRequest,
        success: function (responseBody) {
            if (responseBody?.data) {
                const blocks = responseBody?.data?.map((block, index) => {
                    return `<option value="${block.blockId}">${block.blockName}</option>`;
                });
                blocks.unshift('<option value="">Chọn block</option>');
                $('#modifyBlockId').html(blocks);
            }
            $('#loading-ele').hide();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin block');
            $('#loading-ele').hide();
        }
    });
}

const getSemester = (semesterRequest) => {
    $('#loading-ele').show();
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT + '/semester-by-name-year',
        dataType: 'json',
        data: semesterRequest,
        async: false,
        success: function (responseBody) {
            $('#modifyBlockId').empty();
            if (responseBody?.data?.semesterId) {
                $("#block").removeClass("d-none")
                const blockRequest = {
                    semesterId: responseBody?.data?.semesterId,
                    year: $("#modifyYearId").val()
                }
                fetchBlocks(blockRequest)
            } else {
                showToastError('Không tìm thấy học kỳ phù hợp');
            }
            $('#loading-ele').hide();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin block');
            $('#loading-ele').hide();
        }
    });
}

const fetchFacilityChildes = () => {
    $('#loading-ele').show();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT + '/facility-child',
        success: function (responseBody) {
            if (responseBody?.data) {
                const facilityChildes = responseBody?.data?.map((facilityChild, index) => {
                    return `<option value="${facilityChild.id}">${facilityChild.name}</option>`;
                });
                facilityChildes.unshift('<option value="">Chọn cơ sở con</option>');
                $('#modifyFacilityChildId').html(facilityChildes);
                $('#facilityChildId').html(facilityChildes);
            }
            $('#loading-ele').hide();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin block');
            $('#loading-ele').hide();
        }
    });
}

// SUBMIT, EVENT
const submitSubjectForm = () => {
    if ($('#classSubjectId').val() === '') {
        createClassSubject();
    } else {
        updateClassSubject();
    }
}

const submitFormFilter = () => {
    let size = $('#pageSize').val();
    const facilityChildId = $('#facilityChildId').val().trim();
    const subjectName = $('#subjectName').val().trim();
    const staffName = $('#staffName').val().trim();
    const startEndDate = $('#startEndDate').val().trim();
    const shift = $('#shift').val().trim();
    const classSubjectCode = $('#classSubjectCode').val().trim();

    let startDateString = startEndDate.substring(0, 10);
    let endDateString = startEndDate.substring(12);
    let startDate = null;
    let endDate = null;
    if (startEndDate !== '') {
        startDate = moment(startDateString, "DD/MM/YYYY").toDate().getTime();
        endDate = moment(endDateString, "DD/MM/YYYY").toDate().getTime();
    }

    getClassSubjects(1, size, facilityChildId, subjectName, staffName, startDate, endDate, shift, classSubjectCode);
}

const resetFilterForm = () => {
    $('#subjectCode').val('');
    $('#subjectName').val('');
    $('#departmentId').val('');
    $('#subjectType').val('');
    $('#startDate').val('');
    getClassSubjects();
}

const closeModal = () => {
    $('#classSubjectModal').modal('hide');
    $("#block").addClass("d-none")
    $("#modifyClassSubjectCode").val("");
    $("#modifySubjectCode").val("");
    $("#modifyShift").val("");
    $("#modifyStaffCode").val("");
    $("#modifyFacilityChildId").val("");
    $("#modifyBlockId").val("");
    $("#modifyDay").val("");
    $("#modifySemesterId").val("");
    $("#modifyYearId").val("");
    $('#classSubjectId').val("");
    $('#classSubjectModalLabel').text('Thêm lớp môn');
    $('.form-control').removeClass('is-invalid');
    $('.form-select').removeClass('is-invalid');
}

// utils
const formatDate = (number) => {
    return moment(number).format('DD-MM-YYYY');
}

function openModalDownloadTemplate() {
    $('#downloadSemester').val('');
    $('#downloadSemesterError').val('');
    $('#downloadYear').val('');
    $('#downloadYearError').val('');
    $('#modalDownloadClassSubject').modal('show');
}

function submitDownload() {

    let check = 0;

    if ($('#downloadYear').val().length !== 4) {
        check = 1;
        $('#downloadYearError').text('Năm học phải gồm 4 ký tự, ví dụ: 2024');
    } else {
        $('#downloadYearError').text('');
    }

    if ($('#downloadSemester').val() === '' || $('#downloadSemester').val().length === 0) {
        check = 1;
        $('#downloadSemesterError').text('Vui lòng chọn học kỳ!');
    } else {
        $('#downloadSemesterError').text('');
    }

    if (check === 0) {
        $('#modalDownloadClassSubject').modal('hide');
        swal({
            title: "Xác nhận tải?",
            text: "Bạn chắc chắn muốn tải template không?",
            type: "warning",
            buttons: {
                cancel: {
                    visible: true,
                    text: "Hủy",
                    className: "btn btn-black",
                },
                confirm: {
                    text: "Tải",
                    className: "btn btn-black",
                },
            },
        }).then((willDelete) => {
            if (willDelete) {
                let url = ApiConstant.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT + '/download-template-class-subject' + '?semester=' + $('#downloadSemester').val() + "&year=" + $('#downloadYear').val();
                $('#loading-ele').show();
                $.ajax({
                    type: "GET",
                    url: url,
                    xhrFields: {
                        responseType: 'blob'
                    },
                    success: function (responseBody) {

                        var link = document.createElement('a');

                        var filename = "template-class-subject.xlsx";

                        var url = window.URL.createObjectURL(responseBody);

                        link.href = url;

                        link.download = filename;

                        document.body.appendChild(link);

                        link.click();

                        document.body.removeChild(link);

                        window.URL.revokeObjectURL(url);

                        $('#loading-ele').hide();
                    },
                    error: function (error) {
                        if (error?.status === 404) {
                            showToastError('Học kì ' + $('#downloadSemester').val() + ' năm ' + $('#downloadYear').val() + ' không tồn tại.');
                        } else {
                            showToastError('Học kì ' + $('#downloadSemester').val() + ' năm ' + $('#downloadYear').val() + ' không tồn tại.');
                        }
                        $('#loading-ele').hide();
                    }
                });
            } else {
                $('#modalDownloadClassSubject').modal('show');
            }
        });
    }

}

function openSelectFile() {
    $('#input-file-class-subject').click();
}

const convertSize = (bytes) => {
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes === 0) return '0 Byte';
    // Kiểm tra nếu kích thước lớn hơn 100 MB
    if (bytes > 100 * 1024 * 1024) return null;
    const i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)), 10);
    return `${(bytes / Math.pow(1024, i)).toFixed(2)} ${sizes[i]}`;
    if (bytes === 0) return '0 Byte';
};

function submitUpload(fileUpload) {
    if (fileUpload == null) {
        return;
    }
    const formData = new FormData();
    formData.append("file", fileUpload);

    let url = ApiConstant.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT + "/file/upload"

    $('#loading-ele').show();
    $.ajax({
        type: 'POST',
        url: url,
        processData: false,
        contentType: false,
        xhrFields: {
            responseType: 'blob'
        },
        data: formData,
        success: (response) => {
            showToastSuccess("import thành công!");
            $('#input-file-class-subject').val('');
            resetFilterForm();
            getClassSubjects();
            $('#loading-ele').hide();
        },
        error: (error) => {
            $('#input-file-class-subject').val('');
            showToastError("import thất bại!");
            resetFilterForm();
            getClassSubjects();
            $('#loading-ele').hide();
        }
    });
}

function handleChangeFile(input) {

    if (input.files.length < 1) {
        return;
    }

    const file = input.files[0];
    const fileType = file.type;

    const acceptType = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel'
    ];

    if (!acceptType.includes(fileType)) {
        showToastError("Định dạng tệp tin không hợp lệ.");
        return;
    }
    if (convertSize(file.size) === null) {
        showToastError("File không được lơn hơn 100MB")
        return;
    }

    submitUpload(file)
}