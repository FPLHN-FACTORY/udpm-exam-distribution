$(document).ready(() => {
    getAllSemesterAndSetDefaultCurrentSemester();

    const bindClickEvent = (selector, callback) => {
        $(selector).click(callback);
    };

    bindClickEvent('#btnCreateSubjectGroup', showCreateSubjectGroupModal);

    bindClickEvent('#btnCreateOrUpdateSubjectGroup', checkCurrentIsCreateOrUpdate);

    bindClickEvent('#btnCloseCreateSubjectGroupModal', closeCreateSubjectGroupModal);

    $('#querySearchSubjectGroup').on('input', handleListenQuerySearch);

    $('#filterSubject').on('input', handleFilterSubject);

    $('#querySearchSemester').change(() => {
        subjectGroupParams.semesterId = $('#querySearchSemester').val();
        getSubjectGroups();
    });

});

const userInfo = getExamDistributionInfo();

const subjectGroupParams = {
    page: INIT_PAGINATION.page,
    size: INIT_PAGINATION.size,
    departmentFacilityId: userInfo.departmentFacilityId,
    semesterId: null,
    attachRoleName: null,
};

const subjectParams = {
    page: INIT_PAGINATION.page,
    size: INIT_PAGINATION.size,
    q: null,
}

let currentSubjectGroupId = null;

const updateTableBody = (selector, data, rowTemplate) => {
    const rows = data.map(rowTemplate).join('');
    $(selector).html(rows);
};

const createPagination = (selector, totalPages, currentPage, changePageCallback) => {
    let paginationHtml = '';

    paginationHtml += currentPage > 1 ? `
        <li class="page-item">
            <a class="page-link" href="#" onclick="${changePageCallback}(${currentPage - 1})">Trước</a>
        </li>` : `
        <li class="page-item disabled">
            <a class="page-link" href="#">Trước</a>
        </li>`;

    for (let i = 1; i <= totalPages; i++) {
        paginationHtml += i === currentPage ?
            `<li class="page-item active"><a class="page-link text-white" href="#">${i}</a></li>` :
            `<li class="page-item"><a class="page-link" href="#" onclick="${changePageCallback}(${i})">${i}</a></li>`;
    }

    paginationHtml += currentPage < totalPages ? `
        <li class="page-item">
            <a class="page-link" href="#" onclick="${changePageCallback}(${currentPage + 1})">Sau</a>
        </li>` : `
        <li class="page-item disabled">
            <a class="page-link" href="#">Sau</a>
        </li>`;

    $(selector).html(paginationHtml);
};

const handleListenQuerySearch = debounce(() => {
    subjectGroupParams.attachRoleName = $('#querySearchSubjectGroup').val();
    getSubjectGroups();
}, 500);

const getSubjectGroups = () => {
    const url = getUrlParameters(ApiConstant.API_HEAD_DEPARTMENT_MANAGE_SUBJECT_GROUP, subjectGroupParams);
    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: (responseBody) => {
            const subjectGroups = responseBody?.data?.data || [];
            if (subjectGroups.length === 0) {
                $('#manageSubjectGroupTableBody').html(`
                    <tr>
                        <td colspan="4" class="text-center">Không có dữ liệu</td>
                    </tr>`);
                $('#paginationSubjectGroup').html('');
                return;
            }
            updateTableBody('#manageSubjectGroupTableBody', subjectGroups, (subjectGroup) => `
                <tr>
                    <td>${subjectGroup.orderNumber}</td>
                    <td>${subjectGroup.attachRoleName}</td>
                    <td>
                        <span class="tag tag-warning">
                            ${subjectGroup.semesterInfo}
                        </span>
                    </td>
                    <td class="text-center">
                        <i 
                            class="fa fa-edit" 
                            style="width: 1px; text-wrap: nowrap; padding: 0 10px; cursor: pointer;"
                            onclick="showUpdateSubjectGroupModal('${subjectGroup.id}', '${subjectGroup.attachRoleName}')"
                            data-bs-toggle="tooltip" 
                            data-bs-title="Chỉnh sửa nhóm môn học"
                           ></i>
                    </td>
                </tr>`);
            createPagination('#paginationSubjectGroup', responseBody?.data?.totalPages || 1, subjectGroupParams.page, 'changePageSubjectGroup');
            callToolTip();
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        }
    });
};

const changePageSubjectGroup = (page) => {
    subjectGroupParams.page = page;
    getSubjectGroups();
};

const getAllSemesterAndSetDefaultCurrentSemester = () => {
    $.ajax({
        type: "GET",
        url: `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS}/semester`,
        contentType: "application/json",
        success: (responseBody) => {
            const semesters = responseBody?.data || [];
            updateTableBody('#querySearchSemester', semesters, (semester) => `
                <option value="${semester.id}">${semester.semesterInfo}</option>`);
            $('#querySearchSemester').val(semesters[0]?.id);
            subjectGroupParams.semesterId = semesters[0]?.id;
            getSubjectGroups();
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        }
    });
};

const showCreateSubjectGroupModal = () => {
    currentSubjectGroupId = null;
    $('#btnCreateOrUpdateSubjectGroup').text("Tạo mới");
    $('#subjectTableContainer').hide();
    $('#subjectGroupName').val("");
    $('#subjectTableBody').html("");
    $('#createSubjectGroupModal').modal('show');
};

const closeCreateSubjectGroupModal = () => {
    currentSubjectGroupId = null;
    $('#btnCreateOrUpdateSubjectGroup').text("Tạo mới");
    $('#subjectTableContainer').hide();
    $('#subjectGroupName').val("");
    $('#subjectTableBody').html("");
    $('#createSubjectGroupModal').modal('hide');
};

const showUpdateSubjectGroupModal = (subjectGroupId, attachRoleName) => {
    $('#createSubjectGroupModal').modal('show');
    $('#subjectGroupName').val(attachRoleName);
    currentSubjectGroupId = subjectGroupId;
    $('#subjectTableContainer').show();
    getListSubject(subjectGroupId);
    $('#btnCreateOrUpdateSubjectGroup').text("Cập nhật");
};

const checkCurrentIsCreateOrUpdate = () => {
    if ($('#btnCreateOrUpdateSubjectGroup').text() === "Cập nhật") {
        updateSubjectGroup();
    } else {
        createSubjectGroup();
    }
};

const getListSubject = (subjectGroupId, subjectParams) => {
    const url = getUrlParameters(
        `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_SUBJECT_GROUP}/subject`,
        {
            page: subjectParams?.page || INIT_PAGINATION.page,
            size: subjectParams?.size || INIT_PAGINATION.size,
            departmentFacilityId: userInfo.departmentFacilityId,
            subjectGroupId: subjectGroupId || currentSubjectGroupId || null,
            semesterId: subjectGroupParams.semesterId,
            q: subjectParams?.q?.trim() || null,
        }
    );
    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: (responseBody) => {
            const subjects = responseBody?.data?.data || [];
            updateTableBody('#subjectTableBody', subjects, (subject) => `
                <tr>
                    <td>${subject.orderNumber}</td>
                    <td>${subject.subjectCode}</td>
                    <td>${subject.subjectName}</td>
                    <td>
                        <span class="tag tag-warning">
                            ${subject.subjectType}
                        </span>
                    </td>
                    <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;" class="text-center">
                        <div class="col-auto">
                            <label class="colorinput">
                                <input name="subjectId" data-id="${subject.id}" type="checkbox" class="colorinput-input" 
                                    ${subject.isSubjectInGroup === 1 ? 'checked' : ''} />
                                <span class="colorinput-color bg-dark"></span>
                            </label>
                        </div>
                    </td>
                </tr>`);
            createPagination(
                '#paginationSubject',
                responseBody?.data?.totalPages || 1,
                subjectParams?.page || INIT_PAGINATION.page,
                'changePageSubject'
            );
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        }
    });
};

const changePageSubject = (page) => {
    subjectParams.page = page;
    getListSubject(
        currentSubjectGroupId,
        subjectParams
    );
};

const createSubjectGroup = () => {
    const attachRoleName = $('#subjectGroupName').val();
    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_DEPARTMENT_MANAGE_SUBJECT_GROUP,
        contentType: "application/json",
        data: JSON.stringify({attachRoleName}),
        success: (responseBody) => {
            showToastSuccess(responseBody.message);
            currentSubjectGroupId = responseBody.data.subjectGroupId;
            $('#subjectTableContainer').show();
            getListSubject(responseBody.data.subjectGroupId, {page: INIT_PAGINATION.page, size: INIT_PAGINATION.size});
            getSubjectGroups();
            $('#btnCreateOrUpdateSubjectGroup').text("Cập nhật");
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        }
    });
};

const updateSubjectGroup = () => {
    const attachSubjectIds = $('input[name="subjectId"]:checked').map(function () {
        return $(this).data('id');
    }).get();
    const deleteSubjectIds = $('input[name="subjectId"]:not(:checked)').map(function () {
        return $(this).data('id');
    }).get();

    $.ajax({
        type: "PUT",
        url: `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_SUBJECT_GROUP}/${currentSubjectGroupId}`,
        contentType: "application/json",
        data: JSON.stringify({
            attachSubjectIds,
            detachSubjectIds: deleteSubjectIds,
            attachRoleName: $('#subjectGroupName').val(),
        }),
        success: (responseBody) => {
            showToastSuccess(responseBody.message);
            getSubjectGroups();
        },
        error: (error) => {
            showToastError(error?.responseJSON?.message || "Có lỗi xảy ra, vui lòng thử lại sau");
        }
    });
};

const handleFilterSubject = debounce(() => {
    const query = $('#filterSubject').val();
    subjectParams.page = INIT_PAGINATION.page;
    subjectParams.q = query;
    getListSubject(currentSubjectGroupId, subjectParams);
});
