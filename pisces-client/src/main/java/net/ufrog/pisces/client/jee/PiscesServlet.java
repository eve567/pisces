package net.ufrog.pisces.client.jee;

import com.alibaba.fastjson.JSON;
import jodd.http.HttpRequest;
import net.ufrog.common.Logger;
import net.ufrog.common.Result;
import net.ufrog.common.exception.ServiceException;
import net.ufrog.common.spring.app.SpringWebApp;
import net.ufrog.common.utils.Calendars;
import net.ufrog.common.utils.Streams;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.client.PiscesConfig;
import net.ufrog.pisces.client.PiscesJob;
import net.ufrog.pisces.client.PiscesJobData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 任务入口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
public class PiscesServlet extends HttpServlet {

    private static final long serialVersionUID = 1608905628781350554L;

    public static final String PARAM_TYPE          = "_type";
    public static final String PARAM_JOB           = "_job";
    public static final String PARAM_NUM           = "_num";
    public static final String PARAM_DATE          = "_date";

    public static final String TYPE_CHECK_HEALTH   = "_check_health";
    public static final String TYPE_DO_JOB         = "_do_job";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        @SuppressWarnings("unchecked") Map<String, String> mArg = JSON.parseObject(getBody(req), Map.class);
        mArg = (mArg == null) ? Collections.emptyMap() : mArg;
        String type = mArg.get(PARAM_TYPE);

        if (Strings.equals(TYPE_CHECK_HEALTH, type)) {
            checkHealth(resp);
        } else if (Strings.equals(TYPE_DO_JOB, type)) {
            doJob(resp, mArg);
        } else {
            throw new ServiceException("cannot resolve type: " + type + ".");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     * 健康检查
     *
     * @param resp 响应
     */
    private void checkHealth(HttpServletResponse resp) {
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("ok");
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }

    /**
     * 执行任务
     *
     * @param resp 响应
     * @param mArg 参数
     */
    private void doJob(HttpServletResponse resp, Map<String, String> mArg) {
        String job = mArg.get(PARAM_JOB);
        String num = mArg.get(PARAM_NUM);
        String dat = mArg.get(PARAM_DATE);
        Logger.info("job '%s' and seq '%s' start...", job, num);

        PiscesJob piscesJob = SpringWebApp.getBean(job, PiscesJob.class);
        PiscesJobData piscesJobData = new PiscesJobData(num);
        try (PrintWriter printWriter = resp.getWriter()) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Result<PiscesJobData> rPjd = Result.success("");
                try {
                    piscesJob.run(Strings.empty(dat) ? null : Calendars.parseDate(dat), mArg, piscesJobData);
                    Logger.info("job '%s' and seq '%s' finished.", job, num);
                } catch (Throwable e) {
                    try (StringWriter sWriter = new StringWriter(); PrintWriter pWriter = new PrintWriter(sWriter)) {
                        Logger.error("job '%s' and seq '%s' failure!!!", job, num);
                        Logger.error(e.getMessage(), e);

                        e.printStackTrace(pWriter);
                        piscesJobData.setStatus(PiscesJobData.Status.FAILURE);
                        piscesJobData.setTemplate("exception");
                        piscesJobData.setNeedLayout(Boolean.TRUE);
                        piscesJobData.putArg("stackTrace", sWriter.toString());
                    } catch (IOException ex) {
                        Logger.error("job '%s' and seq '%s' unknown exception!!!", job, num);
                        ServiceException sex = new ServiceException(ex.getMessage(), ex);

                        piscesJobData.setStatus(PiscesJobData.Status.FAILURE);
                        piscesJobData.setTemplate("exception");
                        piscesJobData.setNeedLayout(Boolean.TRUE);
                        piscesJobData.putArg("stackTrace", sex.getMessage());
                        throw sex;
                    }
                }
                rPjd.setData(piscesJobData);
                HttpRequest.post(PiscesConfig.get().getHost() + "/callback").body(Strings.toUnicode(JSON.toJSONString(rPjd))).charset("utf-8").send();
            });
            printWriter.write("ok");
            printWriter.flush();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * 读取请求体
     *
     * @param req 请求
     * @param charset 字符集
     * @return 请求体字符串
     */
    public static String getBody(HttpServletRequest req, Charset charset) {
        try (InputStream is = req.getInputStream(); ByteArrayOutputStream os = new ByteArrayOutputStream(Math.max(32, is.available()))) {
            Streams.copy(is, os);
            String bodyText = Strings.fromUnicode(new String(os.toByteArray(), charset));
            Logger.debug("body text: %s", bodyText);
            return bodyText;
        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * 读取请求体
     *
     * @param req 请求
     * @param charset 字符集
     * @return 请求体字符串
     */
    public static String getBody(HttpServletRequest req, String charset) {
        return getBody(req, Charset.forName(charset));
    }

    /**
     * 读取请求体
     *
     * @param req 请求
     * @return 请求体字符串
     */
    public static String getBody(HttpServletRequest req) {
        return getBody(req, "utf-8");
    }
}
