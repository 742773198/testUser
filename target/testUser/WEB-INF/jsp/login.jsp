<%--
  Created by IntelliJ IDEA.
  User: kinglee
  Date: 2022/4/7
  Time: 9:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录</title>
</head>
<body>
<form action="/testUser/user/loginDo" method="post">
    <span>账号</span> <input type="text" name="username"><br>
    <span>密码</span><input type="password" name="password"><br>
    <input type="submit" value="登录">
</form>
</body>
</html>
