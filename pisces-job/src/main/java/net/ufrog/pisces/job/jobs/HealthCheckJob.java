package net.ufrog.pisces.job.jobs;

import net.ufrog.aries.common.contract.Response;
import net.ufrog.pisces.client.PiscesJob;
import net.ufrog.pisces.client.contracts.JobCallRequest;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;
import net.ufrog.pisces.domain.models.App;
import net.ufrog.pisces.service.AppService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /** rest template */
    private final RestTemplate restTemplate;

    /**
     * 构造函数
     *
     * @param appService 应用业务接口
     * @param restTemplate rest template
     */
    public HealthCheckJob(AppService appService, RestTemplate restTemplate) {
        this.appService = appService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(JobCallRequest jobCallRequest, JobCallbackRequest jobCallbackRequest) {
        List<App> lApp = appService.findAll();
        List<AppResponse> lAppResponse = Collections.synchronizedList(new ArrayList<>());

        // 检查所有应用状态
        lApp.parallelStream().forEach(app -> {
            try {
                Response response = restTemplate.getForObject(app.getUrl() + "/check_health", Response.class);
                if (response == null || !response.isSuccess()) {
                    lAppResponse.add(new AppResponse(app, response == null ? net.ufrog.common.app.App.message("health.check.unbeknown") : response.getMessage()));
                }
            } catch (Throwable e) {
                lAppResponse.add(new AppResponse(app, e.getMessage()));
            }
        });

        // 判断是否有检查失败的结果
        if (lAppResponse.size() > 0) {
            jobCallbackRequest.setTemplate("health_check");
            jobCallbackRequest.setStatus(JobCallbackRequest.Status.FAILURE);
            jobCallbackRequest.putArg("total", lApp.size());
            jobCallbackRequest.putArg("total_error", lAppResponse.size());
            jobCallbackRequest.putArg("errors", lAppResponse);
        }
    }

    /**
     * 健康检查任务
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.0.0, 2018-07-18
     * @since 3.0.0
     */
    public static class AppResponse {

        /** 应用对象 */
        private App app;

        /** 消息 */
        private String message;

        /** 构造函数 */
        private AppResponse() {}

        /**
         * 构造函数
         *
         * @param app 应用对象
         * @param message 消息
         */
        AppResponse(App app, String message) {
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
         * 设置应用对象
         *
         * @param app 应用对象
         */
        public void setApp(App app) {
            this.app = app;
        }

        /**
         * 读取消息
         *
         * @return 消息
         */
        public String getMessage() {
            return message;
        }

        /**
         * 设置消息
         *
         * @param message 消息
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
