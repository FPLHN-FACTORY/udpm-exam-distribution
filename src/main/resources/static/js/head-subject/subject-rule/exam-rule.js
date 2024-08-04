let stateSubjectId = "";

const getStateSubjectId = () => stateSubjectId;

const setStateSubjectId = (value) => {
    stateSubjectId = value;
}


const handleOpenModalExamRule = (subjectId) => {
    setStateSubjectId(subjectId);

    fetchSearchExamRule();

    $("#examRuleModal").modal("show");
};

const fetchSearchExamRule = (
    page = 1,
) => {
    const params = {
        page: page,
        size: $('#pageSizeExamRule').val(),
        name: $("#nameExamRuleSearch").val(),
        subjectId: getStateSubjectId(),
    };

    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE + "/exam-rules" + '?';

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
                         <td colspan="3" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#paginationExamRule').empty();
                return;
            }
            const examrules = responseData.map(examrule => {
                return `<tr>
                            <td>${examrule.orderNumber}</td>
                            <td>${examrule.name}</td>
                            <td>
                                <div class="col-auto">
                                  <label class="colorinput">
                                    <input
                                        onclick="handleChooseExamRule('${examrule.id}')"
                                        name="color"
                                        type="checkbox"
                                        class="colorinput-input"
                                        ${examrule.isChecked == true ? "checked" : ""}
                                        id="input-choose-exam-rule"
                                    />
                                    <span
                                      class="colorinput-color bg-secondary"
                                    ></span>
                                  </label>
                                </div>
                            </td>
                        </tr>`;
            });
            $('#examRuleTableBody').html(examrules);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationExamRule(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học');
            hideLoading();
        }
    });
};

const createPaginationExamRule = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePageExamRule(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePageExamRule(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePageExamRule(${currentPage + 1})">
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

const changePageExamRule = (page) => {
    fetchSearchExamRule(page);
};

$("#button-filter-exam-rule").on("click", () => {
    fetchSearchExamRule();
});

$("#button-reset-filter-exam-rule").on("click", () => {
    $("#nameExamRuleSearch").val("");
    fetchSearchExamRule();
});

$("#pageSizeExamRule").on("click", () => {
    fetchSearchExamRule();
});


const handleChooseExamRule = (examRuleId) => {
    showLoading();
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE + "/exam-rule",
        contentType: "application/json",
        data: JSON.stringify({
            subjectId: getStateSubjectId(),
            examRuleId: examRuleId
        }),
        success: function (responseBody) {
            showToastSuccess(responseBody.message);
            fetchSearchExamRule();
            hideLoading();
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error?.responseJSON?.message);
            } else {
                showToastError('Có lỗi xảy ra');
            }
            hideLoading();
        }
    });
};