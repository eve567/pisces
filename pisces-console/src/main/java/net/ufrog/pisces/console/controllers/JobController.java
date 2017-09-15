package net.ufrog.pisces.console.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import net.ufrog.common.Result;
import net.ufrog.common.exception.ServiceException;
import net.ufrog.common.utils.Objects;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.console.beans.JobCtrlWrapper;
import net.ufrog.pisces.domain.models.App;
import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.domain.models.JobCtrl;
import net.ufrog.pisces.domain.models.JobParam;
import net.ufrog.pisces.service.AppService;
import net.ufrog.pisces.service.JobService;
import net.ufrog.pisces.service.beans.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 构造函数
     *
     * @param appService 应用业务接口
     */
    @Autowired
    public JobController(AppService appService, JobService jobService) {
        this.appService = appService;
        this.jobService = jobService;
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
    public List<Job> findAll(@PathVariable("appId") String appId) {
        return jobService.findByAppId(appId);
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
     * 创建任务
     *
     * @param job 任务对象
     * @return 创建结果
     */
    @PostMapping("/create")
    @ResponseBody
    public Result<?> create(@RequestBody Job job) {
        Objects.trimStringFields(job);
        jobService.create(job);
        Result<?> result = JSON.parseObject(HttpRequest.get(Props.getServerUrl() + "/api/create/" + job.getId()).send().bodyText(), Result.class);
        return Result.success(result.getData(), net.ufrog.common.app.App.message("job.create.success", job.getName()));
    }

    /**
     * 更新任务
     *
     * @param job 任务对象
     * @return 更新结果
     */
    @PutMapping("/update")
    @ResponseBody
    public Result<?> update(@RequestBody Job job) {
        Objects.trimStringFields(job);
        jobService.update(job);
        Result<?> result = JSON.parseObject(HttpRequest.get(Props.getServerUrl() + "/api/update/" + job.getId()).send().bodyText(), Result.class);
        return Result.success(result.getData(), net.ufrog.common.app.App.message("job.update.success", job.getName()));
    }

    /**
     * 切换任务状态
     *
     * @param jobId 任务编号
     * @return 切换结果
     */
    @PutMapping("/toggle/{jobId}")
    @ResponseBody
    public Result<?> toggle(@PathVariable("jobId") String jobId) {
        Job job = jobService.findById(jobId);
        if (Strings.equals(Job.Status.RUNNING, job.getStatus())) {
            job.setStatus(Job.Status.PAUSED);
        } else if (Strings.equals(Job.Status.PAUSED, job.getStatus())) {
            job.setStatus(Job.Status.RUNNING);
        } else {
            throw new ServiceException("job status '" + job.getStatus() + "' invalid.");
        }

        jobService.update(job);

        return JSON.parseObject(HttpRequest.get(Props.getServerUrl() + "/api/update/" + jobId).send().bodyText(), Result.class);
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
}
