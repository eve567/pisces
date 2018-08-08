package net.ufrog.pisces.server;

import net.ufrog.common.utils.Strings;
import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.domain.models.JobCtrl;
import net.ufrog.pisces.domain.models.JobParam;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务封装
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-12
 * @since 0.1
 */
public class JobWrapper implements Serializable {

    private static final long serialVersionUID = -6896876525223611158L;

    /** 任务 */
    private Job job;

    /** 任务明细 */
    private JobDetail jobDetail;

    /** 触发器 */
    private Trigger trigger;

    /** 下次执行时间 */
    private Date nextFireTime;

    /** 任务控制映射 */
    private Map<String, List<JobCtrl>> mlJobCtrl;

    /** 任务参数映射 */
    private Map<String, String> mJobParam;

    /** 构造函数 */
    private JobWrapper() {}

    /**
     * 构造函数
     *
     * @param job 任务
     * @param jobDetail 任务明细
     * @param trigger 触发器
     * @param lJobCtrl 任务控制列表
     * @param lJobParam 任务参数列表
     */
    JobWrapper(Job job, JobDetail jobDetail, Trigger trigger, List<JobCtrl> lJobCtrl, List<JobParam> lJobParam) {
        this();
        this.job = job;
        this.jobDetail = jobDetail;
        this.trigger = trigger;
        this.setJobCtrl(lJobCtrl);
        this.setJobParam(lJobParam);
    }

    /**
     * 读取编号
     *
     * @return 编号
     */
    public String getId() {
        return job.getId();
    }

    /**
     * 读取名称
     *
     * @return 名称
     */
    public String getName() {
        return job.getName();
    }

    /**
     * 读取代码
     *
     * @return 代码
     */
    public String getCode() {
        return job.getCode();
    }

    /**
     * 读取分组
     *
     * @return 分组
     */
    public String getGroup() {
        return job.getGroup();
    }

    /**
     * 读取日切
     *
     * @return 日切
     */
    public Date getDate() {
        return job.getDate();
    }

    /**
     * 读取表达式
     *
     * @return 表达式
     */
    public String getExpression() {
        return job.getExpression();
    }

    /**
     * 读取通知邮件
     *
     * @return 通知邮件
     */
    public String getEmail() {
        return job.getEmail();
    }

    /**
     * 读取通知手机
     *
     * @return 通知手机
     */
    public String getCellphone() {
        return job.getCellphone();
    }

    /**
     * 读取类型
     *
     * @return 类型
     */
    public String getType() {
        return job.getType();
    }

    /**
     * 读取状态
     *
     * @return 状态
     */
    public String getStatus() {
        return job.getStatus();
    }

    /**
     * 读取应用编号
     *
     * @return 应用编号
     */
    public String getAppId() {
        return job.getAppId();
    }

    /**
     * 读取任务明细
     *
     * @return 任务明细
     */
    public JobDetail getJobDetail() {
        return jobDetail;
    }

    /**
     * 读取触发器
     *
     * @return 触发器
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * 读取下次执行时间
     *
     * @return 下次执行时间
     */
    public Date getNextFireTime() {
        return nextFireTime;
    }

    /**
     * 设置下次执行时间
     *
     * @param nextFireTime 下次执行时间
     */
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    /**
     * 读取类型名称
     *
     * @return 类型名称
     */
    public String getTypeName() {
        return job.getTypeName();
    }

    /**
     * 读取状态名称
     *
     * @return 状态名称
     */
    public String getStatusName() {
        return job.getStatusName();
    }

    /**
     * 读取邮件通知列表
     *
     * @return 邮件通知列表
     */
    public List<String> getEmails() {
        if (!Strings.empty(job.getEmail())) {
            return Strings.explode(job.getEmail(), ",").stream().map(String::trim).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 读取任务控制列表
     *
     * @param type 类型
     * @return 任务控制列表
     */
    List<JobCtrl> getJobCtrls(String type) {
        List<JobCtrl> lJobCtrl = mlJobCtrl.get(type);
        return lJobCtrl == null ? Collections.emptyList() : lJobCtrl;
    }

    /**
     * 读取任务参数映射
     *
     * @return 任务参数映射
     */
    Map<String, String> getJobParams() {
        return mJobParam;
    }

    /**
     * 读取任务
     *
     * @return 任务实例
     */
    public Job getJob() {
        return job;
    }

    /**
     * 设置任务控制
     *
     * @param lJobCtrl 任务控制列表
     */
    private void setJobCtrl(List<JobCtrl> lJobCtrl) {
        mlJobCtrl = new HashMap<>();
        for (JobCtrl jc: lJobCtrl) {
            if (!mlJobCtrl.containsKey(jc.getType())) mlJobCtrl.put(jc.getType(), new ArrayList<>());
            mlJobCtrl.get(jc.getType()).add(jc);
        }
    }

    /**
     * 设置任务参数
     *
     * @param lJobParam 任务参数
     */
    private void setJobParam(List<JobParam> lJobParam) {
        mJobParam = new HashMap<>();
        lJobParam.forEach(jp -> mJobParam.put(jp.getCode(), jp.getValue()));
    }
}
