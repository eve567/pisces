package net.ufrog.pisces.server;

import net.ufrog.common.Logger;
import net.ufrog.common.app.App;
import net.ufrog.common.app.AppUser;
import net.ufrog.common.spring.app.SpringWebApp;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-12
 * @since 0.1
 */
public class PiscesApp extends SpringWebApp {

    private static final String DEFAULT_USER_ID = "user_pisces";

    /**
     * 构造函数
     *
     * @param request 请求
     * @param response 响应
     */
    private PiscesApp(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public Locale getLocale() {
        try {
            return super.getLocale();
        } catch (Exception e) {
            Logger.warn(e.getMessage());
            return Locale.getDefault();
        }
    }

    @Override
    public AppUser getUser() {
        return new AppUser(DEFAULT_USER_ID, null, null);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public static void initialize(ServletContext context) {
        SpringWebApp.initialize(context);
    }

    /**
     * 创建实例
     *
     * @param request 请求
     * @param response 响应
     * @return 应用实例
     */
    static App create(HttpServletRequest request, HttpServletResponse response) {
        return current(new PiscesApp(request, response));
    }
}
