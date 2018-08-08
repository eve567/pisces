package net.ufrog.pisces.service.beans;

/**
 * 属性工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-14
 * @since 0.1
 */
@SuppressWarnings({"WeakerAccess", "UnnecessaryLocalVariable"})
public class Props {

    /** 服务端地址 */ public static final String SERVER_URL = "server_url";

    /**
     * 读取服务端地址
     *
     * @return 服务端地址
     */
    public static java.lang.String getServerUrl() {
        String value = net.ufrog.common.app.App.config(SERVER_URL);
        return value;
    }
}