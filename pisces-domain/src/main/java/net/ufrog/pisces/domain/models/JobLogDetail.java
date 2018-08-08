package net.ufrog.pisces.domain.models;

import net.ufrog.aries.common.jpa.Model;
import net.ufrog.common.dict.Dicts;

/**
 * 任务日志明细模型
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@java.lang.SuppressWarnings("unused")
@javax.persistence.Entity
@javax.persistence.Table(name = "pis_job_log_detail")
public class JobLogDetail extends Model {

    private static final long serialVersionUID = -4673343061028809726L;

    /** 时间 */
    @javax.persistence.Column(name = "dt_datetime")
    private java.util.Date datetime;

    /** 备注 */
    @javax.persistence.Column(name = "vc_remark")
    private java.lang.String remark;

    /** 类型 */
    @javax.persistence.Column(name = "dc_type")
    private java.lang.String type;

    /** 任务日志编号 */
    @javax.persistence.Column(name = "fk_job_log_id")
    private java.lang.String jobLogId;

    /**
     * 读取时间
     *
     * @return 时间
     */
    public java.util.Date getDatetime() {
        return datetime;
    }

    /**
     * 设置时间
     *
     * @param datetime 时间
     */
    public void setDatetime(java.util.Date datetime) {
        this.datetime = datetime;
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
     * 读取类型
     *
     * @return 类型
     */
    public java.lang.String getType() {
        return type;
    }

    /**
     * 设置类型
     *
     * @param type 类型
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }

    /**
     * 读取任务日志编号
     *
     * @return 任务日志编号
     */
    public java.lang.String getJobLogId() {
        return jobLogId;
    }

    /**
     * 设置任务日志编号
     *
     * @param jobLogId 任务日志编号
     */
    public void setJobLogId(java.lang.String jobLogId) {
        this.jobLogId = jobLogId;
    }

    /**
     * 读取类型名称
     *
     * @return 类型名称
     */
    public String getTypeName() {
        return Dicts.name(type, Type.class);
    }

    /**
     * 类型
     *
     * @author ultrafrog
     * @version 0.1, 2017-09-06
     * @since 0.1
     */
    public static final class Type {

        @net.ufrog.common.dict.Element("触发")
        public static final String TRIGGER = "00";

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