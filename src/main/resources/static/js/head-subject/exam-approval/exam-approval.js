$(document).ready(function () {

    fetchSubject();

    getExamPapers();

    //add event for form search
    $("#subject-find").on("change", debounce(() => {
        getExamPapers(1, $('#pageSize').val(),$("#subject-find").val(),$("#staff-upload-find").val().trim());
    }));

    $("#staff-upload-find").on("keyup", debounce(() => {
        getExamPapers(1, $('#pageSize').val(),$("#subject-find").val(),$("#staff-upload-find").val().trim());
    }));

    $('#pageSize').on("change", () => {
        getExamPapers(1, $('#pageSize').val(),$("#subject-find").val(),$("#staff-upload-find").val().trim());
    });

});

//START: Exam Distribution Information
const examDistributionInfor = getExamDistributionInfo();

//END: Exam Distribution Information

function changePage(newCurrentPage) {
    getExamPapers(newCurrentPage, $('#pageSize').val(),$("#subject-find").val(),$("#staff-upload-find").val().trim());
}

function clearFormSearch() {

    $("#subject-find").val('');

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
            paginationHtml += `<li class="page-item active"><a href="#" class="page-link text-white">${i}</a></li>`;
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



function getStatusBadge(status) {
    switch (status) {
        case 'MOCK_EXAM_PAPER':
            return '<span class="tag tag-purple">Đề thi thử</span>';
        case 'OFFICIAL_EXAM_PAPER':
            return '<span class="tag tag-success">Đề thi thật</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}

function getExamPapers(
    page = 1,
    size = $('#pageSize').val(),
    idSubject = null,
    staffUploadCode = null
) {
    const params = {
        page: page,
        size: size,
        idSubject: idSubject,
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
                         <td colspan="6" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const examPapers = responseBody?.data?.content?.map((exam, index) => {
                return `<tr>
                            <td>${index + 1 + responseBody?.data?.pageable?.offset}</td>
                            <td>${exam.examPaperCode}</td>
                            <td>${exam.subjectName}</td>
                            <td>${exam.staffUpload}</td>
                            <td>${formatDateTime(exam.createdExamPaperDate)}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                            <span data-bs-toggle="tooltip" data-bs-title="Phê duyệt đề thi"> 
                                <i
                                    onclick="handleOpenModalExamApproval('${exam.id}')"
                                    class="fs-5 fa-solid fa-square-check"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                            </span>
                            <span data-bs-toggle="tooltip" data-bs-title="Từ chối phê duyệt">
                                <i
                                    onclick="handleRejectedApproval('${exam.id}')"
                                    class="fs-5 fa-solid fa-square-xmark"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                            </span>
                            <span data-bs-toggle="tooltip" data-bs-title="Chi tiết đề thi">
                                <i
                                    onclick="handleDetail('${exam.path}')"
                                    class="fs-5 fas fas fa-eye"
                                    style="cursor: pointer; margin-left: 10px;"
                                ></i>
                            </span>
                            </td>
                        </tr>`;
            });
            $('#examPaperTableBody').html(examPapers);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu đề thi');
        }
    });
}