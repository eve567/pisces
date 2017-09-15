package net.ufrog.pisces.service;

import net.ufrog.pisces.domain.models.App;

import java.util.List;

/**
 * 应用业务接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
public interface AppService {

    /**
     * 通过编号查询应用
     *
     * @param id 编号
     * @return 应用对象
     */
    App findById(String id);

    /**
     * 通过用户中心编号查询应用
     *
     * @param leoAppId 用户中心编号
     * @return 应用对象
     */
    App findByLeoAppId(String leoAppId);

    /**
     * 查询所有应用
     *
     * @return 应用列表
     */
    List<App> findAll();

    /**
     * 创建应用
     *
     * @param app 应用实例
     * @return 应用实例
     */
    App create(App app);

    /**
     * 更新应用
     *
     * @param app 应用实例
     * @return 应用实例
     */
    App update(App app);
}
