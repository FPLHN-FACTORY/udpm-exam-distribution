$(document).ready(() => {
    init();

    $('#btnImportExamShift').click(() => {
        $('#fileImportExamShift').click();
    });

    $('#fileImportExamShift').change(() => {
        handleUploadExamShiftExcel();
    });

    $('#pageSize').change(() => {
        changePageSize($('#pageSize').val());
    });

    $('#btnSaveEditExamShift').click(() => handleEditExamShift());

});

let currentExamShiftId = null;

const examShiftParams = {
    page: INIT_PAGINATION.page,
    size: INIT_PAGINATION.size,
    shift: null,
    blockId: null,
    staffCode: null,
    room: null,
    classSubjectCode: null,
    subjectCode: null,
    examDate: null,
};

const handleUploadExamShiftExcel = () => {
    const formData = new FormData();
    const file = $('#fileImportExamShift')[0].files[0];
    formData.append('file', file);
    $.ajax({
        url: `${ApiConstant.API_HEAD_DEPARTMENT_FILE}/upload`,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: (res) => {
            console.log(res);
            showToastSuccess(res);
        },
        error: (error) => {
            console.error(error);
            showToastError('Tải lên thất bại');
        }
    });
};

const getListExamShift = () => {
    const url = getUrlParameters(`${ApiConstant.API_HEAD_DEPARTMENT_EXAM_SHIFT}`, examShiftParams);
    $.ajax({
        url: url,
        type: 'GET',
        contentType: "application/json",
        success: (res) => {
            const data = res?.data?.data || [];
            if (data.length === 0) {
                $('#manageExamShiftTableBody').html('<tr><td colspan="8" style="text-align: center;">Không có dữ liệu</td></tr>');
                $('#paginationExamShift').empty();
                return;
            }

            const examShifts = data.map((examShift, index) => {
                return `
                    <tr>
                        <td>${examShift.orderNumber}</td>
                        <td>${examShift.classSubjectCode}</td>
                        <td>${examShift.subjectInfo}</td>
                        <td>${examShift.firstSupervisor || '<span class="tag tag-danger">Chưa phân công</span>'}</td>
                        <td>${examShift.secondSupervisor || '<span class="tag tag-danger">Chưa phân công</span>'}</td>
                        <td>${examShift.joinCode}</td>
                        <td>${formatFromUnixTimeToDate(examShift.examDate)}</td>
                        <td>${examShift.room}</td>
                        <td>${examShift.shift}</td>
                        <td style="text-align: center;">
                            ${examShift.isCanEdit === 1 ?
                    `<i class="fas fa-edit" style="cursor: pointer;" data-bs-toggle="tooltip" 
                                    data-bs-title="Chỉnh sửa ca thi" onclick="showModalEditExamShift('${examShift.id}')"></i>` :
                    '<span class="tag tag-danger">Ca thi đã / đang diễn ra</span>'}
                        </td>
                    </tr>`;
            });

            $('#manageExamShiftTableBody').html(examShifts.join(''));
            const totalPages = res?.data?.totalPages || 1;
            createPagination(totalPages, examShiftParams.page);
            callToolTip();
        },
        error: (error) => {
            console.error(error);
        }
    });
};

const createPagination = (totalPages, currentPage) => {
    let paginationHtml = '';

    paginationHtml += currentPage > 1 ?
        `<li class="page-item"><a class="page-link" href="#" onclick="changePage(${currentPage - 1})">Trước</a></li>` :
        `<li class="page-item disabled"><a class="page-link" href="#">Trước</a></li>`;

    for (let i = 1; i <= totalPages; i++) {
        paginationHtml += i === currentPage ?
            `<li class="page-item active"><a class="page-link text-white" href="#">${i}</a></li>` :
            `<li class="page-item"><a class="page-link" href="#" onclick="changePage(${i})">${i}</a></li>`;
    }

    paginationHtml += currentPage < totalPages ?
        `<li class="page-item"><a class="page-link" href="#" onclick="changePage(${currentPage + 1})">Sau</a></li>` :
        `<li class="page-item disabled"><a class="page-link" href="#">Sau</a></li>`;

    $('#paginationExamShift').html(paginationHtml);
};

const changePage = (page) => {
    examShiftParams.page = page;
    getListExamShift();
};

const changePageSize = (size) => {
    examShiftParams.size = size;
    getListExamShift();
};

const showModalEditExamShift = (examShiftId) => {
    currentExamShiftId = examShiftId;
    getDetailExamShift(examShiftId);
    $('#editExamShiftModal').modal('show');
}

const getDetailExamShift = (examShiftId) => {
    $.ajax({
        url: `${ApiConstant.API_HEAD_DEPARTMENT_EXAM_SHIFT}/${examShiftId}`,
        type: 'GET',
        contentType: "application/json",
        success: (res) => {
            const data = res?.data || {};
            $('#editExamShiftModal').find('#editClassSubjectCode').val(data.classSubjectCode);
            $('#editExamShiftModal').find('#editSubjectInfo').val(data.subjectInfo);
            $('#editExamShiftModal').find('#editExamDate').val(getValueForInputDate(data.examDate));
            $('#editExamShiftModal').find('#editRoom').val(data.room);
            $('#editExamShiftModal').find('#editExamShift').val(data.shift);
            $('#editExamShiftModal').find('#editStaffCode1').val(data.firstSupervisorCode);
            $('#editExamShiftModal').find('#editStaffCode2').val(data.secondSupervisorCode);
            $('#editExamShiftModal').find('#editJoinCode').val(data.joinCode);
        },
        error: (error) => {
            console.error(error);
        }
    });

}

const editExamShift = () => {
    const data = {
        examDate: new Date($('#editExamDate').val()).getTime(),
        room: $('#editRoom').val(),
        shift: $('#editExamShift').val(),
        firstSupervisorCode: $('#editStaffCode1').val(),
        secondSupervisorCode: $('#editStaffCode2').val(),
    };

    $.ajax({
        url: `${ApiConstant.API_HEAD_DEPARTMENT_EXAM_SHIFT}/${currentExamShiftId}`,
        type: 'PUT',
        contentType: "application/json",
        data: JSON.stringify(data),
        success: (res) => {
            $('#editExamShiftModal').modal('hide');
            resetField();
            showToastSuccess(res?.data?.message || 'Cập nhật thành công');
            getListExamShift();
        },
        error: (error) => {
            console.error(error);
            showToastError('Cập nhật thất bại');
        }
    });
};

const handleEditExamShift = () => {
    showAlert(
        "Xác nhận cập nhật ca thi",
        "Bạn chắc chắn muốn cập nhật ca thi này?",
        "warning",
        "Xác nhận",
        () => editExamShift(),
        () => swal.close()
    );
}

const resetField = () => {
    $('#examShift').val('');
    $('#blockInfo').val('');
    $('#staffCode').val('');
    $('#room').val('');
    $('#classSubjectCode').val('');
    $('#subjectCode').val('');
    $('#examDate').val('');

}

const handleFilter = () => {
    examShiftParams.page = INIT_PAGINATION.page;
    examShiftParams.shift = $('#examShift').val();
    examShiftParams.blockId = $('#blockInfo').val();
    examShiftParams.staffCode = $('#staffCode').val();
    examShiftParams.room = $('#room').val();
    examShiftParams.classSubjectCode = $('#classSubjectCode').val();
    examShiftParams.subjectCode = $('#subjectCode').val();
    examShiftParams.examDate = $('#examDate').val() ? new Date($('#examDate').val()).getTime() : null;
    getListExamShift();
};

const handleListenChange = debounce(() => {
    handleFilter();
});

const addEventListenInput = () => {
    $('#examShift').on('change', handleListenChange);
    $('#blockInfo').on('change', handleListenChange);
    $('#staffCode').on('input', handleListenChange);
    $('#room').on('input', handleListenChange);
    $('#classSubjectCode').on('input', handleListenChange);
    $('#subjectCode').on('input', handleListenChange);
    $('#examDate').on('input', handleListenChange);
};

const getBlockList = () => {
    $.ajax({
        url: `${ApiConstant.API_HEAD_DEPARTMENT_EXAM_SHIFT}/block-info`,
        type: 'GET',
        contentType: "application/json",
        success: (res) => {
            const data = res?.data || [];
            const blocks = data.map(block => `<option value="${block.id}">${block.blockInfo}</option>`);
            $('#blockInfo').html(blocks.join(''));
        },
        error: (error) => {
            console.error(error);
        }
    });
};

const init = () => {
    getListExamShift();
    getBlockList();
    addEventListenInput();
};
