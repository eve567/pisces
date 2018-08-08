package net.ufrog.pisces.server;

import net.ufrog.common.Logger;
import net.ufrog.common.web.app.WebAppFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-13
 * @since 0.1
 */
public class PiscesAppFilter implements Filter {

    /** 任务管理器 */
    private final PiscesJobManager piscesJobManager;

    /**
     * 构造函数
     *
     * @param piscesJobManager 任务管理器
     */
    PiscesAppFilter(PiscesJobManager piscesJobManager) {
        this.piscesJobManager = piscesJobManager;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        Logger.info("initialize pisces application filter.");
        PiscesApp.initialize(filterConfig.getServletContext());
        piscesJobManager.init();
        Logger.info("pisces application filter is running...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = HttpServletRequest.class.cast(request);
        HttpServletResponse httpServletResponse = HttpServletResponse.class.cast(response);

        httpServletRequest.setAttribute(WebAppFilter.PARAM_KEY, PiscesApp.create(httpServletRequest, httpServletResponse));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Logger.info("destroyed pisces application filter!");
    }
}
