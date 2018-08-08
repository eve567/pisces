package net.ufrog.pisces.domain.repositories;

import net.ufrog.pisces.domain.models.Prop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 属性仓库
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Repository
public interface PropRepository extends JpaRepository<Prop, String> {

    /**
     * 通过代码查询属性
     *
     * @param code 代码
     * @return 属性对象
     */
    Optional<Prop> findByCode(String code);
}
