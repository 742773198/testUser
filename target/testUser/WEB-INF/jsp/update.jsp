<%@ page import="com.zlt.entity.User" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2022/4/8 0008
  Time: 9:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>修改界面</title>
</head>
<body>
<%
    User user = (User) request.getAttribute("user");
%>
<form action="/testUser/user/updateDo?username=<%= user.getUsername() %>" method="post" enctype="multipart/form-data">
    <span>用户：<%= user.getUsername() %>;</span><br>
    <span>原昵称：<%= user.getNickname() %>;</span><br>
    <span>原邮箱：<%= user.getEmail() %>;</span><br>
    <span>昵称</span> <input type="text" name="nickname"><br>
    <span>邮箱</span><input type="text" name="email"><br>
    <span>头像</span><input type="file" name="photo" multiple="multiple">
    <input type="submit" value="修改">
</form>
</body>
</html>
