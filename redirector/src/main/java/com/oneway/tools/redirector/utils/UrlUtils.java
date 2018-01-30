package com.oneway.tools.redirector.utils;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {

    public static  final String patternString1 = "location.replace\\([\"'](.*?)[\"']\\)";
    public static  final String patternString2 = "navigate\\([\"'](.*?)[\"']\\)";
    public static  final String patternString3 = "location.href\\s*=\\s*[\"'](.*?)[\"']";
    public static  final String patternString4 = "location\\s*=\\s*[\"'](.*?)[\"']";
    public static  final String patternString5 = "<meta.+http-equiv=[\"']refresh[\"'].+content\\s*=\\s*.+url\\s*=\\s*(.*?)[\\s\"']\\s*/*>";
    public static  final String patternString6 = "<meta.+http-equiv=[\"']refresh[\"'].+content\\s*=\\s*.*;(.*?)[\\s\"']\\s*/*>";
    public static  final String patternString7 = "var web_store_link = \"(.*?)\";";
    public static  final String patternString8 = "[\"'](https*://itunes\\.apple\\.com[^\"']*)[\"']";
    public static  final String patternString9 = "[\"'](itms-apps://itunes\\.apple\\.com[^\"']*)[\"']";
    public static  final String patternString10 = "[\"'](https*://play\\.google\\.com[^\"']*)[\"']";
    public static  final String patternString11 = "[\"'](itms-apps://play\\.google\\.com[^\"']*)[\"']";

    public static List<Pattern> patternList = new ArrayList<>();
    static {
        patternList.add(Pattern.compile(patternString1, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString2, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString3, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString4, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString5, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString6, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString7, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString8, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString9, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString10, Pattern.DOTALL));
        patternList.add(Pattern.compile(patternString11, Pattern.DOTALL));
    }

    public static String parseUrl(String content) {
        String ret;
        List<Pattern> patterns = new ArrayList<>();
        try {
            File file = new File(PathKit.getRootClassPath() + File.separator + "regex.properties");
            List<String> list = FileUtils.readLines(file, "utf-8");
            for (String s : list) {
                patterns.add(Pattern.compile(s, Pattern.DOTALL));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (patterns.isEmpty()){
            patterns = patternList;
        }

        for (Pattern pattern : patterns) {
            Matcher m = pattern.matcher(content);
            if(m.find()) {
                ret = m.group(1);
                if (!StrKit.isBlank(ret)) {
                    return ret;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String html = "<html><body onload=\\\"document.location.replace(\"https://app.adjust.com/6acjbb?adgroup=7391132&install_callback=http%3A%2F%2Fservice.youappi.com%2Ftracking%2Freport%3FtrackerToken%3Dd5434f68-5bfe-48e7-a66a-5677fcd78109%26params%3Df61dbb8f-5bb1-4f02-85eb-8be0a6c3b3fb_72972_2017-10-30&event_callback_ntt6p3=http%3A%2F%2Fservice.youappi.com%2Ftracking%2Fevent%3Feventid%3Dsimpleregistration%26trackerToken%3Dd5434f68-5bfe-48e7-a66a-5677fcd78109%26params%3Df61dbb8f-5bb1-4f02-85eb-8be0a6c3b3fb_72972_2017-10-30&event_callback_4myn3s=http%3A%2F%2Fservice.youappi.com%2Ftracking%2Fevent%3Feventid%3Dbidderregistration%26trackerToken%3Dd5434f68-5bfe-48e7-a66a-5677fcd78109%26params%3Df61dbb8f-5bb1-4f02-85eb-8be0a6c3b3fb_72972_2017-10-30&idfa=2ADDDF07-C491-4F20-930F-E7E4446B595B&referrer=youappi_referrer\")\\\"></body></html>";
        String html2 = "<a href=\"http://www.111cn.net/sj/206/108522.htm\" title=\"中国移动手机营业厅怎么打印发票？\" alt=\"中国移动手机营业厅怎么打印发票？\" target=\"_blank\">中国移动手机营业厅怎么打印发票？</a>";
        String html3 = "aaaaaa window.location.href=\"logout.asp?act=logout\"";
        String html4 = "<html>\n    <head>\n        <noscript>\n    <meta http-equiv=\"refresh\" content=\"0; url=http://md.apptrknow.com/dir/click?placement_id=7619&campaign_id=22936333&affid=6043&cid=26327429308169XU06VRBWRFYDARFUHX&data1=[data1]&data2=[data2]&data3=[data3]&data4=[data4]&affsub1=test&device_id=61C82E29-0D8F-4587-9D86-8DBB459F90A8&idfa=61C82E29-0D8F-4587-9D86-8DBB459F90A8&gaid=61C82E29-0D8F-4587-9D86-8DBB459F90A8&uuid=514a9bca-2b92-47f8-aa65-8ce95f3a08cb&ref=apptrknow.com\"/>\n</noscript>\n\n        <script>\n    function redirect() {\n        window.location = 'http://md.apptrknow.com/dir/click?placement_id=7619&campaign_id=22936333&affid=6043&cid=26327429308169XU06VRBWRFYDARFUHX&data1=[data1]&data2=[data2]&data3=[data3]&data4=[data4]&affsub1=test&device_id=61C82E29-0D8F-4587-9D86-8DBB459F90A8&idfa=61C82E29-0D8F-4587-9D86-8DBB459F90A8&gaid=61C82E29-0D8F-4587-9D86-8DBB459F90A8&uuid=514a9bca-2b92-47f8-aa65-8ce95f3a08cb&ref=apptrknow.com';\n    }\n</script>\n    </head>\n    <body onload=\"redirect()\">\n        <script type=\"text/javascript\" data-cfasync=\"false\" src=\"//am.appwalls.mobi/ai/ai_script.js\"></script>\n<script type=\"text/javascript\">\n    if (typeof TTCbup  === \"function\") { TTCbup(\"514a9bca-2b92-47f8-aa65-8ce95f3a08cb\", \"MD\", {s1: '7619~22936333', se: 't'}, true, \"//am.appwalls.mobi/track\"); }\n</script>\n<noscript>\n    <img src=\"//am.appwalls.mobi/track?pid=MD&eid=514a9bca-2b92-47f8-aa65-8ce95f3a08cb&jsb=t&ckb=t&s1=7619~22936333&se=t&etp=c\" width=\"1\" height=\"1\" border=\"0\" />\n</noscript>\n\n        <img src=\"//statisticresearch.com/match?p=MD&adxguid=3a351f5a-1f68-459f-a62f-1a9f3c73454c\" width=\"1\" height=\"1\" border=\"0\" />\n    </body>\n</html>";
        String html5 = "<!DOCTYPE html>\n" +
                "<html lang=\"en-US\">\n" +
                "<head>\n" +
                "    <meta name=\"referrer\" content=\"never\">\n" +
                "</head>\n" +
                "<script language='javascript' type='text/javascript'>window.top.location.replace('http://clinkadtracking.com/tracking?camp=26537499&pubid=1971&subpubid=test&sid=3430f22bb38169249e36c2e1cd4adc28&gaid=bdabd8dc-4558-4dcd-8103-8a077fab1179&idfa=')</script>";

        String html6 = "<!DOCTYPE html><html lang=\"en\"><meta attr='' http-equiv=\"refresh\" rel=\"noreferrer\" content=\"0;url=/redirect?_time=1515539239&_sign=fb4e684eea74b3df909dc2f1e2a3ee2c&campid=10973989630074-9696&aff_sub=263168327961699I6XC4HYN6LQJ8R2DB&gaid=bdabd8dc-4558-4dcd-8103-8a077fab1179&sub_channel=169&ref=\"><head><meta charset=\"UTF-8\"><title>Going to landing page</title></head><body></body></html>";
        String html7 = "<html><head><meta name='referrer' content='never'><meta http-equiv='refresh' content='0;https://carrierapptraffic.go2affise.com/click?pid=36&offer_id=879978&sub1=5a55669893bfeb0001ad6725&sub2=11_14331492&sub3=&sub4=&sub5=1492_0'></head><body></body></html>";
        String html8 = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Wait a second ...</title>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>\n" +
                "    <meta name=\"robots\" content=\"noindex, nofollow\">\n" +
                "    <noscript>\n" +
                "        <meta http-equiv=\"refresh\" content=\"0;url=https://smartoffer.site/v/417ebae0-f5a2-11e7-883f-0141f5a4185c/c/b3a81d38-a91f-11e7-bb4f-02e85ca242fd/?clickid=%5Bclickid%5D&amp;pubid=6043-7619-303&amp;back=V7hWs2tDmfdcEE8hwD4Gt-XuMa0VVRWxs-Khznb5ItyXpBUfQtyYs2AzgiFekt_dkni6bAnn7LV54b3E9ht_QnCtqhAhFk3o_jCMOnvSf8Q&amp;_i=1&amp;_s=417e5b40-f5a2-11e7-bcd7-0141f5a4183e&amp;_r=&amp;_n=&_d=ns6\"/>\n" +
                "    </noscript>\n" +
                "    <meta http-equiv=\"refresh\" content=\"2;url=https://smartoffer.site/v/417ebae0-f5a2-11e7-883f-0141f5a4185c/c/b3a81d38-a91f-11e7-bb4f-02e85ca242fd/?clickid=%5Bclickid%5D&amp;pubid=6043-7619-303&amp;back=V7hWs2tDmfdcEE8hwD4Gt-XuMa0VVRWxs-Khznb5ItyXpBUfQtyYs2AzgiFekt_dkni6bAnn7LV54b3E9ht_QnCtqhAhFk3o_jCMOnvSf8Q&amp;_i=1&amp;_s=417e5b40-f5a2-11e7-bcd7-0141f5a4183e&amp;_r=&amp;_n=&_d=to6\"/>\n" +
                "    <meta name=\"referrer\" content=\"no-referrer\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<div style=\"visibility: hidden\">\n" +
                "    <div id=div-default><span id=span-default></span></div>\n" +
                "    <div id=div-sans-serif><span id=span-sans-serif></span></div>\n" +
                "    <div id=div-serif><span id=span-serif></span></div>\n" +
                "    <div id=div-monospace><span id=span-monospace></span></div>\n" +
                "</div>\n" +
                "<script type=\"text/javascript\">\n" +
                "        function hError() {\n" +
                "    }\n" +
                "    window.dynamicVariables = {\n" +
                "    3: true,\n" +
                "    4: 2,\n" +
                "    5: 'https://smartoffer.site/v/417ebae0-f5a2-11e7-883f-0141f5a4185c/c/b3a81d38-a91f-11e7-bb4f-02e85ca242fd/?clickid=%5Bclickid%5D&pubid=6043-7619-303&back=V7hWs2tDmfdcEE8hwD4Gt-XuMa0VVRWxs-Khznb5ItyXpBUfQtyYs2AzgiFekt_dkni6bAnn7LV54b3E9ht_QnCtqhAhFk3o_jCMOnvSf8Q&_i=1&_s=417e5b40-f5a2-11e7-bcd7-0141f5a4183e&_r=&_n=',\n" +
                "    7: '_d',\n" +
                "    9: 0\n" +
                "};\n" +
                "window.bbbutton = function(getRUrl) {\n" +
                "            try {\n" +
                "            var hUrl = 'https://smartoffer.site/h/417ebae0-f5a2-11e7-883f-0141f5a4185c/c/b3a81d38-a91f-11e7-bb4f-02e85ca242fd/?clickid=%5Bclickid%5D&pubid=6043-7619-303&back=V7hWs2tDmfdcEE8hwD4Gt-XuMa0VVRWxs-Khznb5ItyXpBUfQtyYs2AzgiFekt_dkni6bAnn7LV54b3E9ht_QnCtqhAhFk3o_jCMOnvSf8Q&_i=1&_s=417e5b40-f5a2-11e7-bcd7-0141f5a4183e&_r=&_n=&_d=' + jd.join('|');\n" +
                "            var htxt = 'Wait a second ...';\n" +
                "            history.replaceState(null, htxt, hUrl);\n" +
                "            history.pushState(null, htxt, hUrl);\n" +
                "            history.pushState(null, htxt, hUrl);\n" +
                "            history.pushState(null, htxt, hUrl);\n" +
                "            history.pushState(null, htxt, hUrl);\n" +
                "            location.href = getRUrl();\n" +
                "        } catch (exc) {\n" +
                "            jd[5] = ex2str(exc);\n" +
                "            location.replace(getRUrl());\n" +
                "        }\n" +
                "    }\n" +
                "</script>\n" +
                "<script src=\"/static.min.js?t=3\"></script>\n" +
                "</body>\n" +
                "</html>\n";
        String html9 = "<head><meta http-equiv=\"refresh\" content=\"0; url=http://52.77.99.53/acs.php?adid=14733899&auto=1&gaid=bdabd8dc-4558-4dcd-8103-8a077fab1179\"></head><meta name=\"referrer\" content=\"never\"><img src=\"http://13.113.11.185/imp.php?aff_sub1=r.cn&aff_sub2=9.9&aff_sub3=1&aff_sub4=usa&source=redirect&aff_sub5=1&os=android\" style=\"display:none\" rel=\"noreferrer\"><img src=\"http://13.113.11.185/imp.php?aff_sub1=r.cn&aff_sub2=9.9&aff_sub3=1&aff_sub4=usa&source=redirect&aff_sub5=2&os=android\" style=\"display:none\" rel=\"noreferrer\"><img src=\"http://13.113.11.185/imp.php?aff_sub1=r.cn&aff_sub2=9.9&aff_sub3=1&aff_sub4=usa&source=redirect&aff_sub5=3&os=android\" style=\"display:none\" rel=\"noreferrer\"><img src=\"http://13.113.11.185/imp.php?aff_sub1=r.cn&aff_sub2=9.9&aff_sub3=1&aff_sub4=usa&source=redirect&aff_sub5=4&os=android\" style=\"display:none\" rel=\"noreferrer\">";
        String html10 = "<html> <head> </head> <link href=\"https://fonts.googleapis.com/css?family=Open+Sans:600\" rel=\"stylesheet\"> <style> html, body { height: 100%; width: 100%; } body { background: #F5F6F8; font-size: 16px; font-family: 'Open Sans', sans-serif; color: #2C3E51; } .main { display: flex; align-items: center; justify-content: center; height: 100vh; } .main > div > div, .main > div > span { text-align: center; } .main span { display: block; padding: 80px 0 170px; font-size: 3rem; } .main .app img { width: 400px; } </style> <script type=\"text/javascript\"> var fallback_url = \"null\"; " +
                "var store_link = \"itms-apps://itunes.apple.com/us/app/id1036661603?ls=1&mt=8\"; " +
                "var web_store_link = \"https://itunes.apple.com/us/app/id1036661603?mt=8\"; " +
                "var loc = window.location; function redirect_to_web_store(loc) { loc.href = web_store_link; } function redirect(loc) { loc.href = store_link; if (fallback_url.startsWith(\"http\")) { setTimeout(function() { loc.href = fallback_url; },5000); } } </script> <body onload=\"redirect(loc)\"> <div class=\"main\"> <div class=\"workarea\"> <div class=\"logo\"> <img src=\"https://cdnappicons.appsflyer.com/app|id1036661603.png\" style=\"width:200px;height:200px;border-radius:20px;\" onclick=\"redirect_to_web_store(loc)\"/> </div> <span>Rolling Sky</span> <div class=\"app\"> <img src=\"https://cdn.appsflyer.com/af-statics/images/rta/app_store_badge.png\" onclick=\"redirect_to_web_store(loc)\"/> </div> </div> </div> </body> </html>";
        String html11 = "<html> <head \"var store_link = \"itms-apps://itunes.apple.com/us/app/id1036661603?ls=1&mt=8\\\"; \" > </head></html>";
        String html12 = "<html> <head \"var store_link = \"http://itunes.apple.com/us/app/id1036661603?ls=1&mt=8\\\"; \" > </head></html>";


        String url = parseUrl(html4);
        System.out.println(url);
    }
}
