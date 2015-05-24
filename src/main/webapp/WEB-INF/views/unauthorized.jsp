<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<c:set var="ctx_path" value="${pageContext.request.contextPath}"></c:set>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AbleSky代码发布系统</title>
<%@ include file="./include/includeCss.jsp" %>
</head>
<body>
<c:choose>
	<c:when test="${param.type eq 'simple'}">
		<jsp:include page="./unauthzBody/simple.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:include page="./unauthzBody/normal.jsp" />
	</c:otherwise>
</c:choose>
</body>
</html>