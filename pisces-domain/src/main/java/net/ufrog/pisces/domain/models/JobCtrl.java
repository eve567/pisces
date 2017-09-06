package net.ufrog.pisces.domain.models;

/**
 * 任务控制模型
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "pis_job_ctrl")
public class JobCtrl extends Model {

    private static final long serialVersionUID = 4611661028325264451L;

    /** 类型 */
    @javax.persistence.Column(name = "dc_type")
    private java.lang.String type;

    /** 相关编号 */
    @javax.persistence.Column(name = "fk_related_id")
    private java.lang.String relatedId;

    /** 任务编号 */
    @javax.persistence.Column(name = "fk_job_id")
    private java.lang.String jobId;

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
     * 读取相关编号
     *
     * @return 相关编号
     */
    public java.lang.String getRelatedId() {
        return relatedId;
    }

    /**
     * 设置相关编号
     *
     * @param relatedId 相关编号
     */
    public void setRelatedId(java.lang.String relatedId) {
        this.relatedId = relatedId;
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
     * 类型
     *
     * @author ultrafrog
     * @version 0.1, 2017-09-06
     * @since 0.1
     */
    public static final class Type {

        @net.ufrog.common.dict.Element("触发")
        public static final String TRIGGER = "00";

        @net.ufrog.common.dict.Element("阻塞")
        public static final String BLOCK = "01";

        @net.ufrog.common.dict.Element("挂起")
        public static final String HANG = "02";
    }
}