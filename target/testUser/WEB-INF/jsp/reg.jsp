<%--
  Created by IntelliJ IDEA.
  User: kinglee
  Date: 2022/4/7
  Time: 9:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/testUser/user/regDo" method="post">
    <span>账号</span><input type="text" name="username"><br>
    <span>密码</span><input type="password" name="password"><br>
    <span>昵称</span><input type="text" name="nickname"><br>
    <span>邮箱</span><input type="email" name="email"><br>
    <span>记住账号:</span><input type="checkbox" name="save" value="save"><br>
    <input type="submit" value="注册">
</form>
</body>
</html>
