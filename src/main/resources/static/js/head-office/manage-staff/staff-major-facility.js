$(document).ready(function () {

    getMajorFacilitiesByStaffId();

    $('#facilitySelect').on('change', function () {
        handleChangeFacility($('#facilitySelect').val()?.trim());
    });

    $('#departmentSelect').on('change', function () {
        handleChangeDepartment($('#departmentSelect').val()?.trim(), $('#facilitySelect').val()?.trim());
    });

    $('#addMajorDepartmentButton').on('click', handleSubmitAddMajorDepartment);

})

//start state
let idStaffMajorFacility = '';
let staffMajorFacilityDetail = '';
//end state

//start getter
const getIdStaffMajorFacility = () => idStaffMajorFacility;
const getStaffMajorFacilityDetail = () => staffMajorFacilityDetail;
//end getter

//start setter
const setStaffMajorFacilityDetail = (staffMajorFacility) => {
    staffMajorFacilityDetail = staffMajorFacility;
};
const setIdStaffMajorFacility = (staffMajorFacilityId) => {
    idStaffMajorFacility = staffMajorFacilityId;
};

//end setter

function getMajorFacilitiesByStaffId() {

    let url = window.location.href;

    let idStaff = url.substring(url.lastIndexOf('/') + 1);

    let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/major-facility/' + idStaff;
    showLoading();
    $.ajax({
        type: "GET",
        url: api,
        success: function (responseBody) {
            if (responseBody?.data?.length === 0) {
                $('#majorFacilityTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Nhân viên này không có bộ môn chức vụ</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const roles = responseBody?.data?.map((item, index) => {
                return `<tr>
                            <td>${index + 1}</td>
                            <td>${item.facilityName}</td>  
                            <td>${item.departmentName}</td>  
                            <td>${item.majorName}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            <a>
                                <i
                                    onclick="openModalUpdateStaffMajorFacility('${item.staffMajorFacilityId}')"
                                    class="fas fa-pen-nib"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                                <a>
                                <i
                                    onclick="handleDeleteStaffMajorFacility('${item.staffMajorFacilityId}')"
                                    class="fas fa-trash-alt"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a></td>
                        </tr>`;
            });
            $('#majorFacilityTableBody').html(roles);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu bộ môn chuyên ngành theo cơ sở!');
        }
    });
    hideLoading();
}

function handleSubmitAddMajorDepartment() {

    $('#majorFacilityModal').modal('hide');

    let title = getIdStaffMajorFacility().length === 0 ? 'thêm' : 'sửa';

    swal({
        title: "Xác nhận " + title + "?",
        text: "Bạn chắc chắn muốn " + title + " bộ môn chuyên ngành cho nhân viên này?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Xác nhận",
                className: "btn btn-black",
            },
        },
    }).then((ok) => {
        if (ok) {

            let type = getIdStaffMajorFacility().length === 0 ? 'post' : 'put';

            let url = window.location.href;

            let idStaff = url.substring(url.lastIndexOf('/') + 1);

            let params = {};
            if (type === 'post') {
                params = {
                    idStaff: idStaff,
                    idFacility: $('#facilitySelect').val(),
                    idDepartment: $('#departmentSelect').val(),
                    idMajor: $('#majorSelect').val()
                };
            } else {
                params = {
                    idStaff: idStaff,
                    idFacility: $('#facilitySelect').val(),
                    idDepartment: $('#departmentSelect').val(),
                    idMajor: $('#majorSelect').val(),
                    idStaffMajorFacility:getIdStaffMajorFacility()
                };
            }

            let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/staff-major-facility';

            showLoading();

            $.ajax({
                type: type,
                url: api,
                data: JSON.stringify(params),
                contentType: "application/json",
                success: function (responseBody) {
                    showToastSuccess(responseBody?.message)
                    getMajorFacilitiesByStaffId();
                    hideLoading();
                },
                error: function (error) {
                    if (error?.responseJSON?.length > 0) {
                        error.responseJSON.forEach(err => {
                            $(`#${err.fieldError}Error`).text(err.message);
                        });
                    } else {
                        let mess = error?.responseJSON?.message
                            ? error?.responseJSON?.message : 'Có lỗi xảy ra khi thêm bộ môn chuyên ngành';
                        showToastError(mess);
                    }
                    hideLoading();
                    $('#majorFacilityModal').modal('show');
                }
            });

        } else {
            $('#majorFacilityModal').modal('show');
        }
    });
}

function handleChangeFacility(idFacility) {
    let url = ApiConstant.API_HEAD_OFFICE_STAFF + '/department/' + idFacility;
    showLoading();
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: url,
            success: function (responseBody) {
                const departments = responseBody?.data?.map((department) => {
                    return `<option value="${department.departmentId}">${department.departmentName}</option>`;
                });
                departments.unshift('<option value="">--Chọn bộ môn--</option>');
                $('#departmentSelect').html(departments);
                const major = '<option value="">--Chọn chuyên ngành--</option>';
                $('#majorSelect').html(major);
                hideLoading();
                resolve();
            },
            error: function (error) {
                showToastError('Có lỗi xảy ra khi lấy danh sách bộ môn theo cơ sở!');
                hideLoading();
                reject(error);
            }
        });
    });
}

function handleChangeDepartment(idDepartment, idFacility) {
    let url = ApiConstant.API_HEAD_OFFICE_STAFF + '/major';
    showLoading();
    const data = {
        idDepartment: idDepartment,
        idFacility: idFacility
    }
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: url,
            data: data,
            success: function (responseBody) {
                const major = responseBody?.data?.map((major) => {
                    return `<option value="${major.majorId}">${major.majorName}</option>`;
                });
                major.unshift('<option value="">--Chọn chuyên ngành--</option>');
                $('#majorSelect').html(major);
                hideLoading();
                resolve();
            },
            error: function (error) {
                showToastError('Có lỗi xảy ra khi lấy danh sách bộ môn theo cơ sở!');
                hideLoading();
                reject(error);
            }
        });
    });
}

function getFacilitiesAtModalMajorFacility() {

    let url = window.location.href;

    let idStaff = url.substring(url.lastIndexOf('/') + 1);

    let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/facilities-select?idStaff=' + idStaff;

    showLoading();
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: api,
            success: function (responseBody) {
                const facilities = responseBody?.data?.map((facility) => {
                    return `<option value="${facility.idFacility}" ${facility.idFacility === examDistributionInfor.facilityId ? "selected" : ""}>${facility.facilityName}</option>`;
                });
                $('#facilitySelect').html(facilities);
                resolve();
                hideLoading();
            },
            error: function (error) {
                showToastError('Có lỗi xảy ra khi lấy dữ liệu cơ sở!');
                reject(error);
                hideLoading();
            }
        });
    });
}

async function openModalMajorFacility() {

    await getFacilitiesAtModalMajorFacility();

    setIdStaffMajorFacility('');

    handleChangeFacility($('#facilitySelect').val()?.trim());
    $('#facilityLabel').text('');
    $('#facilitySelect').prop('disabled', false);
    $('#idDepartmentError').text('');
    $('#idFacilityError').text('');
    $('#idMajorError').text('');
    $('#departmentSelect').val('');
    $('#majorSelect').val('');
    $('#majorFacilityModalLabel').text('Thêm bộ môn chuyên ngành');
    $('#majorFacilityModal').modal('show');
}

function handleDeleteStaffMajorFacility(idStaffMajorFacility) {
    swal({
        title: "Xác nhận xóa?",
        text: "Bạn chắc chắn muốn xóa bộ môn chuyên ngành của nhân viên này?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Xóa",
                className: "btn btn-black",
            },
        },
    }).then((willDelete) => {
        if (willDelete) {

            let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/staff-major-facility?idStaffMajorFacility=' + idStaffMajorFacility;

            showLoading();

            $.ajax({
                type: "delete",
                url: api,
                contentType: "application/json",
                success: function (responseBody) {
                    getFacilitiesAtModalMajorFacility();
                    if (responseBody?.status === "OK") {
                        showToastSuccess("Xóa bộ môn, chuyên ngành thành công!");
                    }
                    getMajorFacilitiesByStaffId();
                },
                error: function (error) {
                    let mess = error?.responseJSON?.message
                        ? error?.responseJSON?.message : 'Có lỗi xảy ra khi xóa bộ môn, chuyên ngành của nhân viên này';
                    showToastError(mess);
                }

            });
            hideLoading();
        }
    });
}

function getStaffMajorFacilityById(idStaffMajorFacility) {

    showLoading();

    let api = ApiConstant.API_HEAD_OFFICE_STAFF + '/staff-major-facility?idStaffMajorFacility=' + idStaffMajorFacility;

    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: api,
            success: function (responseBody) {
                setStaffMajorFacilityDetail(responseBody?.data);
                $('#facilitySelect').val(responseBody?.data?.facilityId);
                hideLoading();
                resolve();
            },
            error: function (error) {
                showToastError('Có lỗi xảy ra khi lấy chuyên ngành bộ môn!');
                hideLoading();
                reject(error);
            }
        });
    });
}


async function openModalUpdateStaffMajorFacility(idStaffMajorFacility) {

    await getFacilities();

    await getStaffMajorFacilityById(idStaffMajorFacility);

    await handleChangeFacility($('#facilitySelect').val()?.trim());

    const staffMajorFacilityDetail = getStaffMajorFacilityDetail();

    await Promise.resolve($('#departmentSelect').val(staffMajorFacilityDetail.departmentId));

    await handleChangeDepartment($('#departmentSelect').val()?.trim(), $('#facilitySelect').val()?.trim());

    await Promise.resolve($('#majorSelect').val(staffMajorFacilityDetail.majorId));

    setIdStaffMajorFacility(idStaffMajorFacility);

    $('#facilitySelect').prop('disabled', true);
    $('#facilityLabel').text('(Không sửa trường này)');
    $('#idDepartmentError').text('');
    $('#idFacilityError').text('');
    $('#idMajorError').text('');
    $('#majorFacilityModalLabel').text('Sửa bộ môn chuyên ngành');
    $('#majorFacilityModal').modal('show');
}
