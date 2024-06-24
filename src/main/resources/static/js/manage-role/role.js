$(document).ready(function () {

    getRoles();

    getFacilities();

    $('#roleName').on('keyup', debounce(() => {
        getRoles(1, $('#pageSize').val(), $('#roleName').val()?.trim(), $('#idFacility').val());
    }));

    $('#idFacility').on('change', () => {
        getRoles(1, $('#pageSize').val(), $('#roleName').val()?.trim(), $('#idFacility').val());
    });

    $('#pageSize').on("change", () => {
        getRoles(1, $('#pageSize').val(), $('#roleName').val()?.trim(), $('#idFacility').val());
    });

});

function getFacilities() {
    let url = ApiConstant.API_HEAD_OFFICE_ROLE + '/facilities';
    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const facilities = responseBody?.data?.map((facility) => {
                return `<option value="${facility.idFacility}">${facility.facilityName}</option>`
            });
            facilities.unshift('<option value="">Chọn cơ sở</option>');
            $('#idFacility').html(facilities);
            $('#modifyFacility').html(facilities);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu cơ sở!');
        }
    });
}


function getRoles(
    page = 1,
    size = $('#pageSize').val(),
    roleName = null,
    idFacility = null
) {
    const params = {
        page: page,
        size: size,
        roleName: roleName,
        idFacility: idFacility
    };
    let url = ApiConstant.API_HEAD_OFFICE_ROLE + '?';

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
                $('#roleTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const roles = responseBody?.data?.content?.map((role, index) => {
                return `<tr>
                            <td>${index + 1 + responseBody?.data?.pageable?.offset}</td>
                            <td>${role.roleName}</td>  
                            <td>${role.facilityName}</td>  
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            
                           <a onclick="openModalUpdateRole('${role.idRole}')">
                                <i 
                                    class="fas fa-pen-nib"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a> 
                                 <a onclick="comfirmDelete('${role.idRole}','${role.roleName}')">
                                <i 
                                    class="fas fa-trash-alt"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                            </td>
                        </tr>`;
            });
            $('#roleTableBody').html(roles);
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
                        <p class="page-link" onclick="changePage(${currentPage - 1})">
                            Trước
                        </p>
                     </li>`;
    } else {
        paginationHtml += `
                <li class="page-item disabled">
                    <p class="page-link">
                        Trước
                    </p>
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
                    <p class="page-link" onclick="changePage(${currentPage + 1})">
                          Sau
                    </p>
                </li>
`;
    } else {
        paginationHtml += `
            <li class="page-item disabled">
                <p class="page-link">
                    Sau
                </p>
            </li>
`;
    }

    $('#pagination').html(paginationHtml);
}

function changePage(newCurrentPage) {
    getRoles(newCurrentPage, $('#pageSize').val(), $('#roleName').val(), $('#idFacility').val());
}

const openModalAddRole = () => {
    $('#modifyRoleName').val('');
    $('#roleId').val('');
    getFacilities();
    $('#roleModalLabel').text('Thêm chức vụ');
    $('#modifyRoleModal').modal('show');
}

const openModalUpdateRole =async (roleId) => {
    await getFacilities();
    let url = ApiConstant.API_HEAD_OFFICE_ROLE + '/'+roleId;
    await $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            $('#modifyRoleName').val(`${responseBody?.data?.name}`);
            $('#roleId').val(`${responseBody?.data?.id}`);
            $('#modifyFacility').val(`${responseBody?.data?.facility?.id}`);
            $('#roleModalLabel').text('Cập nhật chức vụ');
            $('#modifyRoleModal').modal('show');
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu chức vụ!');
        }
    });
}

const clearFormSearch = () => {
    $('#roleName').val('');
    $('#idFacility').val('');
    getRoles();
}

$('#modifyRoleButton').on("click", () => {
    let check = true;
    let roleName = $('#modifyRoleName').val();
    let idFacility = $('#modifyFacility').val();
    if (idFacility==='' || idFacility?.length===0){
        check = false;
        $('#modifyFacilityError').text('Vui lòng chọn cơ sở!')
    }
    if (roleName==='' || roleName?.trim().length===0){
        check = false;
        $('#modifyRoleNameError').text('Tên chức vụ không được trống!')
    }
    if (check) {
        const roleId = $('#roleId').val();
        let mess = roleId.length!==0?"sửa":"thêm";
        $('#modifyRoleModal').modal('hide');
        swal({
            title: "Xác nhận "+mess+"?",
            text: "Bạn chắn muốn "+mess+" chức vụ "+roleName+" không?",
            type: "warning",
            buttons: {
                cancel: {
                    visible: true,
                    text: "Hủy",
                    className: "btn btn-black",
                },
                confirm: {
                    text: mess,
                    className: "btn btn-secondary",
                },
            },
        }).then((willDelete) => {
            if (willDelete) {
                saveRole();
            }
        });
    }
});

function saveRole() {
    const roleId = $('#roleId').val();
    let data = {
        roleId: roleId.length === 0 ? null : roleId,
        roleName: $('#modifyRoleName').val().trim(),
        idFacility: $('#modifyFacility').val().trim()
    }
    let url = ApiConstant.API_HEAD_OFFICE_ROLE;
    let type = 'post';
    if (roleId.length !== 0) {
        type = 'put';
    }
    $.ajax({
        type: type,
        url: url,
        data: JSON.stringify(data),
        contentType: "application/json",
        success: function (responseBody) {
            if (responseBody.status === "CREATED") {
                showToastSuccess("Thêm chức vụ thành công!");
            }
            if (responseBody.status === 'OK') {
                showToastSuccess("Cập nhật chức vụ thành công!");
            }
            clearFormSearch();
            $('#modifyRoleModal').modal('hide');
        },
        error: function (error) {
            if (error?.responseJSON?.status === 'CONFLICT') {
                showToastError("Chức vụ đã tồn tại!");
            }else{
                showToastError('Có lỗi xảy ra khi lưu chức vụ!');
            }

        }
    });
}

function comfirmDelete(roleId,roleName){
    swal({
        title: "Xác nhận xóa?",
        text: "Bạn chắn muốn xóa chức vụ "+roleName+" không?",
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
            let url = ApiConstant.API_HEAD_OFFICE_ROLE + '?id='+roleId;
            $.ajax({
                type: "delete",
                url: url,
                success: function (responseBody) {
                    if (responseBody?.status==="OK"){
                        showToastSuccess("Xóa thành công!")
                    }
                    clearFormSearch();
                },
                error: function (error) {
                    showToastError('Có lỗi xảy ra khi xóa chức vụ!');
                }
            });
        }
    });
}