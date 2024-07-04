$(document).ready(function () {
    fetchSearchSubject();
    onChangePageSize();
});

//START: Exam Distribution Information
const examDistributionInfor = getExamDistributionInfo();
//END: Exam Distribution Information

//------------------------------------------------state getter setter---------------------------------------------------


//START: setter
const setCurrentSubjectNameOnView = (value) => {
    $("#currentSubjectName").text(value);
};
//END: setter
//------------------------------------------------state getter setter---------------------------------------------------

const fetchSearchSubject = (
    page = 1,
    size = $('#pageSize').val(),
    paramSearch = {
        subjectCode: "",
        subjectName: "",
        staffId: examDistributionInfor.userId
    },
) => {
    const params = {
        page: page,
        size: size,
        subjectCode: paramSearch.subjectCode,
        subjectName: paramSearch.subjectName,
        staffId: paramSearch.staffId
    };

    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER + "/subject/" + examDistributionInfor.departmentFacilityId + '?';

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
                $('#subjectTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const subjects = responseData.map(subject => {
                return `<tr>
                            <td>${subject.orderNumber}</td>
                            <td>${subject.subjectCode}</td>
                            <td>${subject.subjectName}</td>
                            <td>${subject.departmentName}</td>
                            <td>${subject.subjectType}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span onclick="handleOpenModalAssignUpload('${subject.id}','${subject.subjectName}')" class="fs-4">
                                    <i 
                                        class="fa-solid fa-eye"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
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
};

//------------------------------------------------------function--------------------------------------------------------

const getGlobalParamsSearch = () => {
    return {
        subjectCode: $("#subjectCode").val(),
        subjectName: $("#subjectName").val(),
        staffId: examDistributionInfor.userId
    }
}

const resetGlobalParamsSearch = () => {
    $("#subjectCode").val("");
    $("#subjectName").val("");
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
    fetchSearchSubject(page, $('#pageSize').val(), getGlobalParamsSearch());
};

const onChangePageSize = () => {
    $("#pageSize").on("change", function () {
        resetGlobalParamsSearch();
        fetchSearchSubject(1, $('#pageSize').val(), getGlobalParamsSearch());
    });
};

const handleSearchSubject = () => {
    fetchSearchSubject(1, $('#pageSize').val(), getGlobalParamsSearch());
};

const handleClearSearch = () => {
    resetGlobalParamsSearch();
    fetchSearchSubject();
};
//------------------------------------------------------function--------------------------------------------------------


//-------------------------------------------------------------------------------------------------Assign Uploader-----------------------------------------------------------------------------------------------

//------------------------------------------------state getter setter---------------------------------------------------
let stateCurrentSubjectId = "";

//START: getter
const getCurrentSubjectId = () => stateCurrentSubjectId
//END: getter

//START: setter
const setCurrentSubjectId = (value) => {
    stateCurrentSubjectId = value
};
//END: setter
//------------------------------------------------state getter setter---------------------------------------------------

//------------------------------------------------------function--------------------------------------------------------
const handleOpenModalAssignUpload = (subjectId, subjectName) => {
    setCurrentSubjectId(subjectId);
    setCurrentSubjectNameOnView(subjectName);
    fetchSearchStaff();
    onChangePageSizeAssignUploader();
    $("#assignUploaderModal").modal("show");
};

const getGlobalParamsSearchAssignUploader = () => {
    return {
        accountFptOrFe: $("#staffFPTFE").val(),
        staffCode: $("#staffCode").val(),
        staffName: $("#staffName").val(),
        subjectId: getCurrentSubjectId()
    }
}

const createPaginationAssignUploader = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePageAssignUploader(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePageAssignUploader(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePageAssignUploader(${currentPage + 1})">
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

    $('#paginationAssignUploader').html(paginationHtml);
};

const changePageAssignUploader = (page) => {
    fetchSearchStaff(page, $('#pageSize').val(), getGlobalParamsSearchAssignUploader());
}

const handleSearchAssignUploader = () => {
    fetchSearchStaff(1, $('#pageSize').val(), getGlobalParamsSearchAssignUploader());
};

const resetGlobalParamsSearchAssignUploader = () => {
    $("#staffFPTFE").val("");
    $("#staffCode").val("");
    $("#staffName").val("")
};

const handleClearSearchAssignUploader = () => {
    resetGlobalParamsSearchAssignUploader();
    fetchSearchStaff();
};

const onChangePageSizeAssignUploader = () => {
    $("#pageSizeAssignUploader").on("change", function () {
        resetGlobalParamsSearchAssignUploader();
        fetchSearchStaff(1, $('#pageSizeAssignUploader').val(), getGlobalParamsSearchAssignUploader());
    });
};

const handleAssignUploader = (staffId) => {
    $.ajax({
        contentType: "application/json",
        type: "POST",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER + "/assign-uploader",
        dataType: 'json',
        data: JSON.stringify({
            staffId: staffId,
            subjectId: getCurrentSubjectId()
        }),
        success: function (responseBody) {
            showToastSuccess(responseBody.message);
        },
        error: function (error) {
            showToastError(error?.responseJSON?.message);
        }
    });
};
//------------------------------------------------------function--------------------------------------------------------

const fetchSearchStaff = (
    page = 1,
    size = $('#pageSize').val(),
    paramSearch = {
        staffCode: "",
        staffName: "",
        accountFptOrFe: "",
        subjectId: getCurrentSubjectId()
    }
) => {
    const params = {
        page: page,
        size: size,
        staffCode: paramSearch.staffCode,
        staffName: paramSearch.staffName,
        accountFptOrFe: paramSearch.accountFptOrFe,
        subjectId: paramSearch.subjectId
    };

    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER + "/staff/" + examDistributionInfor.departmentFacilityId + '?';

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
                $('#assignUploaderTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const staffs = responseData.map(staff => {
                return `<tr>
                            <td>${staff.orderNumber}</td>
                            <td>${staff.staffCode}</td>
                            <td>${staff.name}</td>
                            <td>${staff.accountFpt}</td>
                            <td>${staff.accountFe}</td>
                            <td>
                                <div class="col-auto">
                                  <label class="colorinput">
                                    <input
                                        onclick="handleAssignUploader('${staff.id}')"
                                      name="color"
                                      type="checkbox"
                                      class="colorinput-input"
                                      ${staff.isAssigned == true ? "checked" : ""}
                                    />
                                    <span
                                      class="colorinput-color bg-secondary"
                                    ></span>
                                  </label>
                                </div>
                            </td>
                        </tr>`;
            });
            $('#assignUploaderTableBody').html(staffs);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationAssignUploader(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học');
        }
    });
};