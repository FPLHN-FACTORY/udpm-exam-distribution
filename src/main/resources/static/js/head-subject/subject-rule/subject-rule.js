$(document).ready(function () {
    fetchSearchSubject();
    onChangePageSize();
});

//START: Exam Distribution Information
const examDistributionInfor = getExamDistributionInfo();
//END: Exam Distribution Information

const fetchSearchSubject = (
    page = 1,
    size = $('#pageSize').val(),
    paramSearch = {
        subjectCode: "",
        subjectName: "",
        staffId: examDistributionInfor.userId
    },
) => {
    const params = {
        page: page,
        size: size,
        subjectCode: paramSearch.subjectCode,
        subjectName: paramSearch.subjectName,
        staffId: paramSearch.staffId
    };

    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE + "/subjects" + '?';

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
                         <td colspan="6" style="text-align: center;">Không có dữ liệu</td>
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
                            <td>${convertSubjectType(subject.subjectType)}</td>
                            <td class="text-center">${subject.examTime === null ? 0 : subject.examTime} phút</td>
                            <td class="text-center">${subject.percentRandom === null ? 0 : subject.percentRandom}%</td>
                            <td class="text-center">
                                <div class="col-auto">
                                  <label class="colorinput">
                                    <input
                                        onclick="handleAllowOnlineSubject('${subject.id}')"
                                        name="color"
                                        type="checkbox"
                                        class="colorinput-input"
                                        ${subject.allowOnline == true ? "checked" : ""}
                                        id="input-choose-exam-rule"
                                    />
                                    <span
                                      class="colorinput-color bg-secondary"
                                    ></span>
                                  </label>
                                </div>
                            </td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span
                                    onclick="handleOpenModalExamRule('${subject.id}')"
                                    data-bs-toggle="tooltip" data-bs-title="Chọn quy định" class="fs-6">
                                    <i
                                        class="fa-solid fa-square-check"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span onclick="handleOpenModalExamTime('${subject.id}')" data-bs-toggle="tooltip" data-bs-title="Cập nhật thời gian thi" class="fs-6">
                                    <i 
                                        class="fa-solid fa-pen-to-square"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span onclick="handleOpenModalPercentRandom('${subject.id}')" data-bs-toggle="tooltip" data-bs-title="Phần trăm random" class="fs-6">
                                    <i
                                        class="fa-solid fa-bars-progress"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#subjectTableBody').html(subjects);
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

const handleAllowOnlineSubject = (subjectId) => {
    showLoading();
    $.ajax({
        type: "PUT",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE + "/allow-online-subject/" + subjectId,
        contentType: "application/json",
        success: function (responseBody) {
            showToastSuccess(responseBody.message);
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
//------------------------------------------------------function--------------------------------------------------------

const convertSubjectType = (status) => {
    switch (status) {
        case "TRADITIONAL":
            return '<span class="tag tag-success">TRADITIONAL</span>';
        case 'ONLINE':
            return '<span class="tag tag-cyan">ONLINE</span>';
        case 'BLEND':
            return '<span class="tag tag-blue">BLEND</span>';
        case 'TRUC_TUYEN':
            return '<span class="tag tag-lime">TRUC_TUYEN</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}