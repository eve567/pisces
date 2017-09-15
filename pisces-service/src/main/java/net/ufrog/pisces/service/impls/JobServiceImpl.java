package net.ufrog.pisces.service.impls;

import net.ufrog.common.utils.Objects;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.domain.Models;
import net.ufrog.pisces.domain.models.*;
import net.ufrog.pisces.domain.repositories.*;
import net.ufrog.pisces.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 任务业务实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-07
 * @since 0.1
 */
@Service
@Transactional(readOnly = true)
public class JobServiceImpl implements JobService {

    /** 任务仓库 */
    private final JobRepository jobRepository;

    /** 任务控制仓库 */
    private final JobCtrlRepository jobCtrlRepository;

    /** 任务日志仓库 */
    private final JobLogRepository jobLogRepository;

    /** 任务日志明细仓库 */
    private final JobLogDetailRepository jobLogDetailRepository;

    /** 任务参数仓库 */
    private final JobParamRepository jobParamRepository;

    /**
     * 构造函数
     *
     * @param jobRepository 任务仓库
     * @param jobCtrlRepository 任务控制仓库
     * @param jobLogRepository 任务日志仓库
     * @param jobLogDetailRepository 任务日志明细仓库
     * @param jobParamRepository 任务参数仓库
     */
    @Autowired
    public JobServiceImpl(
            JobRepository jobRepository,
            JobCtrlRepository jobCtrlRepository,
            JobLogRepository jobLogRepository,
            JobLogDetailRepository jobLogDetailRepository,
            JobParamRepository jobParamRepository) {
        this.jobRepository = jobRepository;
        this.jobCtrlRepository = jobCtrlRepository;
        this.jobLogRepository = jobLogRepository;
        this.jobLogDetailRepository = jobLogDetailRepository;
        this.jobParamRepository = jobParamRepository;
    }

    @Override
    public Job findById(String id) {
        return jobRepository.findOne(id);
    }

    @Override
    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    @Override
    public List<Job> findByAppId(String appId) {
        return jobRepository.findByAppId(appId);
    }

    @Override
    public List<JobParam> findParams(String jobId) {
        return jobParamRepository.findByJobId(jobId);
    }

    @Override
    public List<JobCtrl> findCtrls(String jobId) {
        return jobCtrlRepository.findByJobId(jobId);
    }

    @Override
    public JobLog findLogById(String jobLogId) {
        return jobLogRepository.findOne(jobLogId);
    }

    @Override
    @Transactional
    public Job create(Job job) {
        return jobRepository.save(job);
    }

    @Override
    @Transactional
    public Job update(Job job) {
        Job oJob = jobRepository.findOne(job.getId());
        Objects.copyProperties(oJob, job, Boolean.TRUE, "id", "creator", "createTime", "updater", "updateTime");
        return jobRepository.save(oJob);
    }

    @Override
    @Transactional
    public JobParam createParam(JobParam jobParam) {
        return jobParamRepository.save(jobParam);
    }

    @Override
    @Transactional
    public JobParam deleteParam(String jobParamId) {
        JobParam jobParam = jobParamRepository.findOne(jobParamId);
        jobParamRepository.delete(jobParam);
        return jobParam;
    }

    @Override
    @Transactional
    public JobCtrl createCtrl(JobCtrl jobCtrl) {
        return jobCtrlRepository.save(jobCtrl);
    }

    @Override
    @Transactional
    public JobCtrl deleteCtrl(String jobCtrlId) {
        JobCtrl jobCtrl = jobCtrlRepository.findOne(jobCtrlId);
        jobCtrlRepository.delete(jobCtrl);
        return jobCtrl;
    }

    @Override
    @Transactional
    public JobLog createLog(String jobId, String remark) {
        JobLog jobLog = jobLogRepository.save(Models.newJobLog(jobId, remark));
        createLogDetail(jobLog.getId(), remark, JobLogDetail.Type.TRIGGER);
        return jobLog;
    }

    @Override
    @Transactional
    public JobLog updateLog(String jobLogId, String status, String email, String cellphone) {
        JobLog jobLog = jobLogRepository.findOne(jobLogId);

        jobLog.setStatus(status);
        jobLog.setDatetimeEnd(Strings.in(status, JobLog.Status.SUCCESS, JobLog.Status.FAILURE, JobLog.Status.BLOCK) ? new Date() : null);
        return jobLogRepository.save(jobLog);
    }

    @Override
    @Transactional
    public JobLogDetail createLogDetail(String jobLogId, String remark, String type) {
        return jobLogDetailRepository.save(Models.newJobLogDetail(jobLogId, remark, type));
    }
}
