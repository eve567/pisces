package net.ufrog.pisces.server;

import com.alibaba.fastjson.JSON;
import net.ufrog.aries.common.contract.Response;
import net.ufrog.common.Logger;
import net.ufrog.common.app.App;
import net.ufrog.common.exception.ServiceException;
import net.ufrog.common.utils.Calendars;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.client.contracts.JobCallRequest;
import net.ufrog.pisces.domain.models.*;
import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.service.AppService;
import net.ufrog.pisces.service.JobService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务管理器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-23
 * @since 3.0.0
 */
@Component
public class PiscesJobManager {

    private final AppService appService;
    private final JobService jobService;
    private final RestTemplate restTemplate;

    private Scheduler scheduler;
    private Map<String, JobWrapper> mJobWrapper;
    private Map<String, Map<String, JobWrapper>> mmJobWrapper;
    private Map<String, JobStatus> mJobStatus;

    /**
     * 构造函数
     *  @param appService 应用业务接口
     * @param jobService 任务业务接口
     * @param restTemplate rest template
     */
    @Autowired
    public PiscesJobManager(AppService appService, JobService jobService, RestTemplate restTemplate) {
        this.appService = appService;
        this.jobService = jobService;
        this.restTemplate = restTemplate;
    }

    /** 初始化 */
    void init() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            mJobWrapper = new ConcurrentHashMap<>();
            mmJobWrapper = new ConcurrentHashMap<>();
            mJobStatus = new ConcurrentHashMap<>();

            jobService.findAll().stream().filter(job -> !Strings.equals(Job.Status.REMOVED, job.getStatus())).forEach(this::add);
            scheduler.start();
        } catch (SchedulerException e) {
            Logger.error(e.getMessage(), e);
            throw new Error();
        }
    }

    /**
     * 添加任务
     *
     * @param job 任务
     * @return 任务封装
     */
    private JobWrapper add(Job job) {
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
                Logger.info("add job '%s - %s' to scheduler. next fire time is '%s'", job.getName(), job.getCode(), Calendars.datetime(jobWrapper.getNextFireTime()));
            } catch (SchedulerException e) {
                throw new ServiceException("cannot register job '" + job.getName() + "-" + job.getCode() + "'", e);
            }
        }
        return jobWrapper;
    }

    /**
     * 暂停任务
     *
     * @param key 键值
     * @return 任务封装
     */
    public JobWrapper pause(String key) {
        return Optional.ofNullable(get(key)).map(jobWrapper -> {
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
        }).orElseThrow(() -> new ServiceException("cannot find job by id: " + key + ".", "job.get.no-exists"));
    }

    /**
     * 恢复任务
     *
     * @param key 键值
     * @return 任务封装
     */
    public JobWrapper resume(String key) {
        return Optional.ofNullable(jobService.findById(key)).map(this::add).orElseThrow(() -> new ServiceException("cannot find job by id: " + key + ".", "job.get.no-exists"));
    }

    /**
     * 执行任务
     *
     * @param jobWrapper 任务封装
     * @param jobLogId 任务日志编号
     * @param remark 备注
     */
    public void run(JobWrapper jobWrapper, String jobLogId, String remark) {
        JobLog jobLog = Strings.empty(jobLogId) ? jobService.createLog(jobWrapper.getId(), remark) : jobService.findLogById(jobLogId);
        JobCallRequest jobCallRequest = new JobCallRequest();

        jobCallRequest.setJobCode(jobWrapper.getCode());
        jobCallRequest.setDate(jobWrapper.getDate());
        jobCallRequest.setNum(jobLog.getId());
        jobCallRequest.setParams(new HashMap<>(jobWrapper.getJobParams()));
        Logger.info("job '%s - %s' trigger, params: %s", jobWrapper.getName(), jobWrapper.getCode(), JSON.toJSONString(jobCallRequest));

        if (checkCtrlHang(jobWrapper, jobLog) && checkCtrlBlock(jobWrapper, jobLog)) {
            Response response = restTemplate.postForObject(appService.findById(jobWrapper.getAppId()).getUrl() + "/do_job", jobCallRequest, Response.class);
            if (response != null && response.isSuccess()) {
                Logger.info("job '%s - %s - %s' triggered.", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId());
                synchronized (jobWrapper.getId()) {
                    if (!mJobStatus.containsKey(jobWrapper.getId())) {
                        mJobStatus.put(jobWrapper.getId(), new JobStatus(jobWrapper.getId(), jobWrapper.getName()));
                    }
                    Logger.info("job '%s - %s - %s' total: %s", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId(), mJobStatus.get(jobWrapper.getId()).incr());
                }
            } else {
                jobService.createLogDetail(jobLog.getId(), App.message("job.trigger.failure.response"), JobLog.Status.FAILURE);
                jobService.updateLog(jobLog.getId(), JobLog.Status.FAILURE, null, null);    //TODO 发送失败邮件
                Logger.error("job '%s - %s - %s' trigger failure, %s.", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId(), JSON.toJSONString(jobCallRequest));
            }
        }
    }

    /**
     * 执行任务
     *
     * @param key 键值
     * @param jobLogId 任务日志编号
     * @param remark 备注
     */
    private void run(String key, String jobLogId, String remark) {
        run(get(key), jobLogId, remark);
    }

    /**
     * 完成任务
     *
     * @param key 键值
     */
    public void complete(String key) {
        JobWrapper jobWrapper = get(key);
        if (jobWrapper != null && mJobStatus.get(key) != null) {
            Integer total = mJobStatus.get(key).decr();
            if (total == 0) {
                JobStatus jobStatus = mJobStatus.get(key);
                mJobStatus.remove(key);
                jobStatus.getHangJobs().forEach(jobLog -> {
                    jobService.createLogDetail(jobLog.getId(), App.message("job.ctrl.trigger", jobStatus.getName()), JobLogDetail.Type.TRIGGER);
                    run(jobLog.getJobId(), jobLog.getId(), null);
                });
            }
        }
    }

    /**
     * 读取任务封装
     *
     * @param key 键值
     * @return 任务封装
     */
    public JobWrapper get(String key) {
        return mJobWrapper.get(key);
    }

    /**
     * 读取所有任务封装
     *
     * @param appId 应用编号
     * @return 任务封装列表
     */
    public List<JobWrapper> getAll(String appId) {
        Map<String, JobWrapper> mJobWrapper = mmJobWrapper.get(appId);
        return (mJobWrapper != null) ? new ArrayList<>(mJobWrapper.values()) : Collections.emptyList();
    }

    /**
     * 读取定时器构造器
     *
     * @param job 任务
     * @return 定时构造器
     */
    private ScheduleBuilder<? extends Trigger> getScheduleBuilder(Job job) {
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
     * @return 判断结果
     */
    private Boolean checkCtrlHang(JobWrapper jobWrapper, JobLog jobLog) {
        List<JobCtrl> lJobCtrl = jobWrapper.getJobCtrls(JobCtrl.Type.HANG);
        for (JobCtrl jobCtrl: lJobCtrl) {
            JobStatus jobStatus = mJobStatus.get(jobCtrl.getRelatedId());
            if (jobStatus != null) {
                jobStatus.addHangJob(jobLog);
                jobService.createLogDetail(jobLog.getId(), App.message("job.ctrl.hang", jobStatus.getName()), JobLogDetail.Type.HANG);
                jobService.updateLog(jobLog.getId(), JobLog.Status.HANG, null, null);
                Logger.info("job '%s - %s- %s' has been hung, because job '%s' is running...", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId(), jobStatus.getName());
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
    private Boolean checkCtrlBlock(JobWrapper jobWrapper, JobLog jobLog) {
        List<JobCtrl> lJobCtrl = jobWrapper.getJobCtrls(JobCtrl.Type.BLOCK);
        for (JobCtrl jobCtrl: lJobCtrl) {
            JobStatus jobStatus = mJobStatus.get(jobCtrl.getRelatedId());
            if (jobStatus != null) {
                jobService.createLogDetail(jobLog.getId(), App.message("job.ctrl.block", jobStatus.getName()), JobLogDetail.Type.BLOCK);
                jobService.updateLog(jobLog.getId(), JobLog.Status.BLOCK, null, null);
                Logger.info("job '%s - %s - %s' has been blocked, because job '%s' is running...", jobWrapper.getName(), jobWrapper.getCode(), jobWrapper.getAppId(), jobStatus.getName());
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
