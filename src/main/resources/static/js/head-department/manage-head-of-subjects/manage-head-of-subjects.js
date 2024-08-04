//GLOBAL VARIABLES

const headOfSubjectsTableBody = $('#manageHeadSubjectsTableBody');

const subjectViewTableBody = $('#showSubjectByHeadSubjectTableBody');

const semesterSelect = $('#searchSemester');

let currentSemesterId = null;

const headOfSubjectsParams = {
    page: INIT_PAGINATION.page,
    size: INIT_PAGINATION.size,
    currentSemesterId: null,
    q: null
};

const subjectViewParams = {
    page: INIT_PAGINATION.page,
    size: INIT_PAGINATION.size,
}

const subjectAssignParams = {
    page: INIT_PAGINATION.page,
    size: INIT_PAGINATION.size,
}

const currentHeadSubjectInfo = {
    id: null,
    staffCode: null,
    staffName: null
}


//DOM READY
$(document).ready(() => {

    viewSelectForNewHeadSubject();

    getAllSemesterAndSelectCurrentSemester();

    checkCanSyncData();

    addListenerToFiredEvent(
        ['#searchHeadSubjects', '#searchSemester'],
        'input',
        handleOnChangeFilterHeadSubjects
    );

    $('#btnAssignNewHeadSubject').on('click', handleAssignNewHeadSubject);

    $('#searchSubjectToAssign').on('input', handleFilterSubjectsToAssign);

    $('#searchSemester').on('change', handleChangeSemester);

    $('#pageSizeHeadSubjects').on('change', function () {
        changeSizeHeadSubjects($(this).val());
    });

    $('#pageSizeShowSubjectToAssign').on('change', function () {
        changeSizeSubjectsToAssign($(this).val());
    });

    $('#pageSizeShowSubjectByHeadSubject').on('change', function () {
        changeSizeViewSubjectsByHeadSubject($(this).val());
    });

    $('#syncHeadSubjects').on('click', handleSyncData);

});

//HEAD SUBJECT

const getHeadSubjects = () => {

    const url = getUrlParameters(ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS, headOfSubjectsParams);

    $.ajax({
        url: url,
        type: 'GET',
        contentType: "application/json",
        success: (res) => {
            const headSubjects = res?.data?.data || [];
            if (headSubjects.length === 0) {
                headOfSubjectsTableBody.empty();
                headOfSubjectsTableBody.append(`
                    <tr>
                        <td colspan="8" class="text-center">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const totalPages = res?.data?.totalPages || 1;
            headOfSubjectsTableBody.empty();
            headSubjects.forEach(headSubject => {
                headOfSubjectsTableBody.append(`
                    <tr>
                      <td>${headSubject.orderNumber}</td>
                      <td>${headSubject.staffCode}</td>
                      <td>${headSubject.staffName}</td>
                      <td>${headSubject.emailFPT}</td>
                      <td>${headSubject.emailFE}</td>
                      <td>
                        ${headSubject.isAssigned ? '<span class="tag tag-success">Đã phân công</span>' : '<span class="tag tag-danger">Chưa phân công</span>'}
                      </td>
                      <td class="text-center">${headSubject.assignedCount}</td>
                      <td>
                        ${currentSemesterId === headOfSubjectsParams.currentSemesterId ? `<i
                          class="fas fa-edit"
                          style="cursor: pointer;"
                          data-bs-toggle="tooltip"
                          data-bs-title="Phân công môn học"
                          onclick="getSubjectsToAssign('${headSubject.id}')"
                        ></i>
                        <i
                          class="fa-regular fa-eye ms-2"
                          style="cursor: pointer;"
                          data-bs-toggle="tooltip"
                          data-bs-title="Các môn đang quản lý / Chuyên phân công nhanh"
                          onclick="getViewSubjectsByHeadSubject('${headSubject.id}', '${headSubject.staffName}', '${headSubject.staffCode}')"
                        ></i>` : `
                            <span class="tag tag-warning">Không thể phân công</span>
                        `}
                      </td>
                    </tr>
                `);
            });
            initPagination(
                '#paginationHeadSubjects',
                totalPages,
                headOfSubjectsParams.page,
                'changePageHeadSubjects'
            );
            callToolTip();
        }
    });
};

const changePageHeadSubjects = (page) => {
    headOfSubjectsParams.page = page;
    getHeadSubjects();
}

const changeSizeHeadSubjects = (size) => {
    headOfSubjectsParams.size = size;
    getHeadSubjects();
}

const handleOnChangeFilterHeadSubjects = debounce(() => {
    headOfSubjectsParams.page = 1;
    headOfSubjectsParams.q = $('#searchHeadSubjects').val();
    headOfSubjectsParams.currentSemesterId = semesterSelect.val();
    getHeadSubjects();
}, 500);

const handleChangeSemester = () => {
    headOfSubjectsParams.currentSemesterId = semesterSelect.val();
    getHeadSubjects();
}

//SUBJECT JUST FOR HEAD SUBJECT VIEW

const getViewSubjectsByHeadSubject = (headSubjectId, staffName, staffCode) => {

    currentHeadSubjectInfo.id = headSubjectId;
    currentHeadSubjectInfo.staffName = staffName;
    currentHeadSubjectInfo.staffCode = staffCode;

    $('#modalShowSubjectByHeadSubjectLabel')
        .text(`Danh sách môn học đang quản lý của giảng viên ${staffCode} - ${staffName}`);

    const url = getUrlParameters(
        `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS}/${headSubjectId}/subjects`,
        subjectViewParams
    );

    $.ajax({
        url: url,
        type: 'GET',
        contentType: "application/json",
        success: (data) => {
            const subjects = data?.data?.data || [];
            if (subjects.length === 0) {
                showToastError('Nhân viên chưa được phân công môn học nào !');
                $('#modalShowSubjectByHeadSubject').modal('hide');
                return;
            }
            const totalPages = data?.data?.totalPages || 1;
            $('#modalShowSubjectByHeadSubject').modal('show');
            const viewSubjectsTableBody = $('#showSubjectByHeadSubjectTableBody');
            viewSubjectsTableBody.empty();
            subjects.forEach(subject => {
                viewSubjectsTableBody.append(`
                    <tr>
                        <td>${subject.orderNumber}</td>
                        <td>${subject.subjectCode}</td>
                        <td>${subject.subjectName}</td>
                        <td><span class="tag tag-warning">${subject.subjectType}</span></td>
                    </tr>
                `);
            });
            initPagination(
                '#paginationShowSubjectByHeadSubject',
                totalPages,
                subjectViewParams.page,
                'changePageViewSubjectsByHeadSubject'
            );
        }
    });
};

const changePageViewSubjectsByHeadSubject = (page) => {
    subjectViewParams.page = page;
    getViewSubjectsByHeadSubject();
}

const viewSelectForNewHeadSubject = () => {
    $("#selectNewHeadSubject").select2({
        width: '100%',
        dropdownParent: $('#modalShowSubjectByHeadSubject'),
        closeOnSelect: true,
        placeholder: 'Chọn trưởng môn để chuyển phân công',
        allowClear: true,
        ajax: {
            url: `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS}/staff/search`,
            type: "GET",
            dataType: 'json',
            contentType: "application/json",
            delay: 250,
            data: function (query) {
                return {
                    q: query.term
                };
            },
            processResults: function (data) {
                console.log(data);
                let results = [];
                data.data.forEach(e => {
                    results.push({id: e.id, text: e.staffInfo});
                });

                return {
                    results: results
                };
            }
        },
        templateResult: formatResult,
        templateSelection: formatSelection
    });

    function formatResult(item) {
        if (item.loading) {
            return item.text;
        }
        return $('<span>').text(item.text);
    }

    function formatSelection(item) {
        return item.text;
    }
};

const handleAssignNewHeadSubject = () => {
    showAlert(
        'Xác nhận phân công trưởng môn mới',
        'Bạn có chắc chắn muốn phân công trưởng môn mới ?',
        'info',
        'Xác nhận',
        () => {
            const newHeadSubjectId = $('#selectNewHeadSubject').val();
            if (!newHeadSubjectId) {
                showToastError('Vui lòng chọn trưởng môn mới !');
                return;
            }
            $.ajax({
                url: `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS}/subjects/reassign`,
                type: 'PUT',
                contentType: "application/json",
                data: JSON.stringify({
                    currentHeadSubjectId: currentHeadSubjectInfo.id,
                    newHeadSubjectId: newHeadSubjectId
                }),
                success: (data) => {
                    showToastSuccess('Phân công trưởng môn mới thành công !');
                    $('#modalShowSubjectByHeadSubject').modal('hide');
                    getHeadSubjects();
                    $('#selectNewHeadSubject').val(null).trigger('change');
                },
                error: (res) => {
                    showToastError(res?.responseJSON?.message || 'Có lỗi xảy ra, vui lòng thử lại sau !');
                }
            });
        },
        () => {
        }
    )
};

const changeSizeViewSubjectsByHeadSubject = (size) => {
    subjectViewParams.size = size;
    getViewSubjectsByHeadSubject(
        currentHeadSubjectInfo.id,
        currentHeadSubjectInfo.staffName,
        currentHeadSubjectInfo.staffCode
    );
};

//GET ALL SUBJECTS WITH ASSIGNED HEAD SUBJECT

const getSubjectsToAssign = (headSubjectId) => {
    currentHeadSubjectInfo.id = headSubjectId;

    const url = getUrlParameters(
        `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS}/${headSubjectId}/subjects/assign`,
        subjectAssignParams
    );

    $.ajax({
        url: url,
        type: 'GET',
        contentType: "application/json",
        success: (data) => {
            const subjects = data?.data?.data || [];
            if (subjects.length === 0) {
                const assignSubjectModalBody = $('#showSubjectToAssignTableBody');
                assignSubjectModalBody.empty();
                assignSubjectModalBody.append(`
                    <tr>
                        <td colspan="5" class="text-center">Không có dữ liệu</td>
                    </tr>
                `);
                $('#modalShowSubjectToAssign').modal('show');
                return;
            }
            const totalPages = data?.data?.totalPages || 1;
            const assignSubjectModalBody = $('#showSubjectToAssignTableBody');
            assignSubjectModalBody.empty();
            subjects.forEach(subject => {
                assignSubjectModalBody.append(`
                    <tr>
                        <td>${subject.orderNumber}</td>
                        <td>${subject.subjectCode}</td>
                        <td>${subject.subjectName}</td>
                        <td><span class="tag tag-warning">${subject.subjectType}</span></td>
                         <td class="text-center">
                                <div class="col-auto">
                                  <label class="colorinput">
                                    <input
                                        name="color"
                                        type="checkbox"
                                        class="colorinput-input"
                                        ${subject.isAssigned == 1 ? "checked" : ""}
                                        id="input-assign-${subject.id}"
                                        onclick="handleAssignSubject('${subject.id}')"
                                    />
                                    <span
                                      class="colorinput-color bg-black"
                                    ></span>
                                  </label>
                                </div>
                            </td>
                    </tr>
                `);
            });
            initPagination(
                '#paginationShowSubjectToAssign',
                totalPages,
                subjectAssignParams.page,
                'changePageSubjectsToAssign'
            );
            $('#modalShowSubjectToAssign').modal('show');
        }
    });
};

const handleAssignSubject = (subjectId) => {
    const checkbox = $(`#input-assign-${subjectId}`);
    const wasChecked = checkbox.is(':checked');

    const url = `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS}/${currentHeadSubjectInfo.id}/subjects/assign`;

    if (wasChecked) {
        $.ajax({
            url: url,
            type: 'PUT',
            data: JSON.stringify({
                subjectId: subjectId
            }),
            contentType: "application/json",
            success: (data) => {
                showToastSuccess('Phân công môn học thành công !');
                getSubjectsToAssign(currentHeadSubjectInfo.id);
                getHeadSubjects();
            },
            error: (res) => {
                showToastError(res?.responseJSON?.message || 'Có lỗi xảy ra, vui lòng thử lại sau !');
                checkbox.prop('checked', !wasChecked);
            }
        });
    } else {
        $.ajax({
            url: url,
            type: 'DELETE',
            data: JSON.stringify({
                subjectId: subjectId
            }),
            contentType: "application/json",
            success: (data) => {
                showToastSuccess('Hủy phân công môn học thành công !');
                getSubjectsToAssign(currentHeadSubjectInfo.id);
                getHeadSubjects();
            },
            error: (res) => {
                showToastError(res?.responseJSON?.message || 'Có lỗi xảy ra, vui lòng thử lại sau !');
                checkbox.prop('checked', !wasChecked);
            }
        });
    }
};

const changePageSubjectsToAssign = (page) => {
    subjectAssignParams.page = page;
    getSubjectsToAssign(currentHeadSubjectInfo.id);
}

const handleFilterSubjectsToAssign = debounce(() => {
    subjectAssignParams.q = $('#searchSubjectToAssign').val();
    getSubjectsToAssign(currentHeadSubjectInfo.id);
});

const changeSizeSubjectsToAssign = (size) => {
    subjectAssignParams.size = size;
    getSubjectsToAssign(currentHeadSubjectInfo.id);
}

// SYNC DATA HEAD SUBJECT FROM PREVIOUS SEMESTER TO CURRENT SEMESTER

const checkCanSyncData = () => {
    $.ajax({
        url: `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS}/subjects/can-sync`,
        type: 'GET',
        contentType: "application/json",
        success: (data) => {
            const canSync = data?.data;
            if (!canSync) {
                $('#syncHeadSubjects').addClass('d-none');
            } else {
                $('#syncHeadSubjects').removeClass('d-none');
            }
        }
    });
};

const handleSyncData = () => {
    showAlert(
        'Xác nhận đồng bộ dữ liệu',
        'Bạn có chắc chắn muốn đồng bộ dữ liệu từ học kỳ trước ?',
        'info',
        'Xác nhận',
        () => {
            $.ajax({
                url: `${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS}/subjects/sync`,
                type: 'PUT',
                contentType: "application/json",
                success: (data) => {
                    showToastSuccess('Đồng bộ dữ liệu thành công !');
                    getHeadSubjects();
                },
                error: (res) => {
                    showToastError(res?.responseJSON?.message || 'Có lỗi xảy ra, vui lòng thử lại sau !');
                }
            });
        }
    );
};

//COMMON
const getAllSemesterAndSelectCurrentSemester = () => {
    $.ajax({
        url: `${ApiConstant.API_COMMON}/semester`,
        type: 'GET',
        contentType: "application/json",
        success: (data) => {
            const semesters = data?.data;
            semesterSelect.empty();
            semesters.forEach(semester => {
                semesterSelect.append(`<option value="${semester.id}">${semester.semesterInfo}</option>`);
            });
            currentSemesterId = semesters[0].id;
            headOfSubjectsParams.currentSemesterId = semesters[0].id;
            getHeadSubjects();
        }
    });
}

const initPagination = (selector, totalPages, currentPage, changePageCallback) => {
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

const addListenerToFiredEvent = (listSelector, event, callback) => {
    for (let i = 0; i < listSelector.length; i++) {
        $(listSelector[i]).on(event, callback);
    }
};



