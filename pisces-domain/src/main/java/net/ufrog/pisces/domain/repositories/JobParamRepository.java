package net.ufrog.pisces.domain.repositories;

import net.ufrog.pisces.domain.models.JobParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务参数仓库
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Repository
public interface JobParamRepository extends JpaRepository<JobParam, String> {

    /**
     * 通过任务编号查询任务参数列表
     *
     * @param jobId 任务编号
     * @return 任务参数列表
     */
    List<JobParam> findByJobId(String jobId);
}
