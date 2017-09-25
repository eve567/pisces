package net.ufrog.pisces.server.controllers;

import net.ufrog.common.Result;
import net.ufrog.common.app.App;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.client.PiscesJob;
import net.ufrog.pisces.server.JobWrapper;
import net.ufrog.pisces.server.PiscesApp;
import org.hibernate.service.spi.ServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用接口控制器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-14
 * @since 0.1
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class APIController {

    /**
     * 查询所有任务
     *
     * @param appId 应用编号
     * @return 任务封装列表
     */
    @GetMapping("/find_all/{appId}")
    public List<JobWrapper> findAll(@PathVariable("appId") String appId) {
        return PiscesApp.getJobs(appId);
    }

    /**
     * 创建任务
     *
     * @param id 任务编号
     * @return 创建结果
     */
    @GetMapping("/create/{id}")
    public Result<JobWrapper> create(@PathVariable("id") String id) {
        JobWrapper jobWrapper = PiscesApp.getJob(id);
        return (jobWrapper != null) ? Result.warning(jobWrapper, App.message("job.add.warning.exists")) : Result.success(PiscesApp.resumeJob(id), App.message("job.add.success"));
    }

    /**
     * 更新任务
     *
     * @param id 任务编号
     * @return 更新结果
     */
    @GetMapping("/update/{id}")
    public Result<JobWrapper> update(@PathVariable("id") String id) {
        try {
            PiscesApp.pauseJob(id);
            return Result.success(PiscesApp.resumeJob(id), App.message("job.update.success"));
        } catch (ServiceException e) {
            return Result.failure(e.getLocalizedMessage());
        }
    }

    /**
     * 触发任务
     *
     * @param id 任务编号
     * @param remark 备注
     * @return 触发结果
     */
    @GetMapping("/trigger/{id}")
    public Result<JobWrapper> trigger(@PathVariable("id") String id, String remark) {
        JobWrapper jobWrapper = PiscesApp.getJob(id);
        remark = Strings.empty(remark) ? App.message("job.trigger.manual") : Strings.fromUnicode(remark);
        if (jobWrapper != null) {
            PiscesApp.doJob(jobWrapper, null, remark);
            return Result.success(jobWrapper, App.message("job.trigger.success"));
        }
        return Result.failure(App.message("job.get.no-exists"));
    }
}
