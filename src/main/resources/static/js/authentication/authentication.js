
const MAPPING_SCREEN_CONSTANT = [
    {status: 1, role: "BAN_DAO_TAO", redirect: "head-office"},
    {status: 2, role: "TM_CNBM", redirect: "head-office"},
    {status: 3, role: "GIANG_VIEN", redirect: "head-office"},
    {status: 4, role: "SINH_VIEN", redirect: "head-office"},
];

const handleRedirectGoogleLogin = (status) => {
    const mapping = MAPPING_SCREEN_CONSTANT.find(item => item.status === status);
    if (screen) {
        window.location.href = `http://localhost:8888${ApiConstant.REDIRECT_AUTHENTICATION_AUTHOR_SWITCH}?screen=${mapping.role}&redirect_uri=${mapping.redirect}`;
    } else {
        console.error("Invalid status provided");
    }
};