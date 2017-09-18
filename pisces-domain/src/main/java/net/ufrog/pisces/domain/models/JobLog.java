package net.ufrog.pisces.domain.models;

import net.ufrog.common.dict.Dicts;

/**
 * 任务日志模型
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "pis_job_log")
public class JobLog extends Model {

    private static final long serialVersionUID = -7407464926789098819L;

    /** 启动时间 */
    @javax.persistence.Column(name = "dt_datetime_begin")
    private java.util.Date datetimeBegin;

    /** 结束时间 */
    @javax.persistence.Column(name = "dt_datetime_end")
    private java.util.Date datetimeEnd;

    /** 备注 */
    @javax.persistence.Column(name = "vc_remark")
    private java.lang.String remark;

    /** 状态 */
    @javax.persistence.Column(name = "dc_status")
    private java.lang.String status;

    /** 任务编号 */
    @javax.persistence.Column(name = "fk_job_id")
    private java.lang.String jobId;

    /**
     * 读取启动时间
     *
     * @return 启动时间
     */
    public java.util.Date getDatetimeBegin() {
        return datetimeBegin;
    }

    /**
     * 设置启动时间
     *
     * @param datetimeBegin 启动时间
     */
    public void setDatetimeBegin(java.util.Date datetimeBegin) {
        this.datetimeBegin = datetimeBegin;
    }

    /**
     * 读取结束时间
     *
     * @return 结束时间
     */
    public java.util.Date getDatetimeEnd() {
        return datetimeEnd;
    }

    /**
     * 设置结束时间
     *
     * @param datetimeEnd 结束时间
     */
    public void setDatetimeEnd(java.util.Date datetimeEnd) {
        this.datetimeEnd = datetimeEnd;
    }

    /**
     * 读取备注
     *
     * @return 备注
     */
    public java.lang.String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(java.lang.String remark) {
        this.remark = remark;
    }

    /**
     * 读取状态
     *
     * @return 状态
     */
    public java.lang.String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    /**
     * 读取任务编号
     *
     * @return 任务编号
     */
    public java.lang.String getJobId() {
        return jobId;
    }

    /**
     * 设置任务编号
     *
     * @param jobId 任务编号
     */
    public void setJobId(java.lang.String jobId) {
        this.jobId = jobId;
    }

    /**
     * 读取状态名称
     *
     * @return 状态名称
     */
    public String getStatusName() {
        return Dicts.name(status, Status.class);
    }

    /**
     * 状态
     *
     * @author ultrafrog
     * @version 0.1, 2017-09-06
     * @since 0.1
     */
    public static final class Status {

        @net.ufrog.common.dict.Element("运行中")
        public static final String RUNNING = "00";

        @net.ufrog.common.dict.Element("挂起")
        public static final String HANG = "01";

        @net.ufrog.common.dict.Element("成功")
        public static final String SUCCESS = "10";

        @net.ufrog.common.dict.Element("失败")
        public static final String FAILURE = "20";

        @net.ufrog.common.dict.Element("阻塞")
        public static final String BLOCK = "21";
    }
}