$(document).ready(function () {
    fetchListSubject();
    handleFetchMajorFacility();
    fetchListExamPaper("");
});

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
    const subjectId = $("#subjectId").val();
    fetchListExamPaper(subjectId);
};

const fetchListExamPaper = (subjectId) => {
    showLoading();
    $.ajax({
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER + "/exam-paper",
        method: "GET",
        data: {
            subjectId: subjectId
        },
        success: function (responseBody) {
            const responseLength = responseBody.data.length;
            $("#swiper-container").prop("hidden", responseLength === 0);
            $("#empty-exam-paper").prop("hidden", responseLength !== 0);
            if (responseLength > 0) {
                const examPapers = responseBody?.data.map(item => {
                    return `
                    <div class="swiper-slide">
                        <img src=${item.thumbnailLink} alt="Ảnh đề thi"/>
                            <ul>
                                <li><span>Môn:</span> ${item.subjectName}</li>
                                <li><span>Chuyên ngành:</span> ${item.majorName}</li>
                                <li><span>Mã đề thi:</span> ${item.examPaperCode}</li>
                                <li><span>Người tạo:</span> ${item.staffName}</li>
                                <li><span>Ngày tạo:</span> ${item.createdDate}</li>
                                <li><span>Trạng thái:</span> ${item.status}</li>
                                <li><span>Cơ sở:</span> ${item.facilityName}</li>
                            </ul>
                            <div class="manipulate">
                              <i class="fa-solid fa-pen-to-square" style="cursor: pointer;"
                                onclick="handleOpenModalExamPaper('${item?.fileId}',2,'${item?.examPaperType}','${item?.majorFacilityId}','${item?.subjectId}','${item.id}')"
                              ></i>
                              <i class="fa-solid fa-eye" style="cursor: pointer;" onclick="handleOpenModalExamPaper('${item.fileId}',1)"></i>
                              <i class="fa-solid fa-trash-can" style="cursor: pointer;" onclick="handleDeleteExamPaper('${item.id}')"></i>
                            </div>
                    </div>
                        `;
                });
                $('#swiper-wrapper').html(examPapers);
                startSwiper(examPapers.length);
            }
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
                    fetchListExamPaper($("#subjectId").val());
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

const startSwiper = (examPaperLength) => {
    console.log(examPaperLength)
    //START: swiper
    const slidePerView = 4;

    const rows = examPaperLength > slidePerView ? 2 : 1;

    let swiper = new Swiper(".mySwiper", {
        slidesPerView: slidePerView,
        slidesPerGroup: slidePerView,
        grid: {
            rows: rows,
        },
        grabCursor: true,
        keyboard: {
            enabled: true,
        },
        spaceBetween: 30,
        scrollbar: {
            el: ".swiper-scrollbar",
        },
        navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev",
        }
    });
//END: swiper
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
