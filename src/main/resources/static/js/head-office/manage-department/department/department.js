$(document).ready(function () {
    fetchSearchDepartment();
    onChangePageSize();
    handleFilter();
    handleResetFilter();

    $("#modifyDepartmentButton").on("click", function () {
        handleAddOrUpdateDepartmentConfirm();
    });
});

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
};
const setStateDepartmentId = (departmentId) => {
    stateDepartmentId = departmentId;
};
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
const setValueFields = (departmentName, departmentCode) => {
    $("#modifyDepartmentCode").val(departmentCode);
    $("#modifyDepartmentName").val(departmentName);
};

const getValueFields = () => {
    return {
        departmentName: $("#modifyDepartmentName").val(),
        departmentCode: $("#modifyDepartmentCode").val(),
    };
};
const isFieldHasAnyError = () => {
    let isError = false;
    for (let field in getValueFields()) {
        if (getValueFields()[field].trim() === "") {
            isError = true;
        }
    }
    return isError;
};
//----------------------------------------------------------------------------------------------------------------------

const fetchSearchDepartment = (
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
            const responseData = responseBody?.data?.data;
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
                                <span onclick="handleOpenModalManipulateMajor('${department.id}')" class="fs-4">
                                    <i 
                                        class="fa-solid fa-receipt"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span onclick="openModalAddOrUpdateDepartment(1,'${department.id}','${department.departmentName}','${department.departmentCode}')" class="fs-4">
                                    <i class="fa-solid fa-pen-to-square"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span class="fs-4" onclick="handleRedirectDepartmentFacility('${department.id}')">
                                    <i 
                                        class="fa-solid fa-circle-info"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#departmentTableBody').html(departments);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu bộ môn');
        }
    });
};

const handleResetFilter = () => {
    $("#resetFilter").on("click", function () {
        $("#pageSize").val("5");
        resetGlobalParamsSearch();
        fetchSearchDepartment();
    });
};

const handleFilter = () => {
    $("#buttonFilter").on("click", function () {
        fetchSearchDepartment(1, $('#pageSize').val(), getGlobalParamsSearch());
    });
};

const changePage = (page) => {
    fetchSearchDepartment(page, $('#pageSize').val(), getGlobalParamsSearch());
};

const onChangePageSize = () => {
    $("#pageSize").on("change", function () {
        resetGlobalParamSearch();
        fetchSearchDepartment(1, $('#pageSize').val(), getGlobalParamsSearch());
    });
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

const handleRedirectDepartmentFacility = (departmentId) => {
    window.location.href = ApiConstant.REDIRECT_HEAD_OFFICE_DEPARTMENT_FACILITY + "/" + departmentId;
};

const openModalAddOrUpdateDepartment = (status, departmentId, departmentName, departmentCode) => {
    if (status === 0) {
        //add
        resetFields();
    } else {
        //update
        setStateDepartmentId(departmentId);
        setValueFields(departmentName, departmentCode);
    }
    viewShowModalAddOrUpdateDepartment(status);
    resetFieldsError();
    setStateAddOrUpdate(status);
    $('#departmentModal').modal('show');
};

const viewShowModalAddOrUpdateDepartment = (status) => {
    $("#departmentModalLabel").text(status === 0 ? "Thêm bộ môn" : "Cập nhật bộ môn");
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
                    className: "btn btn-black",
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
        data: JSON.stringify(getValueFields()),
        success: function (responseBody) {
            showToastSuccess(getStateAddOrUpdate() === 0 ? "Thêm bộ môn thành công" : "Cập nhật bộ môn thành công");
            fetchSearchDepartment();
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



