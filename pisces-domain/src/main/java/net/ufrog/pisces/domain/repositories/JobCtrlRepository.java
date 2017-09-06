package net.ufrog.pisces.domain.repositories;

import net.ufrog.pisces.domain.models.JobCtrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务控制仓库
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Repository
public interface JobCtrlRepository extends JpaRepository<JobCtrl, String> {

    /**
     * 通过任务编号查询任务控制
     *
     * @param jobId 任务编号
     * @return 任务控制列表
     */
    List<JobCtrl> findByJobId(String jobId);
}
