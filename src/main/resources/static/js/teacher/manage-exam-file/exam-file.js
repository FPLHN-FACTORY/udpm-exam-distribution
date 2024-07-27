$(document).ready(function () {

    fetchSearchSubject();

    onChangePageSize();

    //add event for form search
    $('#findSubject').on("keyup", debounce(() => {
        fetchSearchSubject(1,
            $('#pageSize').val(),
            getGlobalParamsSearch());
    }));

    $('#subjectType').on("change", debounce(() => {
        fetchSearchSubject(1,
            $('#pageSize').val(),
            getGlobalParamsSearch());
    }));

    $('#pageSize').on("change", debounce(() => {
        fetchSearchSubject(1,
            $('#pageSize').val(),
            getGlobalParamsSearch());
    }));

});

//START: Exam Distribution Information
const examDistributionInfor = getExamDistributionInfo();
//END: Exam Distribution Information

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

const fetchSearchSubject = (
    page = 1,
    size = $('#pageSize').val(),
    paramSearch = {
        findSubject: "",
        subjectType: ""
    },
) => {
    const params = {
        page: page,
        size: size,
        findSubject: paramSearch.findSubject,
        subjectType: paramSearch.subjectType
    };

    let url = ApiConstant.API_TEACHER_EXAM_FILE + "/subject?";

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
            const responseData = responseBody?.data?.data;
            if (responseData.length === 0) {
                $('#subjectTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const subjects = responseData.map(subject => {
                return `<tr>
                            <td>${subject.orderNumber}</td>
                            <td>${subject.subjectCode}</td>
                            <td>${subject.subjectName}</td>
                            <td>${subject.departmentName}</td>
                            <td>${getStatusType(subject.subjectType)}</td>
                            <td>${subject.uploaded}/${subject.maxUpload}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                               ${subject.uploaded >= subject.maxUpload ? '' : `
                                <a href="/teacher/exam-file/subject/${subject.id}"
                                   data-bs-toggle="tooltip" 
                                   data-bs-title="Upload đề"
                                   class="fs-4 text-dark text-decoration-none">
                                    <i class="fa-solid fa-upload"
                                       style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </a>`}
                                <span onclick="handleDetailSampleExam('${subject.id}')" 
                                    data-bs-toggle="tooltip" 
                                    data-bs-title="Xem đề mẫu"
                                    class="fs-4">
                                <i class="fa-solid fa-eye"
                                   style="cursor: pointer; margin-left: 10px;"
                                ></i>
                            </span>
                            <span onclick="handleDownloadSampleExamPaper('${subject.id}')" 
                                   data-bs-toggle="tooltip" 
                                    data-bs-title="Download đề mẫu"
                                    class="fs-4">
                                <i class="fa-solid fa-download"
                                   style="cursor: pointer; margin-left: 10px;"
                                ></i>
                            </span>
                            <i ></i>
                            </td>
                        </tr>`;
            });
            $('#subjectTableBody').html(subjects);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page)
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học');
        }
    });
};


const getGlobalParamsSearch = () => {
    return {
        findSubject: $("#findSubject").val(),
        subjectType: $("#subjectType").val(),
        staffId: examDistributionInfor.userId
    }
}

const resetGlobalParamsSearch = () => {
    $("#subjectType").val("");
    $("#findSubject").val("");
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
    fetchSearchSubject(page, $('#pageSize').val(), getGlobalParamsSearch());
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


