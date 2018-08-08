package net.ufrog.pisces.client;

import lombok.Getter;
import lombok.Setter;
import net.ufrog.common.jetbrick.Templates;
import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.HtmlUtils;

/**
 * 属性
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-17
 * @since 3.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ufrog.pisces.template")
public class PiscesTemplateProperties {

    /** 目录  */
    private String dir = "/templates/email";

    /** 前缀 */
    private String prefix = "";

    /** 后缀 */
    private String suffix = ".html";

    /**
     * 渲染模版
     *
     * @param jobCallbackRequest 任务回调请求
     * @return 渲染模版后的任务回调请求
     */
    public JobCallbackRequest render(JobCallbackRequest jobCallbackRequest) {
        if (!Strings.empty(jobCallbackRequest.getTemplate())) {
            String content = Templates.renderFile(dir + "/" + prefix + jobCallbackRequest.getTemplate() + suffix, jobCallbackRequest.getArgs());
            jobCallbackRequest.setContent(HtmlUtils.htmlEscape(content));
        }
        return jobCallbackRequest;
    }
}
