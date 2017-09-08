package net.ufrog.pisces.service;

import net.ufrog.pisces.domain.models.Job;

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
     * 通过应用编号查询任务
     *
     * @param appId 应用编号
     * @return 任务列表
     */
    List<Job> findByAppId(String appId);
}
