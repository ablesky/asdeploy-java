<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setStatus(response.SC_NOT_FOUND);
%>
<c:set var="ctx_path" value="${pageContext.request.contextPath}"></c:set>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>404</title>
<link type="text/css" rel="stylesheet" href="${ctx_path}/css/bootstrap.css" />
</head>
<body>
<div style="width: 1000px; margin: 200px auto;">
	<h1 style="text-align: center;">404 页面不存在!</h1>
</div>
</body>
</html>