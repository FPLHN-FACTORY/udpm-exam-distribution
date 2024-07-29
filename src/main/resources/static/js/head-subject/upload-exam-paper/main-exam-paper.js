$(document).ready(function () {
    fetchListCurrentSubject();
    fetchListStaff();

    fetchListExamPaper();
    onChangePageSizeFirstTab();
});
//----------------------------------------------------------------------------------------------------------------------
let listMockExamPaperPublic = [];

//START: getter
const getListMockExamPaperPublic = () => listMockExamPaperPublic;
//END: getter

//START: setter
const setListMockExamPaperPublic = (value) => {
    listMockExamPaperPublic = value;
};

const setValueSubject = (value) => {
    $("#subjectId").val(value);
};
const setValueStaff = (value) => {
    $("#staffId").val(value);
};
const setValueExamPaperTypeFirstPage = (value) => {
    $("#examPaperTypeId").val(value);
};
//END: setter
//----------------------------------------------------------------------------------------------------------------------


const getGlobalParamsSearchFirstPage = () => {
    return {
        subjectId: $("#subjectId").val(),
        staffId: $("#staffId").val(),
        examPaperType: $("#examPaperTypeId").val(),
    }
};

const clearGlobalParamsSearchFirstPage = () => {
    setValueSubject("");
    setValueStaff("");
    setValueExamPaperTypeFirstPage("");
};


//----------------------------------------------------------------------------------------------------------------------
const fetchListCurrentSubject = () => {
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/current-subject",
        method: "GET",
        success: function (responseBody) {
            const subjects = responseBody?.data?.map((item) => {
                return `<option value="${item.id}">${item.name}</option>`
            });
            subjects.unshift('<option value="">--Chọn môn học--</option>');
            $('#exam-paper-subject').html(subjects);
            $('#subjectId').html(subjects);
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

const fetchListStaff = () => {
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/staff",
        method: "GET",
        success: function (responseBody) {
            const staffs = responseBody?.data?.map((item) => {
                return `<option value="${item.id}">${item.name}</option>`
            });
            staffs.unshift('<option value="">--Chọn người tải--</option>');
            $('#staffId').html(staffs);
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
//----------------------------------------------------------------------------------------------------------------------

//----------------------------------------------------------------------------------------------------------------------

const fetchListExamPaper = (
    page = 1,
    size = $('#pageSizeFirstPage').val(),
    paramSearch = getGlobalParamsSearchFirstPage(),
) => {

    const params = {
        page: page,
        size: size,
        semesterId: paramSearch.semesterId,
        blockId: paramSearch.blockId,
        subjectId: paramSearch.subjectId,
        staffId: paramSearch.staffId,
        examPaperType: paramSearch.examPaperType,
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
                $('#pagination').empty();
                return;
            }
            const examPapers = responseData.map(item => {
                return `<tr>
                            <td class="d-flex justify-content-between align-items-center">
                                ${item.orderNumber}
                                ${
                    item.examPaperType === "MOCK_EXAM_PAPER" && item.isPublic === false ?
                        `<input type="checkbox" onclick="handlePushInListPublic('${item.id}')"/>`
                        : ""
                }
                            </td>
                            <td>
                                <a target="_blank" href='https://drive.google.com/file/d/${item.fileId}/view'>${item.examPaperCode}</a>
                            </td>
                            <td>${item.subjectName}</td>
                            <td>${item.majorName}</td>
                            <td>${convertExamPaperType(item.examPaperType, item.isPublic)}</td>
                            <td>${item.staffName}</td>
                            <td>${formatDateTime(item.createdDate)}</td>
                            <td>${convertExamPaperStatus(item.status)}</td>
                            <td>${item.facilityName}</td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                <span onclick="handleOpenModalExamPaper('${item.fileId}')" style="margin: 0 3px;">
                                    <i class="fa-solid fa-eye"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
                                <span onclick="handleDownloadExamPaper('${item.fileId}')" style="margin: 0 3px;">
                                    <i 
                                        class="fa-solid fa-file-arrow-down"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
                                ${
                    item.examPaperType === "MOCK_EXAM_PAPER" && item.isPublic === false ?
                        `<span onclick="handleSendEmailPublicExamPaper('${item.id}')" style="margin: 0 3px;">
                                                            <i class="fa-solid fa-envelope" style="cursor: pointer;"></i>
                                                        </span>`
                        : ""
                }
                                <span onclick="handleOpenModalSharePermission('${item.subjectId}','${item.examPaperId}')" style="margin: 0 3px;">
                                    <i class="fa-solid fa-share-nodes"
                                        style="cursor: pointer;"
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

const handlePushInListPublic = (examPaperId) => {
    const examPaperIdFound = getListMockExamPaperPublic().find(item => item === examPaperId);
    if (examPaperIdFound === undefined) {
        setListMockExamPaperPublic([
            ...getListMockExamPaperPublic(),
            examPaperId
        ]);
    } else {
        setListMockExamPaperPublic(getListMockExamPaperPublic().filter(item => item !== examPaperId));
    }
};

$("#button-public-exam-paper").on("click", () => {
    if (getListMockExamPaperPublic().length === 0) {
        showToastError("Vui lòng chọn đề thi trước khi công khai");
    } else {
        swal({
            title: "Xác nhận",
            text: getListMockExamPaperPublic().length === 1 ? "Bạn có chắc muốn công khai đề thi thử này không?" :
                "Bạn có chắc muốn công khai các đề thi thử này không?"
            ,
            type: "warning",
            buttons: {
                cancel: {
                    visible: true,
                    text: "Hủy",
                    className: "btn btn-black",
                },
                confirm: {
                    text: "Công khai",
                    className: "btn btn-black",
                },
            },
        }).then((willDelete) => {
            if (willDelete) {
                showLoading();
                $.ajax({
                    url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/public-mock-exam-paper",
                    data: JSON.stringify({
                        listExamPaperId: getListMockExamPaperPublic()
                    }),
                    contentType: "application/json",
                    method: "POST",
                    success: function (responseBody) {
                        showToastSuccess(responseBody?.message);
                        fetchListExamPaper();
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
    }
});

const handleSearchListExamPaper = () => {
    fetchListExamPaper();
};

const handleClearSearchListExamPaper = () => {
    clearGlobalParamsSearchFirstPage();
    fetchListExamPaper();
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
        title: "Xác nhận",
        text: "Bạn có chắc muốn thay đổi trạng thái của đề thi này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Xác nhận",
                className: "btn btn-black",
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
                    fetchListExamPaper();
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

const onChangePageSizeFirstTab = () => {
    $("#pageSizeFirstPage").on("change", () => {
        fetchListExamPaper();
    });
};

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

const convertExamPaperType = (status, isPublic) => {
    if (status === "OFFICIAL_EXAM_PAPER") {
        return '<span class="tag tag-success">Đề thi thật</span>';
    } else if (status === "MOCK_EXAM_PAPER") {
        if (isPublic === false) {
            return '<span class="tag tag-magenta">Đề thi thử <i class="fa-solid fa-ban"></i> </span>';
        } else {
            return '<span class="tag tag-purple">Đề thi thử <i class="fa-solid fa-check"></i> </span>';
        }
    }
}
