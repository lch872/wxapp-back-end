package com.oneway.tools.redirector;

import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.config.*;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.template.Engine;
import com.oneway.tools.redirector.model.Report;
import com.oneway.tools.redirector.model._MappingKit;
import com.oneway.tools.redirector.web.controller.IndexController;
import com.oneway.tools.redirector.web.controller.WXAppController;

/**
 * JFinal总配置文件，挂接所有接口与插件
 *
 * @author CHEN
 */
public class AppConfig extends JFinalConfig {

    @Override
    public void configConstant(Constants me) {

    }

    @Override
    public void configRoute(Routes me) {
        me.add("/", IndexController.class);
        me.add("/wx", WXAppController.class);
    }

    @Override
    public void configEngine(Engine me) {

    }

    @Override
    public void configPlugin(Plugins me) {
        DruidPlugin dp = new DruidPlugin("jdbc:mysql://127.0.0.1/Test", "root", "u3LT3eSc7by6hT");
        me.add(dp);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        me.add(arp);
        _MappingKit.mapping(arp);
//
    }

    @Override
    public void configInterceptor(Interceptors me) {

    }

    @Override
    public void configHandler(Handlers me) {

    }
}