let fileUpload = null;

$(document).ready(function () {

    getStaffs();

    handleAddEvent($('#pageSize'),'change',function () {
        getStaffs(1, $('#pageSize').val(), $('#searchQuery').val()?.trim(), $('#staffStatus').val());
    })

    handleAddEvent($('#staffStatus'),'change',function () {
        getStaffs(1, $('#pageSize').val(), $('#searchQuery').val()?.trim(), $('#staffStatus').val());
    })

    handleAddEvent($('#searchQuery'),'keyup',function () {
        getStaffs(1, $('#pageSize').val(), $('#searchQuery').val()?.trim(), $('#staffStatus').val());
    })

    $('#modifyStaffButton').on('click', function () {
        let check = true;
        let staffName = $('#modifyName').val();
        let modifyStaffCode = $('#modifyStaffCode').val();
        let modifyAccountFPT = $('#modifyAccountFPT').val();
        let modifyAccountFE = $('#modifyAccountFE').val();
        //validate staff name
        if (!staffName || staffName?.trim().length === 0) {
            check = false;
            $('#modifyNameError').text('Tên nhân viên không được trống!');
        } else if (staffName?.trim().length > 250) {
            check = false;
            $('#modifyNameError').text('Tên nhân viên không được lớn hơn 250 ký tự!')
        } else {
            $('#modifyNameError').text('')
        }
        //validate staff code
        if (!modifyStaffCode || modifyStaffCode.trim().length === 0) {
            check = false;
            $('#modifyStaffCodeError').text('Mã nhân viên không được trống!');
        } else if (modifyStaffCode.trim().length > 50) {
            check = false;
            $('#modifyStaffCodeError').text('Mã nhân viên không được lớn hơn 50 ký tự!');
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
                        className: "btn btn-black",
                    },
                },
            }).then((confirm) => {
                if (confirm) {
                    saveStaff();
                } else {
                    $('#modifyStaffModal').modal('show');
                }
            });
        }
    });

});

function isValidFptEmail(email) {
    const fptEmailRegex = /^[A-Za-z0-9._%+-]+@fpt\.edu\.vn$/;
    return fptEmailRegex.test(email);
}

function isValidFeEmail(email) {
    const feEmailRegex = /^[A-Za-z0-9._%+-]+@fe\.edu\.vn$/;
    return feEmailRegex.test(email);
}

function saveStaff() {
    const staffId = $('#modifyStaffId').val();
    let data = {
        id: staffId?.length === 0 ? null : staffId,
        name: $('#modifyName').val().trim(),
        staffCode: $('#modifyStaffCode').val().trim(),
        accountFpt: $('#modifyAccountFPT').val().trim(),
        accountFe: $('#modifyAccountFE').val().trim()
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
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                });
            } else {
                let mess = error?.responseJSON?.message
                    ? error?.responseJSON?.message : 'Có lỗi xảy ra khi lưu nhân viên';
                showToastError(mess);
            }
        }
    });
}

function clearFormSearch() {
    $('#searchStaffCode').val('');
    $('#searchName').val('');
    $('#searchAccountFptOrFe').val('');
    getStaffs(1, $('#pageSize').val(), $('#searchQuery').val()?.trim());
}

function getStaffs(
    page = 1,
    size = $('#pageSize').val(),
    query = $('#searchQuery').val()?.trim(),
    status = $('#staffStatus').val()?.trim()
) {
    const url = getUrlParameters(ApiConstant.API_HEAD_OFFICE_STAFF, {
        page: page,
        size: size,
        searchQuery: query,
        status: status
    })
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
                $('#pagination').empty();
                return;
            }
            const staffs = responseBody?.data?.content?.map((staff, index) => {
                return `<tr>
                            <td>${index + 1 + responseBody?.data?.pageable?.offset}</td>
                            <td>${staff.staffCode}</td>
                            <td>${staff.name}</td>
                            <td>${staff.accountFpt}</td>
                            <td>${staff.accountFe}</td>
                            <td>${staff.status == 0 ? 'Đang hoạt động' : 'Ngừng hoạt động'}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                           <a data-bs-toggle="tooltip" 
                              data-bs-title="Cập nhật">
                                <i
                                    onclick="openModalUpdateStaff('${staff.id}')"
                                    class="fas fa-pen-nib"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                                 <a data-bs-toggle="tooltip" 
                                    data-bs-title="Trạng thái">
                                <i
                                    onclick="handleDelete('${staff.id}','${staff.name}')"
                                    class="fas fa-trash-alt"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                                <a data-bs-toggle="tooltip" 
                                    data-bs-title="Chức vụ / bộ môn / chuyên ngành">
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
            callToolTip();
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
    getStaffs(newCurrentPage, $('#pageSize').val(),$('#searchQuery').val()?.trim());
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
    $('#staffModalLabel').text('Thêm nhân viên');
    $('#modifyStaffModal').modal('show');
}

const openModalUpdateStaff = async (staffId) => {
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
            $('#staffModalLabel').text('Sửa nhân viên');
            $('#modifyStaffModal').modal('show');
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu chức vụ!');
        }
    });

}

function handleDelete(staffId, staffName) {
    swal({
        title: "Xác nhận thay đổi trạng thái?",
        text: "Bạn chắc chắn muốn thay đổi trạng thái nhân viên " + staffName + " không?",
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
            let url = ApiConstant.API_HEAD_OFFICE_STAFF + '?id=' + staffId;
            $.ajax({
                type: "delete",
                url: url,
                success: function (responseBody) {
                    if (responseBody?.status === "OK") {
                        showToastSuccess("Thay đổi trạng thái thành công!")
                    }
                    clearFormSearch();
                },
                error: function (error) {
                    showToastError('Có lỗi xảy ra khi thay đổi trạng thái nhân viên!');
                }
            });
        }
    });
}

function handleDetail(idStaff) {
    window.location.href = ApiConstant.REDIRECT_HEAD_OFFICE_STAFF + "/" + idStaff
}


function showModalImport() {
    fileUpload = null;
    $('#btn_upload').addClass('disabled');
    $('#input_file').val('').change();
    $('#upload-box-name').text('');
    $('#upload-box-size').text('');
    $('#modalUploadExcel').modal('show');
};

function selectFile() {
    $('#input-file').click();
}

const convertSize = (bytes) => {
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes === 0) return '0 Byte';
    const i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)), 10);
    return `${(bytes / Math.pow(1024, i)).toFixed(2)} ${sizes[i]}`;
};

function changeFile(input) {

    if (input.files.length < 1) {
        return;
    }

    const file = input.files[0];
    const fileType = file.type;
    const maxSize = 50 * 1024 * 1024; // 50MB in bytes

    const acceptType = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel'
    ];

    if (!acceptType.includes(fileType)) {
        showToastError("Định dạng tệp tin không hợp lệ.");
        return;
    }
    if (file.size > maxSize) {
        showToastError("Kích thước tệp tin không được vượt quá 50MB.");
        return;
    }

    $('#upload-box-name').text(file.name);
    $('#upload-box-size').text(convertSize(file.size));

    $('#btn_upload').removeClass('disabled');
    fileUpload = file;
    input.value = null;
};

function submitUpload() {
    if (fileUpload == null) {
        return;
    }
    const formData = new FormData();
    formData.append("file", fileUpload);

    let url = ApiConstant.API_HEAD_OFFICE_STAFF + "/file/upload"

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
            $('#modalUploadExcel').modal('hide');
            $('#input_file').val('');
            clearFormSearch();
            getStaffs();
        },
        error: (error) => {
            $('#modalUploadExcel').modal('hide');
            $('#input_file').val('');
            showToastError("upload thất bại!");
        }
    });
};

