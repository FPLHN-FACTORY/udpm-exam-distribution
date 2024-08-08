let statePRSubjectId = "";

const getStatePRSubjectId = () => statePRSubjectId;

const setStatePRSubjectId = (value) => {
    statePRSubjectId = value;
}

const handleOpenModalPercentRandom = (subjectId) => {
    $("#valueLabel").attr("hidden", true);

    setStatePRSubjectId(subjectId);
    fetchDetailPercentRandom();

    $("#percentRandomModal").modal("show");
};

$("#progressRange").on("input", function () {
    if($("#valueLabel").attr("hidden")){
        $("#valueLabel").attr("hidden", false);
    }

    var value = $(this).val();
    $("#progressBar")
        .css("width", value + "%")
        .attr("aria-valuenow", value)
        .text(value + "%");
    var rangeWidth = $(this).width();
    var labelWidth = $("#valueLabel").width();
    var thumbOffset = (value / 100) * rangeWidth - labelWidth / 2;
    $("#valueLabel")
        .css("left", thumbOffset + "px")
        .text(value + "%");
});

const fetchDetailPercentRandom = () => {
    showLoading();
    $.ajax({
        type: "GET",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE + "/percent-random/" + getStatePRSubjectId(),
        contentType: "application/json",
        success: function (responseBody) {
            const value = responseBody?.data?.percentRandom;
            $("#progressRange").val(value);
            $("#progressBar")
                .css("width", value + "%")
                .attr("aria-valuenow", value)
                .text(value + "%");

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

$("#button-save-percent-random").on("click", () => {
    showLoading();
    $.ajax({
        type: "POST",
        url: ApiConstant.API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE + "/percent-random",
        contentType: "application/json",
        data: JSON.stringify({
            subjectId: getStatePRSubjectId(),
            percentRandom: $("#progressRange").val()
        }),
        success: function (responseBody) {
            showToastSuccess(responseBody?.message);

            fetchSearchSubject();

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
});