$(function () {
    $("#uploadForm").submit(upload);
    $("#forgetForm").submit(forget);
});

function upload() {
    $.ajax({
        url:
            "http://upload.qiniup.com",
        method:
            "post",
        processData:
            false,
        contentType:
            false,
        data:
            new FormData($("#uploadForm")[0]),
        success: function (data) {
            if (data && data.code == 0) {
                // 更新头像路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName": $("input[name='key']").val()},
                    function (data) {
                        data = $.parseJSON(data);
                        if (data.code == 0) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传失败！");
            }
        }

    })
    ;
    return false;
}

function forget() {
    $.post(
        CONTEXT_PATH + "/user/forgetPassword",
        {"oldPassword": $("input[name='oldPassword']").val(), "newPassword": $("input[name='newPassword']").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                // window.location.href = "/logout";
            } else {
                alert(data.msg);
            }
        }
    );
}
