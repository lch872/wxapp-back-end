package com.oneway.tools.redirector.web.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;


import java.util.*;

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


        String actId = getPara("actId");

        List<Applyinfo> sss22 = Applyinfo.dao.find("SELECT * from `applyinfo` where `confirm`=1");
        sss.put("applied",sss22.size());


        Partylist act = Partylist.dao.findById(actId);
        long totalViews = act.getViews();
        sss.put("views",++totalViews);
        act.setViews(totalViews).update();

        Map<String,Object> dd = new HashMap();
        dd.put("icon","money");
        dd.put("text",sss.getPay());

        Map<String,Object> dd2 = new HashMap();
        dd2.put("icon","time");
        dd2.put("text",sss.getDate());

        Map<String,Object> dd3 = new HashMap();
        dd3.put("icon","local");
        dd3.put("text",sss.getAddress());

        List<Map> pp = new ArrayList();
        pp.add(dd);
        pp.add(dd2);
        pp.add(dd3);

        sss.put("table",pp);

//        List<Userlist> lo = Userlist.dao.find("SELECT b.`avatarUrl` from `applyinfo` a left join `userlist` b on a.`userId`=b.`openId` WHERE a.`confirm` = 1 ORDER BY a.`createTime` DESC LIMIT 6");
//
//        sss.put("userArr",lo);

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



    public void appliedInfo() throws Exception {
        System.out.println("applied");
        String limit = getPara("limit");
        String actId = getPara("actId","actId");
        String openId = getPara("openId","openId");

        Map aMap = new HashMap();
        List<Userlist> lo = appliedList("500");
        aMap.put("appliedCount",lo.size());

        if (limit != null){
            aMap.put("appliedList",lo.subList(0,Integer.parseInt(limit)));
        }else {
            aMap.put("appliedList",lo);
        }


        if (openId != ""){
            System.out.println(openId);
            Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where confirm=1 and partyId=? and userId=?",actId, openId);
            aMap.put("isApply",info == null ? 0 : 1);
        }else {
            aMap.put("isApply",0);
        }

        String st = Json.getJson().toJson(aMap);
        renderText(st);

    }

    public List<Userlist> appliedList(String limit) throws Exception {
        String sql = String.format("SELECT b.`nickName`, b.`avatarUrl`,b.`gender`,b.`openId`  from `applyinfo` a left join `userlist` b on a.`userId`=b.`openId` WHERE a.`confirm` = 1 ORDER BY a.`createTime` LIMIT %s",limit);
        List<Userlist> lo = Userlist.dao.find(sql);

        return lo;
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

        String listString = getPara("group");

        makeGroup(listString);

        sendMessageA("sss","sss");


        renderText("ok");

    }

    public void makeGroup(String listString) throws Exception {
        JSONArray jsonArray = JSON.parseArray(listString);
        int group = 0;
        for (Object o : jsonArray) {
            JSONArray ja = (JSONArray) o;
            group++;
            for (Object o1 : ja) {
                JSONObject ja1 = (JSONObject) o1;
                Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where userId=?", ja1.getString("openId"));
                info.setGroup(group).update();
            }
        }
    }


    public boolean sendMessageA(String openId, String formId) throws Exception {




        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx2fdfd17e37781b91&secret=ddc696a333d44c713d8723f26d0e8182";
        String jsonData = null;
        OkHttpClient client1 = new OkHttpClient();
        Request tokenRequest = new Request.Builder().url(tokenUrl).build();

        Response response1 = client1.newCall(tokenRequest).execute();
        jsonData = response1.body().string();


        JSONObject obj22 = JSON.parseObject(jsonData);
        String access_token = obj22.getString("access_token");
        MediaType mediaType = MediaType.parse("application/json");



        String aa = "{\n" +
                "        \"keyword1\":{\n" +
                "            \"value\":\"第二组\"\n" +
                "        },\n" +
                "        \"keyword2\":{\n" +
                "            \"value\":\"周末日常话剧活动\"\n" +
                "        },\n" +
                "        \"keyword3 \":{\n" +
                "            \"value\":\"客村站D出口丽影广场B座5栋1102室\"\n" +
                "        },\n" +
                "        \"keyword4 \":{\n" +
                "            \"value\":\"2018年2月10日 13:30\"\n" +
                "        },\n" +
                "        \"keyword5 \":{\n" +
                "            \"value\":\"请务必准时到达活动现场进行签到，如果不能参加，请与活动管理者取得联系，谢谢。\"\n" +
                "        }\n" +
                "    }";

        Map<String, Object> total = new HashMap<>();

        JSONObject data = new JSONObject();
        JSONObject obj = JSON.parseObject(aa);
        total.put("data",obj);

        System.out.println(total);

        total.put("template_id","JGEk6Ooi6hoi15TCrdPnwvwtcRWIsv7NtqHVw5rFLvU");
        total.put("page","/pages/apply/apply");
        total.put("form_id",formId);
        total.put("touser",openId);
        total.put("emphasis_keyword","keyword1.DATA");


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
//
//    public void () throws Exception {
//        System.out.println("log");
//        String openId = getPara("log", "null");
//
//
//        System.out.println(openId);
//        renderText("ok");
//    }
}
