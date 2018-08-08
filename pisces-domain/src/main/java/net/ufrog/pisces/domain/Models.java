package net.ufrog.pisces.domain;

import net.ufrog.pisces.domain.models.JobLog;
import net.ufrog.pisces.domain.models.JobLogDetail;
import net.ufrog.pisces.domain.models.Prop;

import java.util.Date;

/**
 * 模型工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
public class Models {

    /** 构造函数 */
    private Models() {}

    /**
     * 新建任务日志实例
     *
     * @param jobId 任务编号
     * @param remark 备注
     * @return 任务日志实例
     */
    public static JobLog createJobLog(String jobId, String remark) {
        JobLog jobLog = new JobLog();

        jobLog.setDatetimeBegin(new Date());
        jobLog.setRemark(remark);
        jobLog.setStatus(JobLog.Status.RUNNING);
        jobLog.setJobId(jobId);
        return jobLog;
    }

    /**
     * 新建任务日志明细实例
     *
     * @param jobLogId 任务日志编号
     * @param remark 备注
     * @param type 类型
     * @return 任务日志明细
     */
    public static JobLogDetail createJobLogDetail(String jobLogId, String remark, String type) {
        JobLogDetail jobLogDetail = new JobLogDetail();

        jobLogDetail.setDatetime(new Date());
        jobLogDetail.setRemark(remark);
        jobLogDetail.setType(type);
        jobLogDetail.setJobLogId(jobLogId);
        return jobLogDetail;
    }

    /**
     * 创建属性模型实例
     *
     * @param code 代码
     * @return 属性模型实例
     */
    public static Prop createProp(String code) {
        Prop prop = new Prop();
        prop.setCode(code);
        return prop;
    }
}
