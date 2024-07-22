let currentPage = 1;
let currentPageChild = 1;
let currentFacilityId = null;

$(document).ready(function () {

    getFacilitis();

    $('#filterForm').on('submit', function (e) {
        e.preventDefault();
        const facilityName = $('#facilityName').val()?.trim();
        const facilityStatus = $('#facilityStatus').val();
        getFacilitis(1, $('#pageSize').val(), facilityName, facilityStatus);
    });

    $('#facilityCreateButton').on('click', function () {
        confirmCreateFacility();
    });

    $('#facilityUpdateButton').on('click', function () {
        confirmUpdateFacility();
    });

    $('#facilityChildButton').on('click', function () {
        submitFacilityChildForm();
    });

    $('#closeFacilityChildButton').on('click', function () {
        getFacilityUpdate(currentFacilityId);
    });

    $('#resetFilter').on('click', function () {
        resetFilterForm();
    });

    $('#pageSize').on('change', function () {
        getFacilitis();
    });

});


const getFacilitis = (
    page = currentPage,
    size = $('#pageSize').val(),
    facilityName = $('#facilityName').val()?.trim(),
    facilityStatus = $('#facilityStatus').val()?.trim()
) => {

    const params = {
        page: page,
        size: size,
        name: facilityName,
        status: facilityStatus
    };

    let url = ApiConstant.API_HEAD_OFFICE_FACILITY + '?';

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
            if (responseBody?.data?.data?.length === 0) {
                $('#facilityTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
$('#pagination').empty();
                return;
            }
            const facilities = responseBody?.data?.data?.map((facility, index) => {
                return `<tr>
                            <td>${facility.orderNumber}</td>
                            <td>${facility.facilityCode}</td>
                            <td>${facility.facilityName}</td>
                            <td>${facility.facilityStatus === 0 ? "Hoạt động" : "Ngưng hoạt động"}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                 <span onclick="confirmDelete('${facility.id}')">
                                    <i 
                                        class="fas fa-trash-alt"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span onclick="getFacilityUpdate('${facility.id}')">
                                     <i class="fa-solid fa-edit"
                                            style="cursor: pointer; margin-left: 10px"
                                     ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#facilityTableBody').html(facilities);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu cơ sở');
        }
    });
}

const changePage = (page) => {
    if ($('#facilityUpdateModal').hasClass('show')) {
        currentPageChild = page;
        // Nếu modal đang hiển thị, thực hiện hành động này
        getFacilityChild(page);
    } else {
        currentPage = page;
        // Nếu modal không hiển thị, thực hiện hành động khác
        getFacilitis(page, $('#pageSize').val());
    }
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

const createPaginationChild = (totalPages, currentPage) => {
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

    $('#paginationChild').html(paginationHtml);
}

const createFacility = () => {
    const facilityName = $('#modifyFacilityNameCreate').val();

    const facilityRequest = {
        facilityName: facilityName
    };

    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_OFFICE_FACILITY,
        data: JSON.stringify(facilityRequest),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody) {
                showToastSuccess('Thêm cơ sở thành công');
                getFacilitis();
                $('#facilityCreateModal').modal('hide');
            }
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}ErrorCreate`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}Create`).addClass('is-invalid');
                });
            } else {
                showToastError(error.responseJSON.message);
            }
        }
    });
}

const updateFacility = () => {
    const facilityName = $('#modifyFacilityNameUpdate').val();
    const facilityId = $('#facilityUpdateId').val();

    const facilityUpdateRequest = {
        facilityName: facilityName,
    }

    $.ajax({
        type: "PUT",
        url: ApiConstant.API_HEAD_OFFICE_FACILITY + `/${facilityId}`,
        data: JSON.stringify(facilityUpdateRequest),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody) {
                showToastSuccess('Cập nhật cơ sở thành công');
                getFacilitis();
                $('#facilityUpdateModal').modal('hide');
            }
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}ErrorUpdate`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}Update`).addClass('is-invalid');
                });
            } else {
                showToastError(error.responseJSON.message);
            }
        }
    });
}

const confirmUpdateFacility = () =>
{
    swal({
        title: "Xác nhận sửa?",
        text: "Bạn chắn muốn sửa cơ sở này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Lưu",
                className: "btn btn-black",
            },
        },
    }).then((willUpdate) => {
        if (willUpdate) {
            updateFacility();
        }else {
            $('#facilityUpdateModal').modal('show');
            $('#facilityChildModal').modal('hide');
        }
    });
}

const confirmCreateFacility = () => {
    swal({
        title: "Xác nhận thêm?",
        text: "Bạn chắn muốn thêm cơ sở này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Thêm",
                className: "btn btn-black",
            },
        },
    }).then((willCreate) => {
        if (willCreate) {
            createFacility();
        }
    });
}

const confirmDelete = (id) => {
    swal({
        title: "Xác nhận thay đổi trạng thái?",
        text: "Bạn chắn muốn thay đổi trạng thái cơ sở này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Thay đổi",
                className: "btn btn-black",
            },
        },
    }).then((willDelete) => {
        if (willDelete) {
            changeStatus(id);
        }
    });
}

const changeStatus = (id) => {
    if ($('#facilityUpdateModal').hasClass('show')) {
        $.ajax({
            type: "PUT",
            url: ApiConstant.API_HEAD_OFFICE_FACILITY + "/facility-child" + `/${id}/change-status`,
            success: function (responseBody) {
                if (responseBody) {
                    showToastSuccess('Thay đổi trạng thái cơ sở con');
                    getFacilityChild();
                }
            },
            error: function (error) {
                showToastError('Có lỗi xảy ra khi thay đổi trạng thái cơ sở con');
            }
        });
    } else {
        // Ẩn phần tử có id là "fieldUpdate"
        $.ajax({
            type: "PUT",
            url: ApiConstant.API_HEAD_OFFICE_FACILITY + `/${id}/change-status`,
            success: function (responseBody) {
                if (responseBody) {
                    showToastSuccess('Thay đổi trạng thái cơ sở');
                    getFacilitis();
                }
            },
            error: function (error) {
                showToastError('Có lỗi xảy ra khi thay đổi trạng thái cơ sở');
            }
        });
    }
}

const createFacilityChild = () => {
    const facilityChildCode = $('#modifyFacilityChildCode')?.val()?.trim();
    const facilityChildName = $('#modifyFacilityChildName')?.val()?.trim();

    const facilityChildRequest = {
        facilityChildName: facilityChildName,
        facilityChildCode:facilityChildCode
    };

    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_OFFICE_FACILITY + "/" + currentFacilityId + "/facility-child",
        data: JSON.stringify(facilityChildRequest),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody) {
                showToastSuccess('Thêm cơ sở con thành công');
                $('#facilityChildModal').modal('hide');
                getFacilityUpdate(currentFacilityId);
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
                showToastError(error.responseJSON.message);
            }
        }
    });
}

const confirmCreateFacilityChild = () => {
    swal({
        title: "Xác nhận thêm?",
        text: "Bạn chắn muốn thêm cơ sở con này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Thêm",
                className: "btn btn-black",
            },
        },
    }).then((willCreate) => {
        if (willCreate) {
            createFacilityChild();
        }
    });
}

const confirmUpdateFacilityChild = () => {
    swal({
        title: "Xác nhận sửa?",
        text: "Bạn chắn muốn sửa cơ sở con này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Lưu",
                className: "btn btn-black",
            },
        },
    }).then((willCreate) => {
        if (willCreate) {
            updateFacilityChild();
        }
    });
}


const updateFacilityChild = () => {
    const facilityChildCode = $('#modifyFacilityChildCode').val()?.trim();
    const facilityChildName = $('#modifyFacilityChildName').val()?.trim();
    const facilityChildId = $('#facilityChildId').val();

    const facilityUpdateRequest = {
        facilityChildName: facilityChildName,
        facilityChildCode:facilityChildCode
    }

    $.ajax({
        type: "PUT",
        url: ApiConstant.API_HEAD_OFFICE_FACILITY + `/${facilityChildId}` + "/facility-child",
        data: JSON.stringify(facilityUpdateRequest),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody) {
                showToastSuccess('Cập nhật cơ sở thành công');
                getFacilityChild();
                $('#facilityChildModal').modal('hide');
                getFacilityUpdate(currentFacilityId);
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
                showToastError(error.responseJSON.message);
            }
        }
    });
}

const getFacilityUpdate = (facilityId) => {
    currentFacilityId = facilityId;
    currentPageChild = 1;
    $('#facilityNameErrorUpdate').text('');
    $('.form-control').removeClass('is-invalid');
    
    $('#facilityUpdateId').val(facilityId);
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_OFFICE_FACILITY + `/${facilityId}`,
        success: function (responseBody) {
            if (responseBody?.data) {
                const facility = responseBody?.data;
                $('#modifyFacilityNameUpdate').val(facility.facilityName);
                getFacilityChild();
                $('#facilityUpdateModal').modal('show');
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin cơ sở');
        }
    });
}

const getFacilityChild = (
    page = currentPageChild,
    size = 5,
) => {
    const params = {
        page: currentPageChild,
        size: size,
    }

    let url = ApiConstant.API_HEAD_OFFICE_FACILITY + "/" + currentFacilityId + "/facility-child?";

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
            if (responseBody?.data?.data?.length === 0) {
                $('#facilityChildTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const facilityChilds = responseBody?.data?.data?.map((facilityChild, index) => {
                return `<tr>
                            <td>${facilityChild.orderNumber}</td>
                            <td>${facilityChild.facilityChildCode}</td>
                            <td>${facilityChild.facilityChildName}</td>
                            <td>${facilityChild.facilityChildStatus === 0 ? "Hoạt động" : "Ngưng hoạt động"}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span onclick="confirmDelete('${facilityChild.id}')">
                                    <i 
                                        class="fas fa-trash-alt"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span onclick="getDetailFacilityChild('${facilityChild.id}')">
                                    <i class="fa-solid fa-edit"
                                        style="cursor: pointer; margin-left: 10px"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#facilityChildTableBody').html(facilityChilds);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationChild(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin bộ môn');
        }
    });
}

const getDetailFacilityChild = (facilityChildId) => {
    $.ajax({
        type: "GET",
        url:ApiConstant.API_HEAD_OFFICE_FACILITY + "/facility-child/" + facilityChildId,
        success: function (response) {
            if (response?.data) {
                const facilityChild = response?.data;
                $('#facilityChildId').val(facilityChild.id);
                $('#modifyFacilityChildCode').val(facilityChild.facilityChildCode);
                $('#modifyFacilityChildName').val(facilityChild.facilityChildName);
                $('#facilityChildModalLabel').text('Cập nhật cơ sở con');
                $('#facilityUpdateModal').modal('hide');
                $('#facilityChildModal').modal('show');
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin block');
        }
    });
}

const submitFacilityChildForm = () => {
    if ($('#facilityChildId').val() === '') {
        confirmCreateFacilityChild();
    } else {
        confirmUpdateFacilityChild();
    }
}

const resetFilterForm = () => {
    $('#facilityName').val('');
    $('#facilityStatus').val('');
    getFacilitis();
}

const openModalAddFacility = () => {
    $('#modifyFacilityNameCreate').text('');
    $('.form-control').removeClass('is-invalid');

    $('#facilityCreateModal').modal('show');
};

const openModalAddFacilityChild = () => {
    $('#facilityChildId').val('');
    $('#modifyFacilityChildName').val('');
    $('#facilityChildNameError').text('');

    $('#modifyFacilityChildCode').val('');
    $('#facilityChildCodeError').text('');
    $('.form-control').removeClass('is-invalid');

    $('#facilityChildModalLabel').text('Thêm cơ sở con');
    $('#facilityUpdateModal').modal('hide');
    $('#facilityChildModal').modal('show');
}


