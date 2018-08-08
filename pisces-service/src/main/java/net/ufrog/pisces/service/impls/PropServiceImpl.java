package net.ufrog.pisces.service.impls;

import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.domain.Models;
import net.ufrog.pisces.domain.models.Prop;
import net.ufrog.pisces.domain.repositories.PropRepository;
import net.ufrog.pisces.service.PropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 属性业务实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Service
@Transactional(readOnly = true)
public class PropServiceImpl implements PropService {

    /** 系统属性仓库 */
    private final PropRepository propRepository;

    /**
     * 构造函数
     *
     * @param propRepository 系统属性仓库
     */
    @Autowired
    public PropServiceImpl(PropRepository propRepository) {
        this.propRepository = propRepository;
    }

    @Override
    public List<Prop> findAll() {
        return propRepository.findAll();
    }

    @Override
    @Transactional
    public Prop save(String code, String value) {
        Prop prop = propRepository.findByCode(code).orElseGet(() -> Models.createProp(code));
        if (!Strings.equals(prop.getValue(), value)) {
            prop.setValue(value);
            propRepository.save(prop);
        }
        return prop;
    }
}
