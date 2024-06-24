//----------------------------------------------------------------------------------------------------------------------
// START: state
let stateAddOrUpdate = 0; // 0 -> add, 1 -> update
let stateDepartmentId = 0; // 0 -> add, 1 -> update
// END: state

// START: getter
const getStateAddOrUpdate = () => stateAddOrUpdate;
const getStateDepartmentId = () => stateDepartmentId;
// END: getter

// START: setter
const setStateAddOrUpdate = (state) => {
    stateAddOrUpdate = state;
}
const setStateDepartmentId = (departmentId) => {
    stateDepartmentId = departmentId;
}
// END: setter
//----------------------------------------------------------------------------------------------------------------------
const getGlobalParamsSearch = () => {
    return {
        departmentName: $("#departmentName").val()
    }
};
const resetGlobalParamsSearch = () => {
    $("#departmentName").val("");
};

const resetGlobalParamSearch = () => {
    $("#departmentName").val("");
};

const resetFieldsError = () => {
    $('.form-control').removeClass('is-invalid');
    $("#departmentCodeError").text("");
    $("#departmentNameError").text("");
};

const resetFields = () => {
    $("#modifyDepartmentCode").val("");
    $("#modifyDepartmentName").val("");
};

/**
 * Set value cho fields.
 * @param {string} departmentName - Giá trị của departmentName
 * @param {string} departmentCode - Giá trị của departmentCode
 */
const setAllFields = (departmentName, departmentCode) => {
    $("#modifyDepartmentCode").val(departmentCode);
    $("#modifyDepartmentName").val(departmentName);
};

const getAllFields = () => {
    return {
        departmentName: $("#modifyDepartmentName").val(),
        departmentCode: $("#modifyDepartmentCode").val(),
    };
};
const isFieldHasAnyError = () => {
    let isError = false;
    for (let field in getAllFields()) {
        if (getAllFields()[field].trim() === "") {
            isError = true;
        }
    }
    return isError;
};
//----------------------------------------------------------------------------------------------------------------------
$(document).ready(function () {
    getSearchDepartment();
    onChangePageSize();

    $("#modifyDepartmentButton").on("click", function () {
        handleAddOrUpdateDepartmentConfirm();
    });

    $("#buttonFilter").on("click", function () {
        getSearchDepartment(1, $("#pageSize").val(),getGlobalParamsSearch());
    });

    $("#resetFilter").on("click", function () {
        $("#pageSize").val("5");
        resetGlobalParamsSearch();
        getSearchDepartment(1, $("#pageSize").val(), getGlobalParamsSearch());
    });
});

const getSearchDepartment = (
    page = 1,
    size = $('#pageSize').val(),
    paramSearch = {
        departmentName: ""
    },
) => {

    const params = {
        page: page,
        size: size,
        departmentName: paramSearch.departmentName
    };

    let url = ApiConstant.API_HEAD_OFFICE_DEPARTMENT + "/get-all-department" + '?';

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
                $('#departmentTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const departments = responseData.map((department, index) => {
                return `<tr>
                            <td>${department.orderNumber}</td>
                            <td>${department.departmentName + " - " + department?.departmentCode}</td>
                            <td>${department.departmentStatus === 0 ? "Hoạt động" : "Ngưng hoạt động"}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span onclick="openModalAddOrUpdateDepartment(1,'${department.id}','${department.departmentName}','${department.departmentCode}')" class="fs-4">
                                    <i class="fa-solid fa-clipboard-list"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span class="fs-4" onclick="getDetailDepartment('${department.id}')">
                                    <i 
                                        class="fa-solid fa-eye"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#departmentTableBody').html(departments);
            const totalPages = responseBody?.totalPages ? responseBody?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu bộ môn');
        }
    });
}

const changePage = (page) => {
    getSearchDepartment(page, $('#pageSize').val(), getGlobalParamsSearch());
};

const onChangePageSize = () => {
    $("#pageSize").on("change", function () {
        resetGlobalParamSearch();
        getSearchDepartment(1, $('#pageSize').val(), getGlobalParamsSearch());
    });
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

const getDetailDepartment = (id) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_OFFICE_DEPARTMENT + `/${id}`,
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

const openModalAddOrUpdateDepartment = (status, departmentId, departmentName, departmentCode) => {
    if (status === 0) {
        //add
        resetFields();
    } else {
        //update
        setStateDepartmentId(departmentId);
        setAllFields(departmentName, departmentCode);
    }
    viewShowModalAddOrUpdateDepartment(status);
    resetFieldsError();
    setStateAddOrUpdate(status);
    $('#departmentModal').modal('show');
};

const viewShowModalAddOrUpdateDepartment = (status) => {
    $("#departmentModalLabel").text(status === 0 ? "Thêm môn học" : "Cập nhật môn học");
    $("#modifyDepartmentButton").text(status === 0 ? "Thêm" : "Cập nhật");
};

const handleAddOrUpdateDepartmentConfirm = () => {
    if (isFieldHasAnyError()) {
        handleAddOrUpdateDepartment();
    } else {
        swal({
            title: getStateAddOrUpdate() === 0 ? "Xác nhận thêm?" : "Xác nhận cập nhật?",
            text: getStateAddOrUpdate() === 0 ? "Bạn có chắc muốn thêm bộ môn này không?" : "Bạn có chắc muốn cập nhật bộ môn này không?",
            type: "warning",
            buttons: {
                cancel: {
                    visible: true,
                    text: "Hủy",
                    className: "btn btn-black",
                },
                confirm: {
                    text: "Xác nhận",
                    className: "btn btn-secondary",
                },
            },
        }).then((willDelete) => {
            if (willDelete) {
                handleAddOrUpdateDepartment();
            }
        });
    }
};

const handleAddOrUpdateDepartment = () => {
    const addOrUpdateApiUrl = getStateAddOrUpdate() === 0 ? "/add-department" : "/update-department/" + getStateDepartmentId();
    const finalUrl = ApiConstant.API_HEAD_OFFICE_DEPARTMENT + addOrUpdateApiUrl;
    $.ajax({
        contentType: "application/json",
        type: getStateAddOrUpdate() === 0 ? "POST" : "PUT",
        url: finalUrl,
        dataType: 'json',
        data: JSON.stringify(getAllFields()),
        success: function (responseBody) {
            showToastSuccess(getStateAddOrUpdate() === 0 ? "Thêm bộ môn thành công" : "Cập nhật bộ môn thành công");
            getSearchDepartment(1, $('#pageSize').val(), {departmentName: ""});
            $('#departmentModal').modal('hide');
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}`).addClass('is-invalid');
                });
            } else if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            } else {
                showToastError('Có lỗi xảy ra');
            }
        }
    });
};



