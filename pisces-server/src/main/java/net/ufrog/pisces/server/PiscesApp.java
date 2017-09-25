package net.ufrog.pisces.server;

import com.alibaba.fastjson.JSON;
import jodd.http.HttpRequest;
import net.ufrog.common.Logger;
import net.ufrog.common.app.App;
import net.ufrog.common.app.AppUser;
import net.ufrog.common.exception.ServiceException;
import net.ufrog.common.spring.app.SpringWebApp;
import net.ufrog.common.utils.Calendars;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.client.jee.PiscesServlet;
import net.ufrog.pisces.domain.models.*;
import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.service.AppService;
import net.ufrog.pisces.service.JobService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-12
 * @since 0.1
 */
public class PiscesApp extends SpringWebApp {

    private static final String DEFAULT_USER_ID = "user_pisces";

    private static Scheduler scheduler;
    private static Map<String, JobWrapper> mJobWrapper;
    private static Map<String, Map<String, JobWrapper>> mmJobWrapper;
    private static Map<String, JobStatus> mJobStatus;

    private static AppService appService;
    private static JobService jobService;

    /**
     * 构造函数
     *
     * @param request 请求
     * @param response 响应
     */
    private PiscesApp(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public Locale getLocale() {
        try {
            return super.getLocale();
        } catch (Exception e) {
            Logger.warn(e.getMessage());
            return Locale.getDefault();
        }
    }

    @Override
    public AppUser getUser() {
        return new AppUser(DEFAULT_USER_ID, null, null);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public static void initialize(ServletContext context) {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            mJobWrapper = new ConcurrentHashMap<>();
            mmJobWrapper = new ConcurrentHashMap<>();
            mJobStatus = new ConcurrentHashMap<>();

            SpringWebApp.initialize(context);
            appService = SpringWebApp.getBean(AppService.class);
            jobService = SpringWebApp.getBean(JobService.class);

            jobService.findAll().stream().filter(job -> !Strings.equals(Job.Status.REMOVED, job.getStatus())).forEach(PiscesApp::addJob);
            scheduler.start();
        } catch (Throwable e) {
            Logger.error(e.getMessage(), e);
            throw new Error();
        }
    }

    /**
     * 创建实例
     *
     * @param request 请求
     * @param response 响应
     * @return 应用实例
     */
    public static App create(HttpServletRequest request, HttpServletResponse response) {
        return current(new PiscesApp(request, response));
    }

    /**
     * 添加任务
     *
     * @param job 任务实例
     * @return 任务封装
     */
    public static JobWrapper addJob(Job job) {
        JobDetail jobDetail = JobBuilder.newJob(JobExecutor.class).withIdentity(job.getId()).build();
        ScheduleBuilder<? extends Trigger> scheduleBuilder = getScheduleBuilder(job);
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger_" + job.getId()).withSchedule(scheduleBuilder).build();
        List<JobCtrl> lJobCtrl = jobService.findCtrls(job.getId());
        List<JobParam> lJobParam = jobService.findParams(job.getId());
        JobWrapper jobWrapper = new JobWrapper(job, jobDetail, trigger, lJobCtrl, lJobParam);

        if (!mmJobWrapper.containsKey(job.getAppId())) mmJobWrapper.put(job.getAppId(), new ConcurrentHashMap<>());
        mJobWrapper.put(job.getId(), jobWrapper);
        mmJobWrapper.get(job.getAppId()).put(job.getId(), jobWrapper);
        if (Strings.equals(Job.Status.RUNNING, job.getStatus())) {
            try {
                jobWrapper.setNextFireTime(scheduler.scheduleJob(jobDetail, trigger));
                Logger.info("add job '%s - %s' to scheduler. next fire time is '%s'.", job.getName(), job.getCode(), Calendars.datetime(jobWrapper.getNextFireTime()));
            } catch (SchedulerException e) {
                throw new ServiceException("cannot register job '" + job.getName() + " - " + job.getCode() + "'", e);
            }
        }
        return jobWrapper;
    }

    /**
     * 暂停任务
     *
     * @param key 键值
     * @return 暂停任务封装
     */
    public static JobWrapper pauseJob(String key) {
        JobWrapper jobWrapper = getJob(key);
        if (jobWrapper != null) {
            try {
                if (scheduler.checkExists(jobWrapper.getJobDetail().getKey())) {
                    scheduler.pauseJob(jobWrapper.getJobDetail().getKey());
                    scheduler.pauseTrigger(jobWrapper.getTrigger().getKey());
                    scheduler.deleteJob(jobWrapper.getJobDetail().getKey());
                }
                jobWrapper.setNextFireTime(null);
                jobWrapper.getJob().setStatus(Job.Status.PAUSED);
                Logger.info("job '%s' is paused.", jobWrapper.getName());
                return jobWrapper;
            } catch (SchedulerException e) {
                Logger.error("cannot pause job '%s'.", jobWrapper.getName());
                throw new ServiceException(e.getMessage(), e);
            }
        }
        throw new ServiceException("cannot find job by id: " + key + ".", "job.get.no-exists");
    }

    /**
     * 恢复任务
     *
     * @param key 键值
     * @return 恢复任务封装
     */
    public static JobWrapper resumeJob(String key) {
        Job job = jobService.findById(key);
        if (job != null) return addJob(job);
        throw new ServiceException("cannot find job by id: " + key + ".", "job.get.no-exists");
    }

    /**
     * 执行任务
     *
     * @param jobWrapper 任务封装
     * @param jobLogId 任务日志编号
     * @param remark 备注
     */
    public static void doJob(JobWrapper jobWrapper, String jobLogId, String remark) {
        JobLog jobLog = Strings.empty(jobLogId) ? jobService.createLog(jobWrapper.getId(), remark) : jobService.findLogById(jobLogId);
        Map<String, String> mParam = new HashMap<>(jobWrapper.getJobParams());

        mParam.put(PiscesServlet.PARAM_TYPE, PiscesServlet.TYPE_DO_JOB);
        mParam.put(PiscesServlet.PARAM_JOB, jobWrapper.getCode());
        mParam.put(PiscesServlet.PARAM_DATE, (jobWrapper.getDate() == null) ? null : Calendars.date(jobWrapper.getDate()));
        mParam.put(PiscesServlet.PARAM_NUM, jobLog.getId());
        String paramJson = JSON.toJSONString(mParam);
        Logger.info("job '%s - %s' trigger, params: %s", jobWrapper.getName(), jobWrapper.getCode(), paramJson);    //TODO 记录日志文件

        if (!checkCtrlHang(jobWrapper, jobLog) || !checkCtrlBlock(jobWrapper, jobLog)) return;
        String bodyText = HttpRequest.post(appService.findById(jobWrapper.getAppId()).getUrl()).charset("utf-8").body(Strings.toUnicode(paramJson)).send().bodyText();
        if (Strings.equals("ok", bodyText)) {
            Logger.info("job '%s - %s - %s' triggered.", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId());
            synchronized (jobWrapper.getId()) {
                if (!mJobStatus.containsKey(jobWrapper.getId())) mJobStatus.put(jobWrapper.getId(), new JobStatus(jobWrapper.getId(), jobWrapper.getName()));
                Logger.info("job '%s - %s - %s' total: %s", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId(), mJobStatus.get(jobWrapper.getId()).incr());
            }
        } else {
            jobService.createLogDetail(jobLog.getId(), message("job.trigger.failure.response"), JobLog.Status.FAILURE);
            jobService.updateLog(jobLog.getId(), JobLog.Status.FAILURE, null, null);    //TODO 发送失败邮件
            Logger.error("job '%s - %s - %s' trigger failure, %s.", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId(), bodyText);
        }
    }

    /**
     * 执行任务
     *
     * @param key 键值
     * @param jobLogId 任务日志编号
     * @param remark 备注
     */
    public static void doJob(String key, String jobLogId, String remark) {
        doJob(getJob(key), jobLogId, remark);
    }

    /**
     * 完成任务
     *
     * @param key 键值
     */
    public static void completeJob(String key) {
        JobWrapper jobWrapper = getJob(key);
        if (jobWrapper != null && mJobStatus.get(key) != null) {
            Integer total = mJobStatus.get(key).decr();
            if (total == 0) {
                JobStatus jobStatus = mJobStatus.get(key);
                mJobStatus.remove(key);
                jobStatus.getHangJobs().forEach(jobLog -> {
                    jobService.createLogDetail(jobLog.getId(), message("job.ctrl.trigger", jobStatus.getName()), JobLogDetail.Type.TRIGGER);
                    doJob(jobLog.getJobId(), jobLog.getId(), null);
                });
            }
            Logger.info("job '%s - %s - %s' completed, and remainder %d.", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId(), total);
        } else {
            Logger.warn("cannot find job by key: %s", key);
        }
    }

    /**
     * 读取任务封装
     *
     * @param key 键值
     * @return 任务封装
     */
    public static JobWrapper getJob(String key) {
        return mJobWrapper.get(key);
    }

    /**
     * 读取任务封装列表
     *
     * @param appId 应用编号
     * @return 任务封装列表
     */
    public static List<JobWrapper> getJobs(String appId) {
        Map<String, JobWrapper> mJobWrapper = mmJobWrapper.get(appId);
        return (mJobWrapper != null) ? new ArrayList<>(mJobWrapper.values()) : Collections.emptyList();
    }

    /**
     * 读取定时构造器
     *
     * @param job 任务实例
     * @return 定时构造器
     */
    private static ScheduleBuilder<? extends Trigger> getScheduleBuilder(Job job) {
        if (Strings.equals(Job.Type.INTERVAL, job.getType())) {
            return CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(Calendars.parseDuration(job.getExpression()), DateBuilder.IntervalUnit.SECOND);
        } else if (Strings.equals(Job.Type.CRON, job.getType())) {
            return CronScheduleBuilder.cronSchedule(job.getExpression());
        }
        throw new ServiceException("cannot parse schedule type: " + job.getType());
    }

    /**
     * 检查挂起控制
     *
     * @param jobWrapper 任务封装
     * @param jobLog 任务日志
     * @return 检查结果
     */
    private static Boolean checkCtrlHang(JobWrapper jobWrapper, JobLog jobLog) {
        List<JobCtrl> lJobCtrl = jobWrapper.getJobCtrls(JobCtrl.Type.HANG);
        for (JobCtrl jobCtrl: lJobCtrl) {
            JobStatus jobStatus = mJobStatus.get(jobCtrl.getRelatedId());
            if (jobStatus != null) {
                jobStatus.addHangJob(jobLog);
                jobService.createLogDetail(jobLog.getId(), message("job.ctrl.hang", jobStatus.getName()), JobLogDetail.Type.HANG);
                jobService.updateLog(jobLog.getId(), JobLog.Status.HANG, null, null);
                Logger.info("job '%s - %s - %s' has been hung, because job '%s' is running...", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId(), jobStatus.getName());
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 检查阻塞控制
     *
     * @param jobWrapper 任务封装
     * @param jobLog 任务日志
     * @return 检查结果
     */
    private static Boolean checkCtrlBlock(JobWrapper jobWrapper, JobLog jobLog) {
        List<JobCtrl> lJobCtrl = jobWrapper.getJobCtrls(JobCtrl.Type.BLOCK);
        for (JobCtrl jobCtrl: lJobCtrl) {
            JobStatus jobStatus = mJobStatus.get(jobCtrl.getRelatedId());
            if (jobStatus != null) {
                jobService.createLogDetail(jobLog.getId(), message("job.ctrl.block", jobStatus.getName()), JobLogDetail.Type.BLOCK);
                jobService.updateLog(jobLog.getId(), JobLog.Status.BLOCK, null, null);
                Logger.info("job '%s - %s - %s' has been blocked, because job '%s' is running...", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId(), jobStatus.getName());
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
