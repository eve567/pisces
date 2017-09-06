package net.ufrog.pisces.domain.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 编号模型
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@MappedSuperclass
public class ID implements Serializable {

    private static final long serialVersionUID = -3725370594945659816L;

    /** 编号 */
    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "pk_id")
    private String id;

    /**
     * 读取编号
     *
     * @return 模型编号
     * @see #id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置编号
     *
     * @param id 模型编号
     * @see #id
     */
    public void setId(String id) {
        this.id = id;
    }
}
