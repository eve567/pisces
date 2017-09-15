package net.ufrog.pisces.server;

import net.ufrog.common.Logger;
import net.ufrog.common.app.App;
import net.ufrog.common.exception.ServiceException;
import net.ufrog.common.spring.app.SpringWebApp;
import net.ufrog.common.spring.interceptor.PropertiesInterceptor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 任务执行入口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-13
 * @since 0.1
 */
public class JobExecutor implements Job {

    /** 属性拦截器 */
    private static PropertiesInterceptor propertiesInterceptor;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobWrapper jobWrapper = PiscesApp.getJob(context.getJobDetail().getKey().getName());
        try {
            PiscesApp.create(null, null);
            getPropertiesInterceptor().preHandle(null, null, null);
            PiscesApp.doJob(jobWrapper, null, App.message("job.trigger.auto"));
        } catch (Throwable e) {
            ServiceException ex = (e instanceof ServiceException) ? (ServiceException) e : new ServiceException(e.getMessage(), e);
            Logger.error("job '%s' failure, error code: %s", jobWrapper.getName(), ex.getCode());
            Logger.error(ex.getMessage(), ex);
        }
        jobWrapper.setNextFireTime(context.getNextFireTime());
    }

    /**
     * 读取属性拦截器
     *
     * @return 属性拦截器
     */
    private PropertiesInterceptor getPropertiesInterceptor() {
        if (propertiesInterceptor == null) {
            propertiesInterceptor = SpringWebApp.getBean(PropertiesInterceptor.class);
        }
        return propertiesInterceptor;
    }
}
