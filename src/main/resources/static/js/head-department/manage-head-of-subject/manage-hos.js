const examDistributionInfo = getExamDistributionInfo();

let currentStaffId = null;
let currentSemesterId = null;

$(document).ready(() => {
    getAllSemesterAndSetDefaultCurrentSemester();

    $('#querySearchSemester').change((e) => {
        currentSemesterId = e.target.value;
        getListHeadSubject(INIT_PAGINATION.page, INIT_PAGINATION.size, currentSemesterId);
    });

    $('#btnAssignSubjectForStaff').click(saveAssignSubjectGroupForStaff);
    $('#querySearchStaff').on('input', handleListenSearchQuery);
    $('#btnSearchSubjectAssign').click(handleSearchSubjectAssign);
    $('#btnResetSearchSubjectAssign').click(handleResetSearchSubjectAssign);
    $('#btnShowHistoryLog').click(handleShowModalHistory);

    handleCheckAssignSubjectGroup();

    $('#querySearchAttachRoleName').on('input', handleSearchSubjectGroup);

    $('#btnCloseAssignSubjectGroupForStaff').click(closeModalModifyAssignSubject);
});

const getListHeadSubject = (
    page = INIT_PAGINATION.page,
    size = INIT_PAGINATION.size,
    semesterId = null,
    query = null
) => {
    const url = getUrlParameters(ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS, {
        page,
        size,
        departmentFacilityId: examDistributionInfo.departmentFacilityId,
        currentUserId: examDistributionInfo.userId,
        semesterId,
        q: query ? query.trim() : null,
    });

    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: (responseBody) => {
            const data = responseBody?.data?.data || [];
            if (data.length === 0) {
                $('#manageHOSTableBody').html('<tr><td colspan="8" style="text-align: center;">Không có dữ liệu</td></tr>');
                $('#pagination').empty();
                return;
            }

            const staffs = data.map(staff => `
                <tr>
                    <td>${staff.orderNumber}</td>
                    <td>${staff.staffCode}</td>
                    <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">${staff.staffName}</td>
                    <td>${staff.accountFPT}</td>
                    <td>${staff.accountFE}</td>
                    <td>
                        <span class="tag tag-warning">${staff.roleName}</span>
                    </td>
                    <td>${staff.semesterInfo ? ('<span class="tag tag-warning">' + staff.semesterInfo + '</span>') : '<span class="tag tag-warning">Empty</span>'}</td>
                    <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;" class="text-center">
                        <a data-bs-toggle="tooltip" data-bs-placement="top" title="Phân công môn học"
                           onclick="showModalModifyAssignSubject('${staff.id}', '${staff.staffName}', '${staff.staffCode}')">
                            <i class="fa-solid fa-eye" style="cursor: pointer; margin-left: 10px;"></i>
                        </a>
                    </td>
                </tr>`).join('');

            $('#manageHOSTableBody').html(staffs);
            createPagination(responseBody?.data?.totalPages || 1, page);
        },
        error: (error) => showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau")
    });
};

const createPagination = (totalPages, currentPage) => {
    let paginationHtml = '';

    paginationHtml += currentPage > 1 ? `
        <li class="page-item"><a class="page-link" href="#" onclick="changePage(${currentPage - 1})">Trước</a></li>`
        : `<li class="page-item disabled"><a class="page-link" href="#">Trước</a></li>`;

    for (let i = 1; i <= totalPages; i++) {
        paginationHtml += i === currentPage ? `<li class="page-item active"><a class="page-link text-white" href="#">${i}</a></li>`
            : `<li class="page-item"><a class="page-link" href="#" onclick="changePage(${i})">${i}</a></li>`;
    }

    paginationHtml += currentPage < totalPages ? `
        <li class="page-item"><a class="page-link" href="#" onclick="changePage(${currentPage + 1})">Sau</a></li>`
        : `<li class="page-item disabled"><a class="page-link" href="#">Sau</a></li>`;

    $('#pagination').html(paginationHtml);
};

const handleSearchSubjectAssign = () => {
    getSubjectGroupAssignForStaff(currentStaffId, INIT_PAGINATION.page, INIT_PAGINATION.size, $('#subjectCode').val(), $('#subjectName').val(), $('#subjectType').val());
};

const handleResetSearchSubjectAssign = () => {
    $('#subjectCode, #subjectName, #subjectType').val('');
    getSubjectGroupAssignForStaff(currentStaffId);
};

const changePage = (page) => getListHeadSubject(page);

const showModalModifyAssignSubject = (staffId, staffName, staffCode) => {
    currentStaffId = staffId;
    $('#modifyAssignSubjectForStaffLabel').text(`Phân công môn học cho giảng viên: ${staffCode} - ${staffName}`);
    getSubjectGroupAssignForStaff(staffId);
    $('#modifyAssignSubjectForStaff').modal('show');
};

const closeModalModifyAssignSubject = () => {
    $('#modifyAssignSubjectForStaff').modal('hide');
    $('#querySearchAttachRoleName').val('');
}

const getAllSemesterAndSetDefaultCurrentSemester = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS + "/semester",
        contentType: "application/json",
        success: (responseBody) => {
            const semesters = responseBody?.data?.map(semester => `<option value="${semester.id}">${semester.semesterInfo}</option>`).join('');
            $('#querySearchSemester').html(semesters);
            $('#querySearchSemester').val(responseBody?.data[0]?.id);
            currentSemesterId = responseBody?.data[0]?.id;
            getListHeadSubject(INIT_PAGINATION.page, INIT_PAGINATION.size, currentSemesterId);
        },
        error: (error) => showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau")
    });
};

const handleListenSearchQuery = debounce(() => {
    getListHeadSubject(INIT_PAGINATION.page, INIT_PAGINATION.size, currentSemesterId, $('#querySearchStaff').val());
}, 300);

const getSubjectGroupAssignForStaff = (
    staffId,
    page = INIT_PAGINATION.page,
    size = INIT_PAGINATION.size,
    attachRoleName = null
) => {
    const url = getUrlParameters(ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS + "/subject-group", {
        page,
        size,
        staffId,
        attachRoleName: attachRoleName ? attachRoleName.trim() : null
    });

    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: (responseBody) => {
            const data = responseBody?.data?.data || [];
            if (data.length === 0) {
                $('#subjectAssignedTableBody').html('<tr><td colspan="4" style="text-align: center;">Không có dữ liệu</td></tr>');
                $('#pagination').empty();
                return;
            }

            const subjectGroupAssigneds = data.map(subjectGroup => `
                <tr>
                    <td style="width: 1px; text-wrap: nowrap;" class="text-center">
                        <div class="col-auto">
                            <label class="colorinput">
                                <input name="color" data-subject-id="${subjectGroup.id}" ${subjectGroup.assigned === 1 ? 'checked' : ''} type="checkbox" class="colorinput-input"/>
                                <span class="colorinput-color bg-dark"></span>
                            </label>
                        </div>
                    </td>
                    <td style="width: 1px; text-wrap: nowrap;">${subjectGroup.attachRoleName}</td>
                </tr>`).join('');

            $('#subjectAssignedTableBody').html(subjectGroupAssigneds);
            createPaginationAssignSubject(responseBody?.data?.totalPages || 1, page);
        },
        error: (error) => showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau")
    });
};

const saveAssignSubjectGroupForStaff = () => {
    const data = {
        staffId: currentStaffId,
        semesterId: currentSemesterId,
        subjectGroupId: $('input[name="color"]:checked').data('subject-id')
    };

    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS + "/subject-assigned",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: (responseBody) => {
            showToastSuccess(responseBody?.message || "Cập nhật phân công nhóm môn học thành công");
            getSubjectGroupAssignForStaff(currentStaffId);
            getListHeadSubject(INIT_PAGINATION.page, INIT_PAGINATION.size, currentSemesterId);
        },
        error: (error) => showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau")
    });
};

const createPaginationAssignSubject = (totalPages, currentPage) => {
    let paginationHtml = '';

    paginationHtml += currentPage > 1 ? `
        <li class="page-item"><a class="page-link" href="#" onclick="changePageAssignSubject(${currentPage - 1})">Trước</a></li>`
        : `<li class="page-item disabled"><a class="page-link" href="#">Trước</a></li>`;

    for (let i = 1; i <= totalPages; i++) {
        paginationHtml += i === currentPage ? `<li class="page-item active"><a class="page-link text-white" href="#">${i}</a></li>`
            : `<li class="page-item"><a class="page-link" href="#" onclick="changePageAssignSubject(${i})">${i}</a></li>`;
    }

    paginationHtml += currentPage < totalPages ? `
        <li class="page-item"><a class="page-link" href="#" onclick="changePageAssignSubject(${currentPage + 1})">Sau</a></li>`
        : `<li class="page-item disabled"><a class="page-link" href="#">Sau</a></li>`;

    $('#paginationSubjectAssigned').html(paginationHtml);
};

const changePageAssignSubject = (page) => getSubjectGroupAssignForStaff(currentStaffId, page);

const handleCheckAssignSubjectGroup = () => {
    $(document).on('click', '.colorinput-input', function () {
        $('.colorinput-input').not(this).prop('checked', false);
    });
};

const handleSearchSubjectGroup = debounce(() => {
    const attachRoleName = $('#querySearchAttachRoleName').val();
    getSubjectGroupAssignForStaff(currentStaffId, INIT_PAGINATION.page, INIT_PAGINATION.size, attachRoleName);
}, 300);

const getHistoryAssignSubject = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS + "/history",
        contentType: "application/json",
        success: (responseBody) => {
            const data = responseBody?.data || [];
            if (data.length === 0) {
                $('#historyTableBody').html('<tr><td colspan="4" style="text-align: center;">Không có dữ liệu</td></tr>');
                $('#pagination').empty();
                return;
            }

            const historyAssignSubjects = data.map(history => `
                <tr>
                    <td>${history.orderNumber}</td>
                    <td>${history.createDate}</td>
                    <td>${history.mail + ' ' + history.content}</td>
                    <td>${history.author}</td>
                </tr>`).join('');

            $('#historyLogTableBody').html(historyAssignSubjects);
        },
        error: (error) => showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau")
    });
};

const handleShowModalHistory = () => {
    getHistoryAssignSubject();
    $('#viewHistoryLog').modal('show');
};

