package com.twinape.hello.app.test;

import com.twinape.common.bootstrap.AppStarter;
import com.twinape.hello.app.HelloTwinApeApp;
import com.twinape.hello.app.HelloTwinApeAppConfig;

final class _Launcher {

    private static final String APP_CONFIG = "/home/rob/Work/TwinApe/git/hello-twinape/app/config/app.yaml";

    public static void main(String[] args) {
        var configFile = args != null && args.length > 0 ? args[0] : APP_CONFIG;
        AppStarter.of(HelloTwinApeApp.class) //
                .configType(HelloTwinApeAppConfig.class) //
                .configPath(configFile) //
                .module(ApiModule.class) //
                .start();

    }
}
