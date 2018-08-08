package net.ufrog.pisces.client.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ufrog.aries.common.contract.Request;

import java.util.Date;

/**
 * 任务请求
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-17
 * @since 3.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest extends Request {

    private static final long serialVersionUID = 6784519749779496943L;

    /** 名称 */
    private String name;

    /** 代码 */
    private String code;

    /** 分组 */
    private String group;

    /** 日切 */
    private Date date;

    /** 表达式 */
    private String expression;

    /** 通知邮件 */
    private String email;

    /** 通知手机 */
    private String cellphone;

    /** 类型 */
    private String type;

    /** 状态 */
    private String status;

    /** 应用编号 */
    private String appId;
}
