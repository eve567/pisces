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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Logger.info("initialize pisces application filter.");
        PiscesApp.initialize(filterConfig.getServletContext());
        Logger.info("pisces application filter is running...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = HttpServletRequest.class.cast(request);
        HttpServletResponse resp = HttpServletResponse.class.cast(response);

        req.setAttribute(WebAppFilter.PARAM_KEY, PiscesApp.create(req, resp));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Logger.info("destroyed pisces application filter!");
    }
}
