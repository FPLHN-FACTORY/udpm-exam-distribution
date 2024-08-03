$(document).ready(function () {
    fetchListExamRule();
    onChangePageSize();
});

//START: Exam Distribution Information
const examDistributionInfor = getExamDistributionInfo();
//END: Exam Distribution Information

const getGlobalParamsSearch = () => {
    return {
        valueSearch: $("#valueSearch").val()
    }
}

const resetGlobalParamsSearch = () => {
    $("#valueSearch").val("");
};

const fetchListExamRule = (
    page = 1,
) => {
    const params = {
        page: page,
        size: $('#pageSize').val(),
        valueSearch: getGlobalParamsSearch().valueSearch
    };

    let url = ApiConstant.API_HEAD_OFFICE_EXAM_RULE + "/exam-rule" + '?';

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
            hideLoading();
            const responseData = responseBody?.data?.data;
            if (responseData.length === 0) {
                $('#examRuleTableBody').html(`
                    <tr>
                         <td colspan="4" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#pagination').empty();
                return;
            }
            const examRules = responseData.map(examRule => {
                return `<tr>
                            <td>${examRule.orderNumber}</td>
                            <td>${examRule.name}</td>
                            <td>${convertStatus(examRule.status)}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span data-bs-toggle="tooltip" data-bs-title="Tải quy định"
                                onclick="handleOpenModalExamRule(2,'${examRule.id}','${examRule.name}')" class="fs-6">
                                    <i 
                                        class="fa-solid fa-pen-to-square"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span data-bs-toggle="tooltip" data-bs-title="Chi tiết quy định" onclick="handleOpenModalDetailExamRule('${examRule.id}')" class="fs-6">
                                    <i 
                                        class="fa-solid fa-eye"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#examRuleTableBody').html(examRules);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPagination(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học');
            hideLoading();
        }
    });
};

//------------------------------------------------------function--------------------------------------------------------

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
    fetchListExamRule(page);
};

const onChangePageSize = () => {
    $("#pageSize").on("change", function () {
        resetGlobalParamsSearch();
        fetchListExamRule(1);
    });
};

const handleSearchExamRule = () => {
    fetchListExamRule(1);
};

const handleClearSearch = () => {
    resetGlobalParamsSearch();
    fetchListExamRule();
};
//------------------------------------------------------function--------------------------------------------------------

const convertStatus = (status) => {
    switch (status) {
        case 0:
            return '<span class="tag tag-success">Đang sử dụng</span>';
        case 1:
            return '<span class="tag tag-danger">Ngưng sử dụng</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}
