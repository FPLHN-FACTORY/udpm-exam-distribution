$(document).ready(function () {

    handleFetch();

    onChangePageSize();

//     add event for form search
    $('#subjectType').on('change', debounce(() => {
        fetchSearchSubject(1,
            $('#pageSize').val(),
            {
                subjectAndDepartment: $('#subjectAndDepartment').val()?.trim(),
                subjectType: $('#subjectType').val()?.trim(),
                subjectStatus: $('#subjectStatus').val()?.trim(),
                semester: $('#semesterFind').val()?.trim()
            });
    }))

    $('#subjectStatus').on('change', debounce(() => {
        fetchSearchSubject(1,
            $('#pageSize').val(),
            {
                subjectAndDepartment: $('#subjectAndDepartment').val()?.trim(),
                subjectType: $('#subjectType').val()?.trim(),
                subjectStatus: $('#subjectStatus').val()?.trim(),
                semester: $('#semesterFind').val()?.trim()
            });
    }))

    $('#semesterFind').on('change', debounce(() => {
        fetchSearchSubject(1,
            $('#pageSize').val(),
            {
                subjectAndDepartment: $('#subjectAndDepartment').val()?.trim(),
                subjectType: $('#subjectType').val()?.trim(),
                subjectStatus: $('#subjectStatus').val()?.trim(),
                semester: $('#semesterFind').val()?.trim()
            });
    }))

    $('#subjectAndDepartment').on('keyup', debounce(() => {
        fetchSearchSubject(1,
            $('#pageSize').val(),
            {
                subjectAndDepartment: $('#subjectAndDepartment').val()?.trim(),
                subjectType: $('#subjectType').val()?.trim(),
                subjectStatus: $('#subjectStatus').val()?.trim(),
                semester: $('#semesterFind').val()?.trim()
            });
    }))

    $('#pageSize').on("change", debounce(() => {
        fetchSearchSubject(1,
            $('#pageSize').val(),
            {
                subjectAndDepartment: $('#subjectAndDepartment').val()?.trim(),
                subjectType: $('#subjectType').val()?.trim(),
                subjectStatus: $('#subjectStatus').val()?.trim(),
                semester: $('#semesterFind').val()?.trim()
            });
    }));

});

const handleFetch = async () => {
    const recentlySemester = await fetchSemester();
    fetchSearchSubject(1,
        $('#pageSize').val(),
        {
            subjectAndDepartment: '',
            subjectType: '',
            subjectStatus: '',
            semester: recentlySemester ? recentlySemester : ''
        });
}

//START: Exam Distribution Information
const examDistributionInfor = getExamDistributionInfo();
//END: Exam Distribution Information
//start state
let semesterId = '';
//end state
const fetchSearchSubject = (
    page = 1,
    size = $('#pageSize').val(),
    paramSearch = {
        subjectAndDepartment: '',
        subjectType: '',
        subjectStatus: '',
        semester: ''
    }
) => {
    const params = {
        page: page,
        size: size,
        subjectAndDepartment: paramSearch.subjectAndDepartment,
        subjectType: paramSearch.subjectType,
        subjectStatus: paramSearch.subjectStatus,
        semester: paramSearch.semester
    };

    let url = ApiConstant.API_TEACHER_MOCK_EXAM_PAPER + "/subject?";

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const responseData = responseBody?.data?.content;
            if (responseData.length === 0) {
                $('#subjectTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
$('#pagination').empty();
                return;
            }
            const subjects = responseData.map(function (subject, index) {
                return `<tr>
                            <td>${index + 1 + responseBody?.data?.pageable?.offset}</td>  
                           <td>${subject.subjectName}</td>
                           <td>${subject.departmentName}</td>
                            <td>${getStatusType(subject.subjectType)}</td>
                            <td>${getStatusBadge(subject.subjectStatus)}</td>
                            <td>${subject.semesterName}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span onclick="handleOpenModalMockExam('${subject.id}','${subject.subjectName}')" class="fs-4">

                                    <i  class="fa-solid fa-receipt"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#subjectTableBody').html(subjects);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học');
        }
    });
};

function getStatusBadge(status) {
    switch (status) {
        case 'MO':
            return '<span class="tag tag-success">Mở</span>';
        case 'DANG_DANG_KY':
            return '<span class="tag tag-secondary">Đang đăng ký</span>';
        case 'DONG':
            return '<span class="tag tag-danger">Đóng</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}

function getStatusType(type) {
    switch (type) {
        case 'TRADITIONAL':
            return '<span class="tag tag-purple">Truyền thống</span>';
        case 'ONLINE':
            return '<span class="tag tag-success">Online</span>';
        case 'BLEND':
            return '<span class="tag tag-primary">Truyền thống kết hợp Online</span>';
        case 'TRUC_TUYEN':
            return '<span class="tag tag-warning">Trực tuyến</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}

const fetchSemester = async () => {
    let url = ApiConstant.API_TEACHER_MOCK_EXAM_PAPER + "/semester";

    let recentlySemesterId = '';

    try {
        const response = await new Promise((resolve, reject) => {
            $.ajax({
                type: "GET",
                url: url,
                success: function (response) {
                    resolve(response);
                },
                error: function (error) {
                    reject(error);
                }
            });
        });

        const semesters = response?.data?.map((s, index) => {
            if (index === 0) {
                recentlySemesterId = s.id;
            }
            return `<option value="${s.id}">${s.name}</option>`;
        }).join('');

        $('#semesterFind').html(semesters);
    } catch (error) {
        showToastError('Có lỗi xảy ra khi lấy dữ liệu học kì');
        console.error(error);
    }

    return recentlySemesterId;
};

const getGlobalParamsSearch = () => {
    return {
        subjectCode: $("#subjectCode").val(),
        subjectName: $("#subjectName").val(),
        staffId: examDistributionInfor.userId
    }
}

const resetGlobalParamsSearch = () => {
    $("#subjectCode").val("");
    $("#subjectName").val("");
};

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
};

const changePage = (page) => {
    fetchSearchSubject(page,
        $('#pageSize').val(),
        {
            subjectAndDepartment: $('#subjectAndDepartment').val()?.trim(),
            subjectType: $('#subjectType').val()?.trim(),
            subjectStatus: $('#subjectStatus').val()?.trim(),
            semester: $('#semesterFind').val()?.trim()
        });
};

const onChangePageSize = () => {
    $("#pageSize").on("change", function () {
        resetGlobalParamsSearch();
        fetchSearchSubject(1, $('#pageSize').val(), getGlobalParamsSearch());
    });
};

const handleSearchSubject = () => {
    fetchSearchSubject(1, $('#pageSize').val(), getGlobalParamsSearch());
};

const handleClearSearch = () => {
    resetGlobalParamsSearch();
    fetchSearchSubject();
};


