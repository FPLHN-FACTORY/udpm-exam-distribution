$(document).ready(function () {
    checkIfUserLoggedIn();
    fetchListFacility();
});

const getFacilityId = () => $("#listFacility").val();

const getListMappingScreenConstant = () => {
    return [
        {status: 1, role: "BAN_DAO_TAO", redirect: "head-office", facility: getFacilityId()},
        {status: 2, role: "TRUONG_MON", redirect: "head-subject", facility: getFacilityId()},
        {status: 3, role: "CHU_NHIEM_BO_MON", redirect: "head-department", facility: getFacilityId()},
        {status: 4, role: "GIANG_VIEN", redirect: "teacher", facility: getFacilityId()},
        {status: 5, role: "SINH_VIEN", redirect: "student/choose-exam-type", facility: getFacilityId()}
    ]
}

const handleRedirectGoogleLogin = async (status) => {
    const mapping = getListMappingScreenConstant().find(item => item.status === status);
    if (!mapping) return;

    const origin = window.location.origin;
    const redirectUrl = `${origin}${ApiConstant.REDIRECT_AUTHENTICATION_AUTHOR_SWITCH}?role=${mapping.role}&redirect_uri=${mapping.redirect}&facility_id=${mapping.facility}`;

    if (mapping.role === "SINH_VIEN") {
        const installed = await checkExtensionInstalled();
        if (!installed) {
            showToastError("Bạn chưa cài đặt Extension Tab-Tracker");
            return;
        }
    }

    window.location.href = redirectUrl;
};

const fetchListFacility = () => {
    $('#loading-ele').show();
    $.ajax({
        contentType: "application/json",
        type: "GET",
        url: ApiConstant.API_AUTHENTICATION_PREFIX + "/get-list-facility",
        dataType: 'json',
        success: function (data) {
            const facilities = data?.data.map(item => {
                return `<option value="${item.id}">${item.name}</option>`;
            });
            $('#listFacility').html(facilities);

            $('#loading-ele').hide();
        },
        error: function (error) {
            setTimeout(() => {
                $('#loading-ele').hide();
            }, 1000);
            showToastError('Có lỗi xảy ra khi lấy danh sách cơ sở');
        }
    });
};

const checkIfUserLoggedIn = () => {
    const userInfo = getExamDistributionInfo();
    if (userInfo) {
        const {userRole} = userInfo;
        const userRoleString = userRole?.replace("[", "").replace("]", "");
        const mapping = getListMappingScreenConstant()
            .find(item => item?.role === userRoleString && item?.facility === getFacilityId());
        if (mapping) window.location.href = `${window.location.origin}/${mapping.redirect}`;
    }
}

