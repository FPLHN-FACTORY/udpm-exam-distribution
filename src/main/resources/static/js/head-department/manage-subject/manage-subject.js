const userLoginInfo = getExamDistributionInfo();

const searchFields = ['#subjectCode', '#subjectName', '#staffCodeSearch', '#staffNameSearch'];

const currentPaginationParams = {
    page: INIT_PAGINATION.page,
    size: INIT_PAGINATION.size,
    departmentFacilityId: userLoginInfo.departmentFacilityId,
    semesterId: null,
    subjectCode: null,
    subjectName: null,
    staffCode: null,
    staffName: null,
}

let currentSubjectId = null;

$(document).ready(function () {

    getAllSemesterAndSetDefaultCurrentSemester();

    $('#btnReassignStaffForSubject').on('click', assignStaffForSubject);

    $('#querySearchSemester').change((e) => {
        currentSemesterId = e.target.value;
        getSubjectsAssignWithHeadSubject(
            currentPaginationParams.page,
            currentPaginationParams.size,
            currentPaginationParams.departmentFacilityId,
            currentSemesterId,
            currentPaginationParams?.subjectCode,
            currentPaginationParams?.subjectName,
            currentPaginationParams?.staffCode,
            currentPaginationParams?.staffName
        );
    });


    searchFields.forEach(field => {
        $(field).on('input', handleListenSearchQuery);
    });

});

const getSubjectsAssignWithHeadSubject = (
    page = INIT_PAGINATION.page,
    size = $('#pageSize').val() || INIT_PAGINATION.size,
    departmentFacilityId = null,
    semesterId = null,
    subjectCode = null,
    subjectName = null,
    staffCode = null,
    staffName = null,
) => {

    const url = getUrlParameters(
        ApiConstant.API_HEAD_DEPARTMENT_MANAGE_SUBJECT,
        {
            departmentFacilityId,
            semesterId,
            subjectCode,
            subjectName,
            staffCode,
            staffName
        }
    );

    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: (responseBody) => {
            if (responseBody?.data?.data?.length === 0) {
                $('#manageSubjectTableBody').html(`
                    <tr>
                         <td colspan="6" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                console.log('No data')
                return;
            }

            const subjectsAssignWithHeadSubject = responseBody?.data?.data?.map((swhs, _) => {
                return `
                    <tr>
                        <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            ${swhs.orderNumber}
                        </td>
                        <td>${swhs.subjectCode}</td>
                        <td>${swhs.subjectName}</td>
                        <td>${swhs.subjectType}</td>
                        <td  style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            ${swhs.staffInChargeInfo ? swhs.staffInChargeInfo : '<div class="badge badge-danger">Chưa phân công</div>'}
                        </td>
                        <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;" class="text-center">
                            <i class="fas fa-edit text-center" style="cursor: pointer;"
                                 onclick="openModalAssignSubjectForStaff('${swhs.id}', '${swhs.subjectCode}', '${swhs.subjectName}')"
                            ></i>
                        </td>
                    </tr>
                `;
            });

            $('#manageSubjectTableBody').html(subjectsAssignWithHeadSubject);
            const totalPages = responseBody?.data?.totalPages || 1;
            createPagination(totalPages, page);
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        },
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
}

const openModalAssignSubjectForStaff = (subjectId, subjectCode, subjectName) => {
    currentSubjectId = subjectId;
    $('#assignStaffForSubjectModalLabel').text(`Phân công trưởng môn cho môn học: ${subjectCode} - ${subjectName}`);
    getStaffs();
    $('#assignStaffForSubjectModal').modal('show');
}

const getStaffs = (
    page = INIT_PAGINATION.page,
    size = $('#staffPageSize').val() || INIT_PAGINATION.size,
    subjectId = currentSubjectId,
) => {
    const url = getUrlParameters(
        ApiConstant.API_HEAD_DEPARTMENT_MANAGE_SUBJECT + "/staffs-by-subject",
        {
            page,
            size,
            departmentFacilityId: userLoginInfo.departmentFacilityId,
            subjectId: currentSubjectId,
            currentUserId: userLoginInfo.userId,
            semesterId: currentPaginationParams.semesterId
        }
    );

    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: (responseBody) => {
            const staffs = responseBody?.data?.data.map((staff, _) => {
                return `
                    <tr>
                          <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            <div class="col-auto">
                              <label class="colorinput">
                                <input
                                  name="staffSelection"
                                  data-staff-subject-id="${staff.id}" ${staff.isHeadOfSubject === 1 ? 'checked' : ''}
                                  type="checkbox"
                                  class="colorinput-input"
                                />
                                <span
                                  class="colorinput-color bg-secondary"
                                ></span>
                              </label>
                            </div>
                        </td>
                        <td>${staff.staffCode}</td>
                        <td>${staff.staffName}</td>
                        <td>${staff.accountFPT}</td>
                        <td>${staff.accountFE}</td>
                    </tr>  
                `;
            });
            $('#assignStaffForSubjectTableBody').html(staffs);
            const totalPages = responseBody?.data?.totalPages || 1;
            createPaginationStaffAssignWithSubject(totalPages, page);
            $('input[name="staffSelection"]').on('click', function () {
                $('input[name="staffSelection"]').not(this).prop('checked', false);
            });
        },
    });
};

const createPaginationStaffAssignWithSubject = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePageStaffAssignWithSubject(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePageStaffAssignWithSubject(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePageStaffAssignWithSubject(${currentPage + 1})">
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

    $('#staffPagination').html(paginationHtml);
};

const assignStaffForSubject = () => {
    const staffSubjectId = $('input[name="staffSelection"]:checked').data('staff-subject-id');
    if (!staffSubjectId) {
        showToastError("Vui lòng chọn một giáo viên để phân công");
        return;
    }

    const url = ApiConstant.API_HEAD_DEPARTMENT_MANAGE_SUBJECT + "/reassign-subject";

    const data = {
        staffId: staffSubjectId,
        subjectId: currentSubjectId,
        currentSemesterId: currentPaginationParams.semesterId
    };

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: () => {
            showToastSuccess("Phân công trưởng môn thành công");
            $('#assignStaffForSubjectModal').modal('hide');
            getSubjectsAssignWithHeadSubject(
                currentPaginationParams.page,
                currentPaginationParams.size,
                currentPaginationParams.departmentFacilityId,
                currentPaginationParams.semesterId,
                currentPaginationParams?.subjectCode,
                currentPaginationParams?.subjectName,
                currentPaginationParams?.staffCode,
                currentPaginationParams?.staffName
            );
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        },
    });
};

const changePage = (page) => {
    getSubjectsAssignWithHeadSubject(
        page,
        currentPaginationParams.size,
        currentPaginationParams.semesterId,
        currentPaginationParams?.subjectCode,
        currentPaginationParams?.subjectName,
        currentPaginationParams?.staffCode,
        currentPaginationParams?.staffName
    );
}

const changePageStaffAssignWithSubject = (page) => {
    getStaffs(page);
}

const getAllSemesterAndSetDefaultCurrentSemester = () => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS + "/semester",
        contentType: "application/json",
        success: (responseBody) => {
            const semesters = responseBody?.data?.map((semester, _) => {
                return `<option value="${semester.id}">${semester.semesterInfo}</option>`;
            });
            $('#querySearchSemester').html(semesters);
            $('#querySearchSemester').val(responseBody?.data[0]?.id);
            currentPaginationParams.semesterId = responseBody?.data[0]?.id;
            getSubjectsAssignWithHeadSubject(
                currentPaginationParams.page,
                currentPaginationParams.size,
                currentPaginationParams.departmentFacilityId,
                responseBody?.data[0]?.id,
                currentPaginationParams?.subjectCode,
                currentPaginationParams?.subjectName,
                currentPaginationParams?.staffCode,
                currentPaginationParams?.staffName
            );
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        }
    });
};

const handleListenSearchQuery = debounce(() => {
    const subjectCode = $('#subjectCode').val();
    const subjectName = $('#subjectName').val();
    const staffCode = $('#staffCodeSearch').val();
    const staffName = $('#staffNameSearch').val();
    getSubjectsAssignWithHeadSubject(
        currentPaginationParams.page,
        currentPaginationParams.size,
        currentPaginationParams.departmentFacilityId,
        currentPaginationParams.semesterId,
        subjectCode,
        subjectName,
        staffCode,
        staffName
    );
}, 300);

