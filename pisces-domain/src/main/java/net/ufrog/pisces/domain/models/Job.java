package net.ufrog.pisces.domain.models;

/**
 * 任务模型
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "pis_job")
public class Job extends Model {

    private static final long serialVersionUID = 5324983740175121435L;

    /** 名称 */
    @javax.persistence.Column(name = "vc_name")
    private java.lang.String name;

    /** 代码 */
    @javax.persistence.Column(name = "vc_code")
    private java.lang.String code;

    /** 分组 */
    @javax.persistence.Column(name = "vc_group")
    private java.lang.String group;

    /** 日切 */
    @javax.persistence.Column(name = "dt_date")
    private java.util.Date date;

    /** 表达式 */
    @javax.persistence.Column(name = "vc_expression")
    private java.lang.String expression;

    /** 通知邮件 */
    @javax.persistence.Column(name = "vc_email")
    private java.lang.String email;

    /** 通知手机 */
    @javax.persistence.Column(name = "vc_cellphone")
    private java.lang.String cellphone;

    /** 类型 */
    @javax.persistence.Column(name = "dc_type")
    private java.lang.String type;

    /** 状态 */
    @javax.persistence.Column(name = "dc_status")
    private java.lang.String status;

    /** 应用编号 */
    @javax.persistence.Column(name = "fk_app_id")
    private java.lang.String appId;

    /**
     * 读取名称
     *
     * @return 名称
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

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
     * 读取分组
     *
     * @return 分组
     */
    public java.lang.String getGroup() {
        return group;
    }

    /**
     * 设置分组
     *
     * @param group 分组
     */
    public void setGroup(java.lang.String group) {
        this.group = group;
    }

    /**
     * 读取日切
     *
     * @return 日切
     */
    public java.util.Date getDate() {
        return date;
    }

    /**
     * 设置日切
     *
     * @param date 日切
     */
    public void setDate(java.util.Date date) {
        this.date = date;
    }

    /**
     * 读取表达式
     *
     * @return 表达式
     */
    public java.lang.String getExpression() {
        return expression;
    }

    /**
     * 设置表达式
     *
     * @param expression 表达式
     */
    public void setExpression(java.lang.String expression) {
        this.expression = expression;
    }

    /**
     * 读取通知邮件
     *
     * @return 通知邮件
     */
    public java.lang.String getEmail() {
        return email;
    }

    /**
     * 设置通知邮件
     *
     * @param email 通知邮件
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }

    /**
     * 读取通知手机
     *
     * @return 通知手机
     */
    public java.lang.String getCellphone() {
        return cellphone;
    }

    /**
     * 设置通知手机
     *
     * @param cellphone 通知手机
     */
    public void setCellphone(java.lang.String cellphone) {
        this.cellphone = cellphone;
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
     * 读取应用编号
     *
     * @return 应用编号
     */
    public java.lang.String getAppId() {
        return appId;
    }

    /**
     * 设置应用编号
     *
     * @param appId 应用编号
     */
    public void setAppId(java.lang.String appId) {
        this.appId = appId;
    }

    /**
     * 类型
     *
     * @author ultrafrog
     * @version 0.1, 2017-09-06
     * @since 0.1
     */
    public static final class Type {

        @net.ufrog.common.dict.Element("CRON")
        public static final String CRON = "00";

        @net.ufrog.common.dict.Element("间隔")
        public static final String INTERVAL = "01";
    }

    /**
     * 状态
     *
     * @author ultrafrog
     * @version 0.1, 2017-09-06
     * @since 0.1
     */
    public static final class Status {

        @net.ufrog.common.dict.Element("暂停")
        public static final String PAUSED = "00";

        @net.ufrog.common.dict.Element("运行中")
        public static final String RUNNING = "01";

        @net.ufrog.common.dict.Element("移除")
        public static final String REMOVED = "99";
    }
}