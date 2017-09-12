package net.ufrog.pisces.service;

import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.domain.models.JobCtrl;
import net.ufrog.pisces.domain.models.JobParam;

import java.util.List;

/**
 * 任务业务接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-07
 * @since 0.1
 */
public interface JobService {

    /**
     * 通过编号查询任务
     *
     * @param id 任务编号
     * @return 任务对象
     */
    Job findById(String id);

    /**
     * 通过应用编号查询任务
     *
     * @param appId 应用编号
     * @return 任务列表
     */
    List<Job> findByAppId(String appId);

    /**
     * 通过任务编号查询参数
     *
     * @param jobId 任务编号
     * @return 任务参数列表
     */
    List<JobParam> findParams(String jobId);

    /**
     * 通过任务编号查询控制
     *
     * @param jobId 任务编号
     * @return 任务参数列表
     */
    List<JobCtrl> findCtrls(String jobId);

    /**
     * 创建任务
     *
     * @param job 任务实例
     * @return 持久化任务实例
     */
    Job create(Job job);

    /**
     * 更新任务
     *
     * @param job 任务实例
     * @return 持久化任务实例
     */
    Job update(Job job);

    /**
     * 创建任务参数
     *
     * @param jobParam 任务参数实例
     * @return 持久化任务参数实例
     */
    JobParam createParam(JobParam jobParam);

    /**
     * 删除任务参数
     *
     * @param jobParamId 任务参数编号
     * @return 被删除任务参数
     */
    JobParam deleteParam(String jobParamId);

    /**
     * 创建任务控制
     *
     * @param jobCtrl 任务控制实例
     * @return 持久化任务控制实例
     */
    JobCtrl createCtrl(JobCtrl jobCtrl);

    /**
     * 删除任务控制
     *
     * @param jobCtrlId 任务控制编号
     * @return 被删除任务控制
     */
    JobCtrl deleteCtrl(String jobCtrlId);
}
