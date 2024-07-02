const getFacilityId = () => $("#listFacility").val();

const getListMappingScreenConstant = () => {
    return [
        {status: 1, role: "BAN_DAO_TAO", redirect: "head-office", facility: getFacilityId()},
        {status: 2, role: "TM_CNBM", redirect: "head-office", facility: getFacilityId()},
        {status: 3, role: "GIANG_VIEN", redirect: "head-office", facility: getFacilityId()},
        {status: 4, role: "SINH_VIEN", redirect: "head-office", facility: getFacilityId()}
    ]
}

const handleRedirectGoogleLogin = (status) => {
    const mapping = getListMappingScreenConstant().find(item => item.status === status);
    if (screen) {
        window.location.href = `http://localhost:8888${ApiConstant.REDIRECT_AUTHENTICATION_AUTHOR_SWITCH}?screen=${mapping.role}&redirect_uri=${mapping.redirect}&facility_id=${mapping.facility}`;
    } else {
        console.error("Invalid status provided");
    }
};

$(document).ready(function () {
    fetchListFacility();
});

const fetchListFacility = () => {
    $.ajax({
        contentType: "application/json",
        type: "GET",
        url: ApiConstant.API_AUTHENTICATION_PREFIX + "/get-list-facility",
        dataType: 'json',
        success: function (data) {
            const select = document.getElementById('listFacility');
            data?.data.forEach(item => {
                const option = document.createElement('option');
                option.value = item.id;
                option.text = item.name;
                select.appendChild(option);
            });
            $('#listFacility').val(select);
            if ($("#listFacility").find('option').length > 0) {
                $("#listFacility").prop('selectedIndex', 0);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy danh sách cơ sở');
        }
    });
};

