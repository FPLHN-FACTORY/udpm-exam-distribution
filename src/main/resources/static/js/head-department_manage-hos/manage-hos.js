const examDistributionInfo = getExamDistributionInfo();

let currentStaffId = null;

$(document).ready(() => {

    getListStaff();

    $('#btnAssignSubjectForStaff').click(() => {
        saveAssignSubjectForStaff();
    });

    $('#btnSearchStaff').click(() => {
        handleSearchStaff();
    });

    $('#btnResetSearchStaff').click(() => {
        handleResetSearchStaff();
    });

    $('#btnSearchSubjectAssign').click(() => {
        handleSearchSubjectAssign();
    });

    $('#btnResetSearchSubjectAssign').click(() => {
        handleResetSearchSubjectAssign();
    });

});

const getListStaff = (
    page = INIT_PAGINATION.page,
    size = INIT_PAGINATION.size,
    departmentFacilityId = examDistributionInfo.departmentFacilityId,
    staffName = null,
    staffCode = null,
) => {
    const params = {
        page,
        size,
        departmentFacilityId,
        staffName,
        staffCode,
    };

    const url = getUrlParameters(ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS, params);

    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: (responseBody) => {
            if (responseBody?.data?.data?.length === 0) {
                $('#manageHOSTableBody').html(`
                    <tr>
                         <td colspan="6" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const staffs = responseBody?.data?.data.map((staff, _) => {
                return `
                    <tr>
                        <td>${staff.orderNumber}</td>
                        <td>${staff.staffCode}</td>
                        <td>${staff.staffName}</td>
                        <td>${staff.accountFPT}</td>
                        <td>${staff.accountFE}</td>
                       <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                           <a 
                                data-bs-toggle="tooltip" 
                                data-bs-placement="top" 
                                title="Phân công môn học"
                                onclick="showModalModifyAssignSubject(
                                    '${staff.id}', 
                                    '${staff.staffName}',
                                    '${staff.staffCode}'
                                    )"
                           >
                                <i 
                                    class="fa-solid fa-eye"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                           </a>
                       </td>
                    </tr>
                `;
            });
            $('#manageHOSTableBody').html(staffs);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        }
    })
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
}

const getSubjectAssignForStaff = (
    staffId,
    page = INIT_PAGINATION.page,
    size = INIT_PAGINATION.size,
    subjectCode = null,
    subjectName = null,
    subjectType = null,
) => {

    const params = {
        page,
        size,
        staffId,
        subjectCode,
        subjectName,
        subjectType,
        departmentFacilityId: examDistributionInfo.departmentFacilityId,
    };

    const url = getUrlParameters(ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS + "/subject-assigned", params);

    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: (responseBody) => {
            if (responseBody?.data?.data?.length === 0) {
                $('#subjectAssignedTableBody').html(`
                    <tr>
                         <td colspan="4" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const subjectAssigneds = responseBody?.data?.data.map((subject, _) => {
                return `
                    <tr>
                        <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            <input type="checkbox" class="form-check-input subject-checkbox" data-subject-id="${subject.id}" ${subject.assigned === 1 ? 'checked' : ''}>
                        </td>
                        <td>${subject.subjectCode}</td>
                        <td>${subject.subjectName}</td>
                        <td>${subject.subjectType}</td>
                    </tr>
                `;
            });
            $('#subjectAssignedTableBody').html(subjectAssigneds);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationAssignSubject(totalPages, page);
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        }
    });
};

const createPaginationAssignSubject = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePageAssignSubject(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePageAssignSubject(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePageAssignSubject(${currentPage + 1})">
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

    $('#paginationSubjectAssigned').html(paginationHtml);
}

const handleSearchStaff = () => {
    const staffName = $('#staffName').val();
    const staffCode = $('#staffCode').val();
    getListStaff(INIT_PAGINATION.page, INIT_PAGINATION.size, examDistributionInfo.departmentFacilityId, staffName, staffCode);
}

const handleResetSearchStaff = () => {
    $('#staffName').val('');
    $('#staffCode').val('');
    getListStaff();
}

const handleSearchSubjectAssign = () => {
    const subjectCode = $('#subjectCode').val();
    const subjectName = $('#subjectName').val();
    const subjectType = $('#subjectType').val();
    getSubjectAssignForStaff(currentStaffId, INIT_PAGINATION.page, INIT_PAGINATION.size, subjectCode, subjectName, subjectType);
}

const handleResetSearchSubjectAssign = () => {
    $('#subjectCode').val('');
    $('#subjectName').val('');
    $('#subjectType').val('');
    getSubjectAssignForStaff(currentStaffId);

}

const changePage = (page) => {
    getListStaff(page);
}

const changePageAssignSubject = (page) => {
    getSubjectAssignForStaff(currentStaffId, page);
}

const showModalModifyAssignSubject = (staffId, staffName, staffCode) => {
    currentStaffId = staffId;
    $('#modifyAssignSubjectForStaffLabel').text(`Phân công môn học cho giảng viên: ${staffCode} - ${staffName}`);
    getSubjectAssignForStaff(staffId);
    $('#modifyAssignSubjectForStaff').modal('show');
}

const saveAssignSubjectForStaff = () => {
    const assignedSubjectIds = [];
    const unassignedSubjectIds = [];
    $('.subject-checkbox').each((_, subject) => {
        const subjectId = $(subject).data('subject-id');
        if ($(subject).is(':checked')) {
            assignedSubjectIds.push(subjectId);
        } else {
            unassignedSubjectIds.push(subjectId);
        }
    });

    const data = {
        staffId: currentStaffId,
        assignedSubjectIds,
        unassignedSubjectIds,
    };

    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS + "/assign-subject",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: (responseBody) => {
            showToastSuccess(responseBody?.message || "Cập nhật phân công môn học thành công");
            getSubjectAssignForStaff(currentStaffId);
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        }
    });
};