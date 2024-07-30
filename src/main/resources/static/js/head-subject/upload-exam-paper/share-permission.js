let sPCurrentSubjectId = "";
let sPCurrentExamPaperId = "";
let listStaffToSharePermission = [];

//START: getter
const getValueSearchSharePermission = () => $("#searchSharePermission").val();
const getValueCurrentSubjectId = () => sPCurrentSubjectId;
const getValueCurrentExamPaperId = () => sPCurrentExamPaperId;
const getListStaffToSharePermission = () => listStaffToSharePermission;
//END: getter

//START: setter
const setValueSearchSharePermission = (value) => {
    $("#searchSharePermission").val(value);
}
const setValueCurrentSubjectId = (value) => {
    sPCurrentSubjectId = value;
}
const setValueCurrentExamPaperId = (value) => {
    sPCurrentExamPaperId = value;
}
const setListStaffToSharePermission = (value) => {
    listStaffToSharePermission = value;
}
//END: setter

const handleOpenModalSharePermission = (subjectId, examPaperId) => {
    setListStaffToSharePermission([]);
    setValueSearchSharePermission("");
    setValueCurrentSubjectId(subjectId);
    setValueCurrentExamPaperId(examPaperId);

    fetchListStaffBySubjectId();

    $("#sharePermissionModal").modal("show");
};

$("#resetFilterSharePermission").on("click", () => {
    setValueSearchSharePermission("");
    fetchListStaffBySubjectId();
});


const fetchListStaffBySubjectId = (
    page = 1
) => {

    const params = {
        page: page,
        size: $('#pageSizeSharePermission').val(),
        searchValue: getValueSearchSharePermission(),
        examPaperId: getValueCurrentExamPaperId()
    };

    let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/staffs/" + getValueCurrentSubjectId() + '?';

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
                $('#sharePermissionTableBody').html(`
                    <tr>
                         <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                $('#paginationSharePermission').empty();
                return;
            }

            const examPapers = responseData.map(item => {
                if (item.isPublic === 1) {
                    setListStaffToSharePermission([
                        ...getListStaffToSharePermission(),
                        item.id
                    ]);
                }

                return `<tr>
                            <td>
                                ${item.orderNumber}
                            </td>
                            <td>${item.code}</td>
                            <td>${item.name}</td>
                            <td>${item.emailFpt}</td>
                            <td>${item.emailFe}</td>
                            <td>
                                <div class="col-auto">
                                  <label class="colorinput">
                                    <input
                                        onclick="handleChooseStaffToSharePermission('${item.id}')"
                                        name="color"
                                        type="checkbox"
                                        class="colorinput-input"
                                        id="input-assign-uploader"
                                        ${item.isPublic === 1 ? "checked" : ""}
                                    />
                                    <span
                                      class="colorinput-color bg-secondary"
                                    ></span>
                                  </label>
                                </div>
                            </td>
                        </tr>`;
            });
            $('#sharePermissionTableBody').html(examPapers);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationSharePermission(totalPages, page);
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

const createPaginationSharePermission = (totalPages, currentPage) => {
    let paginationHtml = '';

    if (currentPage > 1) {
        paginationHtml += `
                     <li class="page-item">
                        <a class="page-link" href="#" onclick="changePageSharePermission(${currentPage - 1})">
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
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePageSharePermission(${i})">${i}</a></li>`;
        }
    }

    if (currentPage < totalPages) {
        paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePageSharePermission(${currentPage + 1})">
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

    $('#paginationSharePermission').html(paginationHtml);
};

const changePageSharePermission = (page) => {
    fetchListStaffBySubjectId(page);
};

$("#pageSizeSharePermission").on("change", () => {
    fetchListStaffBySubjectId();
});

const handleChooseStaffToSharePermission = (staffId) => {
    const staffIdFound = getListStaffToSharePermission().find(item => item === staffId);
    if (staffIdFound === undefined) {
        setListStaffToSharePermission([
            ...getListStaffToSharePermission(),
            staffId
        ]);
    } else {
        setListStaffToSharePermission(getListStaffToSharePermission().filter(item => item !== staffId));
    }
};

$("#buttonFilterSharePermission").on("click", () => {
    fetchListStaffBySubjectId();
});

$("#button-share-permission").on("click", () => {
    swal({
        title: "Xác nhận",
        text: getListStaffToSharePermission().length === 1 ? "Bạn có chắc muốn chia sẻ quyền truy cập cho giảng viên này không?" :
            "Bạn có chắc muốn chia sẻ quyền truy cập cho những giảng viên này không?"
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
                url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/share-permission-exam-paper",
                data: JSON.stringify({
                    examPaperId: getValueCurrentExamPaperId(),
                    listStaffId: getListStaffToSharePermission()
                }),
                contentType: "application/json",
                method: "POST",
                success: function (responseBody) {
                    showToastSuccess(responseBody?.message);
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
});