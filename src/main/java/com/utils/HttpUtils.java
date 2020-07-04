package com.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;


public class HttpUtils {
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT  = 7000;

    private static final Logger logger = Logger.getLogger(HttpUtils.class);
    public static ResponesData responesData=new ResponesData();


    public static void main(String[] args) {
        String url="http://123.56.65.197:8080/mybatis/user/showUser";
        String url1="http://123.56.65.197:8080/mybatis/user/register";
        //JSONObject s=doGet("http://123.56.65.197:8080/mybatis/user/getUserList");

//        Map<String, Object> params=new HashMap<>();
//        params.put("userName","nem");
//        System.out.println(params);
//        JSONObject s=doGet(url,params);
//        System.out.println("jieguo "+s);

        Object json=JSONObject.parseObject("{\"userName\":\"zhangsan\", \"password\":\"123456\", \"age\":1}");
        System.out.println(json);
        ResponesData s=doPost(url1,json);
        System.out.println("jieguo:"+s);

    }
    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        // Validate connections after 1 sec of inactivity
        connMgr.setValidateAfterInactivity(1000);
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);

        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static ResponesData doGet(String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     */
    public static ResponesData doGet(String url, Map<String, Object> params) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        System.out.println(params);
        int i = 0;
        if(params!=null){
            for (String key : params.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(params.get(key));
                i++;
            }
        }

        apiUrl += param;
        System.out.println("apiUrl: "+apiUrl);
        String result = null;
        HttpClient httpClient = null;
        if (apiUrl.startsWith("https")) {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                    .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        } else {
            httpClient = HttpClients.createDefault();
        }
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            ///System.out.println("entity:"+EntityUtils.toString(entity));
            if (entity != null) {
                System.out.println(entity);
                result=EntityUtils.toString(entity);
                System.out.println("jjj"+result);
                responesData.setStatus(response.getStatusLine().getStatusCode());
                responesData.setBody(result);
            }
        } catch (IOException e) {
            System.out.println(e);
            //Logger.error(logger, e, e.getMessage());
            //throw new ServiceException(e.getMessage());
        }
        //return JSON.parseObject(result);
        return responesData;
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     *
     * @param apiUrl
     * @return
     */
    public static ResponesData doPost(String apiUrl) {
        return doPost(apiUrl, new HashMap<String, Object>());
    }



    /**
     * 发送 POST 请求，JSON形式
     *
     * @param apiUrl
     * @param json
     *            json对象
     * @return
     */
    public static ResponesData doPost(String apiUrl, Object json) {
        System.out.println("jsonDe方法");
        CloseableHttpClient httpClient = null;
        if (apiUrl.startsWith("https")) {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                    .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        } else {
            httpClient = HttpClients.createDefault();
        }
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");// 解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);

            System.out.println("状态码"+response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
            System.out.println("post:"+httpStr);
            responesData.setStatus(response.getStatusLine().getStatusCode());
            responesData.setBody(httpStr);
        } catch (IOException e) {
            System.out.println("error"+e);
            //LoggerUtils.error(logger, e, e.getMessage());
            //throw new ServiceException(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    //LoggerUtils.error(logger, e, e.getMessage());
                    //throw new ServiceException(e.getMessage());
                }
            }
        }
        return responesData;
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (GeneralSecurityException e) {
            //LoggerUtils.error(logger, e, e.getMessage());
            //throw new ServiceException(e.getMessage());
        }
        return sslsf;
    }
}
