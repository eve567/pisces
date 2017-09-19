package net.ufrog.pisces.service;

import net.ufrog.pisces.domain.models.*;
import org.springframework.data.domain.Page;

import java.util.Date;
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
     * 查询所有任务
     *
     * @return 任务列表
     */
    List<Job> findAll();

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
     * 通过编号查询任务日志
     *
     * @param jobLogId 任务日志编号
     * @return 任务日志对象
     */
    JobLog findLogById(String jobLogId);

    /**
     * 通过任务编号查询任务日志
     *
     * @param jobId 任务编号
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param page 当前页码
     * @param size 分页大小
     * @return 任务日志分页信息
     */
    Page<JobLog> findLogsByJobId(String jobId, Date beginDate, Date endDate, Integer page, Integer size);

    /**
     * 通过日志编号查询日志明细
     *
     * @param logId 日志编号
     * @return 日志明细列表
     */
    List<JobLogDetail> findLogDetailsByLogId(String logId);

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

    /**
     * 创建任务日志
     *
     * @param jobId 任务编号
     * @param remark 备注
     * @return 任务日志对象
     */
    JobLog createLog(String jobId, String remark);

    /**
     * 更新任务日志
     *
     * @param jobLogId 任务日志编号
     * @param status 状态
     * @param email 通知邮件
     * @param cellphone 通知手机
     * @return 任务日志对象
     */
    JobLog updateLog(String jobLogId, String status, String email, String cellphone);

    /**
     * 创建任务日志明细
     *
     * @param jobLogId 任务日志编号
     * @param remark 备注
     * @param type 类型
     * @return 任务日志明细对象
     */
    JobLogDetail createLogDetail(String jobLogId, String remark, String type);
}
