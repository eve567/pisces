package net.ufrog.pisces.client;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.ufrog.aries.common.contract.ListResponse;
import net.ufrog.aries.common.contract.Response;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;
import net.ufrog.pisces.client.contracts.JobResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 任务服务客户端
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-16
 * @since 3.0.0
 */
@FeignClient(name = Client.NAME, fallbackFactory = JobClientFallbackFactory.class)
@RequestMapping(value = "/jobs")
@Api(description = "任务服务")
public interface JobClient {

    /**
     * 查询相关应用的所有任务
     *
     * @param appId 应用编号
     * @return 任务响应列表
     */
    @RequestMapping(value = "/{appId}", method = RequestMethod.GET)
    @ApiOperation(value = "查询相关应用编号下的所有任务")
    @ApiImplicitParam(name = "appId", required = true, paramType = "path")
    ListResponse<JobResponse> read(@PathVariable("appId") String appId);

    /**
     * 创建任务
     *
     * @param id 编号
     * @return 任务响应
     */
    @RequestMapping(value = "/server/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "创建任务，并自动加入容器中")
    JobResponse create(@PathVariable("id") String id);

    /**
     * 更新任务
     *
     * @param id 编号
     * @return 任务响应
     */
    @RequestMapping(value = "/server/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新任务，并自动刷新容器中任务信息")
    JobResponse update(@PathVariable("id") String id);

    /**
     * 触发任务
     *
     * @param id 编号
     * @param remark 备注
     * @return 任务响应
     */
    @RequestMapping(value = "/trigger/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "触发任务调度")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, paramType = "path", value = "任务编号"),
            @ApiImplicitParam(name = "remark", paramType = "body", value = "备注信息")
    })
    JobResponse trigger(@PathVariable("id") String id, @RequestBody String remark);

    /**
     * 回调任务
     *
     * @param jobCallbackRequest 任务回调请求
     * @return 结果响应
     */
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ApiOperation(value = "任务结束之后的回调")
    Response callback(@RequestBody JobCallbackRequest jobCallbackRequest);
}
