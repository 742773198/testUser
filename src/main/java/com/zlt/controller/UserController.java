package com.zlt.controller;

import com.zlt.entity.Pager;
import com.zlt.entity.User;
import com.zlt.service.UserService;
import com.zlt.service.impl.UserServiceImpl;
import com.zlt.utils.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@WebServlet("/user/*")
@MultipartConfig
public class UserController extends HttpServlet {

    private UserService userService = new UserServiceImpl();


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        doGet(request, response);
    }

    private void defaultMethod(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("未找到对应的方法");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI(); // /userManager/user/reg
        String method = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        try {
            Method declaredMethod = getClass().getDeclaredMethod(method, HttpServletRequest.class, HttpServletResponse.class);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(this,request,response);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            defaultMethod(request, response);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.out.println(1);
            System.out.println(12312412);
        }
    }

    /**
     * 执行注册
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void regDo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String save = request.getParameter("save");
        String username = request.getParameter("username");
        if("save".equals(save)){//选择了记住账号
            //创建了一个cookie的对象

            Cookie cookie = new Cookie("username",username);
            //默认情况下cookie在关闭浏览器后会被直接删除
            //可以设置cookie的生命周期保存的更久一些
            cookie.setMaxAge(60 * 60);//设置cookie的声明周期 单位是s  如果是-1 会话结束消失 0直接删除cookie 正数 从创建时间开始算起到指定的时间结束
            cookie.setHttpOnly(true);//是否只支持请求携带cookie
            cookie.setPath("/testUser");//设置携带cookie的路径
            cookie.setDomain("localhost");//设置域
            //响应给客户端
            response.addCookie(cookie);
        }
        //获取cookie
        /*Cookie[] cookies = request.getCookies();
        //如果客户端没有发送cookie给服务器 这个数组是个null
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("username")){
                    String value = cookie.getValue();//获取到cookie的值
                }
            }
        }*/


        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String email = request.getParameter("email");
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setEmail(email);
        boolean reg = false;
        try {
            reg = userService.reg(user);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        if(reg){
            //跳转到登录界面
            response.sendRedirect("/testUser/user/login");
        }else{
            //跳转注册界面
            response.sendRedirect("/testUser/user/reg");
        }
    }

    /**
     * 显示注册界面
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void reg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/reg.jsp").forward(request,response);
    }


    /**
     * 显示登录界面
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void loginDo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = userService.login(username,password);
        if(user == null){
            System.out.println("登陆失败，未找到该账户");
            response.sendRedirect("/testUser/user/login");
        }else{
            //登录成功
            System.out.println("登陆成功");
            //在线人数+1
            int onLineCount = (int) request.getServletContext().getAttribute("onLineCount");
            request.getServletContext().setAttribute("onLineCount",onLineCount+1);

            HttpSession session = request.getSession();
            session.setAttribute("CUR_USER",user);
            Cookie cookie = new Cookie("JSESSIONID",session.getId());
            cookie.setPath("/testUser");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);
            response.sendRedirect("/testUser/user/main");
        }
    }

    /**
     * 显示登录界面
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request,response);
    }

    /**
     * 注销
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //销毁session对象
        request.getSession().invalidate();
        response.sendRedirect("/testUser/user/main");
    }

    /**
     * 主界面
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void main(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageNow = request.getParameter("pageNow");
        String pageSize = request.getParameter("pageSize");
        if(!StringUtil.isNotNul(pageNow)){  //默认当前页数为第1页
            pageNow = "1";
        }
        if(!StringUtil.isNotNul(pageSize)){  //默认每页数目为5个
            pageSize = "5";
        }
        //查询分页数据
        Pager<User> pager = new Pager<>();
        pager.setPageNow(Integer.parseInt(pageNow));  //设置Pager类的页数
        pager.setPageSize(Integer.parseInt(pageSize));  //每页大小
        pager = userService.selectUser(pager);  //根据页数和每页大小取得对应页数的几组user数据
        request.setAttribute("pager",pager);  //将pager类放入request请求作用域
        request.getRequestDispatcher("/WEB-INF/jsp/main.jsp").forward(request,response);
    }

    /**
     * 删除
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("name");
        boolean judge = userService.deleteByName(username);  //ture表示修改成功，但目前没意义
        response.sendRedirect("/testUser/user/main");
    }

    /**
     * 修改
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("name");
        User user = userService.getUserByName(username);
        //数据检验，数据传过来的数据一般都需要检验

        request.setAttribute("user" , user);
        request.getRequestDispatcher("/WEB-INF/jsp/update.jsp").forward(request,response);  //转发到main.jsp
    }

    /**
     * 进行修改
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void updateDo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nickname = request.getParameter("nickname");
        String email = request.getParameter("email");
        String username = request.getParameter("username");

        //图片（文件）传输
        //头像上传  类上的注解 @MultipartConfig 一定要加否则报错
//        Part photo = request.getPart("photo");
//        if(photo.getSize() > 0){//有文件上传
//            String submittedFileName = photo.getSubmittedFileName();
//            String fileName = StringUtil.uuid() + submittedFileName.substring(submittedFileName.lastIndexOf("."));
//            String path = "D:\\apache-tomcat-8.5.20-windows-x64\\apache-tomcat-8.5.20\\webapps\\files";
//            //开始保存文件
//            photo.write(path + File.separator + fileName);
//            fileName = "http://localhost:8080/files/" + fileName;
//            user.setPhoto(fileName);
//        }

        User user = userService.getUserByName(username);
        user.setEmail(email);
        user.setNickname(nickname);
        boolean judge = userService.update(user);
        response.sendRedirect("/testUser/user/main");
    }
}
