package net.ufrog.pisces.service.impls;

import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.domain.repositories.JobRepository;
import net.ufrog.pisces.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 构造函数
     *
     * @param jobRepository 任务仓库
     */
    @Autowired
    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Job> findByAppId(String appId) {
        return null;
    }
}
