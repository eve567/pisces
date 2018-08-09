package net.ufrog.pisces.job.jobs;

import net.ufrog.common.exception.ServiceException;
import net.ufrog.pisces.client.PiscesJob;
import net.ufrog.pisces.client.contracts.JobCallRequest;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;
import org.springframework.stereotype.Component;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-08-09
 * @since 3.0.0
 */
@Component("exceptionJob")
public class ExceptionJob implements PiscesJob {

    @Override
    public void run(JobCallRequest jobCallRequest, JobCallbackRequest jobCallbackRequest) {
        throw new ServiceException("It's a exception test.");
    }
}
