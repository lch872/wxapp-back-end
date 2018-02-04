package com.oneway.tools.redirector.web.controller;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

import okhttp3.*;

//import static com.sun.activation.registries.LogSupport.log;

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
        String formId = getPara("formId", "null");

        Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where userId=?", openId);
        if (info == null){ // 没有申请过，创建新的
            Applyinfo apply = new Applyinfo();
            apply.setPartyId(Integer.parseInt(activityId));
            apply.setUserId(openId);
            apply.setConfirm(1);
            apply.setFormId(formId);
            apply.save();
        }else { // 已经有过记录，更新
            info.setConfirm(Integer.parseInt(confirm)).update();
            info.setFormId(formId).update();
        }

        renderText("{\"OK\":1}");

    }

    public void sendMessage() throws Exception {
        System.out.println("sendMessage");
        List<Applyinfo> lo = Applyinfo.dao.find("SELECT * from `applyinfo` where `confirm`=1 ORDER BY `createTime`");

        for (final Applyinfo item : lo) {

            sendMessageA(item.getUserId(),item.getFormId());
        }

        renderText("ok");

    }

    public boolean sendMessageA(String openId, String formId) throws Exception {



        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx2fdfd17e37781b91&secret=ddc696a333d44c713d8723f26d0e8182";
        String jsonData = null;
        OkHttpClient client1 = new OkHttpClient();
        Request tokenRequest = new Request.Builder().url(tokenUrl).build();

        Response response1 = client1.newCall(tokenRequest).execute();
        jsonData = response1.body().string();

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<String, Object>();

        map = gson.fromJson(jsonData, map.getClass());
        String access_token=(String) map.get("access_token");

        MediaType mediaType = MediaType.parse("application/json");







        JsonObject total = new JsonObject();

        JsonObject data = new JsonObject();
        JsonObject key1 = new JsonObject();
        key1.addProperty("value","第二组");
//        key1.addProperty("color","#4a4a4a");
        data.add("keyword1",key1);

        JsonObject key2 = new JsonObject();
        key2.addProperty("value","周末日常话剧活动");
//        key2.addProperty("color","#4a4a4a");
        data.add("keyword2",key2);

        JsonObject key3 = new JsonObject();
        key3.addProperty("value","客村站D出口丽影广场B座5栋1102室");
//        key3.addProperty("color","#4a4a4a");
        data.add("keyword3 ",key3);


        JsonObject key4 = new JsonObject();
        key4.addProperty("value","2018年2月10日 13:30");
//        key4.addProperty("color","#4a4a4a");
        data.add("keyword4 ",key4);

//        JsonObject key5 = new JsonObject();
//        key5.addProperty("value","请务必准时到达活动现场进行签到，如果不能参加，请与活动管理者取得联系，谢谢。");
////        key5.addProperty("color","#4a4a4a");
//        data.add("keyword5 ",key5);
        total.add("data",data);



        total.addProperty("template_id","JGEk6Ooi6hoi15TCrdPnwvwtcRWIsv7NtqHVw5rFLvU");
        total.addProperty("page","/pages/apply/apply");
        total.addProperty("form_id",formId);
        total.addProperty("touser",openId);
        total.addProperty("emphasis_keyword","keyword1.DATA");


        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=%s",access_token);
        //创建RequestBody对象，将参数按照指定的MediaType封装
        RequestBody requestBody = RequestBody.create(mediaType,total.toString());
        Request request = new Request
                .Builder()
                .post(requestBody)//Post请求的参数传递
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            response.body().close();

        System.out.println(access_token);
        System.out.println(total.toString());
        System.out.println(result);


            return true;
    }

    public void log() throws Exception {
        System.out.println("log");
        String openId = getPara("log", "null");


        System.out.println(openId);
        renderText("ok");
    }


}
