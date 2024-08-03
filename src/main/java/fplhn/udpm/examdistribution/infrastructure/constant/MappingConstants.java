package fplhn.udpm.examdistribution.infrastructure.constant;

/**
 * This class contains constants that represent the various API endpoints in the application.
 * These constants are used to avoid hardcoding the same strings in multiple places.
 */
public final class MappingConstants {

    // Constants representing the base paths for various resources
    public static final String HEAD_SUBJECT = "/head-subject";
    public static final String HEAD_OFFICE = "/head-office";
    public static final String HEAD_DEPARTMENT = "/head-department";
    public static final String TEACHER = "/teacher";
    public static final String STUDENT = "/student";

    // Constant representing the API version prefix
    public static final String API_VERSION_PREFIX = "/api/v1";

    // Constants representing the common API
    public static final String API_COMMON = API_VERSION_PREFIX + "/common";

    // Constants representing the full paths for various resources
    public static final String API_HEAD_SUBJECT_PREFIX = API_VERSION_PREFIX + HEAD_SUBJECT;
    public static final String API_HEAD_OFFICE_PREFIX = API_VERSION_PREFIX + HEAD_OFFICE;
    public static final String API_HEAD_DEPARTMENT_PREFIX = API_VERSION_PREFIX + HEAD_DEPARTMENT;
    public static final String API_TEACHER_PREFIX = API_VERSION_PREFIX + TEACHER;
    public static final String API_STUDENT_PREFIX = API_VERSION_PREFIX + STUDENT;

    // Constants representing the redirect paths for various resources under head office
    public static final String REDIRECT_HEAD_OFFICE_SUBJECT = HEAD_OFFICE + "/subjects";
    public static final String REDIRECT_HEAD_OFFICE_SEMESTER = HEAD_OFFICE + "/semesters";
    public static final String REDIRECT_HEAD_OFFICE_BLOCK = HEAD_OFFICE + "/blocks";
    public static final String REDIRECT_HEAD_OFFICE_FACILITY = HEAD_OFFICE + "/facilities";
    public static final String REDIRECT_HEAD_OFFICE_CAMPUS = HEAD_OFFICE + "/campuses";
    public static final String REDIRECT_HEAD_OFFICE_DEPARTMENT = HEAD_OFFICE + "/departments";
    public static final String REDIRECT_HEAD_OFFICE_DEPARTMENT_FACILITY = HEAD_OFFICE + "/departments-facility";
    public static final String REDIRECT_HEAD_OFFICE_STAFF = HEAD_OFFICE + "/staffs";
    public static final String REDIRECT_HEAD_OFFICE_STUDENT = HEAD_OFFICE + "/students";
    public static final String REDIRECT_HEAD_OFFICE_CLASS_SUBJECT = HEAD_OFFICE + "/class-subjects";
    public static final String REDIRECT_HEAD_OFFICE_ROLE = HEAD_OFFICE + "/roles";
    public static final String REDIRECT_HEAD_OFFICE_STAFF_SUBJECT = HEAD_OFFICE + "/staff-subject";

    // Constants representing the redirect paths for various resources under head department
    public static final String REDIRECT_HEAD_DEPARTMENT_MANAGE_HEAD_SUBJECTS = HEAD_DEPARTMENT + "/manage-head-of-subjects";
    public static final String REDIRECT_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM = HEAD_DEPARTMENT + "/join-room";
    public static final String REDIRECT_HEAD_DEPARTMENT_MANAGE_EXAM_SHIFT = HEAD_DEPARTMENT + "/exam-shift";
    public static final String REDIRECT_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT = HEAD_DEPARTMENT + "/manage-class-subjects";

    // Constants representing the redirect paths for various resources under head subject
    public static final String REDIRECT_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER = HEAD_SUBJECT + "/upload-exam-paper";
    public static final String REDIRECT_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL = HEAD_SUBJECT + "/exam-approval";
    public static final String REDIRECT_HEAD_SUBJECT_MANAGE_EXAM_RULE = HEAD_SUBJECT + "/exam-rule";
    public static final String REDIRECT_HEAD_SUBJECT_MANAGE_JOIN_ROOM = HEAD_SUBJECT + "/join-room";
    public static final String REDIRECT_HEAD_SUBJECT_CREATE_EXAM_PAPER = HEAD_SUBJECT + "/create-exam-paper";
    public static final String REDIRECT_HEAD_SUBJECT_UPDATE_EXAM_PAPER = HEAD_SUBJECT + "/update-exam-paper";
    public static final String REDIRECT_HEAD_SUBJECT_CHOOSE_EXAM_PAPER = HEAD_SUBJECT + "/choose-exam-paper";

    // Constants representing the full paths for various resources under head office
    public static final String API_HEAD_OFFICE_SUBJECT = API_HEAD_OFFICE_PREFIX + "/subjects";
    public static final String API_HEAD_OFFICE_SEMESTER = API_HEAD_OFFICE_PREFIX + "/semesters";
    public static final String API_HEAD_OFFICE_MAJOR = API_HEAD_OFFICE_PREFIX + "/majors";
    public static final String API_HEAD_OFFICE_BLOCK = API_HEAD_OFFICE_PREFIX + "/blocks";
    public static final String API_HEAD_OFFICE_FACILITY = API_HEAD_OFFICE_PREFIX + "/facilities";
    public static final String API_HEAD_OFFICE_CAMPUS = API_HEAD_OFFICE_PREFIX + "/campuses";
    public static final String API_HEAD_OFFICE_DEPARTMENT = API_HEAD_OFFICE_PREFIX + "/departments";
    public static final String API_HEAD_OFFICE_DEPARTMENT_FACILITY = API_HEAD_OFFICE_PREFIX + "/departments-facility";
    public static final String API_HEAD_OFFICE_MAJOR_FACILITY = API_HEAD_OFFICE_PREFIX + "/majors-facility";
    public static final String API_HEAD_OFFICE_STAFF = API_HEAD_OFFICE_PREFIX + "/staffs";
    public static final String API_HEAD_OFFICE_STUDENT = API_HEAD_OFFICE_PREFIX + "/students";
    public static final String API_HEAD_OFFICE_CLASS_SUBJECT = API_HEAD_OFFICE_PREFIX + "/class-subjects";
    public static final String API_HEAD_OFFICE_ROLE = API_HEAD_OFFICE_PREFIX + "/roles";
    public static final String API_HEAD_OFFICE_STAFF_SUBJECT = API_HEAD_OFFICE_PREFIX + "/staff-subject";

    // Constants representing the full paths for various resources under head department
    public static final String API_HEAD_DEPARTMENT_MANAGE_HEAD_SUBJECTS = API_HEAD_DEPARTMENT_PREFIX + "/head-of-subjects";
    public static final String API_HEAD_DEPARTMENT_MANAGE_SUBJECT = API_HEAD_DEPARTMENT_PREFIX + "/subjects";
    public static final String API_HEAD_DEPARTMENT_CLASS_SUBJECT = API_HEAD_DEPARTMENT_PREFIX + "/class-subjects";
    public static final String API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT = API_HEAD_DEPARTMENT_PREFIX + "/manage-class-subjects";
    public static final String API_HEAD_DEPARTMENT_BLOCK = API_HEAD_DEPARTMENT_PREFIX + "/blocks";
    public static final String API_HEAD_DEPARTMENT_CAMPUS = API_HEAD_DEPARTMENT_PREFIX + "/campuses";
    public static final String API_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM = API_HEAD_DEPARTMENT_PREFIX + "/join-room";
    public static final String API_HEAD_DEPARTMENT_STAFF = API_HEAD_DEPARTMENT_PREFIX + "/staffs";
    public static final String API_HEAD_DEPARTMENT_STUDENT = API_HEAD_DEPARTMENT_PREFIX + "/students";
    public static final String API_HEAD_DEPARTMENT_FILE = API_HEAD_DEPARTMENT_PREFIX + "/file";
    public static final String API_HEAD_DEPARTMENT_EXAM_SHIFT = API_HEAD_DEPARTMENT_PREFIX + "/exam-shift";

    // Constants representing the full paths for various resources under head subject
    public static final String API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER = API_HEAD_SUBJECT_PREFIX + "/assign-uploader";
    public static final String API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER = API_HEAD_SUBJECT_PREFIX + "/upload-exam-paper";
    public static final String API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL = API_HEAD_SUBJECT_PREFIX + "/exam-approval";
    public static final String API_HEAD_SUBJECT_MANAGE_EXAM_RULE = API_HEAD_SUBJECT_PREFIX + "/exam-rule";
    public static final String API_HEAD_SUBJECT_MANAGE_JOIN_ROOM = API_HEAD_SUBJECT_PREFIX + "/join-room";
    public static final String API_HEAD_SUBJECT_SUBJECT = API_HEAD_SUBJECT_PREFIX + "/subjects";
    public static final String API_HEAD_SUBJECT_STAFF = API_HEAD_SUBJECT_PREFIX + "/staffs";
    public static final String API_HEAD_SUBJECT_STUDENT = API_HEAD_SUBJECT_PREFIX + "/students";
    public static final String API_HEAD_SUBJECT_BLOCK = API_HEAD_SUBJECT_PREFIX + "/blocks";
    public static final String API_HEAD_SUBJECT_CAMPUS = API_HEAD_SUBJECT_PREFIX + "/campuses";
    public static final String API_HEAD_SUBJECT_CLASS_SUBJECT = API_HEAD_SUBJECT_PREFIX + "/class-subjects";
    public static final String API_HEAD_SUBJECT_CREATE_EXAM_PAPER = API_HEAD_SUBJECT_PREFIX + "/create-exam-paper";
    public static final String API_HEAD_SUBJECT_UPDATE_EXAM_PAPER = API_HEAD_SUBJECT_PREFIX + "/update-exam-paper";
    public static final String API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER = API_HEAD_SUBJECT_PREFIX + "/choose-exam-paper";

    public static final String REDIRECT_GOOGLE_AUTHORIZATION = "/oauth2/authorization/google";
    public static final String AUTHENTICATION = "/authentication";
    public static final String REDIRECT_AUTHENTICATION_LOGOUT = AUTHENTICATION + "/logout";
    public static final String REDIRECT_AUTHENTICATION_AUTHOR_SWITCH = "/author-switch";
    public static final String REDIRECT_AUTHENTICATION_FORBIDDEN = "/403";
    public static final String REDIRECT_AUTHENTICATION_UNAUTHORIZE = "/401";
    public static final String API_AUTHENTICATION = API_VERSION_PREFIX + AUTHENTICATION;

    // Constants representing the full paths for various resources under teacher
    public static final String REDIRECT_TEACHER_EXAM_SHIFT = TEACHER + "/exam-shift";
    public static final String REDIRECT_TEACHER_EXAM_FILE = TEACHER + "/exam-file";
    public static final String REDIRECT_TEACHER_MOCK_EXAM_PAPER = TEACHER + "/mock-exam-paper";

    // Constants representing the full paths for various resources under teacher
    public static final String API_TEACHER_EXAM_FILE = API_TEACHER_PREFIX + "/exam-file";
    public static final String API_TEACHER_MOCK_EXAM_PAPER = API_TEACHER_PREFIX + "/mock-exam-paper";
    public static final String API_TEACHER_PRACTICE_ROOM = API_TEACHER_PREFIX + "/practice-room";
    public static final String API_TEACHER_EXAM_SHIFT = API_TEACHER_PREFIX + "/exam-shift";
    public static final String API_TEACHER_SUBJECT = API_TEACHER_PREFIX + "/subjects";
    public static final String API_TEACHER_BLOCK = API_TEACHER_PREFIX + "/blocks";
    public static final String API_TEACHER_CAMPUS = API_TEACHER_PREFIX + "/campuses";
    public static final String API_TEACHER_CLASS_SUBJECT = API_TEACHER_PREFIX + "/class-subjects";
    public static final String API_TEACHER_STAFF = API_TEACHER_PREFIX + "/staffs";
    public static final String API_TEACHER_EXAM_PAPER_SHIFT = API_TEACHER_PREFIX + "/exam-paper-shift";
    public static final String API_TEACHER_TRACK_HISTORY = API_TEACHER_PREFIX + "/track-history";

    // Constants representing the full paths for various resources under student
    public static final String REDIRECT_STUDENT_EXAM_SHIFT = STUDENT + "/exam-shift";

    // Constants representing the full paths for various resources under teacher
    public static final String API_STUDENT_EXAM_SHIFT = API_STUDENT_PREFIX + "/exam-shift";
    public static final String API_STUDENT_JOIN_EXAM_SHIFT = API_STUDENT_PREFIX + "/exam-shift/join";
    public static final String API_STUDENT_TRACKER = API_STUDENT_PREFIX + "/tracker";

}