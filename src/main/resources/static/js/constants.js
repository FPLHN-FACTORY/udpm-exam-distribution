const ApiConstant = {
    HEAD_SUBJECT: "/head-subject",
    HEAD_OFFICE: "/head-office",
    HEAD_DEPARTMENT: "/head-department",
    TEACHER: "/teacher",
    STUDENT: "/student",

    // Constant representing the API version prefix
    API_VERSION_PREFIX: "/api/v1",

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

    // Constants representing the redirect paths for various resources under head department
    REDIRECT_HEAD_DEPARTMENT_MANAGE_HOS: "/head-department/manage-head-of-subjects",

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

    // Constants representing the full paths for various resources under head department
    API_HEAD_DEPARTMENT_MANAGE_HOS: "/api/v1/head-department/manage-head-of-subjects",

    // Constants representing the authentication
    AUTHENTICATION: "AUTHENTICATION",
    REDIRECT_AUTHENTICATION_LOGOUT: "/authentication/logout",
    REDIRECT_AUTHENTICATION_AUTHOR_SWITCH: "/authentication/author-switch",

    // Constants representing the redirect paths for various resources under teacher
    REDIRECT_TEACHER_EXAM_SHIFT: "/teacher/exam-shift",

    // Constants representing the full paths for various resources under teacher
    API_TEACHER_EXAM_SHIFT: "/api/v1/teacher/exam-shift",
    API_TEACHER_SUBJECT: "/api/v1/teacher/subjects",
    API_TEACHER_BLOCK: "/api/v1/teacher/blocks",
    API_TEACHER_CAMPUS: "/api/v1/teacher/campuses",
    API_TEACHER_CLASS_SUBJECT: "/api/v1/teacher/class-subjects",

};

const INIT_PAGINATION = {
    page: 1,
    size: 5,
};

const EXAM_DISTRIBUTION_CONSTANT_KEY = "e_d_i";

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
        departmentName: examDistributionInfo?.departmentName || null,
        facilityName: examDistributionInfo?.facilityName || null,
        userEmailFPT: examDistributionInfo?.userEmailFPT || null,
        userEmailFe: examDistributionInfo?.userEmailFe || null,
        userFullName: examDistributionInfo?.userFullName || null,
        userId: examDistributionInfo?.userId || null,
        userPicture: examDistributionInfo?.userPicture || null,
    };
};