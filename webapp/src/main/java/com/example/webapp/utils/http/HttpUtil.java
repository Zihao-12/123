package com.example.webapp.utils.http;

/**
 * Created by gehaisong on 2021/6/28.
 */

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  @author gehaisong
 */
@Slf4j
public class HttpUtil {
    public static final String UTF_8 = "UTF-8";
    public static final String APPLICATION_JSON = "application/json";
    public static final int SOCKET_TIMEOUT=20000;
    public static final int CONNECT_TIMEOUT=20000;
    public static final String SEND_METHOD_DELETE="delete";
    public static final String SEND_METHOD_GET="get";
    public static final String SEND_METHOD_PUT="put";

    public static String sendGet(String url) {
        String respContent = null;
        try {
            CloseableHttpClient httpclient = HttpUtil.createSslClientDefault();
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                respContent = EntityUtils.toString(entity);
                EntityUtils.consume( entity);
                respContent = CharsetUtils.changeCharset(respContent,UTF_8);
            }
        } catch (IOException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return respContent;
    }

    /**入参说明
    *
    * param url 请求地址
    * param jsonObject	请求的json数据
    * param encoding	编码格式
    *
    * */
    public static String sendPostJson(String url, Gson paramJsonObject){
       return sendPostJson(url,paramJsonObject.toString());
    }
    public static String sendPostJson(String url, String param){
        CloseableHttpClient httpclient = HttpUtil.createSslClientDefault();
        HttpPost httpPost = new HttpPost(url);
        String respContent = null;
        try {
            StringEntity stringEntity = new StringEntity(param,UTF_8);
            stringEntity.setContentEncoding(UTF_8);
            stringEntity.setContentEncoding(UTF_8);
            //发送json数据需要设置contentType
            stringEntity.setContentType(APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpclient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity resEntity = response.getEntity();
                // 返回json格式：
                respContent = EntityUtils.toString(resEntity);
                respContent = CharsetUtils.changeCharset(respContent,UTF_8);
                EntityUtils.consume( resEntity);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return respContent;
    }


    /**入参说明
    *
    * param url 请求地址
    * param map	请求的map数据
    * param encoding	编码格式
    *
    * */
    public static String sendPostMap(String url, Map<String,Object> map) throws IOException {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = HttpUtil.createSslClientDefault();
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),String.valueOf(elem.getValue())));
            }
            if(list.size() > 0){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,UTF_8);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                   result = EntityUtils.toString(resEntity,UTF_8);
                }
                //一定要记得把entity fully consume掉，否则连接池中的connection就会一直处于占用状态
                EntityUtils.consume( resEntity);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public static CloseableHttpClient createSslClientDefault(){
        try {
            //SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            // 在JSSE中，证书信任管理器类就是实现了接口X509TrustManager的类。我们可以自己实现该接口，让它信任我们指定的证书。
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            //信任所有
            X509TrustManager x509mgr = new X509TrustManager() {
                //    该方法检查客户端的证书，若不信任该证书则抛出异常
                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String string) {
                }
                //     该方法检查服务端的证书，若不信任该证书则抛出异常
                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String string) {
                }
                //   返回受信任的X509证书数组
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext sslContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            sslContext.init(null, new TrustManager[] { x509mgr }, null);
            ////创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,NoopHostnameVerifier.INSTANCE);
            //  HttpsURLConnection对象就可以正常连接HTTPS了，无论其证书是否经权威机构的验证，只要实现了接口X509TrustManager的类MyX509TrustManager信任该证书。
            return HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        } catch (KeyManagementException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        } catch (NoSuchAlgorithmException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        // 创建默认的httpClient实例.
        return  HttpClients.createDefault();

    }

    /**
     * 获取网络文件流
     * @param httpUrl
     * @return
     */
    public static InputStream getStreamByUrl(String httpUrl){
        CloseableHttpClient httpclient = createSslClientDefault();
        HttpGet httpGet = new HttpGet(httpUrl);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
        httpGet.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            InputStream input = response.getEntity().getContent() ;
            return input;
        } catch (Exception e) {
            log.error("{}",e);
        }
        return null;
    }

    public static InputStream getStreamByUrl(String url, String jsonParam){
        CloseableHttpClient httpclient = HttpUtil.createSslClientDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            StringEntity stringEntity = new StringEntity(jsonParam,UTF_8);
            stringEntity.setContentEncoding(UTF_8);
            stringEntity.setContentEncoding(UTF_8);
            //发送json数据需要设置contentType
            stringEntity.setContentType(APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpclient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                return response.getEntity().getContent();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static String send(String url,String sendMethod) {
        String respContent = null;
        try {
            CloseableHttpClient httpclient = HttpUtil.createSslClientDefault();
            HttpRequestBase req = null;
            if(SEND_METHOD_GET.equals(sendMethod)){
                req = new HttpGet(url);
            }else if(SEND_METHOD_DELETE.equals(sendMethod)){
                req = new HttpDelete(url);
            }else if(SEND_METHOD_PUT.equals(sendMethod)){
                req = new HttpPut(url);
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
            req.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(req);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                respContent = EntityUtils.toString(entity);
                EntityUtils.consume( entity);
                respContent = CharsetUtils.changeCharset(respContent,UTF_8);
            }
        } catch (IOException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return respContent;
    }

    /**
     * 获取xml节点xPathExpress 的内容
     * @param xmlStr
     * @param xPathExpress xml节点路径 //bor-auth-valid/z303/z303-id
     * @return
     */
    public static String getXmlNodeString(String xmlStr,String xPathExpress){
        Document doc;
        String resultStr = "";
        try {
            doc = DocumentHelper.parseText(xmlStr);
            Node result = doc.selectSingleNode(xPathExpress);
            if(result != null){
                resultStr = result.getText();
            }
        } catch (DocumentException ex) {
            throw new RuntimeException(ex);
        }
        return resultStr;
    }
}