$(document).ready(function () {

    getSemesters();

    handleAddEvent($('#semesterName'),'keyup',function () {
        const semesterName = $('#semesterName').val();
        const semesterYear = $('#semesterYear').val();
        const startTime = $('#startDate').val();
        getSemesters(1, 5, semesterName, semesterYear, startTime);
    })

    handleAddEvent($('#semesterYear'),'keyup',function () {
        const semesterName = $('#semesterName').val();
        const semesterYear = $('#semesterYear').val();
        const startTime = $('#startDate').val();
        getSemesters(1, 5, semesterName, semesterYear, startTime);
    })

    handleAddEvent($('#startDate'),'change',function () {
        const semesterName = $('#semesterName').val();
        const semesterYear = $('#semesterYear').val();
        const startTime = $('#startDate').val();
        getSemesters(1, 5, semesterName, semesterYear, startTime);
    })

    $('#modifySemesterButton').on('click', function () {
        submitSemesterForm();
    });

    $('#modifySemesterButtonUpdate').on('click', function () {
        submitUpdateSemesterForm();
    });

    $('#modifyBlockButton').on('click', function () {
        submitBlockForm();
    });

    $('#closeBlockModal').on('click', function () {
        getDetailSemester(currentSemesterId);
    });

});

let currentSemesterId = null;

const getAllBlockBySemesterId = (semesterId) => {
    $.ajax({
        type: 'GET',
        url: ApiConstant.API_HEAD_OFFICE_BLOCK + `/${semesterId}`,
        success: function (response) {
            const blocks = response?.data?.map((block, index) => {
                return `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${block.blockName}</td>
                        <td>${block.startTime ? formatFromUnixTimeToDate(block.startTime) : 'Chưa xác định'}</td>
                        <td>${block.endTime ? formatFromUnixTimeToDate(block.endTime) : 'Chưa xác định'}</td>
                        <td>${block.blockStatus === 0 ? 'Hoạt động' : 'Ngừng hoạt động'}</td>
                        <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            <span class="fs-4">
                                <i onclick="getDetailBlock('${block.id}')"
                                class="fa-solid fa-pen-to-square" style="cursor: pointer; margin-left: 10px;"></i>
                            </span>
                            <span class="fs-4">
                                <i onclick="confirmChangeStatusBlock('${block.id}')"
                                class="fa-solid fa-right-left" style="cursor: pointer; margin-left: 10px;"></i>
                            </span>
                        </td>
                    </tr>
                `;
            });
            $('#blockTableBody').html(blocks);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu block');
        }
    })
}

const getSemesters = (
    page = 1,
    size = $('#pageSize').val(),
    semesterName = null,
    semesterYear = null,
    startDate = null
) => {

    const params = {
        page: page,
        size: size,
        semesterName: semesterName,
        semesterYear: semesterYear,
        startDate: new Date(startDate).getTime()
    };

    let url = ApiConstant.API_HEAD_OFFICE_SEMESTER + '?';

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    $.ajax({
        type: 'GET',
        url: url,
        success: function (response) {
            const semesters = response?.data?.data?.map((semester, index) => {
                return `
                    <tr>
                        <td>${semester.orderNumber}</td>
                        <td>${semester.semesterName}</td>
                        <td>${semester.semesterYear}</td>
                        <td>${semester.startTime ? formatFromUnixTimeToDate(semester.startTime) : 'Chưa xác định'}</td>
                        <td>${semester.semesterStatus === 0 ? 'Hoạt động' : 'Ngừng hoạt động'}</td>
                        <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            <span class="fs-4">
                                <i onclick="getDetailSemester('${semester.id}')"
                                class="fa-solid fa-pen-to-square" style="cursor: pointer; margin-left: 10px;"></i>
                            </span>
                            <span class="fs-4">
                                <i onclick="confirmChangeStatusSemester('${semester.id}')"
                                class="fa-solid fa-right-left" style="cursor: pointer; margin-left: 10px;"></i>
                            </span>
                        </td>
                    </tr>
                `;
            });
            $('#semesterTableBody').html(semesters);
            const totalPages = response?.data?.totalPages ? response?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu học kỳ');
        }
    })
};

const changePage = (page) => {
    getSemesters(page, $('#pageSize').val());
}

const createPagination = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePage(${currentPage - 1})">
                            Trước
                        </a>
                     </li>`;
    } else {
        paginationHtml += `
                <li class="page-item disabled">
                    <a class="page-link" href="#">
                        Trước
                    </a>
                </li>
        `;
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<li class="page-item active"><a class="page-link text-white" href="#">${i}</a></li>`;
        } else {
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePage(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePage(${currentPage + 1})">
                          Sau
                    </a>
                </li>
`;
    } else {
        paginationHtml += `
            <li class="page-item disabled">
                <a class="page-link" href="#">
                    Sau
                </a>
            </li>
`;
    }

    $('#pagination').html(paginationHtml);
}

const createSemester = () => {
    const semesterName = $('#modifySemesterName').val();
    const semesterYear = $('#modifySemesterYear').val();
    const startTime = $('#modifyStartTime').val();

    $.ajax({
        type: 'POST',
        url: ApiConstant.API_HEAD_OFFICE_SEMESTER,
        data: JSON.stringify({
            semesterName: semesterName,
            semesterYear: semesterYear,
            startTime: new Date(startTime).getTime()
        }),
        contentType: 'application/json',
        success: function (response) {
            if (response) {
                showToastSuccess(response?.message);
                getSemesters();
                $('#semesterModal').modal('hide');
            }
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}`).addClass('is-invalid');
                });
            } else if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message)
            } else {
                showToastError('Có lỗi xảy ra khi thêm học kỳ');
            }
        }
    })
};

const confirmCreateSemester = (id) => {
    swal({
        title: "Xác nhận thêm?",
        text: "Bạn chắn muốn thêm học kì này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Thêm",
                className: "btn btn-black",
            },
        },
    }).then((willCreate) => {
        if (willCreate) {
            createSemester();
        }
    });
}

const getDetailSemester = (semesterId) => {
    currentSemesterId = semesterId;
    $('#semesterIdUpdate').val(semesterId);
    console.log(semesterId);
    $.ajax({
        type: 'GET',
        url: ApiConstant.API_HEAD_OFFICE_SEMESTER + `/${semesterId}`,
        success: function (response) {
            if (response?.data) {
                const semester = response?.data;
                $('#modifySemesterNameUpdate').val(semester.semesterName);
                $('#modifySemesterYearUpdate').val(semester.semesterYear);
                $('#modifyStartTimeUpdate').val(getValueForInputDate(semester.startTime));
                getAllBlockBySemesterId(semesterId);
                $('#semesterModalLabel').text('Chỉnh sửa học kỳ');
                $('#semesterUpdateModal').modal('show');
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin học kỳ');
        }
    })
}

const updateSemester = () => {
    const semesterId = $('#semesterIdUpdate').val();
    const semesterName = $('#modifySemesterNameUpdate').val();
    const semesterYear = $('#modifySemesterYearUpdate').val();
    const startTime = $('#modifyStartTimeUpdate').val();

    $.ajax({
        type: 'PUT',
        url: ApiConstant.API_HEAD_OFFICE_SEMESTER + `/${semesterId}`,
        data: JSON.stringify({
            semesterName: semesterName,
            semesterYear: semesterYear,
            startTime: new Date(startTime).getTime()
        }),
        contentType: 'application/json',
        success: function (response) {
            if (response) {
                showToastSuccess(response?.message);
                getSemesters();
                $('#semesterUpdateModal').modal('hide');
            }
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}ErrorUpdate`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}Update`).addClass('is-invalid');
                });
            } else if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message)
            } else {
                showToastError('Có lỗi xảy ra khi cập nhật học kỳ');
            }
        }
    })
};

const confirmUpdateSemester = (id) => {
    swal({
        title: "Xác nhận sửa?",
        text: "Bạn chắn muốn sửa học kì này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Lưu",
                className: "btn btn-black",
            },
        },
    }).then((willCreate) => {
        if (willCreate) {
            updateSemester();
        }
    });
}

const changeStatusSemester = (semesterId) => {
    $.ajax({
        type: 'PUT',
        url: ApiConstant.API_HEAD_OFFICE_SEMESTER + `/status/${semesterId}`,
        success: function (response) {
            if (response) {
                showToastSuccess(response?.message);
                getSemesters();
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi thay đổi trạng thái học kỳ');
        }
    })
}

const confirmChangeStatusSemester = (semesterId) => {
    swal({
        title: "Xác nhận thay đổi trạng thái?",
        text: "Bạn chắn muốn thay đổi trạng thái học kỳ này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Thay đổi",
                className: "btn btn-black",
            },
        },
    }).then((willDelete) => {
        if (willDelete) {
            changeStatusSemester(semesterId);
        }
    });
}

const confirmUpdateBlock = (id) => {
    swal({
        title: "Xác nhận Sửa?",
        text: "Bạn chắn muốn thêm block này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Lưu",
                className: "btn btn-black",
            },
        },
    }).then((willCreate) => {
        if (willCreate) {
            updateBlock();
        }
    });
}

const getDetailBlock = (blockId) => {
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_OFFICE_BLOCK + `/get/${blockId}`,
        success: function (response) {
            if (response?.data) {
                const block = response?.data;
                resetFormErrorBlock();
                $('#blockId').val(block.id);
                $('#modifyBlockName').val(block.blockName);
                $('#modifyStartTimeBlock').val(getValueForInputDate(block.startTime));
                $('#modifyEndTimeBlock').val(getValueForInputDate(block.endTime));
                $('#blockModalLabel').text('Cập nhật block');
                $('#semesterUpdateModal').modal('hide');
                $('#blockModal').modal('show');
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin block');
        }
    });
}

const updateBlock = () => {
    const blockId = $('#blockId').val();
    const blockName = $('#modifyBlockName').val();
    const startTime = $('#modifyStartTimeBlock').val();
    const endTime = $('#modifyEndTimeBlock').val();

    $.ajax({
        type: 'PUT',
        url: ApiConstant.API_HEAD_OFFICE_BLOCK + `/${blockId}`,
        data: JSON.stringify({
            blockName: blockName,
            startTime: new Date(startTime).getTime(),
            endTime: new Date(endTime).getTime()
        }),
        contentType: 'application/json',
        success: function (response) {
            if (response) {
                showToastSuccess(response?.message);
                $('#blockModal').modal('hide');
                getDetailSemester(currentSemesterId);
            }
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}BlockError`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}Block`).addClass('is-invalid');
                });
            } else if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message)
            } else {
                showToastError('Có lỗi xảy ra khi cập nhật học kỳ');
            }
        }
    })
};

const confirmCreateBlock = () => {
    swal({
        title: "Xác nhận Thêm?",
        text: "Bạn chắn muốn thêm block này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Thêm",
                className: "btn btn-black",
            },
        },
    }).then((willCreate) => {
        if (willCreate) {
            createBlock(currentSemesterId);
        }
    });
}

const createBlock = (semesterId) => {
    const blockName = $('#modifyBlockName').val();
    const startTimeBlock = $('#modifyStartTimeBlock').val();
    const endTimeBlock = $('#modifyEndTimeBlock').val();

    $.ajax({
        type: 'POST',
        url: ApiConstant.API_HEAD_OFFICE_BLOCK,
        data: JSON.stringify({
            blockName: blockName,
            semesterId: semesterId,
            startTime: new Date(startTimeBlock).getTime(),
            endTime: new Date(endTimeBlock).getTime()
        }),
        contentType: 'application/json',
        success: function (response) {
            if (response) {
                showToastSuccess(response?.message);
                getDetailSemester(semesterId);
                $('#blockModal').modal('hide');
            }
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    console.log(err)
                    $(`#${err.fieldError}BlockError`).text(err.message);
                    $(`#modify${capitalizeFirstLetter(err.fieldError)}Block`).addClass('is-invalid');
                });
            } else if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message)
            } else {
                showToastError('Có lỗi xảy ra khi thêm học kỳ');
            }
        }
    })
};

const changeStatusBlock = (blockId) => {
    $.ajax({
        type: 'PUT',
        url: ApiConstant.API_HEAD_OFFICE_BLOCK + `/status/${blockId}`,
        success: function (response) {
            if (response) {
                showToastSuccess(response?.message);
                getDetailSemester(currentSemesterId);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi thay đổi trạng thái block');
        }
    })
}

const confirmChangeStatusBlock = (blockId) => {
    swal({
        title: "Xác nhận thay đổi trạng thái?",
        text: "Bạn chắn muốn thay đổi trạng thái block này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Thay đổi",
                className: "btn btn-black",
            },
        },
    }).then((willDelete) => {
        if (willDelete) {
            changeStatusBlock(blockId);
        }
    });
}

const submitSemesterForm = () => {
    if ($('#semesterId').val() === '') {
        confirmCreateSemester();
    }
}

const submitUpdateSemesterForm = () => {
    confirmUpdateSemester();
}

const submitBlockForm = () => {
    if ($('#blockId').val() === '') {
        confirmCreateBlock();
    } else {
        confirmUpdateBlock();
    }
}

const openModalAddSemester = () => {
    resetFormInputSemester();
    resetFormErrorSemester();
    resetFormSemester();
};

const openModalAddBlock = () => {
    resetFormInputBlock();
    resetFormErrorBlock();
    resetFormBlock();
}

const resetFormInputSemester = () => {
    $('#semesterId').val('');
    $('#modifySemesterName').val('SPRING');
    $('#modifySemesterYear').val('');
    $('#modifyStartTime').val('');
};

const resetFormErrorSemester = () => {
    $('#semesterNameError').text('');
    $('#semesterYearError').text('');
    $('#startTimeError').text('');
    $('.form-control').removeClass('is-invalid');
};

const resetFormSemester = () => {
    $('#semesterModalLabel').text('Thêm học kỳ');
    $('#semesterModal').modal('show');
}

const resetFormInputBlock = () => {
    $('#blockId').val('');
    $('#modifyBlockName').val('BLOCK_1');
    $('#modifyStartTimeBlock').val('');
    $('#modifyEndTimeBlock').val('');
}

const resetFormErrorBlock = () => {
    $('#blockNameError').text('');
    $('#startTimeBlockError').text('');
    $('#endTimeBlockError').text('');
    $('.form-control').removeClass('is-invalid');
}

const resetFormBlock = () => {
    $('#blockModalLabel').text('Thêm block');
    $('#blockModal').modal('show');
}

