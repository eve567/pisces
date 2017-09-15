package net.ufrog.pisces.server.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ufrog.common.Logger;
import net.ufrog.common.Mailer;
import net.ufrog.common.Result;
import net.ufrog.common.app.App;
import net.ufrog.common.jetbrick.Templates;
import net.ufrog.common.utils.Calendars;
import net.ufrog.common.utils.Codecs;
import net.ufrog.common.utils.Files;
import net.ufrog.common.utils.Strings;
import net.ufrog.common.web.app.WebApp;
import net.ufrog.pisces.client.PiscesJobData;
import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.domain.models.JobLog;
import net.ufrog.pisces.server.JobWrapper;
import net.ufrog.pisces.server.PiscesApp;
import net.ufrog.pisces.service.AppService;
import net.ufrog.pisces.service.JobService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 索引控制器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-13
 * @since 0.1
 */
@RestController
public class IndexController {

    private static String layout;

    /** 应用业务接口 */
    private final AppService appService;

    /** 任务应用接口 */
    private final JobService jobService;

    /**
     * 构造函数
     *
     * @param appService 应用业务接口
     * @param jobService 任务应用接口
     */
    @Autowired
    public IndexController(AppService appService, JobService jobService) {
        this.appService = appService;
        this.jobService = jobService;
    }

    /**
     * 健康监控
     *
     * @return 监控结果
     */
    @RequestMapping("/check_health")
    public Result<Boolean> checkHealth() {
        return Result.success(Boolean.TRUE, "pisces is running...");
    }

    /**
     * 处理回调
     *
     * @return 处理结果
     */
    @PostMapping("/callback")
    public Result<String> callback() {
        Result<?> rPjd = JSON.parseObject(App.current(WebApp.class).getBodyText(), Result.class);
        PiscesJobData pjd = getJobData(rPjd.getData());

        try {
            String remark = Strings.empty(rPjd.getFirstMessage(), App.message(rPjd.success() ? "job.callback.success" : "job.callback.failure"));
            jobService.createLogDetail(pjd.getNum(), remark, pjd.getStatus());
            JobLog jobLog = jobService.updateLog(pjd.getNum(), pjd.getStatus(), sendEmail(pjd), null);

            setNextDate(jobLog);
            PiscesApp.completeJob(jobLog.getJobId());
            return Result.success("ok");
        } catch (Exception e) {
            ServiceException ex = (e instanceof ServiceException) ? (ServiceException) e : new ServiceException(e.getMessage(), e);
            Result<String> result = Result.failure(ex.getLocalizedMessage());

            result.setData(pjd.getNum());
            Logger.error(ex.getMessage(), ex);
            return result;
        }
    }

    /**
     * 读取任务数据
     *
     * @param data 数据
     * @return 任务数据
     */
    private PiscesJobData getJobData(Object data) {
        if (data instanceof PiscesJobData) return (PiscesJobData) data;
        if (data instanceof JSONObject) return PiscesJobData.fromJSONObject((JSONObject) data);
        return null;
    }

    /**
     * 发送邮件通知
     *
     * @param piscesJobData 任务数据
     * @return 邮件地址串
     */
    private String sendEmail(PiscesJobData piscesJobData) {
        if (!Strings.empty(piscesJobData.getContent())) {
            JobLog jobLog = jobService.findLogById(piscesJobData.getNum());
            Job job = jobService.findById(jobLog.getJobId());
            net.ufrog.pisces.domain.models.App app = appService.findById(job.getAppId());
            List<String> lEmail = new ArrayList<>();
            String content = HtmlUtils.htmlUnescape(piscesJobData.getContent());

            lEmail.addAll(Strings.empty(app.getEmail()) ? Collections.emptyList() : Strings.explode(app.getEmail(), ","));
            lEmail.addAll(Strings.empty(job.getEmail()) ? Collections.emptyList() : Strings.explode(job.getEmail(), ","));
            if (lEmail.size() > 0) {
                if (piscesJobData.getNeedLayout()) {
                    String uuid = Codecs.uuid();
                    Map<String, Object> mArg = new HashMap<>();
                    mArg.put("app", App.current());
                    mArg.put("jobLog", jobLog);
                    mArg.put("jobData", piscesJobData);
                    mArg.put("endTime", new Date());

                    content = "#tag layout_block(\"bodyContent\") " + content + " #end " + getLayout();
                    content = Templates.render(uuid, content, mArg);
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
        if (Strings.empty(layout)) layout = Files.readFile("/templates/email/layout.html");
        return layout;
    }

    /**
     * 设置下一日切
     *
     * @param jobLog 任务日志
     */
    private void setNextDate(JobLog jobLog) {
        JobWrapper jobWrapper = PiscesApp.getJob(jobLog.getJobId());
        if (jobWrapper.getDate() != null && Strings.equals(JobLog.Status.SUCCESS, jobLog.getStatus())) {
            Date next = Calendars.add(Calendar.DATE, 1, jobWrapper.getDate());
            jobWrapper.getJob().setDate(next);
            jobService.update(jobWrapper.getJob());
            Logger.info("set job '%s' next date: %s", jobWrapper.getName(), Calendars.date(next));
        }
    }
}
