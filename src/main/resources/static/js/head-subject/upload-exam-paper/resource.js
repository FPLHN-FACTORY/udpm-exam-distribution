let stateExamPaperResourceId = "";

const getStateExamPaperResourceId = () => stateExamPaperResourceId;

const setStateExamPaperResourceId = (value) => {
    stateExamPaperResourceId = value;
};

const handleModalModalResource = (examPaperId) => {
    setStateExamPaperResourceId(examPaperId);

    fetchListResource();

    $("#resourceTableModal").modal("show");
};

const fetchListResource = (
    page = 1,
) => {

    showLoading();

    const params = {
        page: page,
        size: $('#pageSizeResource').val(),
        examPaperId: getStateExamPaperResourceId()
    };

    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/resources" + '?';

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    $.ajax({
        url: url,
        method: "GET",
        success: function (responseBody) {
            hideLoading();
            const responseData = responseBody?.data?.data;
            if (responseData.length === 0) {
                $('#resourceTableBody').html(`
                    <tr>
                         <td colspan="2" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const examPapers = responseData.map(item => {
                return `<tr>
                            <td>${item.orderNumber}</td>
                            <td>
                                <a target="_blank" href='${item.resource}'>${item.resource}</a>
                            </td>
                        </tr>`;
            });
            $('#resourceTableBody').html(examPapers);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationResource(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            const messageErr = error?.responseJSON?.message;
            if (messageErr) {
                showToastError(messageErr);
            } else {
                showToastError("Có lỗi xảy ra");
            }
            hideLoading();
        }
    });
};

const createPaginationResource = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePageResource(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePageResource(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePageResource(${currentPage + 1})">
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

    $('#paginationResource').html(paginationHtml);
};

const changePageResource = (page) => {
    fetchListResource(page);
};

$("#pageSizeResource").on("click", () => {
   fetchListResource();
});