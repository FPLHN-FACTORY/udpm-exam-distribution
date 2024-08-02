$(document).ready(function () {
    fetchListCurrentSubject();
    fetchListSemester();
    fetchListStaff();

    onChangeSemesterToFetchBlockAndSubject();

    onChangePageSizeFirstTab();
});
//----------------------------------------------------------------------------------------------------------------------
//START: state
let isFirstRender = false;
let stateSemesterIdNow = "";
//END: state

//START: getter
const getIsFirstRender = () => isFirstRender;
const getStateSemesterIdNow = () => stateSemesterIdNow;
//END: getter

//START: setter
const setIsFirstRender = (value) => {
    isFirstRender = value;
};
const setStateSemesterIdNow = (value) => {
    stateSemesterIdNow = value;
};
const setValueSemester = (value) => {
    $("#semesterId").val(value);
};
const setValueBlock = (value) => {
    $("#blockId").val(value);
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
        semesterId: $("#semesterId").val(),
        blockId: $("#blockId").val(),
        subjectId: $("#subjectId").val(),
        staffId: $("#staffId").val(),
        examPaperType: $("#examPaperTypeId").val(),
    }
};

const clearGlobalParamsSearchFirstPage = () => {
    setValueSemester(getStateSemesterIdNow());
    setValueBlock("");
    setValueSubject("");
    setValueStaff("");
    setValueExamPaperTypeFirstPage("");
};


//----------------------------------------------------------------------------------------------------------------------
const fetchListCurrentSubject = () => {
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/current-subject",
        method: "GET",
        success: function (responseBody) {
            const subjects = responseBody?.data?.map((item) => {
                return `<option value="${item.id}">${item.name}</option>`
            });
            subjects.unshift('<option value="">--Chọn môn học--</option>');
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

const fetchListSubject = (semesterId) => {
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/subject/" + semesterId,
        method: "GET",
        success: function (responseBody) {
            const subjects = responseBody?.data?.map((item) => {
                return `<option value="${item.id}">${item.name}</option>`
            });
            $('#subjectId').html(subjects);
            fetchListExamPaper();
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

const fetchListSemester = () => {
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/semester",
        method: "GET",
        success: function (responseBody) {
            const listData = responseBody?.data;
            const semesters = listData?.map((item) => {
                return `<option value="${item.id}">${item.name}</option>`
            });
            $('#semesterId').html(semesters);

            if (!getIsFirstRender()) {
                selectSemesterNowAndFetchBlockAndSubject(listData);
            }
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

const fetchListBlock = (idSemester) => {
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/block/" + idSemester,
        method: "GET",
        success: function (responseBody) {
            const listBlocks = responseBody?.data;
            const blocks = listBlocks?.map((item) => {
                return `<option value="${item.id}">${item.name}</option>`
            });
            blocks.unshift('<option value="">--Chọn block--</option>');
            $('#blockId').html(blocks);

            if (!getIsFirstRender()) {
                selectBlockNow(listBlocks);
                setIsFirstRender(true);
            }
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
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/staff",
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
const selectSemesterNowAndFetchBlockAndSubject = (listSemester) => {
    const now = new Date().getTime();
    let isFoundSemester = false;
    listSemester.forEach(item => {
        if (item.startTime < now && now < item.endTime) {
            setValueSemester(item.id);
            setStateSemesterIdNow(item.id);

            fetchListBlock(item.id);
            fetchListSubject(item.id);

            isFoundSemester = true;
        }
    });

    if (!isFoundSemester) {
        showToastError("Không tìm thấy học kỳ hiện tại");
    }
};

const onChangeSemesterToFetchBlockAndSubject = () => {
    $("#semesterId").on("change", () => {
        const idSemester = $("#semesterId").val();
        fetchListBlock(idSemester);
        fetchListSubject(idSemester);
    });
};

const selectBlockNow = (listBlock) => {
    const now = new Date().getTime();

    let isFoundBlock = false;
    listBlock.forEach(item => {
        if (item.startTime < now && now < item.endTime) {
            setValueBlock(item.id);
            fetchListCurrentSubject();

            isFoundBlock = true;
        }
    });

    if (!isFoundBlock) {
        showToastError("Không tìm thấy block hiện tại");
    }
};

//----------------------------------------------------------------------------------------------------------------------

const fetchListExamPaper = (
    page = 1,
    size = $('#pageSizeFirstPage').val(),
    paramSearch = getGlobalParamsSearchFirstPage(),
) => {

    showLoading();

    const params = {
        page: page,
        size: size,
        semesterId: paramSearch.semesterId,
        blockId: paramSearch.blockId,
        subjectId: paramSearch.subjectId,
        staffId: paramSearch.staffId,
        examPaperType: paramSearch.examPaperType,
    };

    let url = ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/exam-paper" + '?';

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
                $('#examPaperTableBody').html(`
                    <tr>
                         <td colspan="12" style="text-align: center;">Không có dữ liệu</td>
                    </tr>
                `);
                return;
            }
            const examPapers = responseData.map(item => {
                return `<tr>
                            <td>${item.orderNumber}</td>
                            <td>
                                <a target="_blank" href='https://drive.google.com/file/d/${item.fileId}/view'>${item.examPaperCode}</a>
                            </td>
                            <td>${item.subjectName}</td>
                            <td>${item.majorName}</td>
                            <td>${convertExamPaperType(item.examPaperType, item.isPublic)}</td>
                            <td>${item.staffName}</td>
                            <td>${formatDateTime(item.createdDate)}</td>
                            <td>${item.totalUsed}</td>
                            <td>${convertExamPaperStatus(item.status)}</td>
                            <td>${item.facilityName}</td>
                            <td>
                                <div class="col-auto">
                                  <label class="colorinput">
                                    <input
                                        onclick="handleChooseExamPaperBySemester('${item.id}')"
                                        name="color"
                                        type="checkbox"
                                        class="colorinput-input"
                                        ${item.isChoose == true ? "checked" : ""}
                                        id="input-assign-uploader"
                                    />
                                    <span
                                      class="colorinput-color bg-black"
                                    ></span>
                                  </label>
                                </div>
                            </td>
                            <td style="width: 1px; text-wrap: nowrap; padding: 0 10px;">
                                ${
                                    item.isUpdateFile == true ?
                                        `
                                        <span style="margin: 0 3px"
                                        data-bs-toggle="tooltip"
                                        data-bs-title="Cập nhật nội dung đề thi"
                                        onclick="handleRedirectUpdateContentFile('${item.id}')">
                                            <i 
                                                class="fa-solid fa-file-pen"
                                                style="cursor: pointer;"
                                            ></i>
                                        </span>
                                        ` : ""
                                }
                                <span style="margin: 0 3px"
                                    data-bs-toggle="tooltip"
                                    data-bs-title="Cập nhật đề thi"
                                    onclick="handleOpenModalExamPaper('${item.fileId}',2,'${item.examPaperType}','${item.majorFacilityId}','${item.subjectId}','${item.id}')"
                                >
                                    <i 
                                        class="fa-solid fa-pen-to-square"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
                                <span
                                    data-bs-toggle="tooltip"
                                    data-bs-title="Chi tiết đề thi"
                                    onclick="handleOpenModalExamPaper('${item.fileId}',1)" style="margin: 0 3px;"
                                >
                                    <i class="fa-solid fa-eye"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
                                <span
                                    data-bs-toggle="tooltip"
                                    data-bs-title="Tải đề thi"
                                    onclick="handleDownloadExamPaper('${item.fileId}')" style="margin: 0 3px;"
                                >
                                    <i 
                                        class="fa-solid fa-file-arrow-down"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
                                <span
                                    data-bs-toggle="tooltip"
                                    data-bs-title="Thay đổi trạng thái"
                                    onclick="handleDeleteExamPaper('${item.id}')" style="margin: 0 3px"
                                >
                                    <i 
                                        class="fa-solid fa-shuffle"
                                        style="cursor: pointer;"
                                    ></i>
                                </span>
                            </td>
                        </tr>`;
            });
            $('#examPaperTableBody').html(examPapers);
            const totalPages = responseBody?.data?.totalPages ? responseBody?.data?.totalPages : 1;
            createPaginationFirstPage(totalPages, page);
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
                url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/" + examPaperId,
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

const handleChooseExamPaperBySemester = (examPaperId) => {
    showLoading();
    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER + "/choose/" + examPaperId,
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
}

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
