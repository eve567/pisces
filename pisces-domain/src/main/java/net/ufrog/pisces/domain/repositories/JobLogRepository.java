package net.ufrog.pisces.domain.repositories;

import net.ufrog.pisces.domain.models.JobLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 任务日志仓库
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Repository
public interface JobLogRepository extends JpaRepository<JobLog, String> {

    /**
     * 通过任务编号和时间范围查询任务日志
     *
     * @param jobId 任务编号
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页信息
     * @return 任务日志分页数据
     */
    Page<JobLog> findByJobIdAndDatetimeBeginBetween(String jobId, Date beginDate, Date endDate, Pageable pageable);
}
