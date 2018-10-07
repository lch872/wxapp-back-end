package com.oneway.tools.redirector.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import com.oneway.tools.redirector.web.controller.IndexController;
public class TimeJob implements Runnable {

    Log log = Log.getLog(WXAppController.class);
    int lastMsgId = 0;
    public void run() {

        String requestBody = IndexController.loadJSON("http://42.51.174.12:8877/getAllPolog");
        JSONArray jsStr = JSONObject.parseArray(requestBody);
        if (jsStr.size()==0){
            return;
        }

       JSONObject item = jsStr.getJSONObject(0);
        int nowId = item.getInteger("id");
        if (lastMsgId != nowId){

            long lastTime = IndexController.lastTime;
            long nowTime = System.currentTimeMillis() / 1000;
            if (nowTime - lastTime < 60){
                lastMsgId = nowId;
                return;
            }

            StringBuffer emailContent = new StringBuffer("");
            for (int i=jsStr.size();i>0;i--){
                JSONObject subItem = jsStr.getJSONObject(i-1);
                if (subItem.getInteger("id") > lastMsgId){
                    emailContent.append(subItem.getString("addTime"));
                    emailContent.append("\n");
                    emailContent.append(subItem.getString("content"));
                    emailContent.append("\n\n");
                }
            }





            Properties properties = new Properties();
            properties.put("mail.transport.protocol", "smtp");// 连接协议
            properties.put("mail.smtp.host", "smtp.qq.com");// 主机名
            properties.put("mail.smtp.port", 465);// 端口号
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
            properties.put("mail.debug", "false");// 设置是否显示debug信息 true 会在控制台显示相关信息

            try {

                // 得到回话对象
                Session session = Session.getInstance(properties);
                // 获取邮件对象
                Message message = new MimeMessage(session);
                // 设置发件人邮箱地址
                message.setFrom(new InternetAddress("lch872@qq.com"));
                // 设置收件人邮箱地址
                message.setRecipient(Message.RecipientType.TO, new InternetAddress("lch872@qq.com"));//一个收件人
                // 设置邮件标题
                message.setSubject("Info Messages");
                // 设置邮件内容
                message.setText(emailContent.toString());
                // 得到邮差对象
                Transport transport = session.getTransport();
                // 连接自己的邮箱账户
                transport.connect("lch872@qq.com", "vbexpujxrbcvbgch");// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
                // 发送邮件
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();

                lastMsgId = nowId;

            }catch (MessagingException mex) {
                mex.printStackTrace();
            }
        }




        log.info("!!!");
    }
}