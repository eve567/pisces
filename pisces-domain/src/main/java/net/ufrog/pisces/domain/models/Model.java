package net.ufrog.pisces.domain.models;

import net.ufrog.common.Logger;
import net.ufrog.common.app.App;
import net.ufrog.common.exception.NotSignException;

import javax.persistence.*;
import java.util.Date;

/**
 * 基础模型
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@MappedSuperclass
public class Model extends ID {

    private static final long serialVersionUID = 6129682241307477978L;

    /** 创建用户 */
    @Column(name = "fk_creator")
    private String creator;

    /** 创建时间 */
    @Column(name = "dt_create_time")
    private Date createTime;

    /** 更新用户 */
    @Column(name = "fk_updater")
    private String updater;

    /** 更新时间 */
    @Column(name = "dt_update_time")
    private Date updateTime;

    /**
     * 读取创建用户
     *
     * @return 创建用户
     */
    public String getCreator() {
        return creator;
    }

    /**
     * 设置创建用户
     *
     * @param creator 创建用户
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 读取创建时间
     *
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 读取更新用户
     *
     * @return 更新用户
     */
    public String getUpdater() {
        return updater;
    }

    /**
     * 设置更新用户
     *
     * @param updater 更新用户
     */
    public void setUpdater(String updater) {
        this.updater = updater;
    }

    /**
     * 读取更新时间
     *
     * @return 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /** 持久化前回调 */
    @PrePersist
    private void onPrePersist() {
        setCreator(getCurrentUserId());
        setCreateTime(new Date());
        setUpdater(getCurrentUserId());
        setUpdateTime(new Date());
    }

    /** 持久化后回调 */
    @PostPersist
    private void onPostPersist() {
        Logger.debug("insert data '%s - %s' by user '%s'", getClass().getSimpleName(), getId(), getCreator());
    }

    /** 更新前回调 */
    @PreUpdate
    private void onPreUpdate() {
        setUpdater(getCurrentUserId());
        setUpdateTime(new Date());
    }

    /** 更新后回调 */
    @PostUpdate
    private void onPostUpdate() {
        Logger.debug("update data '%s - %s' by user '%s'", getClass().getSimpleName(), getId(), getUpdater());
    }

    /**
     * 读取当前用户编号
     *
     * @return 当前用户编号
     */
    private String getCurrentUserId() {
        try {
            return App.user().getId();
        } catch (NotSignException e) {
            return null;
        } catch (Exception e) {
            Logger.warn(e.getMessage());
            return null;
        }
    }
}
