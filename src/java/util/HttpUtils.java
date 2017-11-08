package util;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * http访问工具类
 *
 * @author 张京辉
 *         创建时间:2015/9/9
 *         更新时间:
 */
public class HttpUtils {
    public static final String DEFULT_CHARSET = "utf-8";
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static Gson g = new Gson();

    /**
     * get请求(默认UTF-8)
     *
     * @param url
     * @param isPrintLog
     * @return
     */
    public static String get(String url, boolean isPrintLog) {
        return get(url, isPrintLog, DEFULT_CHARSET);
    }

    /**
     * get请求
     *
     * @param url
     * @param isPrintLog
     * @param charset
     * @return
     */
    public static String get(String url, boolean isPrintLog, String charset) {
        String result = "";
        BufferedReader in = null;
        HttpURLConnection connection = null;
        try {
            if (isPrintLog) {
                logger.info("get请求参数url:" + url);
            }
            URL getUrl = new URL(url);
            // 请求配置
            connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), charset));
            String line = in.readLine();
            while (line != null) {
                result += line;
                line = in.readLine();
            }
        } catch (Exception e) {
            logger.error("get请求发生异常" + ",url=" + url, e);
            throw new RuntimeException("get请求发生异常" + ",url=" + url, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
        return result;
    }

    /**
     * post请求
     *
     * @param url
     * @return
     */
    public static byte[] post(String url, byte[] postContent) {
        String result = "";
        InputStream in = null;
        HttpURLConnection connection = null;
        OutputStream out = null;
        try {
            URL paostUrl = new URL(url);
            // 参数配置
            connection = (HttpURLConnection) paostUrl.openConnection();
            // connection.setRequestProperty("Accept","application/json");
            // connection.setRequestProperty("Content-Length","101");
            // connection.setRequestProperty("Authorization",
            // "platformID=\"3aaab189-1dcb-4783-8387-7304b10689a5\",password=\"+fzC3Tf6ZpZ2L\"");
            connection.setRequestProperty("Content-Type",
                    "application/json;charset=UTF-8");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(45000);

            // 打开连接
            out = connection.getOutputStream();
            out.write(postContent);
            in = connection.getInputStream();
            return inputStreamToByte(in);
        } catch (Exception e) {
            logger.error("post请求发生异常" + ",url=" + url, e);
            throw new RuntimeException("post请求发生异常" + ",url=" + url, e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
    }

    /**
     * InputStream转换成Byte
     *
     * @param in
     * @return byte
     * @throws Exception
     */
    public static byte[] inputStreamToByte(InputStream in) {
        ByteArrayOutputStream out = null;
        try {
            int BUFFER_SIZE = 4096;
            out = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int count = -1;
            while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
                out.write(data, 0, count);
            }

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * @param params
     * @return
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        String value = null;
        for (String key : params.keySet()) {
            value = params.get(key);
            if (value != null && value.length() > 0 && !value.equals("sign")) {
                sb.append(key + "=" + params.get(key) + "&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 5;
    private static final int DEFAULT_SO_TIMEOUT = 1000 * 60;

    public static String postTLS(String reqURL, Map<String, String> params) {
        String respData = "";
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT,
                DEFAULT_CONNECTION_TIMEOUT);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                DEFAULT_SO_TIMEOUT);
        X509TrustManager trustManager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {

            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            public void verify(String host, X509Certificate cert)
                    throws SSLException {
            }

            public void verify(String host, String[] cns, String[] subjectAlts)
                    throws SSLException {
            }

            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        try {
            SSLContext sslContext = SSLContext
                    .getInstance(SSLSocketFactory.TLS);
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext,
                    hostnameVerifier);
            httpClient.getConnectionManager().getSchemeRegistry()
                    .register(new Scheme("https", 443, socketFactory));
            HttpPost httpPost = new HttpPost(reqURL);
            if (null != params) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry
                            .getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(formParams,
                        DEFAULT_CHARSET));
            }
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                respData = EntityUtils.toString(entity, ContentType
                        .getOrDefault(entity).getCharset());
            }
            return respData;
        } catch (Exception e) {
            logger.error("post请求发生异常" + ",url=" + reqURL, e);
            return "Fault";
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public static String postJSON(String reqURL, String parameters) {
        String respData = "";
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT,
                DEFAULT_CONNECTION_TIMEOUT);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                DEFAULT_SO_TIMEOUT);
        X509TrustManager trustManager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {

            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            public void verify(String host, X509Certificate cert)
                    throws SSLException {
            }

            public void verify(String host, String[] cns, String[] subjectAlts)
                    throws SSLException {
            }

            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        try {
            SSLContext sslContext = SSLContext
                    .getInstance(SSLSocketFactory.TLS);
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext,
                    hostnameVerifier);
            httpClient.getConnectionManager().getSchemeRegistry()
                    .register(new Scheme("https", 443, socketFactory));
            HttpPost httpPost = new HttpPost(reqURL);
            httpPost.setHeader(
                    "Authorization",
                    "platformID=\"3aaab189-1dcb-4783-8387-7304b10689a5\",password=\"+fzC3Tf6ZpZ2L\"");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            if (null != parameters) {
                httpPost.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));
            }
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                respData = EntityUtils.toString(entity, ContentType
                        .getOrDefault(entity).getCharset());
            }
            return respData;
        } catch (Exception e) {
            logger.error("post请求发生异常" + ",url=" + reqURL, e);
            return "Fault";
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }


    public static void main(String[] args)  {
        HttpUtils httpUtils = new HttpUtils();
        Map map = new HashMap();
//        map.put("type", "0");
//        map.put("sub_type", "0");
//        map.put("time", "2016-09-01");
//        map.put("cityId", "1101");
//        String reqURL = "http://localhost:8080/Unicom-Show/dutyRatioInfo";
//        String postTLS = httpUtils.postTLS(reqURL, map);
//        System.out.println(postTLS);

//        HttpUtils httpUtils = new HttpUtils();
//		String url = "http://120.52.10.10:30004/arcgis/rest/services/wisdom/ss_grid/MapServer/0/query?geometry=116.279%40.028%116.299%40.048&geometryType=esriGeometryEnvelope&returnGeometry=false&f=json";
//		String str = HttpUtils.get(url, false);
//		JSONObject jsonObject = JSONObject.parseObject(str);
//		System.out.println(jsonObject.toJSONString());

//		HashMap<String, String> map = new HashMap<>();
//		map.put("geometry","116.279%40.028%116.299%40.048");
//		map.put("geometryType","esriGeometryEnvelope");
//		map.put("returnGeometry","false");
//		map.put("f","json");
//		String str = HttpUtils.postTLS("http://120.52.10.10:30004/arcgis/rest/services/wisdom/ss_grid/MapServer/0/query", map);
//		System.out.println(str);

//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        Map<String, Object> suecssMap = new HashMap<String, Object>();
//        suecssMap.put("result", 0);
//        suecssMap.put("msg", "成功");
//        resultMap.put("result", suecssMap);
//        JSONObject jsonObject = new JSONObject(resultMap);
//        System.out.println(jsonObject.toString());

//        System.out.println(MD5.md5("aa"));

        map.put("outStatistics", "{\n" +
                "    \"statisticType\": \"sum\",\n" +
                "    \"onStatisticField\": \"all_num\",\n" +
                "    \"outStatisticFieldName\": \"all_num\"\n" +
                "  },\n" +
                "{\n" +
                "    \"statisticType\": \"sum\",\n" +
                "    \"onStatisticField\": \"home_people_num\",\n" +
                "    \"outStatisticFieldName\": \"home_people_num\"\n" +
                "  },\n" +
                "{\n" +
                "    \"statisticType\": \"sum\",\n" +
                "    \"onStatisticField\": \"work_people_num\",\n" +
                "    \"outStatisticFieldName\": \"work_people_num\"\n" +
                "  }\n" +
                "");
        map.put("where","sub_type = 0 and city_code = '1101' and stat_time = '20161000'");
        map.put("outFields", "*");;
        map.put("f", "json");
        String s = HttpUtils.postTLS("http://172.16.0.204:6080/arcgis/rest/services/BDVP/ss_grid_city_all/MapServer/2/query", map);

    }
}
