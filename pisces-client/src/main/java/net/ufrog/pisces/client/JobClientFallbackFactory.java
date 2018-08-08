package net.ufrog.pisces.client;

import net.ufrog.aries.common.contract.ClientFallbackFactory;
import net.ufrog.aries.common.contract.ListResponse;
import net.ufrog.aries.common.contract.Response;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;
import net.ufrog.pisces.client.contracts.JobRequest;
import net.ufrog.pisces.client.contracts.JobResponse;
import org.springframework.stereotype.Component;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-17
 * @since 3.0.0
 */
@Component
public class JobClientFallbackFactory extends ClientFallbackFactory<JobClient> {

    @Override
    public JobClient getClientFallback() {
        return new JobClient() {

            @Override
            public ListResponse<JobResponse> read(String appId) {
                //noinspection unchecked
                return Response.createResponse(ResultCode.NETWORK, ListResponse.class);
            }

            @Override
            public JobResponse create(String id) {
                return Response.createResponse(ResultCode.NETWORK, JobResponse.class);
            }

            @Override
            public JobResponse update(String id) {
                return Response.createResponse(ResultCode.NETWORK, JobResponse.class);
            }

            @Override
            public JobResponse trigger(String appId, String remark) {
                return Response.createResponse(ResultCode.NETWORK, JobResponse.class);
            }

            @Override
            public Response callback(JobCallbackRequest jobCallbackRequest) {
                return Response.createResponse(ResultCode.NETWORK, Response.class);
            }
        };
    }
}
