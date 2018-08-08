package net.ufrog.pisces.console.controllers;

import net.ufrog.aries.common.contract.Response;
import net.ufrog.common.Result;
import net.ufrog.common.exception.ServiceException;
import net.ufrog.common.utils.Calendars;
import net.ufrog.common.utils.Objects;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.client.JobClient;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;
import net.ufrog.pisces.client.contracts.JobResponse;
import net.ufrog.pisces.console.beans.JobCtrlWrapper;
import net.ufrog.pisces.domain.models.*;
import net.ufrog.pisces.service.AppService;
import net.ufrog.pisces.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务控制器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-07
 * @since 0.1
 */
@Controller
@RequestMapping("/job")
public class JobController {

    /** 应用业务接口 */
    private final AppService appService;

    /** 任务业务接口 */
    private final JobService jobService;

    /** 任务服务客户端 */
    private final JobClient jobClient;

    /**
     * 构造函数
     *
     * @param appService 应用业务接口
     * @param jobService 任务业务接口
     * @param jobClient 任务服务客户端
     */
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public JobController(AppService appService, JobService jobService, JobClient jobClient) {
        this.appService = appService;
        this.jobService = jobService;
        this.jobClient = jobClient;
    }

    /**
     * 索引
     *
     * @return view for job/index.html
     */
    @GetMapping({"", "/", "/index"})
    public String index() {
        return "job/index";
    }

    /**
     * 查询所有
     *
     * @param appId 应用编号
     * @return 任务列表
     */
    @GetMapping("/find_all/{appId}")
    @ResponseBody
    public List<JobResponse> findAll(@PathVariable("appId") String appId) {
        return jobClient.read(appId).getContent();
    }

    /**
     * 查询参数
     *
     * @param jobId 任务编号
     * @return 任务参数列表
     */
    @GetMapping("/find_params/{jobId}")
    @ResponseBody
    public List<JobParam> findParams(@PathVariable("jobId") String jobId) {
        return jobService.findParams(jobId);
    }

    /**
     * 查询控制
     *
     * @param jobId 任务编号
     * @return 任务控制列表
     */
    @GetMapping("/find_ctrls/{jobId}")
    @ResponseBody
    public List<JobCtrlWrapper> findCtrls(@PathVariable("jobId") String jobId) {
        return jobService.findCtrls(jobId).parallelStream().map(jc -> JobCtrlWrapper.newInstance(jc, jobService.findById(jc.getRelatedId()))).collect(Collectors.toList());
    }

    /**
     * 查询日志
     *
     * @param jobId 任务编号
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param page 当前页码
     * @param size 分页大小
     * @return 任务日志分页信息
     */
    @GetMapping("/find_logs/{jobId}")
    @ResponseBody
    public Page<JobLog> findLogs(@PathVariable("jobId") String jobId, String beginDate, String endDate, Integer page, Integer size) {
        try {
            return jobService.findLogsByJobId(jobId, Calendars.parseDatetime(beginDate), Calendars.parseDatetime(endDate), page, size);
        } catch (ParseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * 查询日志明细
     *
     * @param logId 日志编号
     * @return 日志明细列表
     */
    @GetMapping("/find_log_details/{logId}")
    @ResponseBody
    public List<JobLogDetail> findLogDetails(@PathVariable("logId") String logId) {
        return jobService.findLogDetailsByLogId(logId);
    }

    /**
     * 创建任务
     *
     * @param job 任务对象
     * @return 创建结果
     */
    @PostMapping("/create")
    @ResponseBody
    public Result<JobResponse> create(@RequestBody Job job) {
        Objects.trimStringFields(job);
        jobService.create(job);
        return Result.success(jobClient.create(job.getId()), net.ufrog.common.app.App.message("job.create.success", job.getName()));
    }

    /**
     * 更新任务
     *
     * @param job 任务对象
     * @return 更新结果
     */
    @PutMapping("/update")
    @ResponseBody
    public Result<JobResponse> update(@RequestBody Job job) {
        Objects.trimStringFields(job);
        jobService.update(job);
        return Result.success(jobClient.update(job.getId()), net.ufrog.common.app.App.message("job.update.success", job.getName()));
    }

    /**
     * 切换任务状态
     *
     * @param jobId 任务编号
     * @return 切换结果
     */
    @PutMapping("/toggle/{jobId}")
    @ResponseBody
    public Result<JobResponse> toggle(@PathVariable("jobId") String jobId) {
        Job job = jobService.findById(jobId);
        job.setStatus(switchStatus(Job.Status.RUNNING, Job.Status.PAUSED, job.getStatus()));
        jobService.update(job);
        return Result.success(jobClient.update(job.getId()), net.ufrog.common.app.App.message("job.toggle.success", job.getName(), job.getStatusName()));
    }

    /**
     * 触发任务
     *
     * @param jobId 任务编号
     * @param remark 备注
     * @return 触发结果
     */
    @GetMapping("/trigger/{jobId}")
    @ResponseBody
    public Result<JobResponse> trigger(@PathVariable("jobId") String jobId, String remark) {
        JobResponse jobResponse = jobClient.trigger(jobId, Strings.fromUnicode(remark));
        return Result.success(jobResponse, net.ufrog.common.app.App.message("job.trigger.success", jobResponse.getName()));
    }

    /**
     * 结束任务
     *
     * @param jobLogId 任务日志编号
     * @param remark 备注
     * @return 结束结果
     */
    @GetMapping("/complete/{jobLogId}")
    @ResponseBody
    public Result<JobLog> complete(@PathVariable("jobLogId") String jobLogId, String remark) {
        JobCallbackRequest jobCallbackRequest = new JobCallbackRequest(jobLogId);
        jobCallbackRequest.setRemark(Strings.fromUnicode(remark));
        Response response = jobClient.callback(jobCallbackRequest);
        JobLog jobLog = jobService.findLogById(jobLogId);

        return response.isSuccess() ? Result.success(jobLog, net.ufrog.common.app.App.message("job.complete.success")) : Result.failure(jobLog, response.getMessage());
    }

    /**
     * 创建任务参数
     *
     * @param jobParam 任务参数
     * @return 创建结果
     */
    @PostMapping("/create_param")
    @ResponseBody
    public Result<JobParam> createParam(@RequestBody JobParam jobParam) {
        Objects.trimStringFields(jobParam);
        return Result.success(jobService.createParam(jobParam), net.ufrog.common.app.App.message("job.param.create.success", jobParam.getCode()));
    }

    /**
     * 删除任务参数
     *
     * @param jobParamId 任务参数编号
     * @return 删除结果
     */
    @DeleteMapping("/delete_param/{jobParamId}")
    @ResponseBody
    public Result<JobParam> deleteParam(@PathVariable("jobParamId") String jobParamId) {
        JobParam jobParam = jobService.deleteParam(jobParamId);
        return Result.success(jobParam, net.ufrog.common.app.App.message("job.param.delete.success", jobParam.getCode()));
    }

    /**
     * 创建任务控制
     *
     * @param jobCtrl 任务控制
     * @return 创建结果
     */
    @PostMapping("/create_ctrl")
    @ResponseBody
    public Result<JobCtrlWrapper> createCtrl(@RequestBody JobCtrl jobCtrl) {
        Objects.trimStringFields(jobCtrl);
        Job related = jobService.findById(jobCtrl.getRelatedId());
        return Result.success(JobCtrlWrapper.newInstance(jobService.createCtrl(jobCtrl), related), net.ufrog.common.app.App.message("job.ctrl.create.success", jobCtrl.getTypeName(), related.getName()));
    }

    /**
     * 删除任务控制
     *
     * @param jobCtrlId 任务控制编号
     * @return 删除结果
     */
    @DeleteMapping("/delete_ctrl/{jobCtrlId}")
    @ResponseBody
    public Result<JobCtrl> deleteCtrl(@PathVariable("jobCtrlId") String jobCtrlId) {
        JobCtrl jobCtrl = jobService.deleteCtrl(jobCtrlId);
        Job related = jobService.findById(jobCtrl.getRelatedId());
        return Result.success(jobCtrl, net.ufrog.common.app.App.message("job.ctrl.delete.success", jobCtrl.getTypeName(), related.getName()));
    }

    /**
     * 创建应用
     *
     * @param app 应用实例
     * @return 应用实例
     */
    @PostMapping("/create_app")
    @ResponseBody
    public Result<App> createApp(@RequestBody App app) {
        Objects.trimStringFields(app);
        return Result.success(appService.create(app), net.ufrog.common.app.App.message("job.app.create.success"));
    }

    /**
     * 更新应用
     *
     * @param app 应用实例
     * @return 应用实例
     */
    @PutMapping("/update_app")
    @ResponseBody
    public Result<App> updateApp(@RequestBody App app) {
        Objects.trimStringFields(app);
        return Result.success(appService.update(app), net.ufrog.common.app.App.message("job.app.update.success"));
    }

    /**
     * 检查应用
     *
     * @param leoAppId 应用编号
     * @return 检查结果
     */
    @GetMapping("/check_app/{leoAppId}")
    @ResponseBody
    public Object checkApp(@PathVariable("leoAppId") String leoAppId) {
        App app = appService.findByLeoAppId(leoAppId);
        if (app != null) {
            return app;
        } else {
            return Result.warning(net.ufrog.common.app.App.message("job.app.check.failure.not-exist"));
        }
    }

    /**
     * 切换状态
     *
     * @param status1 状态类型
     * @param status2 状态类型
     * @param status 当前状态
     * @return 切换后状态
     */
    @SuppressWarnings("SameParameterValue")
    private String switchStatus(String status1, String status2, String status) {
        if (Strings.equals(status1, status)) {
            return status2;
        } else if (Strings.equals(status2, status)) {
            return status1;
        } else {
            throw new ServiceException("status '" + status + "' is not in '" + status1 + "', '" + status2 + "'.");
        }
    }
}
