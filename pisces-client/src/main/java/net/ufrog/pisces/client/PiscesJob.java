package net.ufrog.pisces.client;

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
     * @param date 日切
     * @param args 参数
     * @param jobData 任务数据
     */
    void run(Date date, Map<String, String> args, final PiscesJobData jobData);
}
