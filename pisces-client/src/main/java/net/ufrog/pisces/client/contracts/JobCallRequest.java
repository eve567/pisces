package net.ufrog.pisces.client.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ufrog.aries.common.contract.Request;

import java.util.Date;
import java.util.Map;

/**
 * 任务调用请求
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-17
 * @since 3.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobCallRequest extends Request {

    private static final long serialVersionUID = -7218211629520964474L;

    /** 任务代码 */
    private String jobCode;

    /** 序号 */
    private String num;

    /** 日切 */
    private Date date;

    /** 参数映射 */
    private Map<String, String> params;
}
