$(document).ready(function () {
    onChangePageSizeMajor();
    handleAddEvent($("#majorName"),"keyup", function () {
        fetchSearchMajor(1, $('#pageSizeMajor').val(), getGlobalParamsSearchMajor());
    });

    $("#modifyMajorButton").on("click", function () {
        handleAddOrUpdateMajorConfirm();
    });
});

//----------------------------------------------------------------------------------------------------------------------
// START: state
let stateAddOrUpdateMajor = 0; // 0 -> add, 1 -> update
let stateMajorId = 0; // 0 -> add, 1 -> update
let stateCurrentDepartmentIdModal = 0;
// END: state

// START: getter
const getStateAddOrUpdateMajor = () => stateAddOrUpdateMajor;
const getStateMajorId = () => stateMajorId;
const getStateCurrentDepartmentIdModal = () => stateCurrentDepartmentIdModal;
// END: getter

// START: setter
const setStateAddOrUpdateMajor = (state) => {
    stateAddOrUpdateMajor = state;
};
const setStateMajorId = (state) => {
    stateMajorId = state;
};
const setStateCurrentDepartmentIdModal = (departmentId) => {
    stateCurrentDepartmentIdModal = departmentId;
};
// END: setter
//----------------------------------------------------------------------------------------------------------------------

//----------------------------------------------------------------------------------------------------------------------
const getGlobalParamsSearchMajor = () => {
    return {
        majorName: $("#majorName").val()
    }
};

const resetGlobalParamSearchMajor = () => {
    $("#majorName").val("");
};

const onChangePageSizeMajor = () => {
    $("#pageSizeMajor").on("change", function () {
        resetGlobalParamSearchMajor();
        fetchSearchMajor(1, $('#pageSizeMajor').val(), getGlobalParamsSearchMajor());
    });
};

const resetFieldsMajor = () => {
    $("#modifyMajorName").val("");
};

const setValueFieldsMajor = (majorName) => {
    $("#modifyMajorName").val(majorName);
};

const viewShowModalAddOrUpdateMajor = (status) => {
    $("#majorModalLabel").text(status === 0 ? "Thêm chuyên ngành" : "Cập nhật chuyên ngành");
    $("#modifyMajorButton").text(status === 0 ? "Thêm" : "Cập nhật");
};

const resetFieldsErrorMajor = () => {
    $('.form-control').removeClass('is-invalid');
    $("#majorNameError").val("");
};

const openModalAddOrUpdateMajor = (status, majorId, majorName) => {
    if (status === 0) {
        //add
        resetFieldsMajor();
    } else {
        //update
        $("#majorModal").modal("hide");
        setStateMajorId(majorId);
        setValueFieldsMajor(majorName);
    }
    viewShowModalAddOrUpdateMajor(status);
    resetFieldsErrorMajor();
    setStateAddOrUpdateMajor(status);
    $('#addMajorModal').modal('show');
};

const isFieldHasAnyErrorMajor = () => {
    let isError = false;
    for (let field in getValueFieldsMajor()) {
        if (getValueFieldsMajor()[field].trim() === "") {
            isError = true;
        }
    }
    return isError;
};
//----------------------------------------------------------------------------------------------------------------------


const handleOpenModalManipulateMajor = (departmentId) => {
    setStateCurrentDepartmentIdModal(departmentId);
    fetchSearchMajor();
    $("#majorModal").modal("show");
};

const fetchSearchMajor = (
    page = 1,
    size = $('#pageSize').val(),
    paramSearch = {
        majorName: ""
    },
) => {

    const params = {
        page: page,
        size: size,
        majorName: paramSearch.majorName
    };

    let url = ApiConstant.API_HEAD_OFFICE_MAJOR + "/get-all-major/" + getStateCurrentDepartmentIdModal() + '?';

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
            console.log(responseBody);
            const responseData = responseBody?.data?.data;
            if (responseData.length === 0) {
                $('#majorTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const departments = responseData.map((department, index) => {
                return `<tr>
                            <td>${department.orderNumber}</td>
                            <td>${department.majorName}</td>
                            <td>${department.majorStatus === 0 ? "Hoạt động" : "Ngưng hoạt động"}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span onclick="openModalAddOrUpdateMajor(1,'${department.majorId}','${department.majorName}')" 
                                    data-bs-toggle="tooltip" 
                                    data-bs-title="Cập nhật"
                                    class="fs-4">
                                    <i class="fa-solid fa-pen-to-square"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span onclick="handleDeleteMajor('${department.majorId}')" 
                                    data-bs-toggle="tooltip" 
                                    data-bs-title="Đổi trạng thái"
                                    class="fs-4">
                                    <i 
                                        class="fa-solid fa-rotate-left"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#majorTableBody').html(departments);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationMajor(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu bộ môn');
        }
    });
};

const createPaginationMajor = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePageMajor(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePageMajor(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePageMajor(${currentPage + 1})">
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

    $('#paginationMajor').html(paginationHtml);
};

const changePageMajor = (page) => {
    fetchSearchMajor(page, $('#pageSizeMajor').val(), getGlobalParamsSearchMajor());
};

const handleAddOrUpdateMajorConfirm = () => {
    if (isFieldHasAnyErrorMajor()) {
        handleAddOrUpdateMajor();
    } else {
        swal({
            title: getStateAddOrUpdateMajor() === 0 ? "Xác nhận thêm?" : "Xác nhận cập nhật?",
            text: getStateAddOrUpdateMajor() === 0 ? "Bạn có chắc muốn thêm chuyên ngành này không?" : "Bạn có chắc muốn cập nhật chuyên ngành này không?",
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
                handleAddOrUpdateMajor();
            }
        });
    }
};

const getValueFieldsMajor = () => {
    return {
        departmentId: getStateCurrentDepartmentIdModal(),
        majorName: $("#modifyMajorName").val(),
    };
};

const handleAddOrUpdateMajor = () => {
    const addOrUpdateApiUrl = getStateAddOrUpdateMajor() === 0 ? "/add-major" : "/update-major/" + getStateMajorId();
    const finalUrl = ApiConstant.API_HEAD_OFFICE_MAJOR + addOrUpdateApiUrl;
    $.ajax({
        contentType: "application/json",
        type: getStateAddOrUpdateMajor() === 0 ? "POST" : "PUT",
        url: finalUrl,
        dataType: 'json',
        data: JSON.stringify(getValueFieldsMajor()),
        success: function (responseBody) {
            showToastSuccess(getStateAddOrUpdateMajor() === 0 ? "Thêm chuyên ngành thành công" : "Cập nhật chuyên ngành thành công");
            fetchSearchMajor();
            $('#addMajorModal').modal('hide');
            handleOpenModalManipulateMajor(getStateCurrentDepartmentIdModal()); // bật lại màn chi tiết major
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

const handleDeleteMajor = (majorId) => {
    swal({
        title: "Xác nhận thay đổi trạng thái?",
        text: "Bạn có chắc muốn thay đổi trạng thái của chuyên ngành này không?",
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
            $.ajax({
                contentType: "application/json",
                type: "PUT",
                url: ApiConstant.API_HEAD_OFFICE_MAJOR + "/delete-major/" + majorId,
                dataType: 'json',
                success: function (responseBody) {
                    showToastSuccess("Chuyển đổi trạng thái chuyên ngành thành công");
                    fetchSearchMajor();
                },
                error: function (error) {
                    showToastError('Có lỗi xảy ra');
                }
            });
        }
    });
};

const handleCloseModalAddMajor = () => {
    handleOpenModalManipulateMajor(getStateCurrentDepartmentIdModal());
}