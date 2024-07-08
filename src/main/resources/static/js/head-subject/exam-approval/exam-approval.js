$(document).ready(function () {

    fetchSubject();

    getExamPapers();

    //add event for form search
    $("#subject-find").on("change", debounce(() => {
        getExamPapers(1, $('#pageSize').val(),$("#subject-find").val(),$("#exam-type-find").val(),$("#staff-upload-find").val().trim());
    }));

    $("#exam-type-find").on("change", debounce(() => {
        getExamPapers(1, $('#pageSize').val(),$("#subject-find").val(),$("#exam-type-find").val(),$("#staff-upload-find").val().trim());
    }));

    $("#staff-upload-find").on("keyup", debounce(() => {
        getExamPapers(1, $('#pageSize').val(),$("#subject-find").val(),$("#exam-type-find").val(),$("#staff-upload-find").val().trim());
    }));

    $('#pageSize').on("change", () => {
        getExamPapers(1, $('#pageSize').val(),$("#subject-find").val(),$("#exam-type-find").val(),$("#staff-upload-find").val().trim());
    });

});

//START: Exam Distribution Information
const examDistributionInfor = getExamDistributionInfo();

//END: Exam Distribution Information

function changePage(newCurrentPage) {
    getExamPapers(newCurrentPage, $('#pageSize').val(),$("#subject-find").val(),$("#exam-type-find").val(),$("#staff-upload-find").val().trim());
}

function clearFormSearch() {

    $("#subject-find").val('');

    $("#exam-type-find").val('');

    $("#staff-upload-find").val('');

}

function fetchSubject() {
    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL + "/subject/" + examDistributionInfor.departmentFacilityId + '?staffId=' + examDistributionInfor.userId;

    $.ajax({
        type: "GET",
        url: url,
        success: function (responseBody) {
            const responseData = responseBody?.data;
            if (responseData.length !== 0) {
                const subject = responseData?.map((subject) => {
                    return `<option value="${subject.subjectId}">${subject.subjectName}</option>`
                });
                subject.unshift('<option value="">-- Chọn môn học --</option>');
                $('#subject-find').html(subject);
            }
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học');
        }
    });
}

const createPagination = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" onclick="changePage(${currentPage - 1})">
                            Trước
                        </a>
                     </li>`;
    } else {
        paginationHtml += `
                <li class="page-item disabled">
                    <a class="page-link">
                        Trước
                    </a>
                </li>
        `;
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<li class="page-item active"><a class="page-link text-white">${i}</a></li>`;
        } else {
            paginationHtml += `<li class="page-item"><a class="page-link" onclick="changePage(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" onclick="changePage(${currentPage + 1})">
                          Sau
                    </a>
                </li>
`;
    } else {
        paginationHtml += `
            <li class="page-item disabled">
                <a class="page-link">
                    Sau
                </a>
            </li>
`;
    }

    $('#pagination').html(paginationHtml);
}

function formatDateTime(date) {
    const d = new Date(Number(date));
    const day = String(d.getDate()).padStart(2, "0");
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const year = d.getFullYear();
    const hours = String(d.getHours()).padStart(2, "0");
    const minutes = String(d.getMinutes()).padStart(2, "0");
    return `${day}/${month}/${year} ${hours}:${minutes}`;
}

function getStatusBadge(status) {
    switch (status) {
        case 'MOCK_EXAM_PAPER':
            return '<span class="tag tag-success">Đề thi thử</span>';
        case 'OFFICIAL_EXAM_PAPER':
            return '<span class="tag tag-danger">Đề thi thật</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}

function getExamPapers(
    page = 1,
    size = $('#pageSize').val(),
    idSubject = null,
    examPaperType = null,
    staffUploadCode = null
) {
    const params = {
        page: page,
        size: size,
        idSubject: idSubject,
        examPaperType: examPaperType,
        staffUploadCode: staffUploadCode
    };

    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL + '?';

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
            if (responseBody?.data?.content?.length === 0) {
                $('#examPaperTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const examPapers = responseBody?.data?.content?.map((exam, index) => {
                return `<tr>
                            <td>${index + 1 + responseBody?.data?.pageable?.offset}</td>
                            <td>${exam.examPaperCode}</td>
                            <td>${getStatusBadge(exam.examPaperType)}</td>
                            <td>${exam.subjectName}</td>
                            <td>${exam.staffUpload}</td>
                            <td>${formatDateTime(exam.createdExamPaperDate)}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                           <a> 
                                <i
                                    onclick="handleApproval('${exam.id}')"
                                    class="fa-solid fa-square-check"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                                 <a>
                                <i
                                    onclick="handleDelete('${exam.id}','${exam.name}')"
                                    class="fas fa-trash-alt"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                                <a>
                                <i
                                    onclick="handleDetail('${exam.path}')"
                                    class="fas fas fa-eye"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                                </a>
                            </td>
                        </tr>`;
            });
            $('#examPaperTableBody').html(examPapers);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu đề thi');
        }
    });
}