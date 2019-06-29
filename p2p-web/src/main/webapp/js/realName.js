
//同意实名认证协议
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
//检查姓名是否合法
function checkRealName() {
	var realName = $.trim($("#realName").val());
	if (""==realName){
		showError("realName","姓名不能为空");
		return false;
	}else if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
		showError("realName","姓名必须为中文");
		return false;
	}
	showSuccess("realName");
	return true;
}
//检查身份证有效性
function checkIdCard() {
	var idCard = $.trim($("#idCard").val());
	var replayIdCard = $.trim($("#replayIdCard").val());
	if (""==idCard){
		showError("idCard","身份证不能为空");
		return false;
	}else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
		showError("idCard","身份证格式不正确");
		return false;
	}
	if (idCard!=replayIdCard){
		showError("replayIdCard","两次输入结果不一致");
	}
	showSuccess("idCard");
	return true;
}
//检查身份证是否一致
function checkReplayIdCard() {
	var replayIdCard = $.trim($("#replayIdCard").val());
	var idCard = $.trim($("#idCard").val());
	if (replayIdCard!=idCard){
		showError("replayIdCard","两次输入结果不一致");
		return false;
	}
	showSuccess("replayIdCard");
	return true;
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
//发送认证请求
function verifyRealName() {
	var idCard = $.trim($("#idCard").val());
	var realName = $.trim($("#realName").val());
	if (checkIdCard()&&checkRealName()){
		$.ajax({
			url:'loan/verifyRealName',
			type:'post',
			data:{
				"idCard":idCard,
				"realName":realName
			},
			success:function (data) {
				if (data.errorMessage =="OK"){
					window.location.href = "index";
				}else {
					showError("captcha",data.errorMessage);
				}
			},
			error:function () {
				showError("captcha","系统繁忙，请稍后重新访问");
			}
		})
	}
}