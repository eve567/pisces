package net.ufrog.pisces.console.beans;

import net.ufrog.common.utils.Objects;
import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.domain.models.JobCtrl;

/**
 * 任务控制封装
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-12
 * @since 0.1
 */
public class JobCtrlWrapper extends JobCtrl {

    private static final long serialVersionUID = 8322958263905555626L;

    /** 相关任务 */
    private Job related;

    /**
     * 读取相关任务
     *
     * @return 相关任务
     */
    public Job getRelated() {
        return related;
    }

    /**
     * 设置相关任务
     *
     * @param related 相关任务
     */
    public void setRelated(Job related) {
        this.related = related;
    }

    /**
     * 读取相关任务名称
     *
     * @return 相关任务名称
     */
    public String getRelatedName() {
        return related.getName();
    }

    /**
     * 创建实例
     *
     * @param jobCtrl 任务控制实例
     * @param related 相关任务实例
     * @return 任务控制封装
     */
    public static JobCtrlWrapper newInstance(JobCtrl jobCtrl, Job related) {
        JobCtrlWrapper jobCtrlWrapper = new JobCtrlWrapper();
        Objects.copyProperties(jobCtrlWrapper, jobCtrl);
        jobCtrlWrapper.setRelated(related);
        return jobCtrlWrapper;
    }
}
