package net.ufrog.pisces.client.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ufrog.aries.common.contract.Response;

import java.util.Date;

/**
 * 任务响应
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-16
 * @since 3.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse extends Response {

    private static final long serialVersionUID = -5877869692209669991L;

    /** 编号 */
    private String id;

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

    /** 类型名称 */
    private String typeName;

    /** 状态 */
    private String status;

    /** 状态名称 */
    private String statusName;

    /** 应用编号 */
    private String appId;

    /** 下次执行时间 */
    private Date nextFireTime;
}
