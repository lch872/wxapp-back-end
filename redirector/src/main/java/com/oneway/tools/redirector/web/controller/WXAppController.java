package com.oneway.tools.redirector.web.controller;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

import com.jfinal.kit.HttpKit;
import com.oneway.tools.redirector.model.Partylist;
import com.oneway.tools.redirector.model.Userlist;
import com.oneway.tools.redirector.model.Applyinfo;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WXAppController extends Controller {

    public void detail() throws Exception {
        System.out.println("detail");




        List<Partylist> list = Partylist.dao.find("select * from `partylist`");
        Partylist sss = list.get(0);

        String openId = getPara("openId");
        String actId = getPara("actId");

        List<Applyinfo> sss22 = Applyinfo.dao.find("SELECT * from `applyinfo` where `confirm`=1");
        sss.put("applied",sss22.size());

        if (openId != ""){
            System.out.println(openId);
            Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where confirm=1 and userId=? and partyId=?",openId, actId);
            sss.put("isApply",info == null?0:1);
        }else {
            sss.put("isApply",0);
        }


        Partylist act = Partylist.dao.findById(actId);
        long totalViews = act.getViews();
        sss.put("views",++totalViews);
        act.setViews(totalViews).update();

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

        List<Applyinfo> lo = Applyinfo.dao.find("SELECT * from `applyinfo` where `confirm`=1 ORDER BY `createTime` DESC LIMIT 6");
        List<String> icons = new ArrayList<String>();

        for (final Applyinfo item : lo) {
                   Userlist aUser = Userlist.dao.findFirst("select * from `userlist` where openId=?", item.getUserId());
                    icons.add(aUser.getAvatarUrl());
        }

        sss.put("userArr",icons);

        String st = Json.getJson().toJson(sss);
        renderText(st);
    }

    public void adduser() throws Exception {
        System.out.println("adduser");


        IndexController.trustEveryone();
        Userlist data =  getModel(Userlist.class, "", false);
        System.out.println(data);
        Userlist userlist = Userlist.dao.findFirst("select `id` from `userlist` where openId=?", data.getOpenId());
        if (userlist == null){
            data.save();
        }
        renderText("{\"OK\":1}");

    }


    public void main() throws Exception {
        System.out.println("main");



        List<Partylist> list = Partylist.dao.find("select id,title,`imageUrl` from partylist ORDER BY id DESC");

        for (final Partylist item : list) {
            item.put("url",String.format("../detail/detail?actId=%s",item.getId()));
        }

        String st = Json.getJson().toJson(list);
        System.out.println(st);
        renderText(st);
    }


    public void applied() throws Exception {
        System.out.println("applied");


        List<Userlist> lo = Userlist.dao.find("SELECT `nickName`,`avatarUrl`,`gender`,`openId` from `userlist`");

        String st = Json.getJson().toJson(lo);
        renderText(st);

    }


    public void getOpenId() throws Exception {
        String js_code = getPara("js_code");


        String aUrl = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=wx2fdfd17e37781b91&secret=ddc696a333d44c713d8723f26d0e8182&grant_type=authorization_code&js_code=%s",js_code);
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(aUrl).build();

        Response response = client.newCall(request).execute();
        result = response.body().string();


        renderText(result);
    }
    public void apply() throws Exception {
        System.out.println("apply");



        String openId = getPara("openId", "null");
        String activityId = getPara("activity", "null");
        String confirm = getPara("confirm", "0");

        Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where userId=?", openId);
        if (info == null){ // 没有申请过，创建新的
            Applyinfo apply = new Applyinfo();
            apply.setPartyId(Integer.parseInt(activityId));
            apply.setUserId(openId);
            apply.setConfirm(1);
            apply.save();
        }else { // 已经有过记录，更新
            info.setConfirm(Integer.parseInt(confirm)).update();
        }

        renderText("{\"OK\":1}");

    }



}
