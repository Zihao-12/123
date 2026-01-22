package com.example.webapp.utils;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ResponseUtil {

    private ResponseUtil(){}

    public enum TYPE {
        LOGIN, REGISTER, LOGOUT, DEFAULT
    }

    public enum ParamKey {
        SSO_ID, USER_ID, JUMP_BACK, CROSS_DOMAIN
    }
    private static final Log logger = LogFactory.getLog(ResponseUtil.class);

    //Ajax Content Type
    public static final String AJAX_TEXTHTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    public static final String AJAX_TEXTPLAIN_CONTENT_TYPE = "text/plain; charset=UTF-8";
    public static final String AJAX_JSON_CONTENT_TYPE = "application/json; charset=UTF-8";
    public static final String AJAX_UTF8_ENCODING = "UTF-8";

    /**
     * 将结果写到页面内容上，作为Ajax请求的结果
     *
     * @param response
     */
    public static void printWriterAjax(HttpServletRequest request, HttpServletResponse response, Object obj) {
        String message = null;
        boolean isJsonBack = false;
        if (obj instanceof String) {
            message = (String) obj;
        } else {
            isJsonBack = true;
            Gson gson=new Gson();
            message = gson.toJson(obj);
        }
        String type = request.getParameter("type");
        String callback = request.getParameter("callback");
        if ("jsonp".equals(type)) {
            if (isJsonBack) {
                message = callback + "(" + message + ")";
            } else {
                message = callback + "(\"" + message + "\")";
            }
        }
        printWriterAjax(response, message);
    }


    /**
     * 将结果写到页面内容上，作为Ajax请求的结果
     *
     * @param response
     */
    public static void printWriterJson(HttpServletRequest request, HttpServletResponse response, Object obj) {
        String message = null;
        boolean isJsonBack = false;
        if (obj instanceof String) {
            message = (String) obj;
        } else {
            isJsonBack = true;
            Gson gson=new Gson();
            message = gson.toJson(obj);
        }
        String type = request.getParameter("art");
        String callback = request.getParameter("callback");
        if ("jsonp".equals(type)) {
            if (isJsonBack) {
                message = callback + "(" + message + ")";
            } else {
                message = callback + "(\"" + message + "\")";
            }
        }
        printWriterAjax(response, message);
    }

    /**
     * @param response
     * @param message
     * @throws
     * @title: printWriterAjax
     * @description: 将结果写到页面内容上，作为Ajax请求的结果(默认将使用:text/html; charset=UTF-8)。
     */
    private static void printWriterAjax(HttpServletResponse response, String message) {
        printWriterAjax(response, AJAX_TEXTHTML_CONTENT_TYPE, AJAX_UTF8_ENCODING, message);
    }

    /**
     * @param response
     * @param contentType
     * @param characterEncoding
     * @param message
     * @throws
     * @title: printWriterAjax
     * @description: 将结果写到页面内容上，作为Ajax请求的结果。更具需要灵活配置contentType、characterEncoding
     * 。请参见ConstantCommon是否有指定的类型，或自己指定
     */
    public static void printWriterAjax(HttpServletResponse response, String contentType, String characterEncoding,
                                       String message) {
        response.setContentType(contentType);
        response.setCharacterEncoding(characterEncoding);
        PrintWriter pwriter = null;
        try {
            pwriter = response.getWriter();
            pwriter.print(message);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (pwriter != null) {
                pwriter.close();
            }
        }
    }

}
