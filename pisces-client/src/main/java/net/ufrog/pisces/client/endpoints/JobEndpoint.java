package net.ufrog.pisces.client.endpoints;

import net.ufrog.aries.common.contract.Response;
import net.ufrog.common.Logger;
import net.ufrog.common.exception.ServiceException;
import net.ufrog.pisces.client.JobClient;
import net.ufrog.pisces.client.PiscesJob;
import net.ufrog.pisces.client.PiscesTemplateProperties;
import net.ufrog.pisces.client.ResultCode;
import net.ufrog.pisces.client.contracts.JobCallRequest;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Executors;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-17
 * @since 3.0.0
 */
@RestController
@RequestMapping(value = "/pisces")
@EnableConfigurationProperties(PiscesTemplateProperties.class)
@ConditionalOnProperty(value = "ufrog.pisces.enabled", havingValue = "true")
public class JobEndpoint {

    /** application context */
    private final ApplicationContext applicationContext;

    /** 任务服务 */
    private final JobClient jobClient;

    /** 模版属性 */
    private final PiscesTemplateProperties piscesTemplateProperties;

    /**
     * 构造函数
     *  @param applicationContext application context
     * @param jobClient 任务服务
     * @param piscesTemplateProperties 模版属性
     */
    @Autowired
    public JobEndpoint(ApplicationContext applicationContext, JobClient jobClient, PiscesTemplateProperties piscesTemplateProperties) {
        this.applicationContext = applicationContext;
        this.jobClient = jobClient;
        this.piscesTemplateProperties = piscesTemplateProperties;
    }

    /**
     * 调用任务
     *
     * @return 调用响应
     */
    @RequestMapping(value = "/do_job", method = RequestMethod.POST)
    public Response doJob(@RequestBody JobCallRequest jobCallRequest) {
        Logger.info("job '%s' - num '%s' begin...", jobCallRequest.getJobCode(), jobCallRequest.getNum());
        PiscesJob piscesJob = applicationContext.getBean(jobCallRequest.getJobCode(), PiscesJob.class);
        JobCallbackRequest jobCallbackRequest = new JobCallbackRequest(jobCallRequest.getNum());

        // 线程执行任务
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                piscesJob.run(jobCallRequest, jobCallbackRequest);
                Logger.info("job '%s' - num '%s' finished.", jobCallRequest.getJobCode(), jobCallRequest.getNum());
            } catch (Throwable e) {
                try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
                    Logger.error("job '%s' - num '%s' failure!", jobCallRequest.getJobCode(), jobCallRequest.getNum());
                    Logger.error(e.getMessage(), e);

                    e.printStackTrace(printWriter);
                    jobCallbackRequest.setStatus(JobCallbackRequest.Status.FAILURE);
                    jobCallbackRequest.setTemplate("exception");
                    jobCallbackRequest.setNeedLayout(Boolean.TRUE);
                    jobCallbackRequest.putArg("stackTrace", stringWriter.toString());
                } catch (IOException ex) {
                    Logger.error("job '%s' - num '%s' unbeknown exception!", jobCallRequest.getJobCode(), jobCallRequest.getNum());
                    Logger.error(ex.getMessage(), ex);

                    jobCallbackRequest.setStatus(JobCallbackRequest.Status.FAILURE);
                    jobCallbackRequest.setTemplate("exception");
                    jobCallbackRequest.setNeedLayout(Boolean.TRUE);
                    jobCallbackRequest.putArg("stackTrace", ex.getMessage());
                }
            }

            // 发送回调
            Response response = jobClient.callback(piscesTemplateProperties.render(jobCallbackRequest));
            if (!response.isSuccess()) Logger.warn("job '%s' - num '%s' callback failure!", jobCallRequest.getJobCode(), jobCallRequest.getNum());
        });
        return Response.createResponse(ResultCode.SUCCESS, Response.class);
    }

    /**
     * 健康监控
     *
     * @return 监控响应
     */
    @RequestMapping(value = "/check_health", method = RequestMethod.GET)
    public Response checkHealth() {
        return Response.createResponse(ResultCode.SUCCESS, Response.class);
    }
}
