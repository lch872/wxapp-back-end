package com.oneway.tools.redirector.web.controller;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.oneway.tools.redirector.model.Report;
import com.oneway.tools.redirector.utils.UrlUtils;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * offer 跳转测试，需记录：
 * 1. 能到商店页，记录全部 url 经过
 * 2. 不能到商店页，记录经过的 url，记录最后一跳失败的 body
 */
public class IndexController extends Controller {

    private static Map<String, String> iosHeader = new HashMap<>();
    private static Map<String, String> androidHeader = new HashMap<>();
    private static List<Integer> RedirectHttpCode = new ArrayList<>();
    private static Log log = Log.getLog(IndexController.class);

    static {

        RedirectHttpCode.add(301);
        RedirectHttpCode.add(302);
        RedirectHttpCode.add(303);
        RedirectHttpCode.add(304);
        RedirectHttpCode.add(305);
        RedirectHttpCode.add(306);
        RedirectHttpCode.add(307);
        RedirectHttpCode.add(308);

        iosHeader.put("Accep", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        iosHeader.put("Accept-Encoding", "gzip, deflate");
        iosHeader.put("Accept-Language", "zh-CN,zh;en-us,en;q=0.8");
        iosHeader.put("Connection", "keep-alive");
        iosHeader.put("upgrade-insecure-requests", "1");
        iosHeader.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0_3 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A432 Safari/604.1");

        androidHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        androidHeader.put("Accept-Encoding", "gzip, deflate");
        androidHeader.put("Accept-Language", "zh-CN,zh;en-us,en;q=0.8");
        androidHeader.put("Connection", "keep-alive");
        androidHeader.put("upgrade-insecure-requests", "1");
        androidHeader.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 8.0.1; zh-cn; Redmi 3S Build/MMB29M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.146 Mobile Safari/537.36 XiaoMi/MiuiBrowser/9.1.3");
//        androidHeader.put("User-Agent", "Mozilla/5.0 (Linux; Android 4.1.2; GT-I8552 Build/JZO54K) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19")
    }

    /**
     * 信任所有证书
     */
    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            log.error("trustEveryone error");
        }
    }

    public void index() {

        List  ddd = test(30);
        String st = Json.getJson().toJson(ddd);

        renderText(st);
    }

static boolean isEnable = true;
    public void getBet() {
        String eStr = getPara("enable");
         if (eStr!=null){
             isEnable = eStr.equals("1");
         }

        if (isEnable){
            String url = "http://42.51.174.12:8877/getAllBuyRecords?page=1&rows=10";
            String json = loadJSON(url);
            renderText(json);
        }else {

            String  fake = "{\"rows\": [\n" +
                    "\t{\n" +
                    "\t\"id\": 0,\n" +
                    "\t\"buyLeague\": \"A组超级杯联赛\",\n" +
                    "\t\"buyTeam\": \"古德曼 vs 阿克奇\",\n" +
                    "\t\"buyDetail\": \"(滚球) (全场大小) 大2.5球，水位1.050 \",\n" +
                    "\t\"buyMoney\": 50.0,\n" +
                    "\t}\n" +
                    "],\n" +
                    "\"total\": 10\n" +
                    "}\n";

            JSONObject json_test = JSONObject.fromObject(fake);

            renderJson(json_test);

        }
    }

    static long lastTime = 0;
    public void getMsg() {

        lastTime = Long.parseLong(getPara("t"));
        String url = "http://42.51.174.12:8877/getAllPolog";
        String json = loadJSON(url);
        renderText(json);
    }


    public void getOnline() {
        String url = "http://42.51.196.4:8877/cross/getAllOnLineClient";
        String json = loadJSON(url);
        renderText(json);
    }

    public void getSearch() {
        String url = "http://42.51.174.12:8877/getAllSearchRecords?page=1&rows=20";
        String json = loadJSON(url);
        renderText(json);
    }





    public static String loadJSON(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream(),"utf-8"));//防止乱码
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return json.toString();
    }






    public void getdata() throws Exception {
        String time = getPara("time", "2018-1-24");

        Map m1 = new HashMap();
        Map ios = makeData(time,"ios");
        Map andr = makeData(time,"android");
        m1.put("ios", ios);
        m1.put("android", andr);

        String st = Json.getJson().toJson(m1);

        renderText(st);
    }





    public List<Report> checkLineData(String startTime, String endTime, String platform, String type) throws Exception {
        String ss =  String.format("SELECT DISTINCT MainTable.`createTime`,IFNULL(SubTable.`TotalNum`, 0) AS TotalNum FROM `report` AS MainTable LEFT JOIN  \n" +
                "(SELECT `createTime`,COUNT(1) AS TotalNum FROM `report` WHERE `errorType`='js_error' and `platform`='ios' GROUP BY `createTime`) AS SubTable  \n" +
                "ON MainTable.`createTime` = SubTable.`createTime`", startTime, endTime, platform);
        List<Report> li = Report.dao.find(ss);

        return  li;
    }

    public  Map makeData(String time, String platform) throws Exception{
        Map m1 = new HashMap();

        String total = String.format("select `errorType`AS name, count(`errorType`) AS value\n" +
                "from `report` where `createTime` = '%s' and `platform` = '%s'\n" +
                "group by `errorType`",time,platform);
        List<Report> totalArr = Report.dao.find(total);
        m1.put("allErr", totalArr);



        List<Report> adErr = check(time, platform,"ad_error");
        if ( adErr.size() > 0) {
            m1.put("ad_error", adErr);
        }

        List<Report> initErr = check(time, platform,"initialization_error");
        if ( initErr.size() > 0) {
            m1.put("initialization_error", initErr);
        }

        List<Report> jsErr = check(time, platform,"js_error");
        if ( jsErr.size() > 0) {
            m1.put("js_error", jsErr);
        }

        List<Report> playerErr = check(time, platform,"videoplayer_error");
        if ( playerErr.size() > 0) {
            m1.put("videoplayer_error", playerErr);
        }

        return m1;
    }


    public List<Report> check(String time, String platform, String type) throws Exception {

        String ss =  String.format("select `message` AS name, count(`message`) AS value\n" +
                "from `report` where `createTime` = '%s' and `platform` = '%s' and `errorType` = '%s'\n" +
                "group by `message`", time, platform, type);
        List<Report> li = Report.dao.find(ss);

        return  li;
    }




    public void execRedirect() throws Exception {
        Result result = new Result();
        List<String> redirectUrls = new ArrayList<>();

        String url = getPara("url");
        if (url == null || !url.startsWith("http")) {
            String errorMsg = "param error";
            renderFail(result, errorMsg, redirectUrls);
            return;
        }

        String platform = getPara("platform", "ios").toLowerCase();
        Map<String, String> headerMap = getHeaderByPlatform(platform);
        String nextUrl = "";

        while (true) {
            if (url.startsWith("https")) {
                trustEveryone();
            }

            Connection.Response response;
            try {
                response = Jsoup.connect(url).timeout(10000).headers(headerMap).ignoreContentType(true).followRedirects(false).execute();
            } catch (Exception e) {
                renderFail(result, "Exception : " + e.getMessage(), redirectUrls);
                return;
            }

            if (response == null) {
                String errorMsg = "response is null";
                renderFail(result, errorMsg, redirectUrls);
                return;
            }

            Integer responseCode = response.statusCode();
            if (responseCode >= 400){
                renderFail(result, "responseCode:" + responseCode + "-" + response.body(), redirectUrls);
                return;
            }

            if (responseCode >= 200 && responseCode < 300){
                String html = response.body();
                if (html != null) {
                    nextUrl = UrlUtils.parseUrl(html);
                }
                if (StrKit.isBlank(nextUrl)) {
                    renderFail(result, html, redirectUrls);
                    return;
                }else {
                    //特殊处理，跳转记录显示匹配出来的 url 有是带"'"的 'http://becanium.com/59M35/ULUM/XrEc/Bv1L1w_yXek-e3IK6C75-9bcHmLix4DTeDniF2mrVKeDof_dRdQS?VLw=WW_MS'
                    nextUrl = nextUrl.replaceAll("'", "").replaceAll("\"", "");
                }
            }

            if (RedirectHttpCode.contains(responseCode)) {
                nextUrl = response.header("Location");
            }

            if (StrKit.isBlank(nextUrl)){
                String errorMsg = "get url from location is blank";
                renderFail(result, errorMsg, redirectUrls);
                return;
            }

            if (nextUrl.startsWith("/")) {
                URL preUrl = new URL(url);
                nextUrl = preUrl.getProtocol() + "://" + preUrl.getHost() + nextUrl;
            }

            redirectUrls.add(nextUrl);

            if (nextUrl.startsWith("market") || nextUrl.startsWith("itms-appss") || nextUrl.contains("itunes.apple.com") || nextUrl.contains("play.google.com")) {
                result.setSuccess(true);
                result.setUrls(redirectUrls);
                renderJson(result);
                return;
            }

            url = nextUrl;
        }
    }

    private void renderFail(Result result, String errorMsg, List<String> urls){
        result.setMsg(errorMsg);
        result.setSuccess(false);
        result.setUrls(urls);
        log.error(errorMsg);
        renderJson(result);
    }

    private Map<String, String> getHeaderByPlatform(String ua) {
        if (ua.equalsIgnoreCase("ios")) {
            return iosHeader;
        }
        if (ua.equalsIgnoreCase("android")) {
            return androidHeader;
        }
        return new HashMap<>(0);
    }

    public static void main(String[] args) {
        String str = "'http://becanium.com/59M35/ULUM/XrEc/Bv1L1w_yXek-e3IK6C75-9bcHmLix4DTeDniF2mrVKeDof_dRdQS?VLw=WW_MS'";
        str = str.replaceAll("'", "").replaceAll("\"", "");
        System.out.println(str);
    }



    public static ArrayList<String> test(int intervals ) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        ArrayList<String> fetureDaysList = new ArrayList<>();
        for (int i = 0; i <intervals; i++) {
            pastDaysList.add(getPastDate(i));
        }
        return pastDaysList;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
//        Log.e(null, result);
        return result;
    }
}
