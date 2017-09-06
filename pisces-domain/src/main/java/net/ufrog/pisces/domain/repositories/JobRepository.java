package net.ufrog.pisces.domain.repositories;

import net.ufrog.pisces.domain.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务仓库
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    /**
     * 通过应用编号查询任务
     *
     * @param appId 应用编号
     * @return 任务列表
     */
    List<Job> findByAppId(String appId);
}
