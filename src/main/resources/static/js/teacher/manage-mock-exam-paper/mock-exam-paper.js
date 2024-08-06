$(document).ready(function () {

    handleFetch();


//     add event for form search
    $('#subjectType').on('change', debounce(() => {
        handleChange();
    }))

    $('#semesterFind').on('change', debounce(() => {
        handleChange();
    }))

    $('#BlockFind').on('change', debounce(() => {
        handleChange();
    }))

    $('#subjectAndDepartment').on('keyup', debounce(() => {
        handleChange();
    }))

    $('#pageSize').on("change", debounce(() => {
        handleChange();
    }));

});

function handleChange() {
    fetchSearchSubject(1,
        $('#pageSize').val(),
        {
            subjectAndDepartment: $('#subjectAndDepartment').val()?.trim(),
            subjectType: $('#subjectType').val()?.trim(),
            semester: $('#semesterFind').val()?.trim(),
            block: $('#BlockFind').val()?.trim()
        });
}

const handleFetch = async () => {
    const recentlySemester = await fetchSemester();

    fetchSearchSubject(1,
        $('#pageSize').val(),
        {
            subjectAndDepartment: '',
            subjectType: '',
            semester: recentlySemester ? recentlySemester : '',
            block: $('#BlockFind').val()?.trim()
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
        semester: '',
        block: $('#BlockFind').val()?.trim()
    }
) => {
    const params = {
        page: page,
        size: size,
        subjectAndDepartment: paramSearch.subjectAndDepartment,
        subjectType: paramSearch.subjectType,
        semester: paramSearch.semester,
        block: paramSearch.block
    };

    let url = ApiConstant.API_TEACHER_MOCK_EXAM_PAPER + "/subject?";

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);
    showLoading();
    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const responseData = responseBody?.data?.content;
            hideLoading();
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
                <td>${subject.semesterName}</td>
                <td>${subject.blockName}</td>
                <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                    <span onclick="handleOpenModalMockExam('${subject.id}','${subject.subjectName}','${subject.blockId}')" 
                       data-bs-toggle="tooltip" 
                        data-bs-title="Đề thi thử"
                        class="fs-4"> 
                        <i class="fa-solid fa-receipt"
                           style="cursor: pointer; margin-left: 10px;"
                        ></i>
                    </span>
                    ${subject.isCurrentBlock ?
                    subject.isExistsPracticeRoom ? `
                        <span onclick="handleOpenModalPracticeRoomDetail('${subject.practiceRoomId}')" 
                           data-bs-toggle="tooltip"
                            data-bs-title="Thông tin phòng thi thử"
                            class="fs-4"> 
                            <i class="fas fas fa-eye"
                               style="cursor: pointer; margin-left: 10px;"
                            ></i>
                        </span>`
                        :
                        `<span onclick="handleOpenModalPracticeRoom('${subject.id}')" 
                           data-bs-toggle="tooltip" 
                            data-bs-title="Tạo phòng thi thử"
                            class="fs-4"> 
                            <i class="fa-solid fa-landmark"
                               style="cursor: pointer; margin-left: 10px;"
                            ></i>
                        </span>`
                    :
                    ''}
                </td>
            </tr>`;
            });
            $('#subjectTableBody').html(subjects);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            hideLoading();
            let mess = error?.responseJSON?.message
                ? error?.responseJSON?.message : 'Có lỗi xảy ra khi lấy dữ liệu môn học';
            showToastError(mess);
        }
    });
};

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

const fetchBlock = (semesterId) => {
    let url = ApiConstant.API_TEACHER_MOCK_EXAM_PAPER + "/block?id=" + semesterId;

    $.ajax({
        type: "GET",
        url: url,
        success: function (response) {
            $('#semesterFind').html(semesters);
        },
        error: function (error) {
        }
    });

}

const getGlobalParamsSearch = () => {
    return {
        subjectCode: $("#subjectCode").val(),
        subjectName: $("#subjectName").val(),
        staffId: examDistributionInfor.userId
    }
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
};

const changePage = (page) => {
    fetchSearchSubject(page,
        $('#pageSize').val(),
        {
            subjectAndDepartment: $('#subjectAndDepartment').val()?.trim(),
            subjectType: $('#subjectType').val()?.trim(),
            semester: $('#semesterFind').val()?.trim(),
            block: $('#BlockFind').val()?.trim()
        });
};

const handleSearchSubject = () => {
    fetchSearchSubject(1, $('#pageSize').val(), getGlobalParamsSearch());
};

const handleClearSearch = () => {
    resetGlobalParamsSearch();
    fetchSearchSubject();
};


