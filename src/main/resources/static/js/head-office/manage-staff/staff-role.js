$(document).ready(function () {

    getDetailStaff();

    getRolesByStaffId();

    $('#facilitySelect').on('change',function (){

    })

});

//START: Exam Distribution Information
const examDistributionInfor = getExamDistributionInfo();

//END: Exam Distribution Information

function getDetailStaff() {

    let url = window.location.href;

    let idStaff = url.substring(url.lastIndexOf('/') + 1);

    let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/' + idStaff;

    $.ajax({
        type: "GET",
        url: api,
        success: function (responseBody) {
            const detail = responseBody?.data;
            $('#titlePageModal').text(' ' + detail?.staffCode);
            $('#infoCode').text(detail?.staffCode);
            $('#infoName').text(detail?.name);
            $('#infoDepartmentFacility').text(detail?.departmentFacilityName);
            $('#infoEmailFPT').text(detail?.accountFpt);
            $('#infoEmailFE').text(detail?.accountFe);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu nhân viên!');
        }
    });

}

function getFacilities() {
    let url = ApiConstant.API_HEAD_OFFICE_STAFF + '/facilities';
    showLoading();
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: url,
            success: function (responseBody) {
                const facilities = responseBody?.data?.map((facility) => {
                    return `<option value="${facility.idFacility}" ${facility.idFacility === examDistributionInfor.facilityId ? "selected" : ""}>${facility.facilityName}</option>`;
                });
                facilities.unshift('<option value="">Tất cả</option>');
                $('#idFacility').html(facilities);
                $('#facilitySelect').html(facilities);
                resolve();  // Resolve the promise when the AJAX call is successful
                hideLoading();
            },
            error: function (error) {
                showToastError('Có lỗi xảy ra khi lấy dữ liệu cơ sở!');
                reject(error);  // Reject the promise if there's an error
                hideLoading();
            }
        });
    });
}

function getRolesByStaffId() {

    let url = window.location.href;

    let idStaff = url.substring(url.lastIndexOf('/') + 1);

    let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/role/' + idStaff;

    showLoading();

    $.ajax({
        type: "GET",
        url: api,
        success: function (responseBody) {
            if (responseBody?.data?.length === 0) {
                $('#staffRoleTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Nhân viên này không có chức vụ nào</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const roles = responseBody?.data?.map((role, index) => {
                return `<tr>
                            <td>${index + 1}</td>
                            <td>${role.roleCode}</td>  
                            <td>${role.roleName}</td>  
                            <td>${role.facilityName}</td>
                            <td><a data-bs-toggle="tooltip" 
                                   data-bs-title="Xóa">
                                <i
                                    onclick="handleDelete('${role.idRole}','${role.roleName}')"
                                    class="fas fa-trash-alt"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a></td>
                        </tr>`;
            });
            $('#staffRoleTableBody').html(roles);
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu chức vụ!');
        }
    });
    hideLoading();
}

function handleDelete(idRole, roleName) {
    swal({
        title: "Xác nhận xóa?",
        text: "Bạn chắc chắn muốn xóa chức vụ " + roleName + " của nhân viên này?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Xóa",
                className: "btn btn-black",
            },
        },
    }).then((willDelete) => {
        if (willDelete) {

            let url = window.location.href;

            let idStaff = url.substring(url.lastIndexOf('/') + 1);

            const params = {
                idStaff: idStaff,
                idRole: idRole
            };

            let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/change-permission';

            showLoading();

            $.ajax({
                type: "put",
                url: api,
                data: JSON.stringify(params),
                contentType: "application/json",
                success: function (responseBody) {
                    getRolesByStaffId();
                    if (responseBody?.status === "OK") {
                        showToastSuccess("Xóa chức vụ thành công!");
                    }
                },
                error: function (error) {
                    showToastError('Có lỗi xảy ra khi sửa quyền!');
                }

            });
            hideLoading();
        }
    });

}

function getAllRoles(
    page = 1,
    size = $('#pageSize').val(),
    roleName = null,
    idFacility = null
) {

    let url = window.location.href;

    let idStaff = url.substring(url.lastIndexOf('/') + 1);

    const params = {
        page: page,
        size: size,
        roleName: roleName,
        idFacility: idFacility,
        staffId: idStaff
    };
    let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/role-check?';

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            api += `${key}=${value}&`;
        }
    }

    api = api.slice(0, -1);

    showLoading();
    $.ajax({
        type: "GET",
        url: api,
        success: function (responseBody) {
            if (responseBody?.data?.content?.length === 0) {
                $('#roleTableBodyModal').html(`
                                                <tr>
                                                     <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                                                </tr>
                                            `);
                $('#pagination').empty();
                return;
            }
            const roles = responseBody?.data?.content?.map((role, index) => {
                return `<tr>                        
                        <td>${index + 1 + responseBody?.data?.pageable?.offset}</td>
                        <td>${role.roleCode}</td>  
                        <td>${role.roleName}</td>  
                        <td>${role.facilityName}</td> 
                        <td><div class="col-auto">
                          <label class="colorinput">
                            <input
                              name="color"
                              type="checkbox"
                              value="${role.idRole}"
                              class="colorinput-input checkboxRole"
                              ${role.checked == 1 ? "checked" : ""}
                            />
                            <span
                              class="colorinput-color bg-secondary"
                            ></span>
                          </label>
                        </div>
                        </td> 
            </tr>`;
            });
            $('#roleTableBodyModal').html(roles);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu chức vụ!');
        }
    });
    hideLoading();
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
    getAllRoles(newCurrentPage, $('#pageSize').val(), $('#roleName').val(), $('#idFacility').val());
}

const openModalAddRole = async () => {

    await getFacilities();

    getAllRoles(1, $('#pageSize').val(), $('#roleName').val(), $('#idFacility').val());

    handleChangePermission();

    $('#modifyRoleModal').modal('show');

    handleAddEventStaffRole();

}

const clearFormSearch = () => {
    $('#roleName').val('');
    $('#idFacility').val('');
    getAllRoles(1, $('#pageSize').val(), $('#roleName').val()?.trim(), $('#idFacility').val());
}

function handleChangePermission() {
    $(document).off('change', '.checkboxRole');
    $(document).on('change', '.checkboxRole', function () {

        let url = window.location.href;

        let idStaff = url.substring(url.lastIndexOf('/') + 1);

        let checkbox = $(this);

        const params = {
            idStaff: idStaff,
            idRole: checkbox?.val()?.trim()
        };

        let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/change-permission';

        showLoading();

        $.ajax({
            type: "put",
            url: api,
            data: JSON.stringify(params),
            contentType: "application/json",
            success: function (responseBody) {
                if (responseBody?.status === "OK") {
                    if (checkbox?.is(':checked')) {
                        showToastSuccess("Thêm chức vụ thành công!")
                    } else {
                        showToastSuccess("Xóa chức vụ thành công!")
                    }
                }
            },
            error: function (error) {
                showToastError('Có lỗi xảy ra khi sửa quyền!');
            }
        });
        hideLoading();
    });
}

function clearEvents() {
    $('#roleName').off('keyup');
    $('#idFacility').off('change');
    $('#pageSize').off('change');
    $('#modifyRoleModal').off('hidden.bs.modal');
}

function handleAddEventStaffRole() {

    clearEvents();

    $('#roleName').on('keyup', debounce(() => {
        getAllRoles(1, $('#pageSize').val(), $('#roleName').val()?.trim(), $('#idFacility').val());
    }));

    $('#idFacility').on('change', () => {
        getAllRoles(1, $('#pageSize').val(), $('#roleName').val()?.trim(), $('#idFacility').val());
    });

    $('#pageSize').on("change", () => {
        getAllRoles(1, $('#pageSize').val(), $('#roleName').val()?.trim(), $('#idFacility').val());
    });

    $('#modifyRoleModal').on('hidden.bs.modal', function () {
        getRolesByStaffId();
    });

}
