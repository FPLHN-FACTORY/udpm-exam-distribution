$(document).ready(function () {
    fetchSearchSubject();
    onChangePageSize();
});

//START: Exam Distribution Information
const examDistributionInfor = getExamDistributionInfo();
//END: Exam Distribution Information

//------------------------------------------------state getter setter---------------------------------------------------

//START:state
let stateCurrentStaffId = "";
//END:state

//START:getter
const getStateCurrentStaffId = () => stateCurrentStaffId;

const getValueMaxUpload = () => $("#maxUploadNumber").val();
//END:getter

//START: setter
const setStateCurrentStaffId = (value) => {
    stateCurrentStaffId = value;
};
const setValueMaxUpload = (value) => {
    $("#maxUploadNumber").val(value);
};
const setCurrentSubjectNameOnView = (value) => {
    $("#currentSubjectName").text(value);
};
//END: setter
//------------------------------------------------state getter setter---------------------------------------------------

const fetchSearchSubject = (
    page = 1,
    size = $('#pageSize').val(),
    paramSearch = getGlobalParamsSearch(),
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

    showLoading();
    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            hideLoading();
            const responseData = responseBody?.data?.data;
            if (responseData.length === 0) {
                $('#subjectTableBody').html(`
                    <tr>
                         <td colspan="6" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
$('#pagination').empty();
                return;
            }
            const subjects = responseData.map(subject => {
                return `<tr>
                            <td>${subject.orderNumber}</td>
                            <td>${subject.subjectCode}</td>
                            <td>${subject.subjectName}</td>
                            <td>${subject.departmentName}</td>
                            <td>${convertSubjectType(subject.subjectType)}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span
                                    data-bs-toggle="tooltip" data-bs-title="Tải đề thi mẫu"
                                    style="margin: 0 3px"
                                    onclick="handleOpenModalSampleExamPaper(1,'${subject.id}','${subject.fileId}')" class="fs-6"
                                >
                                    <i 
                                        class="fa-solid fa-cloud-arrow-up"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
                                <span
                                    data-bs-toggle="tooltip" data-bs-title="Chi tiết đề thi mẫu"
                                    style="margin: 0 3px"
                                    onclick="handleOpenModalSampleExamPaper(2,'${subject.id}','${subject.fileId}')" class="fs-6" style="margin: 0 3px;"
                                >
                                    <i 
                                        class="fa-solid fa-eye"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
                                <span
                                    data-bs-toggle="tooltip" data-bs-title="Phân người phát đề"
                                    style="margin: 0 3px"
                                    onclick="handleOpenModalAssignUpload('${subject.id}','${subject.subjectName}')" class="fs-6"
                                >
                                    <i 
                                        class="fa-solid fa-person"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#subjectTableBody').html(subjects);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học');
            hideLoading();
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
        fetchSearchSubject();
    });
};

const handleSearchSubject = () => {
    fetchSearchSubject();
};

const handleClearSearch = () => {
    resetGlobalParamsSearch();
    fetchSearchSubject();
};
//------------------------------------------------------function--------------------------------------------------------


//-------------------------------------------------------------------------------------------------Assign Uploader-----------------------------------------------------------------------------------------------

//------------------------------------------------state getter setter---------------------------------------------------
let stateCurrentSubjectId = "";
let stateCurrentSubjectName = "";

//START: getter
const getCurrentSubjectId = () => stateCurrentSubjectId;
const getStateCurrentSubjectName = () => stateCurrentSubjectName;

//END: getter

//START: setter
const setCurrentSubjectId = (value) => {
    stateCurrentSubjectId = value
};
const setStateCurrentSubjectName = (value) => {
    stateCurrentSubjectName = value
};
//END: setter
//------------------------------------------------state getter setter---------------------------------------------------

//------------------------------------------------------function--------------------------------------------------------
const handleOpenModalAssignUpload = (subjectId, subjectName) => {
    setCurrentSubjectId(subjectId);
    setStateCurrentSubjectName(subjectName);

    setCurrentSubjectNameOnView(subjectName);
    fetchSearchStaff();
    onChangePageSizeAssignUploader();
    $("#assignUploaderModal").modal("show");
};

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

const resetGlobalParamsSearchAssignUploader = () => {
    $("#staffFPTFE").val("");
    $("#staffCode").val("");
    $("#staffName").val("")
};

const handleClearSearchAssignUploader = () => {
    resetGlobalParamsSearchAssignUploader();
    fetchSearchStaff();
};

const handleAssignUploaderModal = (staffId, isAssigned, isHasSampleExamPaper) => {
    setStateCurrentStaffId(staffId);
    if (isHasSampleExamPaper === "0") {
        showToastError("Môn học này chưa tải đề thi mẫu");
        $("#input-assign-uploader").prop("checked", false);
    } else {
        if (isAssigned === "0") {
            setValueMaxUpload("");
            $("#input-assign-uploader").prop("checked", false);

            $("#assignUploaderModal").modal("hide");
            $("#maxUploadModal").modal("show");
        } else {
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
                    fetchSearchStaff();
                },
                error: function (error) {
                    showToastError(error?.responseJSON?.message);
                }
            });
        }
    }
};

const handleAssignUploader = () => {
    $.ajax({
        contentType: "application/json",
        type: "POST",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER + "/assign-uploader",
        dataType: 'json',
        data: JSON.stringify({
            staffId: getStateCurrentStaffId(),
            subjectId: getCurrentSubjectId(),
            maxUpload: getValueMaxUpload()
        }),
        success: function (responseBody) {
            showToastSuccess(responseBody.message);
            handleOpenModalAssignUploaderAgain();
            $("#maxUploadModal").modal("hide");
        },
        error: function (error) {
            showToastError(error?.responseJSON?.message);
        }
    });
};

const handleOpenModalAssignUploaderAgain = () => {
    handleOpenModalAssignUpload(getCurrentSubjectId(), getStateCurrentSubjectName());
};

//------------------------------------------------------function--------------------------------------------------------

const fetchSearchStaff = (
    page = 1,
    size = $('#pageSizeAssignUploader').val(),
    paramSearch = getGlobalParamsSearchAssignUploader()
) => {
    const params = {
        page: page,
        size: size,
        staffCode: paramSearch.staffCode,
        staffName: paramSearch.staffName,
        accountFptOrFe: paramSearch.accountFptOrFe,
        subjectId: paramSearch.subjectId
    };

    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER + "/staff" + '?';

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    showLoading();
    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            hideLoading();
            const responseData = responseBody?.data?.data;
            if (responseData.length === 0) {
                $('#assignUploaderTableBody').html(`
                    <tr>
                         <td colspan="7" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
$('#pagination').empty();
                return;
            }
            const staffs = responseData.map(staff => {
                return `<tr>
                            <td>${staff.orderNumber}</td>
                            <td>${staff.staffCode}</td>
                            <td>${staff.name}</td>
                            <td>${staff.accountFpt}</td>
                            <td>${staff.accountFe}</td>
                            <td>${staff.maxUpload == null ? "Không xác định" : staff.maxUpload}</td>
                            <td>
                                <div class="col-auto">
                                  <label class="colorinput">
                                    <input
                                        onclick="handleAssignUploaderModal('${staff.id}','${staff.isAssigned}','${staff.isHasSampleExamPaper}')"
                                        name="color"
                                        type="checkbox"
                                        class="colorinput-input"
                                        ${staff.isAssigned == true ? "checked" : ""}
                                        id="input-assign-uploader"
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

const onChangePageSizeAssignUploader = () => {
    $("#pageSizeAssignUploader").on("change", function () {
        resetGlobalParamsSearchAssignUploader();
        fetchSearchStaff();
    });
};

const getGlobalParamsSearchAssignUploader = () => {
    return {
        accountFptOrFe: $("#staffFPTFE").val(),
        staffCode: $("#staffCode").val(),
        staffName: $("#staffName").val(),
        subjectId: getCurrentSubjectId()
    }
}

const convertSubjectType = (status) => {
    switch (status) {
        case "TRADITIONAL":
            return '<span class="tag tag-success">TRADITIONAL</span>';
        case 'ONLINE':
            return '<span class="tag tag-cyan">ONLINE</span>';
        case 'BLEND':
            return '<span class="tag tag-blue">BLEND</span>';
        case 'TRUC_TUYEN':
            return '<span class="tag tag-lime">TRUC_TUYEN</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}
