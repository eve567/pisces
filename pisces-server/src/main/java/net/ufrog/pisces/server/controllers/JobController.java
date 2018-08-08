package net.ufrog.pisces.server.controllers;

import net.ufrog.aries.common.contract.ListResponse;
import net.ufrog.aries.common.contract.Response;
import net.ufrog.common.Logger;
import net.ufrog.common.Mailer;
import net.ufrog.common.app.App;
import net.ufrog.common.jetbrick.Templates;
import net.ufrog.common.utils.Calendars;
import net.ufrog.common.utils.Codecs;
import net.ufrog.common.utils.Files;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.client.JobClient;
import net.ufrog.pisces.client.ResultCode;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;
import net.ufrog.pisces.client.contracts.JobResponse;
import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.domain.models.JobLog;
import net.ufrog.pisces.server.JobWrapper;
import net.ufrog.pisces.server.PiscesJobManager;
import net.ufrog.pisces.service.AppService;
import net.ufrog.pisces.service.JobService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务控制器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-18
 * @since 3.0.0
 */
@RestController
@SuppressWarnings("MVCPathVariableInspection")
public class JobController implements JobClient {

    /** 应用业务接口 */
    private final AppService appService;

    /** 任务业务接口 */
    private final JobService jobService;

    /** 任务管理器 */
    private final PiscesJobManager piscesJobManager;

    /** 模版内容 */
    private String layout;

    /**
     * 构造函数
     *
     * @param appService 应用业务接口
     * @param jobService 任务业务接口
     * @param piscesJobManager 任务管理器
     */
    @Autowired
    public JobController(AppService appService, JobService jobService, PiscesJobManager piscesJobManager) {
        this.appService = appService;
        this.jobService = jobService;
        this.piscesJobManager = piscesJobManager;
    }

    @Override
    public ListResponse<JobResponse> read(@PathVariable("appId") String appId) {
        List<JobResponse> lJobResponse = piscesJobManager.getAll(appId).stream().map(this::toJobResponse).collect(Collectors.toList());
        return new ListResponse<>(lJobResponse);
    }

    @Override
    public JobResponse create(@PathVariable("id") String id) {
        JobWrapper jobWrapper = piscesJobManager.get(id);
        if (jobWrapper == null) {
            jobWrapper = piscesJobManager.resume(id);
            return toJobResponse(jobWrapper);
        }
        return Response.createResponse(ResultCode.JOB_EXISTS, JobResponse.class);
    }

    @Override
    public JobResponse update(@PathVariable("id") String id) {
        try {
            piscesJobManager.pause(id);
            return toJobResponse(piscesJobManager.resume(id));
        } catch (ServiceException e) {
            return Response.createResponse(ResultCode.JOB_EXISTS, JobResponse.class);
        }
    }

    @Override
    public JobResponse trigger(@PathVariable("id") String id, String remark) {
        JobWrapper jobWrapper = piscesJobManager.get(id);
        if (jobWrapper != null) {
            piscesJobManager.run(jobWrapper, null, Strings.empty(remark) ? App.message("job.trigger.manual") : Strings.fromUnicode(remark));
            return toJobResponse(jobWrapper);
        }
        return Response.createResponse(ResultCode.DATA_NOT_FOUND, JobResponse.class);
    }

    @Override
    public Response callback(@RequestBody JobCallbackRequest jobCallbackRequest) {
        try {
            String remark = Strings.empty(jobCallbackRequest.getRemark(), Strings.equals(JobCallbackRequest.Status.SUCCESS, jobCallbackRequest.getStatus()) ? App.message("job.callback.success") : App.message("job.callback.failure"));
            jobService.createLogDetail(jobCallbackRequest.getNum(), remark, jobCallbackRequest.getStatus());
            JobLog jobLog = jobService.updateLog(jobCallbackRequest.getNum(), jobCallbackRequest.getStatus(), sendEmail(jobCallbackRequest), null);

            setNextDate(jobLog);
            piscesJobManager.complete(jobLog.getJobId());
            return Response.createResponse(ResultCode.SUCCESS, Response.class);
        } catch (Exception e) {
            ServiceException ex = (e instanceof ServiceException) ? (ServiceException) e : new ServiceException(e.getMessage(), e);
            Response response = new Response();

            Logger.error(ex.getMessage(), ex);
            response.setResultCode(ResultCode.UNBEKNOWN);
            response.setMessage("num: " + jobCallbackRequest.getNum() + ", message: " + ex.getMessage());
            return response;
        }
    }

    /**
     * 发送通知邮件
     *
     * @param jobCallbackRequest 任务回调请求
     * @return 邮件地址串
     */
    private String sendEmail(JobCallbackRequest jobCallbackRequest) {
        if (!Strings.empty(jobCallbackRequest.getContent())) {
            JobLog jobLog = jobService.findLogById(jobCallbackRequest.getNum());
            Job job = jobService.findById(jobLog.getJobId());
            net.ufrog.pisces.domain.models.App app = appService.findById(job.getAppId());
            List<String> lEmail = new ArrayList<>();
            String content = HtmlUtils.htmlUnescape(jobCallbackRequest.getContent());

            lEmail.addAll(Strings.empty(app.getEmail()) ? Collections.emptyList() : Strings.explode(app.getEmail(), ","));
            lEmail.addAll(Strings.empty(job.getEmail()) ? Collections.emptyList() : Strings.explode(job.getEmail(), ","));
            if (lEmail.size() > 0) {
                if (jobCallbackRequest.getNeedLayout()) {
                    String uuid = Codecs.uuid();
                    Map<String, Object> args = new HashMap<>();

                    args.put("app", App.current());
                    args.put("jobLog", jobLog);
                    args.put("jobData", jobCallbackRequest);
                    args.put("endTime", new Date());

                    content = "#tag layout_block(\"bodyContent\") " + content + " #end " + getLayout();
                    content = Templates.render(uuid, content, args);
                    Templates.clear(uuid);
                }
                lEmail = lEmail.stream().map(String::trim).distinct().collect(Collectors.toList());
                Mailer.sendHtml(App.message("email.subject.success", app.getName(), job.getName()), content, lEmail.toArray(new String[0]));
                return Strings.implode(lEmail, ",");
            }
        }
        return null;
    }

    /**
     * 读取模版内容
     *
     * @return 模版内容
     */
    private String getLayout() {
        if (Strings.empty(layout)) {
            layout = Files.readFile("/templates/email/layout.html");
        }
        return layout;
    }

    /**
     * 设置下一日切
     *
     * @param jobLog 任务日志
     */
    private void setNextDate(JobLog jobLog) {
        JobWrapper jobWrapper = piscesJobManager.get(jobLog.getJobId());
        if (jobWrapper.getDate() != null && Strings.equals(JobLog.Status.SUCCESS, jobLog.getStatus())) {
            Date next = Calendars.add(Calendar.DATE, 1, jobWrapper.getDate());
            jobWrapper.getJob().setDate(next);
            jobService.update(jobWrapper.getJob());
            Logger.info("set job '%s' next date: %s", jobWrapper.getName(), Calendars.date(next));
        }
    }

    /**
     * 转换成任务响应
     *
     * @param jobWrapper 任务封装
     * @return 任务响应
     */
    private JobResponse toJobResponse(JobWrapper jobWrapper) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.setId(jobWrapper.getId());
        jobResponse.setName(jobWrapper.getName());
        jobResponse.setCode(jobWrapper.getCode());
        jobResponse.setGroup(jobWrapper.getGroup());
        jobResponse.setDate(jobWrapper.getDate());
        jobResponse.setExpression(jobWrapper.getExpression());
        jobResponse.setEmail(jobWrapper.getEmail());
        jobResponse.setCellphone(jobWrapper.getCellphone());
        jobResponse.setType(jobWrapper.getType());
        jobResponse.setTypeName(jobWrapper.getTypeName());
        jobResponse.setStatus(jobWrapper.getStatus());
        jobResponse.setStatusName(jobWrapper.getStatusName());
        jobResponse.setAppId(jobWrapper.getAppId());
        jobResponse.setNextFireTime(jobWrapper.getNextFireTime());
        return jobResponse;
    }
}
