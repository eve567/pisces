package net.ufrog.pisces.console;

import com.alibaba.fastjson.JSON;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import net.ufrog.common.Result;
import net.ufrog.pisces.service.beans.Props;

import java.util.List;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-18
 * @since 0.1
 */
public class PiscesAPIs {

    private static final String URI_FIND_ALL    = "/api/find_all/%s";
    private static final String URI_CREATE      = "/api/create/%s";
    private static final String URI_UPDATE      = "/api/update/%s";
    private static final String URI_TRIGGER     = "/api/trigger/%s";

    /** 构造函数 */
    private PiscesAPIs() {}

    /**
     * 查询所有
     *
     * @param appId 应用编号
     * @return 任务列表
     */
    public static List<?> findAll(String appId) {
        HttpResponse resp = HttpRequest.get(Props.getServerUrl() + String.format(URI_FIND_ALL, appId)).charset("utf-8").send();
        return JSON.parseArray(resp.bodyText());
    }

    /**
     * 创建任务<br>
     * 将任务加入调度内存
     *
     * @param jobId 任务编号
     * @return 创建结果
     */
    public static Result<?> create(String jobId) {
        HttpResponse resp = HttpRequest.get(Props.getServerUrl() + String.format(URI_CREATE, jobId)).charset("utf-8").send();
        return JSON.parseObject(resp.bodyText(), Result.class);
    }

    /**
     * 更新任务<br>
     * 将任务调度内存中的任务重置
     *
     * @param jobId 任务编号
     * @return 更新结果
     */
    public static Result<?> update(String jobId) {
        HttpResponse resp = HttpRequest.get(Props.getServerUrl() + String.format(URI_UPDATE, jobId)).charset("utf-8").send();
        return JSON.parseObject(resp.bodyText(), Result.class);
    }

    /**
     * 触发任务
     *
     * @param jobId 任务编号
     * @return 触发结果
     */
    public static Result<?> trigger(String jobId) {
        HttpResponse resp = HttpRequest.get(Props.getServerUrl() + String.format(URI_TRIGGER, jobId)).charset("utf-8").send();
        return JSON.parseObject(resp.bodyText(), Result.class);
    }
}
