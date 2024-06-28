$(document).ready(function () {

    getSubjects();

    fetchDepartments();

    $('#filterForm').on('submit', function (e) {
        e.preventDefault();
        const subjectCode = $('#subjectCode').val();
        const subjectName = $('#subjectName').val();
        const departmentId = $('#departmentId').val();
        const subjectType = $('#subjectType').val();
        const startDate = $('#startDate').val();
        getSubjects(1, 5, subjectCode, subjectName, departmentId, subjectType, startDate);
    });

    $('#modifySubjectButton').on('click', function () {
        submitSubjectForm();
    });

    $('#resetFilter').on('click', function () {
        resetFilterForm();
    });

    $('#detailSubjectBtn').on('click', function () {
        const subjectId = $(this).closest('tr').find('td').eq(1).text();
        getDetailSubject(subjectId);
    });

    $('#pageSize').on('change', function () {
        getSubjects();
    });

});

const getSubjects = (
    page = 1,
    size = $('#pageSize').val(),
    subjectCode = null,
    subjectName = null,
    departmentId = null,
    subjectType = null,
    startDate = null
) => {

    const params = {
        page: page,
        size: size,
        subjectCode: subjectCode,
        subjectName: subjectName,
        departmentId: departmentId,
        subjectType: subjectType,
        startDate: new Date(startDate).getTime()
    };

    let url = ApiConstant.API_HEAD_OFFICE_SUBJECT + '?';

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    $('#loading').show();

    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            if (responseBody?.data?.data?.length === 0) {
                $('#subjectTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const subjects = responseBody?.data?.data?.map((subject, index) => {
                return `<tr>
                            <td>${subject.orderNumber}</td>
                            <td>${subject.subjectCode}</td>
                            <td>${subject.subjectName}</td>
                            <td>${subject.departmentName ? subject.departmentName : 'Chưa xác định'}</td>
                            <td>${subject.subjectType}</td>
                            <td>${subject.subjectStatus}</td>
                            <td>${subject.createdDate ? formatFromUnixTimeToDate(subject.createdDate) : 'Chưa xác định'}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            <a onclick="getDetailSubject('${subject.id}')">
                                <i 
                                    class="fa-solid fa-eye"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                            </td>
                        </tr>`;
            });
            $('#subjectTableBody').html(subjects);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học');
        }
    });
    $('#loading').hide();
}

const changePage = (page) => {
    getSubjects(page, $('#pageSize').val());
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

const createSubject = () => {
    $('#subjectModalLabel').text('Cập nhật môn học');
    const subjectName = $('#modifySubjectName').val();
    const subjectCode = $('#modifySubjectCode').val();
    const departmentId = $('#modifyDepartmentId').val();
    const subjectType = $('#modifySubjectType').val();
    const startDate = $('#modifyStartDate').val();

    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_OFFICE_SUBJECT,
        data: JSON.stringify({
            subjectName: subjectName,
            subjectCode: subjectCode,
            departmentId: departmentId,
            subjectType: subjectType,
            startDate: new Date(startDate).getTime()
        }),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody) {
                showToastSuccess('Thêm môn học thành công');
                getSubjects();
                $('#subjectModal').modal('hide');
            }
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}`).addClass('is-invalid');
                });
            } else {
                showToastError('Có lỗi xảy ra khi thêm môn học');
            }
        }
    });
}

const updateSubject = () => {
    const subjectName = $('#modifySubjectName').val();
    const subjectCode = $('#modifySubjectCode').val();
    const departmentId = $('#modifyDepartmentId').val();
    const subjectType = $('#modifySubjectType').val();
    const startDate = $('#modifyStartDate').val();
    const subjectId = $('#subjectId').val();

    $.ajax({
        type: "PUT",
        url: ApiConstant.API_HEAD_OFFICE_SUBJECT + `/${subjectId}`,
        data: JSON.stringify({
            subjectName: subjectName,
            subjectCode: subjectCode,
            departmentId: departmentId,
            subjectType: subjectType,
            startDate: new Date(startDate).getTime()
        }),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody) {
                showToastSuccess('Cập nhật môn học thành công');
                getSubjects();
                $('#subjectModal').modal('hide');
            }
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}`).addClass('is-invalid');
                });
            } else {
                showToastError('Có lỗi xảy ra khi cập nhật môn học');
            }
        }
    });
}

const openModalAddSubject = () => {
    $('#subjectId').val('');
    $('#modifySubjectName').val('');
    $('#modifySubjectCode').val('');
    $('#modifyDepartmentId').val('');
    $('#modifySubjectType').val('');
    $('#modifyStartDate').val('');
    fetchDepartments();
    $('#subjectModalLabel').text('Thêm môn học');
    $('#subjectModal').modal('show');
}

const getDetailSubject = (id) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_OFFICE_SUBJECT + `/${id}`,
        success: function (responseBody) {
            if (responseBody?.data) {
                const subject = responseBody?.data;
                $('#modifySubjectName').val(subject.subjectName);
                $('#modifySubjectCode').val(subject.subjectCode);
                $('#modifyDepartmentId').val(subject.departmentId);
                $('#modifySubjectType').val(subject.subjectType);
                $('#modifyStartDate').val(getValueForInputDate(subject.createdDate));
                $('#subjectId').val(subject.id);
                $('#subjectModalLabel').text('Cập nhật môn học');
                $('#subjectModal').modal('show');
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin môn học');
        }
    });
}

const fetchDepartments = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_OFFICE_SUBJECT + '/department',
        success: function (responseBody) {
            if (responseBody?.data) {
                const departments = responseBody?.data?.map((department, index) => {
                    return `<option value="${department.departmentId}">${department.departmentName}</option>`;
                });
                departments.unshift('<option value="">Chọn bộ môn</option>');
                $('#departmentId').html(departments);
                $('#modifyDepartmentId').html(departments);
                $('#addDepartmentId').html(departments);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin bộ môn');
        }
    });
}

const submitSubjectForm = () => {
    if ($('#subjectId').val() === '') {
        swal({
            title: "Xác nhận thêm môn học",
            text: "Bạn có chắc chắn muốn thêm môn học này không?",
            icon: "info",
            buttons: true,
            dangerMode: false,
        }).then((willCreate) => {
            if (willCreate) {
                createSubject();
            }
        });
    } else {
        swal({
            title: "Xác nhận cập nhật môn học",
            text: "Bạn có chắc chắn muốn cập nhật môn học này không?",
            icon: "info",
            buttons: true,
            dangerMode: false,
        }).then((willUpdate) => {
            if (willUpdate) {
                updateSubject();
            }
        });
    }
}

const resetFilterForm = () => {
    $('#subjectCode').val('');
    $('#subjectName').val('');
    $('#departmentId').val('');
    $('#subjectType').val('');
    $('#startDate').val('');
    getSubjects();
}