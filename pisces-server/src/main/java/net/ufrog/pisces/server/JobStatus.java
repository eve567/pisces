package net.ufrog.pisces.server;

import net.ufrog.pisces.domain.models.JobLog;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务状态
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-12
 * @since 0.1
 */
public class JobStatus implements Serializable {

    private static final long serialVersionUID = 4131589336337091015L;

    /** 编号 */
    private String id;

    /** 名称 */
    private String name;

    /** 运行中数量 */
    private AtomicInteger total;

    /** 挂起任务映射 */
    private Map<String, JobLog> mHangJob;

    /** 构造函数 */
    private JobStatus() {
        this.total = new AtomicInteger(0);
        this.mHangJob = new HashMap<>();
    }

    /**
     * 构造函数
     *
     * @param id 编号
     * @param name 名称
     */
    JobStatus(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    /**
     * 读取编号
     *
     * @return 编号
     */
    public String getId() {
        return id;
    }

    /**
     * 读取名称
     *
     * @return 名称
     */
    String getName() {
        return name;
    }

    /**
     * 添加挂起任务
     *
     * @param jobLog 任务日志
     */
    synchronized void addHangJob(JobLog jobLog) {
        if (!mHangJob.containsKey(jobLog.getJobId())) {
            mHangJob.put(jobLog.getJobId(), jobLog);
        }
    }

    /**
     * 累加任务
     *
     * @return 累加后的运行数量
     */
    Integer incr() {
        return total.incrementAndGet();
    }

    /**
     * 完成任务并调启后续任务<br>
     * 当前任务如果同时启动多个，则当所有任务完成才调启后续任务
     *
     * @return 是否已经调启后续任务
     */
    Integer decr() {
        return total.decrementAndGet();
    }

    /**
     * 读取挂起任务
     *
     * @return 挂起任务集合
     */
    Collection<JobLog> getHangJobs() {
        return mHangJob.values();
    }
}
