//GLOBAL VARIABLES

const headOfSubjectsTableBody = $('#manageHeadSubjectsTableBody');

const subjectViewTableBody = $('#showSubjectByHeadSubjectTableBody');

const semesterSelect = $('#searchSemester');

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


//DOM READY
$(document).ready(() => {

    getAllSemesterAndSelectCurrentSemester();

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
                        <td colspan="7" class="text-center">Không có dữ liệu</td>
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
                        <td>${headSubject.isAssigned ? '<span class="tag tag-success">Đã phân công</span>' : '<span class="tag tag-danger">Chưa phân công</span>'}</td>
                        <td>
                            <i class="fas fa-edit" style="cursor: pointer;" data-bs-toggle="tooltip" 
                                    data-bs-title="Phân công môn học" ></i>
                            <i class="fa-regular fa-eye ms-2" style="cursor: pointer;" data-bs-toggle="tooltip"
                                    data-bs-title="Các môn đang quản lý" onclick="getViewSubjectsByHeadSubject('${headSubject.id}')"></i>
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

//SUBJECT JUST FOR HEAD SUBJECT VIEW

const getViewSubjectsByHeadSubject = (headSubjectId) => {

    const url = getUrlParameters(`${ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS}/${headSubjectId}/subjects`, subjectViewParams);

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
                        <td>${subject.subjectType}</td>
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

//GET ALL SUBJECTS WITH ASSIGNED HEAD SUBJECT

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