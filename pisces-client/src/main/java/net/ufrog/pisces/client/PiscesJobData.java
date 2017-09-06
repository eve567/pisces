package net.ufrog.pisces.client;

import com.alibaba.fastjson.JSONObject;
import net.ufrog.common.dict.Dicts;
import net.ufrog.common.jetbrick.Templates;
import net.ufrog.common.utils.Strings;
import org.springframework.web.util.HtmlUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务数据
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
public class PiscesJobData implements Serializable {

    private static final long serialVersionUID = -8994587104365130189L;

    /** 序号 */
    private String num;

    /** 模版 */
    private String template;

    /** 参数 */
    private Map<String, Object> mArg;

    /** 是否需要布局页面 */
    private Boolean needLayout;

    /** 内容 */
    private String content;

    /** 状态 */
    private String status;

    /** 构造函数 */
    private PiscesJobData() {
        this.mArg = new HashMap<>();
        this.needLayout = Boolean.TRUE;
        this.status = Status.SUCCESS;
    }

    /**
     * 构造函数
     *
     * @param num 序号
     */
    public PiscesJobData(String num) {
        this();
        this.num = num;
    }

    /**
     * 设置模版
     *
     * @param template 模版
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * 设置参数
     *
     * @param key 键值
     * @param value 内容
     */
    public void putArg(String key, Object value) {
        this.mArg.put(key, value);
    }

    /**
     * 读取序号
     *
     * @return 序号
     */
    public String getNum() {
        return num;
    }

    /**
     * 读取是否需要布局页面
     *
     * @return 是否需要布局页面
     */
    public Boolean getNeedLayout() {
        return needLayout;
    }

    /**
     * 设置是否需要布局页面
     *
     * @param needLayout 是否需要布局页面
     */
    public void setNeedLayout(Boolean needLayout) {
        this.needLayout = needLayout;
    }

    /**
     * 读取内容
     *
     * @return 内容
     */
    public String getContent() {
        if (Strings.empty(content) && !Strings.empty(template)) {
            content = Templates.renderFile(PiscesConfig.get().getTemplateDir() + "/" + PiscesConfig.get().getTemplatePrefix() + template + PiscesConfig.get().getTemplateSuffix(), mArg);
            content = HtmlUtils.htmlEscape(content);
        }
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 读取状态
     *
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
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
     * 对象转换
     *
     * @param jsonObject 数据对象
     * @return 任务数据
     */
    public static PiscesJobData fromJSONObject(JSONObject jsonObject) {
        PiscesJobData piscesJobData = new PiscesJobData(jsonObject.getString("num"));
        piscesJobData.setNeedLayout(jsonObject.getBoolean("needLayout"));
        piscesJobData.setContent(jsonObject.getString("content"));
        piscesJobData.setStatus(jsonObject.getString("status"));
        return piscesJobData;
    }

    /**
     * 状态
     *
     * @author ultrafrog
     * @version 0.1, 2017-09-06
     * @since 0.1
     */
    public static final class Status {

        @net.ufrog.common.dict.Element("成功")
        public static final String SUCCESS = "10";

        @net.ufrog.common.dict.Element("失败")
        public static final String FAILURE = "20";
    }
}
