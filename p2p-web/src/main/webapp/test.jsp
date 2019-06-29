<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019-5-27
  Time: 9:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <base href="${pageContext.request.contextPath}/">
</head>
<body>
    <h1>历史平均年化收益：${historyAverageRate}</h1>
    <h1>总会员数：${totalUser}</h1>
    <h1>总投资金额：${totalBidMoney}</h1>
    <c:forEach items="${noobLoanList}" var="n">
        <h3>${n.productName}</h3>
        <h3>${n.productType}</h3>
        <h3>${n.bidMaxLimit}</h3>
        <h3>${n.bidMinLimit}</h3>
    </c:forEach>
    <c:forEach items="${excellentLoanInfoList}" var="e">
        <h3>${e.productName}</h3>
        <h3>${e.productType}</h3>
        <h3>${e.bidMaxLimit}</h3>
        <h3>${e.bidMinLimit}</h3>
    </c:forEach>
    <c:forEach items="${commonLoanInfoList}" var="c">
        <h3>${c.productName}</h3>
        <h3>${c.productType}</h3>
        <h3>${c.bidMaxLimit}</h3>
        <h3>${c.bidMinLimit}</h3>
    </c:forEach>
</body>
</html>
