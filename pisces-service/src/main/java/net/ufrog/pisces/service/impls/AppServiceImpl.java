package net.ufrog.pisces.service.impls;

import net.ufrog.common.utils.Objects;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.domain.models.App;
import net.ufrog.pisces.domain.repositories.AppRepository;
import net.ufrog.pisces.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 应用业务实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Service
@Transactional(readOnly = true)
public class AppServiceImpl implements AppService {

    /** 应用仓库 */
    private final AppRepository appRepository;

    /**
     * 构造函数
     *
     * @param appRepository 应用仓库
     */
    @Autowired
    public AppServiceImpl(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    @Override
    public App findById(String id) {
        return appRepository.findOne(id);
    }

    @Override
    public App findByLeoAppId(String leoAppId) {
        return appRepository.findByLeoAppId(leoAppId);
    }

    @Override
    public List<App> findAll() {
        return appRepository.findAll();
    }

    @Override
    @Transactional
    public App create(App app) {
        app.setSecret(Strings.random(64));
        return appRepository.save(app);
    }

    @Override
    @Transactional
    public App update(App app) {
        App oApp = appRepository.findOne(app.getId());
        Objects.copyProperties(oApp, app, Boolean.TRUE, "creator", "createTime", "updater", "updateTime");
        return appRepository.save(oApp);
    }
}
