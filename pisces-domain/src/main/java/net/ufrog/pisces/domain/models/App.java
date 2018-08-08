package net.ufrog.pisces.domain.models;

import net.ufrog.aries.common.jpa.Model;

/**
 * 应用模型
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@java.lang.SuppressWarnings("unused")
@javax.persistence.Entity
@javax.persistence.Table(name = "pis_app")
public class App extends Model {

    private static final long serialVersionUID = 2343319421420496409L;

    /** 名称 */
    @javax.persistence.Column(name = "vc_name")
    private java.lang.String name;

    /** 访问地址 */
    @javax.persistence.Column(name = "vc_url")
    private java.lang.String url;

    /** 密钥 */
    @javax.persistence.Column(name = "vc_secret")
    private java.lang.String secret;

    /** 邮件通知 */
    @javax.persistence.Column(name = "vc_email")
    private java.lang.String email;

    /** 通知手机 */
    @javax.persistence.Column(name = "vc_cellphone")
    private java.lang.String cellphone;

    /** 应用编号 */
    @javax.persistence.Column(name = "fk_leo_app_id")
    private java.lang.String leoAppId;

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
     * 读取访问地址
     *
     * @return 访问地址
     */
    public java.lang.String getUrl() {
        return url;
    }

    /**
     * 设置访问地址
     *
     * @param url 访问地址
     */
    public void setUrl(java.lang.String url) {
        this.url = url;
    }

    /**
     * 读取密钥
     *
     * @return 密钥
     */
    public java.lang.String getSecret() {
        return secret;
    }

    /**
     * 设置密钥
     *
     * @param secret 密钥
     */
    public void setSecret(java.lang.String secret) {
        this.secret = secret;
    }

    /**
     * 读取邮件通知
     *
     * @return 邮件通知
     */
    public java.lang.String getEmail() {
        return email;
    }

    /**
     * 设置邮件通知
     *
     * @param email 邮件通知
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
     * 读取应用编号
     *
     * @return 应用编号
     */
    public java.lang.String getLeoAppId() {
        return leoAppId;
    }

    /**
     * 设置应用编号
     *
     * @param leoAppId 应用编号
     */
    public void setLeoAppId(java.lang.String leoAppId) {
        this.leoAppId = leoAppId;
    }
}