package net.ufrog.pisces.job.jobs;

import com.alibaba.fastjson.JSON;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import net.ufrog.common.Logger;
import net.ufrog.common.exception.ServiceException;
import net.ufrog.common.utils.Objects;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.client.PiscesJob;
import net.ufrog.pisces.client.PiscesJobData;
import net.ufrog.pisces.client.jee.PiscesServlet;
import net.ufrog.pisces.domain.models.App;
import net.ufrog.pisces.domain.models.JobLog;
import net.ufrog.pisces.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 健康检查任务
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-15
 * @since 0.1
 */
@Component("healthCheckJob")
public class HealthCheckJob implements PiscesJob {

    /** 应用业务接口 */
    private final AppService appService;

    /**
     * 构造函数
     *
     * @param appService 应用业务接口
     */
    @Autowired
    public HealthCheckJob(AppService appService) {
        this.appService = appService;
    }

    @Override
    public void run(Date date, Map<String, String> args, PiscesJobData jobData) {
        List<AppChecker> lAppChecker = new ArrayList<>();
        List<App> lApp = appService.findAll();

        // 检查应用是否健康
        lApp.forEach(app -> {
            try {
                Map<String, Object> mArg = Objects.map(PiscesServlet.PARAM_TYPE, PiscesServlet.TYPE_CHECK_HEALTH);
                HttpResponse resp = HttpRequest.post(app.getUrl()).body(JSON.toJSONString(mArg)).charset("utf-8").send();
                if (!Strings.equals("ok", resp.bodyText())) {
                    lAppChecker.add(new AppChecker(app, resp.statusPhrase()));
                }
            } catch (Throwable e) {
                lAppChecker.add(new AppChecker(app, e.getMessage()));
            }
        });

        // 判断并处理结果
        if (lAppChecker.size() > 0) {
            jobData.setTemplate("health_check");
            jobData.setStatus(PiscesJobData.Status.FAILURE);
            jobData.putArg("total", lApp.size());
            jobData.putArg("total_error", lAppChecker.size());
            jobData.putArg("errors", lAppChecker);
        }
    }

    /**
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 0.1, 2017-09-18
     * @since 0.1
     */
    public static class AppChecker {

        /** 应用对象 */
        private App app;

        /** 消息 */
        private String message;

        /** 构造函数 */
        private AppChecker() {}

        /**
         * 构造函数
         *
         * @param app 应用对象
         * @param message 消息
         */
        public AppChecker(App app, String message) {
            this();
            this.app = app;
            this.message = message;
        }

        /**
         * 读取应用对象
         *
         * @return 应用对象
         */
        public App getApp() {
            return app;
        }

        /**
         * 读取消息
         *
         * @return 消息
         */
        public String getMessage() {
            return message;
        }
    }
}
