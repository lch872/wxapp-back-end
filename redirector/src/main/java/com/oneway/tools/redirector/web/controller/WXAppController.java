package com.oneway.tools.redirector.web.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;


import java.util.*;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.oneway.tools.redirector.model.Partylist;
import com.oneway.tools.redirector.model.Userlist;
import com.oneway.tools.redirector.model.Applyinfo;

import okhttp3.*;

import static com.jfinal.plugin.activerecord.Db.find;

//import static com.sun.activation.registries.LogSupport.log;

public class WXAppController extends Controller {

    Log log = Log.getLog(WXAppController.class);

    public void detail() throws Exception {
        System.out.println("detail");

        String actId = getPara("actId");

        Partylist sss = Partylist.dao.findById(actId);
        long totalViews = sss.getViews();
        sss.put("views",++totalViews);
        sss.setViews(totalViews).update();


        List<Applyinfo> sss22 = Applyinfo.dao.find("SELECT * from `applyinfo` where `confirm`=1 and `partyId`=?",actId);
        sss.put("applied",sss22.size());




        Map<String,Object> dd = new HashMap();
        dd.put("icon","money");
        dd.put("tag","费用:");
        dd.put("text",sss.getPay());

        Map<String,Object> dd2 = new HashMap();
        dd2.put("icon","time");
        dd2.put("tag","时间:");
        dd2.put("text",sss.getDate());

        Map<String,Object> dd3 = new HashMap();
        dd3.put("icon","local");
        dd3.put("tag","地点:");
        dd3.put("text",sss.getAddress());

        List<Map> pp = new ArrayList();
        pp.add(dd);
        pp.add(dd2);
        pp.add(dd3);

        sss.put("table",pp);

        String st = Json.getJson().toJson(sss);
        renderText(resultText(1,"OK",st));
    }

    public void adduser() throws Exception {
        System.out.println("adduser");


        IndexController.trustEveryone();
        Userlist data =  getModel(Userlist.class, "", false);
//        System.out.println(data);
        Userlist user = Userlist.dao.findFirst("select * from `userlist` where openId=?", data.getOpenId());
        if (user == null){
            data.save();
        }else {
            user.setNickName(data.getNickName()).update();
            user.setAvatarUrl(data.getAvatarUrl()).update();
            user.setGender(data.getGender()).update();
        }
        renderText(resultText(1,"加入用户成功"));

    }




    public void main() throws Exception {
        System.out.println("main");
//        grouplll();
        List<Partylist> list = Partylist.dao.find("select id,title,`imageUrl` from partylist ORDER BY id DESC");

        String st = Json.getJson().toJson(list);
        System.out.println(st);
        renderText(resultText(1,"OK",st));
    }



    public void appliedInfo() throws Exception {
        System.out.println("applied");
        Integer limit = getParaToInt("limit");
        String actId = getPara("actId","actId");
        String openId = getPara("openId","openId");

        Map aMap = new HashMap();
        String sql = String.format("SELECT b.`nickName`, b.`avatarUrl`,b.`gender`,b.`openId`, a.`tag` from `applyinfo` a left join `userlist` b on a.`userId`=b.`openId` WHERE a.`confirm` = 1 and a.`partyId` = %s ORDER BY a.`updataTime` DESC", actId);
        List<Userlist> lo = Userlist.dao.find(sql);


        aMap.put("appliedCount",lo.size());

        if (openId.equals("ou34b5Naz-ZGQAVMz71BtM-Jef90") || openId.equals("ou34b5HL6d4TB32oehkQRQbkExY8") || openId.equals("ou34b5Edr7HYCOYJfG8Pm48eIARg")){
            aMap.put("sudo",1);
        }




        if (limit != null && lo.size() >= limit){
            aMap.put("appliedList",lo.subList(0,limit));
        }else {
            aMap.put("appliedList",lo);
        }


        if (openId != ""){
            System.out.println(openId);
            Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where confirm=1 and partyId=? and userId=?",actId, openId);
            aMap.put("isApply",info == null ? 0 : 1);
            if (info != null) {
                aMap.put("tag", info.getTag());
            }
        }else {
            aMap.put("isApply",0);
        }

        String st = Json.getJson().toJson(aMap);
        renderText(resultText(1,"OK",st));

    }


    public void getOpenId() throws Exception {
        String js_code = getPara("js_code");


        String aUrl = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=wx2fdfd17e37781b91&secret=ddc696a333d44c713d8723f26d0e8182&grant_type=authorization_code&js_code=%s",js_code);
        String result = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(aUrl).build();

        Response response = client.newCall(request).execute();
        result = response.body().string();


        renderText(resultText(1,"OK",result));

    }
    public void apply() throws Exception {
        System.out.println("apply");

        Integer actId = getParaToInt("activity", 0);
        Integer isConfirm = getParaToInt("confirm",0);
        String tag = getPara("tag", "初出茅庐");
        String openId = getPara("openId", "NULL");
        String formId = getPara("formId", "NULL");

        List<Applyinfo> applyList = Applyinfo.dao.find("SELECT * from `applyinfo` where `confirm`=1 and `partyId`=?",actId);
        Partylist party = Partylist.dao.findById(actId);

        if ( isConfirm == 1 && applyList.size() >= party.getLimit() ){
            renderText(resultText(0,"剧组盒饭已达上限，请关注下一期"));
            return;
        }



        Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where userId=?", openId);


        if (info == null){ // 没有申请过，创建新的
            Applyinfo apply = new Applyinfo();
            apply.setPartyId(actId);
            apply.setUserId(openId);
            apply.setConfirm(1);
            apply.setFormId(formId);
            apply.setTag(tag);
            apply.save();
        }else { // 已经有过记录，更新
            info.setConfirm(isConfirm).update();
            info.setFormId(formId).update();
            info.setTag(tag).update();
        }


        renderText(resultText(isConfirm,isConfirm == 1 ?"报名成功" : "操作成功"));

    }

    public void sendMessage() throws Exception {
        System.out.println("sendMessage");

        String listString = getPara("group","NULL");
        Integer actId = getParaToInt("actId");

        String msgType = getPara("type");

        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx2fdfd17e37781b91&secret=ddc696a333d44c713d8723f26d0e8182";
        OkHttpClient client1 = new OkHttpClient();
        Request tokenRequest = new Request.Builder().url(tokenUrl).build();

        Response response1 = client1.newCall(tokenRequest).execute();
        String jsonData = response1.body().string();

        JSONObject obj22 = JSON.parseObject(jsonData);
        String access_token = obj22.getString("access_token");
        MediaType mediaType = MediaType.parse("application/json");

        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=%s",access_token);



        if (!listString.equals("NULL")) {
            Partylist party = Partylist.dao.findById(actId);
            party.setGroupInfo(listString).update();
            makeGroup(listString);
        }

        if(msgType.equals("reminder")){
            List<Applyinfo> infoList = Applyinfo.dao.find("select * from `applyinfo` where confirm=1 and partyId=?",actId);
            System.out.println(infoList);

            for (Applyinfo info : infoList) {
                sendReminder(info, mediaType, url);
            }
        }else if (msgType.equals("feedback")){
            List<Applyinfo> infoList = Applyinfo.dao.find("select * from `applyinfo` where confirm=1 and `arrived`=1 and partyId=?",actId);
            for (Applyinfo info : infoList) {
                sendFeedback(info, mediaType, url);
            }
        }


        renderText(resultText(1,"发送成功"));

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

    public void sendReminder(Applyinfo info,MediaType type,String url) throws Exception {
        char[] numArray = { '零', '一', '二', '三', '四', '五', '六', '七', '八', '九' };
        String groupName = String.format("第%s组",numArray[info.getGroup()]);
        Partylist party = Partylist.dao.findFirst("select * from `partylist` where id=?",info.getPartyId());


        String aa = String.format("{\n" +
                "        \"keyword1\":{\n" +
                "            \"value\":\"%s\"\n" +
                "        },\n" +
                "        \"keyword2\":{\n" +
                "            \"value\":\"%s\"\n" +
                "        },\n" +
                "        \"keyword3\":{\n" +
                "            \"value\":\"%s\"\n" +
                "        },\n" +
                "        \"keyword4\":{\n" +
                "            \"value\":\"%s\"\n" +
                "        },\n" +
                "        \"keyword5\":{\n" +
                "            \"value\":\"请务必准时到达活动现场进行签到，如时间上有冲突不能参加，请与活动主办者取得联系，谢谢。\"\n" +
                "        }\n" +
                "    }", groupName, party.getTitle(), party.getAddress(), party.getDate());

        Map<String, Object> total = new HashMap<>();

        JSONObject data = new JSONObject();
        JSONObject obj = JSON.parseObject(aa);
        total.put("data",obj);

        System.out.println(total);

        total.put("template_id","XcG32liQDgwwxzuf7rOE-via6CPieBhTuhqu9r7HnBY");
        total.put("page","/pages/group/group");
        total.put("form_id",info.getFormId());
        total.put("touser",info.getUserId());
        total.put("emphasis_keyword","keyword1.DATA");
        System.out.println(total);

        sendNotification(url,type,JSON.toJSONString(total));
    }

    public void sendFeedback(Applyinfo info,MediaType type,String url) throws Exception {
        String aa = String.format("{\"keyword1\":{\"value\":\"感谢你参加本次活动，我们希望你能分享你的感受和意见。\"},\n" +
                "\"keyword2\":{\"value\":\"众乐话剧社\"}}");

        System.out.println(aa);

        Map<String, Object> total = new HashMap<>();

        JSONObject data = new JSONObject();
        JSONObject obj = JSON.parseObject(aa);
        total.put("data",obj);

        System.out.println(total);

        total.put("template_id","hn4Dsc43XIMgZ_PdMsC1KpHCsanVv_cx3nZD1wydDcg");
        total.put("page","pages/feedback/feedback?actId=3");
        total.put("form_id",info.getFormIdFinal());
        total.put("touser",info.getUserId());
        System.out.println(total);

        sendNotification(url,type,JSON.toJSONString(total));
    }

    public void sendNotification(String url, MediaType type, String body) throws Exception {

        RequestBody requestBody = RequestBody.create(type,body);
        Request request = new Request.Builder().post(requestBody).url(url).build();
        OkHttpClient client = new OkHttpClient();

        Response response = client.newCall(request).execute();
        String result = response.body().string();
        response.body().close();
    }


    public void log() throws Exception {
        System.out.println("log");
        String openId = getPara("log", "null");


        System.out.println(openId);
        renderText(resultText(1,"OK"));
    }
//
    public void groupList() throws Exception {
        System.out.println("log");

        Integer actId = getParaToInt("actId");
        Partylist party = Partylist.dao.findById(actId);

        Map<String,Object> res = new HashMap<>();
        res.put("hasGroup",0);
        if (party.getGroupInfo() != null){
            System.out.println(party.getGroupInfo());
            res.put("hasGroup",1);
            String group = party.getGroupInfo();
            JSONArray jsonArray = JSON.parseArray(group);
            System.out.println(jsonArray);
            res.put("list",jsonArray);
        }

        renderText(resultText(1,"OK",JSON.toJSONString(res)));
    }

    public void signIn() throws Exception {
        String actId = getPara("actId");
        String openId = getPara("openId");
        Partylist party = Partylist.dao.findById(actId);
        List<Object> sss = new ArrayList<>();
        if (party.getGroupInfo() != null ) {
            String group = party.getGroupInfo();
            JSONArray jsonArray = JSON.parseArray(group);
            sss = jsonArray;

            loop: for (final Object o : jsonArray) {
                    JSONArray ja = (JSONArray) o;
                    for (final Object o1 : ja) {
                        JSONObject ja1 = (JSONObject) o1;
                        if (openId.equals(ja1.get("openId"))) {
                            ja1.put("signIn", 1);
                            party.setGroupInfo(jsonArray.toJSONString()).update();
                            renderText(resultText(1,"签到成功"));
                            return;
                        }
                    }
            }

            renderText(resultText(0,"剧组未找到你的角色提名"));
            return;
        }

        renderText(resultText(0,"剧组筹备中，请等待通告"));


    }

    public String resultText(int isOk,String msg, String data) throws Exception {
        return String.format("{\"success\":%d,\"message\":\"%s\",\"content\":%s}",isOk,msg,data);
    }

    public String resultText(int isOk,String msg) throws Exception {
        return String.format("{\"success\":%d,\"message\":\"%s\"}",isOk,msg);
    }


    public void grouplll() throws Exception {

        List<Record> re =  Db.find("select GROUP_CONCAT(userId) name,`group`  from `applyinfo`where `partyId`=3 GROUP BY `group`");

        for (Record record : re) {
            String ss =record.get("name");
            String[] ppp =  ss.split(",");
            List<String> stringB = Arrays.asList(ppp);
            System.out.println(stringB);
        }

        renderText("");
    }

    public void addAct() throws Exception {
        System.out.println("addAct");
        String openId = getPara("openId");
        System.out.println(openId);

        if (openId.equals("ou34b5Naz-ZGQAVMz71BtM-Jef90")||openId.equals("ou34b5HL6d4TB32oehkQRQbkExY8")||openId.equals("ou34b5Edr7HYCOYJfG8Pm48eIARg")){
            Partylist data =  getModel(Partylist.class, "", true);
            data.save();
            renderText(resultText(1,"操作成功"));
            return;
        }

        renderText(resultText(0,"操作被禁止"));
    }

    public void feedback() throws Exception {
        System.out.println("feedback");
        String openId = getPara("openId");
        String advice = getPara("advice", "无评论");
        String actId = getPara("actId","3");
        String star = getPara("star","0");

        System.out.println(openId);
        System.out.println(advice);
        System.out.println(actId);

        Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where confirm=1 and partyId=? and userId=?",actId, openId);
        info.setAdvice(advice).update();
        info.setStar(star).update();

        renderText(resultText(1,"提交成功"));
    }

    public void saveFinalFormId() throws Exception {
        System.out.println("saveFinalFormId");
        String openId = getPara("openId");
        String actId = getPara("actId","3");
        String finalFormId = getPara("finalFormId");

        System.out.println(openId);
        System.out.println(actId);

        Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where confirm=1 and partyId=? and userId=?",actId, openId);
        info.setFormIdFinal(finalFormId).update();
        renderText(resultText(1,"提交成功"));
    }


}
