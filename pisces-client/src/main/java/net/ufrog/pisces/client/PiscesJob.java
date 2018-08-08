package net.ufrog.pisces.client;

import net.ufrog.pisces.client.contracts.JobCallRequest;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;

import java.util.Date;
import java.util.Map;

/**
 * 任务接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
public interface PiscesJob {

    /**
     * 执行任务
     *
     * @param jobCallRequest 任务调用请求
     * @param jobCallbackRequest 任务回调请求
     */
    void run(final JobCallRequest jobCallRequest, final JobCallbackRequest jobCallbackRequest);
}
