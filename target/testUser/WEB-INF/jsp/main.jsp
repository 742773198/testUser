<%@ page import="com.zlt.entity.User" %>
<%@ page import="com.zlt.entity.Pager" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"  isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: kinglee
  Date: 2022/4/7
  Time: 14:42
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <title>主界面</title>
</head>
<body>
<span>在线人数：${applicationScope.onLineCount}</span>

<!-- 未登录 -->
<c:if test="${empty sessionScope.CUR_USER}">
    <a href="/testUser/user/login">登录</a>/<a href="/testUser/user/reg">注册</a>
</c:if>

<!-- 登录，展示界面 -->
<c:if test="${not empty sessionScope.CUR_USER}">

    <span>欢迎你:${sessionScope.CUR_USER.nickname}</span>
    <a href="/testUser/user/logout">注销</a>
    <div style="margin: 0 auto;">
        <table>
            <tr>
                <th>uid</th>
                <th>头像</th>
                <th>账号</th>
                <th>昵称</th>
                <th>邮箱</th>
                <th>登录时间</th>
                <th>注册时间</th>
                <th>操作</th>
            </tr>
            <c:forEach items="${requestScope.pager.data}" var="user">
                <tr>
                    <td>${user.uid}</td>
                    <td>
                        <img width="50px" src="/testUser/files/${user.photo}" alt="">
                    </td>
                    <td>${user.username}</td>
                    <td>${user.nickname}</td>
                    <td>${user.email}</td>
                    <td>${user.loginTime}</td>
                    <td>${user.regTime}</td>
                    <td><a href="/testUser/user/update?name=${user.username}" >修改</a>/<a href="/testUser/user/delete?name=${user.username}">删除</a></td>
                </tr>
            </c:forEach>
        </table>

        <!-- 当前页数大于1的时候，显示上一页 -->
        <c:if test="${requestScope.pager.pageNow > 1}">
            <a href="/testUser/user/main?pageNow=${pager.pageNow-1}&pageSize=${pager.pageSize}">上一页</a>
        </c:if>

        <!-- 显示页目录 -->
        <c:forEach begin="1" end="${pager.pageCount}" step="1" var="i">

            <!-- 是当前页 -->
            <c:if test="${i == pager.pageNow}">
                <a>${i}</a>
            </c:if>
            <!-- 非当前页 -->
            <c:if test="${i != pager.pageNow}">
                <a href="/testUser/user/main?pageNow=${i}&pageSize=${pager.pageSize}">${i}</a>
            </c:if>
        </c:forEach>

        <!-- 当前页数小于最大页数的时候，显示下一页 -->
        <c:if test="${requestScope.pager.pageNow < pager.pageCount}">
            <a href="/testUser/user/main?pageNow=${pager.pageNow+1}&pageSize=${pager.pageSize}">下一页</a>
        </c:if>
    </div>
</c:if>

</body>
</html>
