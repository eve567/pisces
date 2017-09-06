package net.ufrog.pisces.domain.repositories;

import net.ufrog.pisces.domain.models.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 应用仓库
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Repository
public interface AppRepository extends JpaRepository<App, String> {

    /**
     * 通过应用编号查询应用
     *
     * @param leoAppId 应用编号
     * @return 应用对象
     */
    App findByLeoAppId(String leoAppId);
}
