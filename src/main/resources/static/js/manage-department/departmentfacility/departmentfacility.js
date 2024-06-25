$(document).ready(function () {
    fetchListFacility();
    fetchListHeadOfDepartment();
    fetchDepartmentName();

    fetchSearchDepartmentFacility();
    onChangePageSize();
    handleFilter();
    handleResetFilter();

    $("#modifyDepartmentFacilityButton").on("click", function () {
        handleAddOrUpdateDepartmentFacilityConfirm();
    });
});

//State module
//----------------------------------------------------------------------------------------------------------------------
//state
let stateAddOrUpdate = 0; // 0 -> add, 1 -> update
let stateDepartmentFacilityId = 0;
///state

//START: getter
const getStateAddOrUpdate = () => stateAddOrUpdate;
const getStateDepartmentFacilityId = () => stateDepartmentFacilityId;
//END: getter

//START: setter
const setStateAddOrUpdate = (state) => {
    stateAddOrUpdate = state;
};
const setStateDepartmentFacilityId = (departmentFacilityId) => {
    stateDepartmentFacilityId = departmentFacilityId;
};
//END: setter
//----------------------------------------------------------------------------------------------------------------------

//Fields Input
//----------------------------------------------------------------------------------------------------------------------
const setValueFields = (facilityId, headOfDepartmentId) => {
    $("#modifyFacilityId").val(facilityId);
    $("#modifyHeadOfDepartmentId").val(headOfDepartmentId);
};

const getValueFields = () => {
    const departmentId = getDepartmentId().replace("#", "");
    return {
        departmentId: departmentId,
        facilityId: $("#modifyFacilityId").val(),
        headOfDepartmentId: $("#modifyHeadOfDepartmentId").val(),
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

//ViewModal
//----------------------------------------------------------------------------------------------------------------------
const viewShowModalAddOrUpdateDepartment = (status) => {
    $("#departmentFacilityModalLabel").text(status === 0 ? "Thêm bộ môn theo cở sở cho bộ môn: " : "Cập nhật bộ môn theo cở sở cho bộ môn: ");
    $("#modifyDepartmentFacilityButton").text(status === 0 ? "Thêm" : "Cập nhật");
};

const resetFieldsError = () => {
    $('.form-select').removeClass('is-invalid');
    $("#facilityIdError").text("");
    $("#headOfDepartmentIdError").text("");
};

const resetFields = () => {
    $("#modifyFacilityId").val("");
    $("#modifyHeadOfDepartmentId").val("");
};
//----------------------------------------------------------------------------------------------------------------------

//GetParamsSearch
const getGlobalParamsSearch = () => {
    return {
        facilityName: $("#departmentFacilityName").val()
    }
};

const resetGlobalParamsSearch = () => {
    $("#departmentFacilityName").val("");
};

//GetDepartmentId -> on URL
const getDepartmentId = () => {
    const pathUrl = location.href.split('/');
    return pathUrl[pathUrl.length - 1];
};

const fetchSearchDepartmentFacility = (
    page = 1,
    size = $('#pageSize').val(),
    paramSearch = {
        facilityName: ""
    },
) => {

    const params = {
        page: page,
        size: size,
        facilityName: paramSearch.facilityName
    };

    let url = ApiConstant.API_HEAD_OFFICE_DEPARTMENT_FACILITY + "/get-all-department-facility/" + getDepartmentId() + "?";

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    url = url.replace("#", '');

    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const responseData = responseBody?.data?.data;
            if (responseData.length === 0) {
                $('#departmentFacilityTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const departmentFacility = responseData.map((departmentFacility, index) => {
                const departmentName = departmentFacility.headOfDepartmentName + " - " + departmentFacility.headOfDepartmentCode;
                return `<tr>
                            <td>${departmentFacility.orderNumber}</td>
                            <td>${departmentFacility.facilityName}</td>
                            <td>${departmentName ? departmentName : "Chưa xác định"}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span class="fs-4">
                                    <i onclick="openModalAddOrUpdateDepartmentFacility(
                                        1, '${departmentFacility.departmentFacilityId}',
                                        '${departmentFacility.facilityId}', '${departmentFacility.headOfDepartmentId}'
                                    )" class="fa-solid fa-pen-to-square" style="cursor: pointer; margin-left: 10px;"></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#departmentFacilityTableBody').html(departmentFacility);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu bộ môn theo cở sở');
        }
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

const changePage = (page) => {
    fetchSearchDepartmentFacility(page, $('#pageSize').val(), getGlobalParamsSearch());
};

const fetchListFacility = () => {
    $.ajax({
        contentType: "application/json",
        type: "GET",
        url: ApiConstant.API_HEAD_OFFICE_DEPARTMENT_FACILITY + "/get-list-facility",
        dataType: 'json',
        success: function (data) {
            const select = document.getElementById('modifyFacilityId');
            data?.data.forEach(item => {
                const option = document.createElement('option');
                option.value = item.facilityId;
                option.text = item.facilityName;
                select.appendChild(option);
            });
            $('#modifyFacilityId').val(select);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy danh sách cơ sở');
        }
    });
};

const fetchListHeadOfDepartment = () => {
    $.ajax({
        contentType: "application/json",
        type: "GET",
        url: ApiConstant.API_HEAD_OFFICE_DEPARTMENT_FACILITY + "/get-list-head-of-department",
        dataType: 'json',
        success: function (data) {
            const select = document.getElementById('modifyHeadOfDepartmentId');
            data?.data.forEach(item => {
                const option = document.createElement('option');
                option.value = item.staffId;
                option.text = item.staffName + " - " + item.staffCode;
                select.appendChild(option);
            });
            $('#modifyHeadOfDepartmentId').val(select);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy danh sách chủ nhiệm bộ môn');
        }
    });
};

const fetchDepartmentName = () => {
    $.ajax({
        contentType: "application/json",
        type: "GET",
        url: ApiConstant.API_HEAD_OFFICE_DEPARTMENT_FACILITY + "/get-department-name/" + getDepartmentId(),
        dataType: 'json',
        success: function (responseData) {
            console.log(responseData?.data);
            $("#showCurrentDepartmentNameScreen").text(responseData?.data);
            $("#showCurrentDepartmentNameModal").text(responseData?.data);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy tên bộ môn');
        }
    });
};

const onChangePageSize = () => {
    $("#pageSize").on("change", function () {
        resetGlobalParamsSearch();
        fetchSearchDepartmentFacility(1, $('#pageSize').val(), getGlobalParamsSearch());
    });
};

const handleResetFilter = () => {
    $("#resetFilter").on("click", function () {
        $("#pageSize").val("5");
        resetGlobalParamsSearch();
        fetchSearchDepartmentFacility();
    });
};

const handleFilter = () => {
    $("#buttonFilter").on("click", function () {
        fetchSearchDepartmentFacility(1, $('#pageSize').val(), getGlobalParamsSearch());
    });
};

const openModalAddOrUpdateDepartmentFacility = (status, departmentFacilityId, facilityId, headOfDepartmentId) => {
    if (status === 0) {
        //add
        resetFields();
    } else {
        //update
        setStateDepartmentFacilityId(departmentFacilityId);
        setValueFields(facilityId, headOfDepartmentId);
    }
    viewShowModalAddOrUpdateDepartment(status);
    resetFieldsError();
    setStateAddOrUpdate(status);
    $('#departmentFacilityModal').modal('show');
};

const handleAddOrUpdateDepartmentFacilityConfirm = () => {
    if (isFieldHasAnyError()) {
        handleAddOrUpdateDepartmentFacility();
    } else {
        swal({
            title: getStateAddOrUpdate() === 0 ? "Xác nhận thêm?" : "Xác nhận cập nhật?",
            text: getStateAddOrUpdate() === 0 ? "Bạn có chắc muốn thêm bộ môn theo cở sở này không?" : "Bạn có chắc muốn cập nhật bộ môn theo cơ sở này không?",
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
                handleAddOrUpdateDepartmentFacility();
            }
        });
    }
};

const handleAddOrUpdateDepartmentFacility = () => {
    const addOrUpdateApiUrl = getStateAddOrUpdate() === 0 ? "/add-department-facility" : "/update-department-facility/" + getStateDepartmentFacilityId();
    const finalUrl = ApiConstant.API_HEAD_OFFICE_DEPARTMENT_FACILITY + addOrUpdateApiUrl;
    $.ajax({
        contentType: "application/json",
        type: getStateAddOrUpdate() === 0 ? "POST" : "PUT",
        url: finalUrl,
        dataType: 'json',
        data: JSON.stringify(getValueFields()),
        success: function (responseBody) {
            showToastSuccess(getStateAddOrUpdate() === 0 ? "Thêm bộ môn theo cở sở thành công" : "Cập nhật bộ môn theo cở sở thành công");
            fetchSearchDepartmentFacility();
            $('#departmentFacilityModal').modal('hide');
        },
        error: function (error) {
            console.log(error);
            $('.form-select').removeClass('is-invalid');
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