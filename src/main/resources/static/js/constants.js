const ApiConstant = {
    HEAD_SUBJECT: "/head-subject",
    HEAD_OFFICE: "/head-office",
    HEAD_DEPARTMENT: "/head-department",
    TEACHER: "/teacher",
    STUDENT: "/student",

    // Constant representing the API version prefix
    API_VERSION_PREFIX: "/api/v1",

    // Constants representing the common API paths
    API_COMMON: "/api/v1/common",

    // Constants representing the full paths for various resources
    API_HEAD_SUBJECT_PREFIX: "/api/v1/head-subject",
    API_HEAD_OFFICE_PREFIX: "/api/v1/head-office",
    API_HEAD_DEPARTMENT_PREFIX: "/api/v1/head-department",
    API_TEACHER_PREFIX: "/api/v1/teacher",
    API_STUDENT_PREFIX: "/api/v1/student",
    API_AUTHENTICATION_PREFIX: "/api/v1/authentication",

    // Constants representing the redirect paths for various resources under head office
    REDIRECT_HEAD_OFFICE_SUBJECT: "/head-office/subjects",
    REDIRECT_HEAD_OFFICE_SEMESTER: "/head-office/semesters",
    REDIRECT_HEAD_OFFICE_MAJOR: "/head-office/majors",
    REDIRECT_HEAD_OFFICE_BLOCK: "/head-office/blocks",
    REDIRECT_HEAD_OFFICE_FACILITY: "/head-office/facilities",
    REDIRECT_HEAD_OFFICE_CAMPUS: "/head-office/campuses",
    REDIRECT_HEAD_OFFICE_DEPARTMENT: "/head-office/departments",
    REDIRECT_HEAD_OFFICE_DEPARTMENT_FACILITY: "/head-office/departments-facility",
    REDIRECT_HEAD_OFFICE_STAFF: "/head-office/staffs",
    REDIRECT_HEAD_OFFICE_STUDENT: "/head-office/students",
    REDIRECT_HEAD_OFFICE_CLASS_SUBJECT: "/head-office/class-subjects",
    REDIRECT_HEAD_OFFICE_ROLE: "/head-office/roles",
    REDIRECT_HEAD_OFFICE_EXAM_RULE: "/head-office/exam-rule",

    // Constants representing the redirect paths for various resources under head department
    REDIRECT_HEAD_DEPARTMENT_MANAGE_HOS: "/head-department/manage-head-of-subjects",
    REDIRECT_HEAD_DEPARTMENT_MANAGE_SUBJECT: "/head-department/manage-subjects",
    REDIRECT_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM: "/head-department/join-room",
    REDIRECT_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT: "/head-department/manage-class-subjects",

    // Constants representing the redirect paths for various resources under head subject
    REDIRECT_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER: "/head-subject/assign-uploader",
    REDIRECT_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL: "/head-subject/exam-approval",
    REDIRECT_HEAD_SUBJECT_MANAGE_SUBJECT_RULE: "/head-subject/subject-rule",
    REDIRECT_HEAD_SUBJECT_MANAGE_JOIN_ROOM: "/head-subject/join-room",
    REDIRECT_HEAD_SUBJECT_CREATE_EXAM_PAPER: "/head-subject/create-exam-paper",
    REDIRECT_HEAD_SUBJECT_UPDATE_EXAM_PAPER: "/head-subject/uep-update-exam-paper",
    REDIRECT_HEAD_SUBJECT_UPDATE_EXAM_PAPER_SUBJECT: "/head-subject/uep-subject",
    REDIRECT_HEAD_SUBJECT_CHOOSE_EXAM_PAPER: "/head-subject/choose-exam-paper",

    // Constants representing the full paths for various resources under head office
    API_HEAD_OFFICE_SUBJECT: "/api/v1/head-office/subjects",
    API_HEAD_OFFICE_SEMESTER: "/api/v1/head-office/semesters",
    API_HEAD_OFFICE_MAJOR: "/api/v1/head-office/majors",
    API_HEAD_OFFICE_BLOCK: "/api/v1/head-office/blocks",
    API_HEAD_OFFICE_FACILITY: "/api/v1/head-office/facilities",
    API_HEAD_OFFICE_CAMPUS: "/api/v1/head-office/campuses",
    API_HEAD_OFFICE_DEPARTMENT: "/api/v1/head-office/departments",
    API_HEAD_OFFICE_DEPARTMENT_FACILITY: "/api/v1/head-office/departments-facility",
    API_HEAD_OFFICE_MAJOR_FACILITY: "/api/v1/head-office/majors-facility",
    API_HEAD_OFFICE_STAFF: "/api/v1/head-office/staffs",
    API_HEAD_OFFICE_STUDENT: "/api/v1/head-office/students",
    API_HEAD_OFFICE_CLASS_SUBJECT: "/api/v1/head-office/class-subjects",
    API_HEAD_OFFICE_ROLE: "/api/v1/head-office/roles",
    API_HEAD_OFFICE_EXAM_RULE: "/api/v1/head-office/exam-rule",

    // Constants representing the full paths for various resources under head department
    API_HEAD_DEPARTMENT_MANAGE_HOS: "/api/v1/head-department/head-of-subjects",
    API_HEAD_DEPARTMENT_MANAGE_SUBJECT_GROUP: "/api/v1/head-department/subject-group",
    API_HEAD_DEPARTMENT_MANAGE_SUBJECT: "/api/v1/head-department/subjects",
    API_HEAD_DEPARTMENT_BLOCK: "/api/v1/head-department/blocks",
    API_HEAD_DEPARTMENT_CAMPUS: "/api/v1/head-department/campuses",
    API_HEAD_DEPARTMENT_CLASS_SUBJECT: "/api/v1/head-department/class-subjects",
    API_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM: "/api/v1/head-department/join-room",
    API_HEAD_DEPARTMENT_STAFF: "/api/v1/head-department/staffs",
    API_HEAD_DEPARTMENT_STUDENT: "/api/v1/head-department/students",
    API_HEAD_DEPARTMENT_FILE: "/api/v1/head-department/file",
    API_HEAD_DEPARTMENT_EXAM_SHIFT: "/api/v1/head-department/exam-shift",
    API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT: "/api/v1/head-department/manage-class-subjects",

    // Constants representing the full paths for various resources under head subject
    API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER: "/api/v1/head-subject/assign-uploader",
    API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER: "/api/v1/head-subject/upload-exam-paper",
    API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL: "/api/v1/head-subject/exam-approval",
    API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE: "/api/v1/head-subject/subject-rule",
    API_HEAD_SUBJECT_MANAGE_JOIN_ROOM: "/api/v1/head-subject/join-room",
    API_HEAD_SUBJECT_STAFF: "/api/v1/head-subject/staffs",
    API_HEAD_SUBJECT_STUDENT: "/api/v1/head-subject/students",
    API_HEAD_SUBJECT_SUBJECT: "/api/v1/head-subject/subjects",
    API_HEAD_SUBJECT_BLOCK: "/api/v1/head-subject/blocks",
    API_HEAD_SUBJECT_CAMPUS: "/api/v1/head-subject/campuses",
    API_HEAD_SUBJECT_CLASS_SUBJECT: "/api/v1/head-subject/class-subjects",
    API_HEAD_SUBJECT_CREATE_EXAM_PAPER: "/api/v1/head-subject/create-exam-paper",
    API_HEAD_SUBJECT_UPDATE_EXAM_PAPER: "/api/v1/head-subject/update-exam-paper",
    API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER: "/api/v1/head-subject/choose-exam-paper",

    // Constants representing the authentication
    AUTHENTICATION: "AUTHENTICATION",
    REDIRECT_AUTHENTICATION_LOGOUT: "/authentication/logout",
    REDIRECT_AUTHENTICATION_AUTHOR_SWITCH: "/authentication/author-switch",

    // Constants representing the redirect paths for various resources under teacher
    REDIRECT_TEACHER_EXAM_SHIFT: "/teacher/exam-shift",

    // Constants representing the full paths for various resources under teacher
    API_TEACHER_EXAM_FILE: "/api/v1/teacher/exam-file",
    API_TEACHER_MOCK_EXAM_PAPER: "/api/v1/teacher/mock-exam-paper",
    API_TEACHER_EXAM_SHIFT: "/api/v1/teacher/exam-shift",
    API_TEACHER_SUBJECT: "/api/v1/teacher/subjects",
    API_TEACHER_BLOCK: "/api/v1/teacher/blocks",
    API_TEACHER_CAMPUS: "/api/v1/teacher/campuses",
    API_TEACHER_CLASS_SUBJECT: "/api/v1/teacher/class-subjects",
    API_TEACHER_STAFF: "/api/v1/teacher/staffs",
    API_TEACHER_PRACTICE_ROOM: "/api/v1/teacher/practice-room",
    API_TEACHER_EXAM_PAPER_SHIFT: "/api/v1/teacher/exam-paper-shift",
    API_TEACHER_TRACK_HISTORY: "/api/v1/teacher/track-history",

    // Constants representing the redirect paths for various resources under student
    REDIRECT_STUDENT_EXAM_SHIFT: "/student/exam-shift",

    // Constants representing the full paths for various resources under student
    API_STUDENT_EXAM_SHIFT: "/api/v1/student/exam-shift",
    API_STUDENT_PRACTICE_ROOM: "/api/v1/student/practice-room",
    API_STUDENT_JOIN_EXAM_SHIFT: "/api/v1/student/exam-shift/join",
    API_STUDENT_TRACKER: "/api/v1/student/tracker",

};

const TopicConstant = {
    TOPIC_EXAM_SHIFT: "/topic/exam-shift",
    TOPIC_STUDENT_EXAM_SHIFT: "/topic/student-exam-shift",
    TOPIC_STUDENT_EXAM_SHIFT_KICK: "/topic/student-exam-shift-kick",
    TOPIC_STUDENT_EXAM_SHIFT_REJOIN: "/topic/student-exam-shift-rejoin",
    TOPIC_STUDENT_EXAM_SHIFT_APPROVE: "/topic/student-exam-shift-approve",
    TOPIC_STUDENT_EXAM_SHIFT_REFUSE: "/topic/student-exam-shift-refuse",
    TOPIC_EXAM_SHIFT_START: "/topic/exam-shift-start",
    TOPIC_TRACK_STUDENT: "/topic/track-student",
    TOPIC_STUDENT_REMOVE_TAB: "/topic/student-remove-tab",
    TOPIC_HEAD_SUBJECT_JOIN_EXAM_SHIFT: "/topic/head-subject-exam-shift-join",
    TOPIC_HEAD_DEPARTMENT_JOIN_EXAM_SHIFT: "/topic/head-department-exam-shift-join",
}

const INIT_PAGINATION = {
    page: 1,
    size: 5,
};

const EXAM_DISTRIBUTION_EXTENSION_ID = "mfjgckbmeakcekilcamhpjglhkiaanol";

const EXAM_DISTRIBUTION_EDIT_FILE_EXAM_PAPER_ID = "e_e_p_i";

const EXAM_DISTRIBUTION_IS_ENABLE_EXT = "i_e_e";

const EXAM_DISTRIBUTION_CONSTANT_KEY = "e_d_i";

const handleSendMessageStartToExt = () => {
    chrome.runtime.sendMessage(
        EXAM_DISTRIBUTION_EXTENSION_ID,
        {active: "onTracking"},
        function (response) {
            console.log(response);
        }
    );
};

const handleSendMessageEndTimeToExt = () => {
    chrome.runtime.sendMessage(
        EXAM_DISTRIBUTION_EXTENSION_ID,
        {active: "stopTracking"},
        function (response) {
            console.log(response);
        }
    );
};

const getExamDistributionInfo = () => {
    const cookieValue = Cookies.get(EXAM_DISTRIBUTION_CONSTANT_KEY);

    if (!cookieValue) return {};

    let examDistributionInfoDecoded;
    try {
        examDistributionInfoDecoded = decodeURIComponent(escape(window.atob(cookieValue)));
    } catch (error) {
        return {};
    }

    let examDistributionInfo;
    try {
        examDistributionInfo = JSON.parse(examDistributionInfoDecoded);
    } catch (error) {
        return {};
    }

    return {
        userRole: examDistributionInfo?.userRole || null,
        departmentFacilityId: examDistributionInfo?.departmentFacilityId || null,
        majorFacilityId: examDistributionInfo?.majorFacilityId || null,
        departmentId: examDistributionInfo?.departmentId || null,
        departmentName: examDistributionInfo?.departmentName || null,
        facilityId: examDistributionInfo?.facilityId || null,
        facilityName: examDistributionInfo?.facilityName || null,
        userEmailFPT: examDistributionInfo?.userEmailFPT || null,
        userEmail: examDistributionInfo?.userEmail || null,
        userEmailFe: examDistributionInfo?.userEmailFe || null,
        userFullName: examDistributionInfo?.userFullName || null,
        userId: examDistributionInfo?.userId || null,
        userPicture: examDistributionInfo?.userPicture || null,
        isAssignUploader: examDistributionInfo?.isAssignUploader || null,
    };
};

const renderPage = (pdfDoc, pageNum, pageNumSelector, pageRendering, pageNumPending, scale, canvas, ctx) => {
    pageRendering = true;
    pdfDoc.getPage(pageNum).then(function (page) {
        const viewport = page.getViewport({scale: scale});
        canvas.height = viewport.height;
        canvas.width = viewport.width;

        const renderContext = {
            canvasContext: ctx,
            viewport: viewport
        };
        const renderTask = page.render(renderContext);

        renderTask.promise.then(function () {
            pageRendering = false;
            if (pageNumPending !== null) {
                renderPage(pdfDoc, pageNum, pageNumSelector, pageRendering, pageNumPending, scale, canvas, ctx);
                pageNumPending = null;
            }
        });
    });

    $(pageNumSelector).text(pageNum);
}

const queueRenderPage = (change, pdfDoc, pageNum, pageNumSelector, pageRendering, pageNumPending, scale, canvas, ctx) => {
    if (pageRendering) {
        pageNumPending = pageNum;
    }
    renderPage(pdfDoc, pageNum, pageNumSelector, pageRendering, pageNumPending, scale, canvas, ctx);
}

const showViewAndPagingPdf = (totalPage, viewerSelector, pagingSelector) => {
    $(viewerSelector).prop("hidden", false);
    if (totalPage > 1) {
        $(pagingSelector).prop("hidden", false);
    }
}

const getExamDate = () => {
    let today = new Date();
    let dd = String(today.getDate()).padStart(2, '0');
    let mm = String(today.getMonth() + 1).padStart(2, '0');
    let yyyy = today.getFullYear();
    today = mm + '/' + dd + '/' + yyyy;
    $('#modifyExamDate').val(today);
};

const fetchShifts = () => {
    let shifts = $('#shiftsData').text().trim().slice(1, -1).split(',').map(shift => shift.trim());
    let currentShift = $('#currentShift').text().trim();

    const shiftOptions = shifts.map((shift, index) => {
        return `<option value="${shift}" ${shift === currentShift ? 'selected' : ''}>${shift}</option>`;
    });

    $('#modifyShift').html(shiftOptions);
};

const checkExtensionInstalled = async () => {
    return true;
    // try {
    //     await fetch(`chrome-extension://${EXAM_DISTRIBUTION_EXTENSION_ID}/icon.png`);
    //     return true;
    // } catch (e) {
    //     if (e instanceof TypeError && e.message === 'Failed to fetch') {
    //         return false;
    //     } else {
    //         throw e;
    //     }
    // }
}