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

public class WXAppController extends Controller {

    public void detail() throws Exception {

        List<Partylist> list = Partylist.dao.find("select * from `partylist`");
        Partylist sss = list.get(0);

        String openId = getPara("openId");
        String actId = getPara("actId");

        List<Applyinfo> sss22 = Applyinfo.dao.find("SELECT * from `applyinfo` where `confirm`=1");
        sss.put("applied",sss22.size());

        System.out.printf("%s",openId);
        Userlist user= Userlist.dao.findFirst("select `id` from `userlist` where openId=?", openId);

        Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where confirm=1 and userId=? and partyId=?",user.getId(), actId);
        sss.put("isApply",info == null?0:1);

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

        List<Userlist> lo = Userlist.dao.find("SELECT `avatarUrl` from `userlist` LIMIT 6");
        List<String> urls = lo.stream().map(userlist -> userlist.getAvatarUrl()).collect(Collectors.toList());
        sss.put("userArr",urls);

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


    public void main() throws Exception {

        List<Partylist> list = Partylist.dao.find("select id,title,`imageUrl` from partylist ORDER BY id DESC");

        for (final Partylist item : list) {
            item.put("url",String.format("../detail/detail?actId=%s",item.getId()));

        }

        String st = Json.getJson().toJson(list);

        renderText(st);
    }


    public void applied() throws Exception {

        List<Userlist> lo = Userlist.dao.find("SELECT `nickName`,`avatarUrl`,`gender`,`openId` from `userlist`");

        String st = Json.getJson().toJson(lo);
        renderText(st);

    }

    public void apply() throws Exception {

        String openId = getPara("openId", "null");
        String activityId = getPara("activity", "null");
        String confirm = getPara("confirm", "0");
        Userlist userlist = Userlist.dao.findFirst("select `id` from `userlist` where openId=?", openId);


        Applyinfo info = Applyinfo.dao.findFirst("select * from `applyinfo` where userId=?", userlist.getId());
        if (info == null){ // 没有申请过，创建新的
            Applyinfo apply = new Applyinfo();
            apply.setPartyId(Integer.parseInt(activityId));
            apply.setUserId(userlist.getId().intValue());
            apply.setConfirm(1);
            apply.save();
        }else { // 已经有过记录，更新
            info.setConfirm(Integer.parseInt(confirm)).update();
        }

        renderText("{\"OK\":1}");

    }



}
