$(document).ready(function () {

    getSemesters();

    setupDate();

    setupDateFormSearch();

    handleAddEvent($('#semesterName'), 'keyup', function () {
        getSemesters(1);
    })

    handleAddEvent($('#startEndDateSemester'), 'change', function () {
        getSemesters(1);
    })

    handleAddEvent($('#startEndDateBlock'), 'change', function () {
        getSemesters(1);
    })

    $('#modifySemesterButton').on('click', function () {
        submitSemesterForm();
    });

});

let currentSemesterId = null;

const getSemesters = (
    page = 1
) => {

    const startEndDateSemester = $('#startEndDateSemester').val()?.trim();
    let startDateStringSemester = startEndDateSemester.substring(0, 10);
    let endDateStringSemester = startEndDateSemester.substring(12);
    let startDateSemester = null;
    let endDateSemester = null;
    if (startEndDateSemester !== '') {
        startDateSemester = moment(startDateStringSemester, "DD/MM/YYYY").toDate().getTime();
        endDateSemester = moment(endDateStringSemester, "DD/MM/YYYY").toDate().getTime();
    }

    const startEndDateBlock = $('#startEndDateBlock').val()?.trim();
    let startDateStringBlock = startEndDateSemester.substring(0, 10);
    let endDateStringBlock = startEndDateSemester.substring(12);
    let startDateBlock = null;
    let endDateBlock = null;
    if (startEndDateBlock !== '') {
        startDateBlock = moment(startDateStringBlock, "DD/MM/YYYY").toDate().getTime();
        endDateBlock = moment(endDateStringBlock, "DD/MM/YYYY").toDate().getTime();
    }

    const params = {
        page: page,
        size: $('#pageSize').val(),
        semesterName: $('#semesterName').val(),
        startDateSemester: startDateSemester,
        endDateSemester: endDateSemester,
        startDateBlock:startDateBlock,
        endDateBlock: endDateBlock,
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
            const semesters = response?.data?.content?.map((semester, index) => {
                return `
                    <tr>
                        <td>${index + 1 + response?.data?.pageable?.offset}</td>
                        <td>${semester.semesterName}</td>
                        <td>${formatTimestampToDate(semester.startTime)}</td>
                        <td>${formatTimestampToDate(semester.endTime)}</td>
                        <td>${formatTimestampToDate(semester.startTimeBlock1)} - ${formatTimestampToDate(semester.endTimeBlock1)}</td>
                        <td>${formatTimestampToDate(semester.startTimeBlock2)} - ${formatTimestampToDate(semester.endTimeBlock2)}</td>
                        <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            <span class="fs-4"
                                    data-bs-toggle="tooltip"
                                    data-bs-title="Cập nhật">
                                <i onclick="getDetailSemester('${semester.semesterId}')"
                                class="fa-solid fa-pen-to-square" style="cursor: pointer; margin-left: 10px;"></i>
                            </span>
                        </td>
                    </tr>
                `;
            });
            $('#semesterTableBody').html(semesters);
            const totalPages = response?.data?.totalPages ? response?.data?.totalPages : 1;
            createPagination(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu học kỳ');
        }
    })
};

const changePage = (page) => {
    getSemesters(page);
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

const setupDate = () => {
    const time = $('#startEndDate');
    time.daterangepicker({
        opens: 'center',
        locale: {
            format: 'DD/MM/YYYY'
        },
        autoUpdateInput: false
    });

    time.on('apply.daterangepicker', (ev, picker) => {
        // Kiểm tra khoảng cách giữa startDate và endDate
        const startDate = picker.startDate;
        const endDate = picker.endDate;

        // Tính khoảng cách giữa hai ngày theo đơn vị tháng
        const monthsDifference = endDate.diff(startDate, 'months');

        if (monthsDifference < 3) {
            // Nếu khoảng cách dưới 2 tháng, gán value trống cho time
            time.val('');
            showToastError("Học kỳ phải trên 3 tháng!");
        } else {
            // Nếu khoảng cách từ 2 tháng trở lên, gán giá trị bình thường
            time.val(startDate.format('DD/MM/YYYY') + ' ⇀ ' + endDate.format('DD/MM/YYYY'));
        }
        $('#modifyStartTimeBlock1').val(picker.startDate.format('DD/MM/YYYY'));
        $('#modifyEndTimeBlock2').val(picker.endDate.format('DD/MM/YYYY'));
    });

    time.on('cancel.daterangepicker', (ev, picker) => {
        time.val('');
        $('#modifyStartTimeBlock1').val('');
        $('#modifyEndTimeBlock2').val('');
    });

    //setup thời gian cho end date block 1
    const endTimeInput = $('#modifyEndTimeBlock1');

    // Thiết lập daterangepicker cho input
    endTimeInput.daterangepicker({
        singleDatePicker: true, // Chỉ chọn một ngày
        showDropdowns: true,
        locale: {
            format: 'DD/MM/YYYY'
        },
        autoUpdateInput: false
    });

    endTimeInput.on('apply.daterangepicker', (ev, picker) => {
        const startEndDate = $('#startEndDate').val()?.trim();
        let startDateString = startEndDate.substring(0, 10);
        let endDateString = startEndDate.substring(12);

        let startDate = null;
        let endDate = null;

        if (startEndDate !== '') {
            startDate = moment(startDateString, "DD/MM/YYYY").add(1, 'months');  // Tăng startDate lên 1 tháng
            endDate = moment(endDateString, "DD/MM/YYYY").subtract(1, 'months');  // Giảm endDate đi 1 tháng
        }

        const selectedDate = picker.startDate;

        // Kiểm tra xem selectedDate có nằm trong khoảng từ startDate đến endDate hay không
        if (selectedDate.isBetween(startDate, endDate, null, '[]')) {
            // Nếu có, gán giá trị cho endTimeInput
            endTimeInput.val(selectedDate.format('DD/MM/YYYY'));

            // Tăng ngày lên một ngày
            const nextDay = selectedDate.add(1, 'days');
            $('#modifyStartTimeBlock2').val(nextDay.format('DD/MM/YYYY'));
        } else {
            // Nếu không, gán giá trị trống cho endTimeInput
            showToastError("Thời gian kết thúc block 1 phải nằm trong khoảng 1 đến 2 tháng giữa học kỳ")
            endTimeInput.val('');
            $('#modifyStartTimeBlock2').val('');
        }
    });


    endTimeInput.on('cancel.daterangepicker', (ev, picker) => {
        endTimeInput.val('');
        $('#modifyStartTimeBlock2').val('');
    });
};

const setupDateFormSearch = () => {
    const timeSemester = $('#startEndDateSemester');
    timeSemester.daterangepicker({
        opens: 'center',
        locale: {
            format: 'DD/MM/YYYY'
        },
        autoUpdateInput: false
    });

    timeSemester.on('apply.daterangepicker', (ev, picker) => {
        timeSemester.val(picker.startDate.format('DD/MM/YYYY') + ' ⇀ ' + picker.endDate.format('DD/MM/YYYY'));
        getSemesters(1);
    });

    timeSemester.on('cancel.daterangepicker', (ev, picker) => {
        timeSemester.val('');
        getSemesters(1);
    });

    timeSemester.on('change', function (e) {
        e.preventDefault();
        getSemesters(1);
    });

    const timeBlock = $('#startEndDateBlock');
    timeBlock.daterangepicker({
        opens: 'center',
        locale: {
            format: 'DD/MM/YYYY'
        },
        autoUpdateInput: false
    });

    timeBlock.on('apply.daterangepicker', (ev, picker) => {
        timeBlock.val(picker.startDate.format('DD/MM/YYYY') + ' ⇀ ' + picker.endDate.format('DD/MM/YYYY'));
        getSemesters(1);
    });

    timeBlock.on('cancel.daterangepicker', (ev, picker) => {
        timeBlock.val('');
        getSemesters(1);
    });
    timeBlock.on('change', function (e) {
        e.preventDefault();
        getSemesters(1);
    });
};

const createSemester = (semesterId = '') => {
    const startEndDate = $('#startEndDate').val()?.trim();
    let startDateString = startEndDate.substring(0, 10);
    let endDateString = startEndDate.substring(12);
    let startDate = null;
    let endDate = null;
    if (startEndDate !== '') {
        startDate = moment(startDateString, "DD/MM/YYYY").toDate().getTime();
        endDate = moment(endDateString, "DD/MM/YYYY").toDate().getTime();
    }
    const endDateBlock1 = $('#modifyEndTimeBlock1').val()?.trim();
    let endTimeBlock1 = null;
    if (endDateBlock1 !== '') {
        endTimeBlock1 = moment(endDateBlock1, "DD/MM/YYYY").toDate().getTime();
    }
    const semesterName = $('#modifySemesterName').val();

    let type = semesterId === '' ? 'post' : 'put';
    let data = {
        semesterName: semesterName,
        startTime: startDate,
        endTime: endDate,
        endTimeBlock1: new Date(endTimeBlock1).getTime()
    };

    if (type === 'put') {
        data.semesterId = semesterId;
    }
    showLoading();
    $.ajax({
        type: type,
        url: ApiConstant.API_HEAD_OFFICE_SEMESTER,
        data: JSON.stringify(data),
        contentType: 'application/json',
        success: function (response) {
            if (response) {
                showToastSuccess(response?.message);
                getSemesters();
                $('#semesterModal').modal('hide');
            }
            hideLoading();
        },
        error: function (error) {
            $('.form-control').removeClass('is-invalid');
            if (error?.responseJSON?.length > 0) {
                error.responseJSON.forEach(err => {
                    $(`#${err.fieldError}Error`).text(err.message);
                });
            } else if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message)
            } else {
                showToastError('Có lỗi xảy ra khi thêm học kỳ');
            }
            hideLoading();
        }
    })
};

const confirmUpdateSemester = (semesterId) => {
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
                text: "Sửa",
                className: "btn btn-black",
            },
        },
    }).then((willCreate) => {
        if (willCreate) {
            createSemester(semesterId);
        }
    });
}

const confirmCreateSemester = () => {
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
    $('#semesterId').val(semesterId);
    $.ajax({
        type: 'GET',
        url: ApiConstant.API_HEAD_OFFICE_SEMESTER + `/${semesterId}`,
        success: function (response) {
            if (response?.data) {
                const semester = response?.data;
                $('#modifySemesterName').val(semester.semesterName);
                $('#startEndDate').val(formatTimestampToDate(semester.startTime) + ' ⇀ ' + formatTimestampToDate(semester.endTime));
                $('#modifyStartTimeBlock1').val(formatTimestampToDate(semester.startTimeBlock1));
                $('#modifyEndTimeBlock1').val(formatTimestampToDate(semester.endTimeBlock1));
                $('#modifyStartTimeBlock2').val(formatTimestampToDate(semester.startTimeBlock2));
                $('#modifyEndTimeBlock2').val(formatTimestampToDate(semester.endTimeBlock2));
                $('#semesterModalLabel').text('Chỉnh sửa học kỳ');
                $('#semesterModal').modal('show');
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy thông tin học kỳ');
        }
    })
}

const submitSemesterForm = () => {
    let semesterId = $('#semesterId').val();
    if (semesterId === '') {
        confirmCreateSemester();
    } else {
        confirmUpdateSemester(semesterId?.trim());
    }
}

const openModalAddSemester = () => {
    resetFormInputSemester();
    resetFormErrorSemester();
    resetFormSemester();
};

const resetFormInputSemester = () => {
    $('#semesterId').val('');
    $('#modifySemesterName').val('SPRING');
    $('#startEndDate').val('');
    $('#modifyStartTimeBlock1').val('');
    $('#modifyEndTimeBlock1').val('');
    $('#modifyStartTimeBlock2').val('');
    $('#modifyEndTimeBlock2').val('');
};

const resetFormErrorSemester = () => {
    $('#semesterNameError').text('');
    $('#endTimeBlock1Error').text('');
    $('#startTimeError').text('');
    $('#endTimeError').text('');
    $('.form-control').removeClass('is-invalid');
};

const resetFormSemester = () => {
    $('#semesterModalLabel').text('Thêm học kỳ');
    $('#semesterModal').modal('show');
}