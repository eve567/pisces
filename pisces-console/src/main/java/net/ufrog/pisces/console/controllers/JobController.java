package net.ufrog.pisces.console.controllers;

import net.ufrog.common.Result;
import net.ufrog.common.utils.Objects;
import net.ufrog.pisces.domain.models.App;
import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.service.AppService;
import net.ufrog.pisces.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
