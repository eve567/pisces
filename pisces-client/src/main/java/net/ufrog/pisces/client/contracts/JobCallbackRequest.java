package net.ufrog.pisces.client.contracts;

import lombok.Getter;
import lombok.Setter;
import net.ufrog.aries.common.contract.Request;
import net.ufrog.common.dict.Dicts;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务回调请求
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-17
 * @since 3.0.0
 */
@Getter
@Setter
public class JobCallbackRequest extends Request {

    private static final long serialVersionUID = -3534499573680497394L;

    /** 序号 */
    private String num;

    /** 模版 */
    private String template;

    /** 是否需要布局页面 */
    private Boolean needLayout;

    /** 内容 */
    private String content;

    /** 状态 */
    private String status;

    /** 备注 */
    private String remark;

    /** 参数 */
    private Map<String, Object> args;

    /** 构造函数 */
    private JobCallbackRequest() {
        this.args = new HashMap<>();
        this.needLayout = Boolean.TRUE;
        this.status = Status.SUCCESS;
    }

    /**
     * 构造函数
     *
     * @param num 序号
     */
    public JobCallbackRequest(String num) {
        this();
        this.num = num;
    }

    /**
     * 设置参数
     *
     * @param key 键值
     * @param value 内容
     */
    public void putArg(String key, Object value) {
        this.args.put(key, value);
    }

    /**
     * 读取状态名称
     *
     * @return 状态名称
     */
    public String getStatusName() {
        return Dicts.name(status, Status.class);
    }

    /**
     * 状态
     *
     * @author ultrafrog
     * @version 3.0.0, 2018-07-17
     * @since 3.0.0
     */
    public static final class Status {

        @net.ufrog.common.dict.Element("成功")
        public static final String SUCCESS = "10";

        @net.ufrog.common.dict.Element("失败")
        public static final String FAILURE = "20";
    }
}
