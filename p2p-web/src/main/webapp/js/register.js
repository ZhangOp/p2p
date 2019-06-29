
//验证手机号
function checkPhone() {
	var phone = $.trim($("#phone").val());
	var flag = true;
	if (""==phone){
		showError("phone","手机号不能为空");
		return false;
	}else if (!/^1[1-9]\d{9}$/.test(phone)){
		showError("phone","请输入正确的手机号");
		return false;
	}else {
		$.ajax({
			url:"loan/checkPhone",
			type:"get",
			data:"phone="+phone,
			asysn:false,
			success:function (data) {
				if (data.errorMessage=="OK"){
					showSuccess("phone")
					flag = true;
				} else {
					showError("phone","手机号已经注册");
					flag = false;
				}
			},
			error:function () {
				showError("phone","系统繁忙，请稍后重试");
				flag = false;
			}
		});
	}
	return flag;
}
//验证密码
function checkLoginPassword() {
	var loginPassword = $.trim($("#loginPassword").val());

	if (""==loginPassword){
		showError("loginPassword","密码不能为空")
		return false;
	}else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)){
		showError("loginPassword","密码只能为数字和大小写字母组成")
		return false;
	}else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
		showError("loginPassword","密码必须同时包含数字和大小写字母")
		return false;
	}else if (loginPassword.length<6||loginPassword.length>20){
		showError("loginPassword","长度必须在6-20位之间");
		return false;

	}
	showSuccess("loginPassword")
	return true;

}
//再次验证密码
function checkReplayLoginPassword() {
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());
	var loginPassword = $.trim($("#loginPassword").val());
	if (replayLoginPassword!=loginPassword){
		showError("replayLoginPassword","两次密码不一致")
		return false;
	}
	showSuccess("replayLoginPassword")
	return true;
}
// 注册验证
function register() {
	var phone = $.trim($("#phone").val());
	var loginPassword = $.trim($("#loginPassword").val());
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());
	if (checkPhone()&&checkCaptcha()&&checkLoginPassword()&&checkReplayLoginPassword()){
		$("#loginPassword").val($.md5(loginPassword));
		$("#replayLoginPassword").val($.md5(replayLoginPassword));
		$.ajax({
			url:"loan/register",
			data:{
				"phone":phone,
				"loginPassword":$.md5(loginPassword)
			},
			type:"post",
			success:function (data) {
				if (data.errorMessage=="OK"){
					window.location.href="realName.jsp";

				}
			},
			error:function () {
				alert("系统繁忙，请稍后重试")
			}

		});
	}
}

// 验证验证码
function checkCaptcha() {
	var captcha = $.trim($("#captcha").val());
	var flag = true;
	if (""==captcha){
		showError("captcha","请输入验证码");
		return false;
	}
	$.ajax({
		url:"loan/checkCaptcha",
		data:"captcha="+captcha,
		type:"get",
		asysn:false,
		success:function (data) {
			if (data.errorMessage=='OK'){
				showSuccess("captcha");
				flag = true;
			}else {
				showError("captcha",data.errorMessage);
			}
		},
		error:function () {
			showError("captcha","系统繁忙，请稍后再尝试")
			flag =false;
		}

	})
	return flag;
}


//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});

//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}