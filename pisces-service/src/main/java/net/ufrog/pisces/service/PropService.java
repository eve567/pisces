package net.ufrog.pisces.service;

import net.ufrog.pisces.domain.models.Prop;

import java.util.List;

/**
 * 属性业务接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
public interface PropService {

    /**
     * 查询所有属性
     *
     * @return 属性列表
     */
    List<Prop> findAll();

    /**
     * 保存属性
     *
     * @param code 代码
     * @param value 内容
     * @return 持久化属性
     */
    Prop save(String code, String value);
}
