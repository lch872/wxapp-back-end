package com.oneway.tools.redirector.web.controller;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.HttpKit;
import com.oneway.tools.redirector.model.Partylist;
import com.oneway.tools.redirector.model.Userlist;

public class WXAppController extends Controller {

    public void main() throws Exception {
        List<Partylist> list = Partylist.dao.find("select * from `partylist`");
        Partylist sss = list.get(0);

        Map dd = new HashMap();
        dd.put("icon","money");
        dd.put("text",sss.getPay());

        Map dd2 = new HashMap();
        dd2.put("icon","time");
        dd2.put("text",sss.getDate());

        Map dd3 = new HashMap();
        dd3.put("icon","local");
        dd3.put("text",sss.getAddress());

        List<Map> pp = new ArrayList();
        pp.add(dd);
        pp.add(dd2);
        pp.add(dd3);


        sss.put("table",pp);

        List<String> sList = new ArrayList<String>();
        sList.add("../images/icon.jpg");
        sList.add("https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIGDumd2Fd9bKjJsy8YaAICc8Aa9eeicJELqEDYyvOm5fPVJm5elVBXga2QzB7adwsGB1fcP2U9nZg/0");
        sList.add("../images/icon.jpg");
        sList.add("https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIGDumd2Fd9bKjJsy8YaAICc8Aa9eeicJELqEDYyvOm5fPVJm5elVBXga2QzB7adwsGB1fcP2U9nZg/0");
        sList.add("../images/icon.jpg");
        sList.add("https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIGDumd2Fd9bKjJsy8YaAICc8Aa9eeicJELqEDYyvOm5fPVJm5elVBXga2QzB7adwsGB1fcP2U9nZg/0");


        sss.put("userArr",sList);

        sss.put("views",3699);

        String st = Json.getJson().toJson(sss);
        renderText(st);
    }

    public void adduser() throws Exception {

        IndexController.trustEveryone();
        Userlist data =  getModel(Userlist.class, "", false);
        System.out.println(data);
        Userlist userlist = Userlist.dao.findFirst("select `id` from `userlist` where openId=?", data.getOpenId());
        if (userlist == null){
            data.save();
        }
        renderText("{\"OK\":1}");

    }





}
