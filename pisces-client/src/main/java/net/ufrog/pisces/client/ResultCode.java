package net.ufrog.pisces.client;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-16
 * @since 3.0.0
 */
public class ResultCode {

    /** 构造函数 */
    private ResultCode() {}

    @net.ufrog.common.dict.Element("成功")
    public static final String SUCCESS              = "0000";

    @net.ufrog.common.dict.Element("未找到相关记录")
    public static final String NOT_FOUND            = "0001";

    @net.ufrog.common.dict.Element("无效的参数")
    public static final String INVALID_PARAM        = "0002";

    @net.ufrog.common.dict.Element("未找到相关数据")
    public static final String DATA_NOT_FOUND       = "0003";

    @net.ufrog.common.dict.Element("任务已存在")
    public static final String JOB_EXISTS           = "1001";

    @net.ufrog.common.dict.Element("网络异常")
    public static final String NETWORK              = "9998";

    @net.ufrog.common.dict.Element("未知异常")
    public static final String UNBEKNOWN            = "9999";
}
