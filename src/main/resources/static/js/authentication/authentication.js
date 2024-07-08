$(document).ready(function () {
    fetchListFacility();
});

const getFacilityId = () => $("#listFacility").val();

const getListMappingScreenConstant = () => {
    return [
        {status: 1, role: "BAN_DAO_TAO", redirect: "head-office", facility: getFacilityId()},
        {status: 2, role: "TRUONG_MON", redirect: "head-subject", facility: getFacilityId()},
        {status: 3, role: "CHU_NHIEM_BO_MON", redirect: "head-department", facility: getFacilityId()},
        {status: 4, role: "GIANG_VIEN", redirect: "teacher", facility: getFacilityId()},
        {status: 5, role: "SINH_VIEN", redirect: "student", facility: getFacilityId()}
    ]
}

const handleRedirectGoogleLogin = (status) => {
    const mapping = getListMappingScreenConstant().find(item => item.status === status);
    const origin = window.location.origin;
    if (mapping) {
        window.location.href = `${origin}${ApiConstant.REDIRECT_AUTHENTICATION_AUTHOR_SWITCH}?role=${mapping.role}&redirect_uri=${mapping.redirect}&facility_id=${mapping.facility}`;
    }
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

