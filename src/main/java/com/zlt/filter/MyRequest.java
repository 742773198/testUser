package com.zlt.filter;

import com.zlt.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class MyRequest extends HttpServletRequestWrapper {
    public MyRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        if (StringUtil.isNotNul(parameter) ){
            try {
                parameter = new String( parameter.getBytes("ISO-8859-1"),"UTF-8" );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return parameter;
    }
}
