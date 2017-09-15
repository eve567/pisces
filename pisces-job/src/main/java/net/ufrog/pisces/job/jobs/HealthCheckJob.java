package net.ufrog.pisces.job.jobs;

import net.ufrog.common.Logger;
import net.ufrog.pisces.client.PiscesJob;
import net.ufrog.pisces.client.PiscesJobData;
import net.ufrog.pisces.domain.models.App;
import net.ufrog.pisces.domain.models.JobLog;
import net.ufrog.pisces.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        List<App> lApp = appService.findAll();
        lApp.forEach(app -> Logger.info("app name: %s, url: %s", app.getName(), app.getUrl()));
        jobData.setStatus(JobLog.Status.SUCCESS);
    }
}
