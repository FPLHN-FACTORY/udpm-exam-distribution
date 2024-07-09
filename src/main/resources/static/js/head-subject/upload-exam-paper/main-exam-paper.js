$(document).ready(function () {
    fetchListSubject();
    handleFetchMajorFacility();
    fetchListExamPaper();
});

const getGlobalParamsSearchFirstPage = () => {
    return {
        subjectId: $("#subjectId").val()
    }
};

const fetchListSubject = () => {
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/subject",
        method: "GET",
        success: function (responseBody) {
            const subjects = responseBody?.data?.map((item) => {
                return `<option value="${item.id}">${item.name}</option>`
            });
            subjects.unshift('<option value="">--Chọn môn học--</option>');
            $('#subjectId').html(subjects);
            $('#exam-paper-subject').html(subjects);
        },
        error: function (error) {
            const messageErr = error?.responseJSON?.message;
            if (messageErr) {
                showToastError(messageErr);
            } else {
                showToastError("Có lỗi xảy ra");
            }
        }
    });
};

const onChangeFilterExamPaper = () => {
    fetchListExamPaper(1, $('#pageSizeFirstPage').val(), getGlobalParamsSearchFirstPage());
};

const fetchListExamPaper = (
    page = 1,
    size = $('#pageSizeFirstPage').val(),
    paramSearch = {
        subjectId: ""
    },
) => {

    const params = {
        page: page,
        size: size,
        subjectId: paramSearch.subjectId
    };

    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/exam-paper" + '?';

    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }

    url = url.slice(0, -1);

    showLoading();
    $.ajax({
        url: url,
        method: "GET",
        success: function (responseBody) {
            hideLoading();
            const responseData = responseBody?.data?.data;
            if (responseData.length === 0) {
                $('#examPaperTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const examPapers = responseData.map((item, index) => {
                return `<tr>
                            <td>${item.orderNumber}</td>
                            <td>${item.examPaperCode}</td>
                            <td>${item.subjectName}</td>
                            <td>${item.majorName}</td>
                            <td>${convertExamPaperType(item.examPaperType)}</td>
                            <td>${item.staffName}</td>
                            <td>${formatDateTime(item.createdDate)}</td>
                            <td>${convertExamPaperStatus(item.status)}</td>
                            <td>${item.facilityName}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span onclick="handleOpenModalExamPaper('${item.fileId}',2,'${item.examPaperType}','${item.majorFacilityId}','${item.subjectId}','${item.id}')" class="fs-4">
                                    <i 
                                        class="fa-solid fa-pen-to-square"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span onclick="handleOpenModalExamPaper('${item.fileId}',1)" class="fs-4">
                                    <i class="fa-solid fa-eye"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                                <span class="fs-4" onclick="handleDeleteExamPaper('${item.id}')">
                                    <i 
                                        class="fa-solid fa-trash-can"
                                        style="cursor: pointer; margin-left: 10px;"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#examPaperTableBody').html(examPapers);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationFirstPage(totalPages, page);
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

const createPaginationFirstPage = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePageFirstPage(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePageFirstPage(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePageFirstPage(${currentPage + 1})">
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

    $('#paginationFirstPage').html(paginationHtml);
};

const changePageFirstPage = (page) => {
    fetchListExamPaper(page, $('#pageSizeFirstPage').val(), getGlobalParamsSearchFirstPage());
};

const handleDeleteExamPaper = (examPaperId) => {
    swal({
        title: "Xác nhận xóa",
        text: "Bạn có chắc muốn xóa đề thi này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Xác nhận",
                className: "btn btn-secondary",
            },
        },
    }).then((willDelete) => {
        if (willDelete) {
            showLoading();
            $.ajax({
                url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/" + examPaperId,
                method: "DELETE",
                success: function (responseBody) {
                    showToastSuccess(responseBody?.message);
                    fetchListExamPaper(1, $('#pageSizeFirstPage').val(), getGlobalParamsSearchFirstPage());
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
        }
    });
};

const handleFetchMajorFacility = () => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/major-facility",
        success: function (responseBody) {
            const majorFacility = responseBody?.data?.map(item => {
                return `<option value=${item.majorFacilityId}>${item.majorFacilityName}</option>`;
            });
            majorFacility.unshift('<option value="">-- Chuyên ngành - Cơ sở --</option>');
            $("#major-facility").html(majorFacility);
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

const formatDateTime = (date) => {
    const d = new Date(Number(date));
    const day = String(d.getDate()).padStart(2, "0");
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const year = d.getFullYear();
    const hours = String(d.getHours()).padStart(2, "0");
    const minutes = String(d.getMinutes()).padStart(2, "0");
    return `${day}/${month}/${year} ${hours}:${minutes}`;
}

const convertExamPaperStatus = (status) => {
    switch (status) {
        case "IN_USE":
            return '<span class="tag tag-success">Đang sử dụng</span>';
        case 'STOP_USING':
            return '<span class="tag tag-danger">Ngưng sử dụng</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}

const convertExamPaperType = (status) => {
    switch (status) {
        case "OFFICIAL_EXAM_PAPER":
            return '<span class="tag tag-success">Đề thi thật</span>';
        case 'MOCK_EXAM_PAPER':
            return '<span class="tag tag-purple">Đề thi thử</span>';
        default:
            return '<span class="tag tag-secondary">Không xác định</span>';
    }
}
