var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
    try {
        if (window.opener) {
            // IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性
            referrer = window.opener.location.href;
        }
    } catch (e) {
    }
}
$(function () {
    loadStat();
    $("#dateBtn1").on("click", function () {
        var phone = $.trim($("#phone").val());
        var _this = $(this);
        if (checkPhone()&&checkLoginPassword()) {
            $.ajax({
                url:"loan/messageCode",
                type:"post",
                data:"phone="+phone,
                success:function (data) {
                    if (data.errorMessage == "OK") {
                        if (!_this.hasClass("on")){
                            alert(data.messageCode)
                            $.leftTime(10,function (d) {
                                if (d.status){
                                    _this.addClass("on");
                                    _this.html((d.s=="00"?"60":d.s)+"秒后获取")
                                    _this.attr("disabled","disabled")
                                }else {
                                    _this.removeClass("on");
                                    _this.html("获取验证码");
                                    _this.removeAttr("disabled")
                                }
                            })
                        }
                    }else {
                        $("#showId").html("短信发送失败，请稍后重新尝试")
                    }
                }

            })



            /*$("#dateBtn1").attr("disabled", "disabled").addClass("on")
            var count = 60
            var interval = window.setInterval(function () {

                $("#dateBtn1").html(count + "秒后重新获取")
                count--;
                if (count==0){
                    $("#dateBtn1").removeAttr("disabled").removeClass("on").html("获取验证码")
                    clearInterval(interval);
                }
            }, 1000)*/

        }
    });




})
//按键盘Enter键即可登录
$(document).keyup(function (event) {
    if (event.keyCode == 13) {
        login();
    }
});

function checkPhone() {
    var phone = $.trim($("#phone").val());
    if ("" == phone) {
        $("#showId").html("请输入手机号码");
        return false;
    } else if (!/^1[1-9]\d{9}$/.test(phone)) {
        $("#showId").html("请输入正确的手机号码");
        return false;
    }
    $("#showId").html("");
    return true;
}

//密码检查
function checkLoginPassword() {
    var loginPassword = $.trim($("#loginPassword").val());
    if ("" == loginPassword) {
        $("#showId").html("请输入密码");
        return false;
    }
    $("#showId").html("");
    return true;
}

//登录
function login() {
    var loginPassword = $.trim($("#loginPassword").val());
    var phone = $.trim($("#phone").val());
    var messageCode = $.trim($("#messageCode").val());

    if (checkPhone() && checkLoginPassword()&&checkMessageCode()) {
        $("#loginPassword").val($.md5(loginPassword));
        $.ajax({
            url: "loan/login",
            data: {
                phone: phone,
                loginPassword: $.md5(loginPassword),
                messageCode:messageCode
            },
            type: "post",
            success: function (data) {
                if (data.errorMessage == "OK") {
                    if ("" == referrer) {
                        window.location.href = "index";
                    } else {
                        window.location.href = referrer;
                    }
                } else {
                    $("#showId").html(data.errorMessage);
                }


            },
            error: function () {
                $("#showId").html("系统繁忙，请稍后尝试");
            }

        })
    }
}

//登录加载
function loadStat() {
    $.ajax({
        url: "loan/loadStat",
        success: function (data) {
            $(".historyAverageRate").html(data.historyAverageRate);
            $("#user").html(data.totalUser);
            $("#gold").html(data.totalBidMoney);
        }
    })
}
//检查验证码
function checkMessageCode() {
    var messageCode = $.trim($("#messageCode").val());
    if (""==messageCode){
        $("#showId").html("请输入验证码")
        return false
    }
    return true;
}


