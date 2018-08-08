package net.ufrog.pisces.domain.models;

import net.ufrog.aries.common.jpa.Model;

/**
 * 任务参数模型
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@java.lang.SuppressWarnings("unused")
@javax.persistence.Entity
@javax.persistence.Table(name = "pis_job_param")
public class JobParam extends Model {

    private static final long serialVersionUID = 5589907945029898414L;

    /** 代码 */
    @javax.persistence.Column(name = "vc_code")
    private java.lang.String code;

    /** 内容 */
    @javax.persistence.Column(name = "vc_value")
    private java.lang.String value;

    /** 任务编号 */
    @javax.persistence.Column(name = "fk_job_id")
    private java.lang.String jobId;

    /**
     * 读取代码
     *
     * @return 代码
     */
    public java.lang.String getCode() {
        return code;
    }

    /**
     * 设置代码
     *
     * @param code 代码
     */
    public void setCode(java.lang.String code) {
        this.code = code;
    }

    /**
     * 读取内容
     *
     * @return 内容
     */
    public java.lang.String getValue() {
        return value;
    }

    /**
     * 设置内容
     *
     * @param value 内容
     */
    public void setValue(java.lang.String value) {
        this.value = value;
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
}