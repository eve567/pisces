package net.ufrog.pisces.service.configurations;

import net.ufrog.common.spring.interceptor.PropertiesManager;
import net.ufrog.pisces.domain.models.Prop;
import net.ufrog.pisces.service.PropService;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库属性管理器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
public class DBPropertiesManager implements PropertiesManager {

    /** 属性业务接口 */
    private final PropService propService;

    /**
     * 构造函数
     *
     * @param propService 属性业务接口
     */
    public DBPropertiesManager(PropService propService) {
        this.propService = propService;
    }

    @Override
    public Map<String, String> load() {
        return propService.findAll().stream().collect(Collectors.toMap(Prop::getCode, Prop::getValue));
    }
}
