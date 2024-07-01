$(document).ready(() => {
});

const examDistributionInfo = getExamDistributionInfo();
console.log(examDistributionInfo);
const getListStaff = (
    page = INIT_PAGINATION.page,
    size = INIT_PAGINATION.size,
    departmentFacilityId = examDistributionInfo.departmentFacilityId,
    staffName = null,
    staffCode = null,
) => {
    const params = {
        page,
        size,
        departmentFacilityId,
        staffName,
        staffCode,
    };

    const url = getUrlParameters(ApiConstant.API_HEAD_DEPARTMENT_MANAGE_HOS, params);


};