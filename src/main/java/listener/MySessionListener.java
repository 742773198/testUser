package listener;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class MySessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        System.out.println("HttpSessionListener监听器创建。");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        /* Session is destroyed. */
        // session销毁的时候如果里面有CUR_USER 则表示离线了
        Object cur_user = httpSessionEvent.getSession().getAttribute("CUR_USER");
        if (cur_user != null) {
            ServletContext servletContext = httpSessionEvent.getSession().getServletContext();
            int onLineCount = (int) servletContext.getAttribute("onLineCount");
            servletContext.setAttribute("onLineCount", onLineCount - 1);
        }
    }
}
