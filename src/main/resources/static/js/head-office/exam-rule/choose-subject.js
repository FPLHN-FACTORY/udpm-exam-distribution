let examRuleId = "";

const getExamRuleId = () => examRuleId;

const setExamRuleId = (value) => {
    examRuleId = value;
}

const handleOpenModalSubject = (examRuleId) => {
    setExamRuleId(examRuleId);

    fetchListSubject();
    $("#subjectModal").modal("show");
};

const fetchListSubject = (
    page = 1,
) => {
    const params = {
        page: page,
        size: $('#pageSize').val(),
        valueSearch: $("#subjectNameSearch").val(),
        examRuleId: getExamRuleId()
    };

    let url = ApiConstant.API_HEAD_OFFICE_EXAM_RULE + "/subjects" + '?';

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
                $('#subjectTableBody').html(`
                    <tr>
                         <td colspan="3" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#paginationSubject').empty();
                return;
            }
            const subjects = responseData.map(subject => {
                return `<tr>
                            <td>${subject.orderNumber}</td>
                            <td>${subject.code}</td>
                            <td>${subject.name}</td>
                            <td>
                                <div class="col-auto">
                                  <label class="colorinput">
                                    <input
                                        onclick="handleChooseExamRuleForSubject('${subject.id}')"
                                        name="color"
                                        type="checkbox"
                                        class="colorinput-input"
                                        ${subject.isChecked == true ? "checked" : ""}
                                        id="input-assign-uploader"
                                    />
                                    <span
                                      class="colorinput-color bg-secondary"
                                    ></span>
                                  </label>
                                </div>
                            </td>
                        </tr>`;
            });
            $('#subjectTableBody').html(subjects);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationSubject(totalPages, page);
            callToolTip();
        },
        error: function (error) {
            showToastError('Có lỗi xảy ra khi lấy dữ liệu môn học');
            hideLoading();
        }
    });
};

const createPaginationSubject = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePageSubject(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePageSubject(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePageSubject(${currentPage + 1})">
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

    $('#paginationAssignUploader').html(paginationHtml);
};

const changePageSubject = (page) => {
    fetchListSubject(page);
}

const handleChooseExamRuleForSubject = (subjectId) => {
    showLoading();

    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_OFFICE_EXAM_RULE + '/choose-exam-rule',
        contentType: "application/json",
        data: JSON.stringify({
            examRuleId: getExamRuleId(),
            subjectId: subjectId
        }),
        success: function (responseBody) {
            showToastSuccess(responseBody?.message);
            hideLoading();
        },
        error: function (error) {
            if (error?.responseJSON?.message) {
                showToastError(error.responseJSON?.message);
            }
            hideLoading();
        }
    });
};

$("#buttonFilterSubject").on("click", () => {
    fetchListSubject();
});

$("#buttonResetFilterSubject").on("click", () => {
    $("#subjectNameSearch").val("");
    fetchListSubject();
});

$("#pageSizeSubject").on("click", () => {
    fetchListSubject();
});