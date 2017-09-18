package net.ufrog.pisces.client;

import net.ufrog.common.app.App;
import net.ufrog.common.app.PropertiesValue;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
public class PiscesConfig {

    /** 唯一配置 */
    private static PiscesConfig config;

    /** 主机地址 */
    @PropertiesValue("host")
    private String host;

    /** 模版目录 */
    @PropertiesValue(value = "template.dir", defaultValue = "/templates/email")
    private String templateDir;

    /** 模版前缀 */
    @PropertiesValue(value = "template.prefix")
    private String templatePrefix;

    /** 模版后缀 */
    @PropertiesValue(value = "template.suffix", defaultValue = ".html")
    private String templateSuffix;

    /**
     * 读取主机地址
     *
     * @return 主机地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 读取模版目录
     *
     * @return 模版目录
     */
    public String getTemplateDir() {
        return templateDir;
    }

    /**
     * 设置模版目录
     *
     * @param templateDir 模版目录
     */
    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    /**
     * 读取模版前缀
     *
     * @return 模版前缀
     */
    public String getTemplatePrefix() {
        return templatePrefix;
    }

    /**
     * 设置模版前缀
     *
     * @param templatePrefix 模版前缀
     */
    public void setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = templatePrefix;
    }

    /**
     * 读取模版后缀
     *
     * @return 模版后缀
     */
    public String getTemplateSuffix() {
        return templateSuffix;
    }

    /**
     * 设置模版后缀
     *
     * @param templateSuffix 模版后缀
     */
    public void setTemplateSuffix(String templateSuffix) {
        this.templateSuffix = templateSuffix;
    }

    /**
     * 读取配置实例
     *
     * @return 配置实例
     */
    public static PiscesConfig get() {
        if (config == null) {
            config = App.bean("pisces", PiscesConfig.class);
        }
        return config;
    }
}
