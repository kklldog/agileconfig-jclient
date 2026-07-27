package com.github.kklldog.agileconfig;

/**
 * 协议常量, 与服务端 / C# 客户端保持一致
 */
public class Protocol {
    private Protocol() {
    }

    public static class ActionConst {
        public static final String Add = "add";
        public static final String Update = "update";
        public static final String Remove = "remove";
        public static final String Offline = "offline";
        public static final String Reload = "reload";
        public static final String Ping = "ping";
    }

    public static class ActionModule {
        public static final String RegisterCenter = "r";
        public static final String ConfigCenter = "c";
    }

    public static final String HeaderKeyPublishTimeLineId = "publish-time-line-id";
}
