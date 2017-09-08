package net.ufrog.pisces.domain.repositories;

import net.ufrog.pisces.domain.models.JobLogDetail;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务日志明细仓库
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Repository
public interface JobLogDetailRepository extends JpaRepository<JobLogDetail, String> {

    /**
     * 通过任务日志编号查询日志明细
     *
     * @param jobLogId 任务日志编号
     * @param sort 排序
     * @return 任务日志明细列表
     */
    List<JobLogDetail> findByJobLogId(String jobLogId, Sort sort);
}
