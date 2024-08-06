let stateExamPaperResourceId = "";
let stateAddOrUpdateResource = 1;
let stateValueResourceExamPaperId = "";

const getStateExamPaperResourceId = () => stateExamPaperResourceId;
const getValueLinkResource = () => $("#linkResource").val();
const getStateAddOrUpdateResource = () => stateAddOrUpdateResource;
const getStateValueResourceExamPaperId = () => stateValueResourceExamPaperId;

const setStateExamPaperResourceId = (value) => {
    stateExamPaperResourceId = value;
};
const setValueLinkResource = (value) => {
    $("#linkResource").val(value);
};
const setStateAddOrUpdateResource = (value) => {
    stateAddOrUpdateResource = value;
};
const setStateValueResourceExamPaperId = (value) => {
    stateValueResourceExamPaperId = value;
};

const handleModalModalResource = (examPaperId) => {
    setStateExamPaperResourceId(examPaperId);

    fetchListResource();

    $("#resourceTableModal").modal("show");
};

const handleOpenModalAddOrUpdateResource = (status, resourceExamPaperId) => {
    setStateAddOrUpdateResource(status);

    setStateValueResourceExamPaperId(resourceExamPaperId);
    setValueLinkResource("");
    $("#resourceError").val("");

    if (status === 2) {
        fetchDetailResource();
    }

    $("#resourceTableModal").modal("hide");

    $("#addResourceTableModal").modal("show");
};

const handleOpenModalAddResource = () => {
    $("#resourceTableModal").modal("show");
};

const fetchDetailResource = () => {
    showLoading();
    $.ajax({
        url: ApiConstant.API_TEACHER_EXAM_FILE + "/detail-resource/" + getStateValueResourceExamPaperId(),
        method: "GET",
        success: function (responseBody) {
            setValueLinkResource(responseBody?.data);
            hideLoading();
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

const fetchListResource = (
    page = 1,
) => {

    showLoading();

    const params = {
        page: page,
        size: $('#pageSizeResource').val(),
        examPaperId: getStateExamPaperResourceId()
    };

    let url = ApiConstant.API_TEACHER_EXAM_FILE + "/resources" + '?';

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
                         <td colspan="3" style="text-align: center;">Không có dữ liệu</td>
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
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span
                                    data-bs-toggle="tooltip"
                                    data-bs-title="Danh sách tài nguyên"
                                    style="margin: 0 3px"
                                    onclick="handleOpenModalAddOrUpdateResource(2,'${item.id}')"
                                >
                                    <i 
                                        class="fa-solid fa-pen-to-square"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
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

$("#button-save-resource").on("click", () => {
    showLoading();
    $.ajax({
        url: ApiConstant.API_TEACHER_EXAM_FILE + "/resources",
        method: getStateAddOrUpdateResource() === 1 ? "POST" : "PUT",
        contentType: "application/json",
        data: getStateAddOrUpdateResource() === 1 ?
            JSON.stringify({
                resource: getValueLinkResource(),
                examPaperId: getStateExamPaperResourceId()
            }) : JSON.stringify({
                resource: getValueLinkResource(),
                resourceExamPaperId: getStateValueResourceExamPaperId()
            }),
        success: function (responseBody) {
            showToastSuccess(responseBody?.message);

            $("#addResourceTableModal").modal("hide");
            $("#resourceTableModal").modal("show");

            fetchListResource();
            hideLoading();
        },
        error: function (error) {
            const myError = error?.responseJSON;
            myError.forEach(err => {
                showToastError(err.message);
            });
            hideLoading();
        }
    });
});

$("#pageSizeResource").on("click", () => {
   fetchListResource();
});