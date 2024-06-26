$(document).ready(function () {

    getStaffs();

    $('#pageSize').on("change", () => {
        getStaffs(1, $('#pageSize').val(), $('#searchStaffCode').val()?.trim(), $('#searchName').val()?.trim(), $('#searchAccountFptOrFe').val()?.trim());
    });

    handleAddEvent([$('#searchStaffCode'), $('#searchName'), $('#searchAccountFptOrFe')])

    $('#modifyStaffButton').on('click', function () {
        let check = true;
        let staffName = $('#modifyName').val();
        let modifyStaffCode = $('#modifyStaffCode').val();
        let modifyAccountFPT = $('#modifyAccountFPT').val();
        let modifyAccountFE = $('#modifyAccountFE').val();
        //validate staff name
        if (!staffName || staffName?.trim().length===0){
            check = false;
            $('#modifyNameError').text('Tên nhân viên không được trống!');
        } else if(staffName?.trim().length > 250){
            check = false;
            $('#modifyNameError').text('Tên nhân viên không được lớn hơn 250 ký tự!')
        }else {
            $('#modifyNameError').text('')
        }
        //validate staff code
        if (!modifyStaffCode || modifyStaffCode.trim().length === 0) {
            check = false;
            $('#modifyStaffCodeError').text('Mã nhân viên không được trống!');
        } else if (modifyStaffCode.trim().length > 50) {
            check = false;
            $('#modifyStaffCodeError').text('Mã nhân viên không được lớn hơn 50 ký tự!');
        } else if (/\s/.test(modifyStaffCode)) {
            check = false;
            $('#modifyStaffCodeError').text('Mã nhân viên không được chứa khoảng trắng!');
        } else {
            $('#modifyStaffCodeError').text('');
        }
        //validate email fpt
        if (!modifyAccountFPT || modifyAccountFPT.trim().length === 0) {
            check = false;
            $('#modifyAccountFPTError').text('Email FPT không được trống!');
        } else if (modifyAccountFPT.trim().length > 100) {
            check = false;
            $('#modifyAccountFPTError').text('Email FPT không được lớn hơn 100 ký tự!');
        } else if (!isValidFptEmail(modifyAccountFPT)) {
            check = false;
            $('#modifyAccountFPTError').text('Email FPT không hợp lệ!');
        } else {
            $('#modifyAccountFPTError').text('');
        }
        //validate email fe
        if (!modifyAccountFE || modifyAccountFE.trim().length === 0) {
            check = false;
            $('#modifyAccountFEError').text('Email FE không được trống!');
        } else if (modifyAccountFE.trim().length > 100) {
            check = false;
            $('#modifyAccountFEError').text('Email FE không được lớn hơn 100 ký tự!');
        } else if (!isValidFeEmail(modifyAccountFE)) {
            check = false;
            $('#modifyAccountFEError').text('Email FE không hợp lệ!');
        } else {
            $('#modifyAccountFEError').text('');
        }

        if (check) {
            const staffId = $('#modifyStaffId').val();
            let mess = staffId.length !== 0 ? "sửa" : "thêm";
            let messUp = staffId.length !== 0 ? "Sửa" : "Thêm";
            $('#modifyStaffModal').modal('hide');
            swal({
                title: "Xác nhận " + mess + "?",
                text: "Bạn chắc chắn muốn " + mess + " nhân viên " + staffName + " không?",
                type: "warning",
                buttons: {
                    cancel: {
                        visible: true,
                        text: "Hủy",
                        className: "btn btn-black",
                    },
                    confirm: {
                        text: messUp,
                        className: "btn btn-secondary",
                    },
                },
            }).then((confirm) => {
                if (confirm) {
                    saveStaff();
                }
            });
        }
    });
});

function isValidFptEmail(email) {
    var fptEmailRegex = /^[A-Za-z0-9._%+-]+@fpt\.edu\.vn$/;
    return fptEmailRegex.test(email);
}

function isValidFeEmail(email) {
    var feEmailRegex = /^[A-Za-z0-9._%+-]+@fe\.edu\.vn$/;
    return feEmailRegex.test(email);
}

function saveStaff() {
    const staffId = $('#modifyStaffId').val();
    let data = {
        id: staffId?.length === 0 ? null : staffId,
        name: $('#modifyName').val().trim(),
        staffCode: $('#modifyStaffCode').val().trim(),
        accountFpt: $('#modifyAccountFPT').val().trim(),
        accountFe: $('#modifyAccountFE').val().trim(),
        departmentFacilityId: $('#modifydepartmentFacility').val()
    }
    let url = ApiConstant.API_HEAD_OFFICE_STAFF;
    let type = 'post';
    if (staffId?.length !== 0) {
        type = 'put';
    }
    $.ajax({
        type: type,
        url: url,
        data: JSON.stringify(data),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody.status === "CREATED") {
                showToastSuccess("Thêm nhân viên thành công!");
            }
            if (responseBody.status === 'OK') {
                showToastSuccess("Cập nhật nhân viên thành công!");
            }
            clearFormSearch();
            $('#modifyStaffModal').modal('hide');
        },
        error: function (error) {
            if (error?.responseJSON?.status === 'CONFLICT') {
                showToastError("Mã nhân viên đã tồn tại!");
            } else {
                showToastError('Có lỗi xảy ra khi lưu nhân viên!');
            }

        }
    });
}

function clearFormSearch() {
    $('#searchStaffCode').val('');
    $('#searchName').val('');
    $('#searchAccountFptOrFe').val('');
    getStaffs(1, $('#pageSize').val(), $('#searchStaffCode').val()?.trim(), $('#searchName').val()?.trim(), $('#searchAccountFptOrFe').val()?.trim());
}

function handleAddEvent(listDom) {
    for (let i = 0; i < listDom.length; i++) {
        listDom[i].on("keyup", debounce(() => {
            getStaffs(1, $('#pageSize').val(), $('#searchStaffCode').val()?.trim(), $('#searchName').val()?.trim(), $('#searchAccountFptOrFe').val()?.trim());
        }));
    }
}

function getStaffs(
    page = 1,
    size = $('#pageSize').val(),
    staffCode = null,
    name = null,
    accountFptOrFe = null
) {
    const params = {
        page: page,
        size: size,
        staffCode: staffCode,
        name: name,
        accountFptOrFe: accountFptOrFe
    };
    let url = ApiConstant.API_HEAD_OFFICE_STAFF + '?';

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
            if (responseBody?.data?.content?.length === 0) {
                $('#staffTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const staffs = responseBody?.data?.content?.map((staff, index) => {
                return `<tr>
                            <td>${index + 1 + responseBody?.data?.pageable?.offset}</td>
                            <td>${staff.staffCode}</td>
                            <td>${staff.name}</td>
                            <td>${staff.accountFpt}</td>
                            <td>${staff.accountFe}</td>
                            <td>${staff.departmentFacilityName}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                           <a  >
                                <i
                                    onclick="openModalUpdateStaff('${staff.id}')"
                                    class="fas fa-pen-nib"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                                 <a>
                                <i
                                    onclick="handleDelete('${staff.id}','${staff.name}')"
                                    class="fas fa-trash-alt"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                                <a>
                                <i
                                    onclick="handleDetail('${staff.id}')"
                                    class="fas fas fa-eye"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                            </td>
                        </tr>`;
            });
            $('#staffTableBody').html(staffs);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu chức vụ!');
        }
    });
}

const createPagination = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" onclick="changePage(${currentPage - 1})">
                            Trước
                        </a>
                     </li>`;
    } else {
        paginationHtml += `
                <li class="page-item disabled">
                    <a class="page-link">
                        Trước
                    </a>
                </li>
        `;
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<li class="page-item active"><a class="page-link text-white">${i}</a></li>`;
        } else {
            paginationHtml += `<li class="page-item"><a class="page-link" onclick="changePage(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" onclick="changePage(${currentPage + 1})">
                          Sau
                    </a>
                </li>
`;
    } else {
        paginationHtml += `
            <li class="page-item disabled">
                <a class="page-link">
                    Sau
                </a>
            </li>
`;
    }

    $('#pagination').html(paginationHtml);
}

function changePage(newCurrentPage) {
    getStaffs(newCurrentPage, $('#pageSize').val(), $('#searchStaffCode').val()?.trim(), $('#searchName').val()?.trim(), $('#searchAccountFptOrFe').val()?.trim());
}

function openModalAddStaff() {
    $('#modifyStaffId').val('');
    $('#modifyStaffCode').val('');
    $('#modifyStaffCodeError').text('');
    $('#modifyName').val('');
    $('#modifyNameError').text('');
    $('#modifyAccountFPT').val('');
    $('#modifyAccountFPTError').text('');
    $('#modifyAccountFE').val('');
    $('#modifyAccountFEError').text('');
    getAllDepartmentFacility();
    $('#staffModalLabel').text('Thêm nhân viên');
    $('#modifyStaffModal').modal('show');
}

const openModalUpdateStaff = async (staffId) => {
    await getAllDepartmentFacility();
    let url = ApiConstant.API_HEAD_OFFICE_STAFF + '/' + staffId;
    await $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const detail = responseBody?.data;
            $('#modifyStaffId').val(staffId);
            $('#modifyStaffCode').val(detail?.staffCode);
            $('#modifyStaffCodeError').text('');
            $('#modifyName').val(detail?.name);
            $('#modifyNameError').text('');
            $('#modifyAccountFPT').val(detail?.accountFpt);
            $('#modifyAccountFPTError').text('');
            $('#modifyAccountFE').val(detail?.accountFe);
            $('#modifyAccountFEError').text('');
            $('#modifydepartmentFacility').val(detail?.departmentFacilityId)
            $('#staffModalLabel').text('Sửa nhân viên');
            $('#modifyStaffModal').modal('show');
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu chức vụ!');
        }
    });

}

function getAllDepartmentFacility() {
    let url = ApiConstant.API_HEAD_OFFICE_STAFF + '/departments-facilities';
    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const departmentFacilities = responseBody?.data?.map((df) => {
                return `<option value="${df.departmentFacilityId}">${df.departmentFacilityName}</option>`
            });
            departmentFacilities.unshift('<option value="">Chọn bộ môn - cơ sở</option>');
            $('#modifydepartmentFacility').html(departmentFacilities);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu bộ môn cơ sở!');
        }
    });
}


function handleDelete(staffId, staffName) {
    swal({
        title: "Xác nhận xóa?",
        text: "Bạn chắc chắn muốn xóa nhân viên " + staffName + " không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Xóa",
                className: "btn btn-secondary",
            },
        },
    }).then((willDelete) => {
        if (willDelete) {
            let url = ApiConstant.API_HEAD_OFFICE_STAFF + '?id=' + staffId;
            $.ajax({
                type: "delete",
                url: url,
                success: function (responseBody) {
                    if (responseBody?.status === "OK") {
                        showToastSuccess("Xóa thành công!")
                    }
                    clearFormSearch();
                },
                error: function (error) {
                    showToastError('Có lỗi xảy ra khi xóa nhân viên!');
                }
            });
        }
    });
}

function handleDetail(idStaff){
    window.location.href = ApiConstant.REDIRECT_HEAD_OFFICE_STAFF+"/"+idStaff
}