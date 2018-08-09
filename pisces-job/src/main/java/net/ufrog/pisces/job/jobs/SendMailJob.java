package net.ufrog.pisces.job.jobs;

import net.ufrog.common.utils.Calendars;
import net.ufrog.pisces.client.PiscesJob;
import net.ufrog.pisces.client.contracts.JobCallRequest;
import net.ufrog.pisces.client.contracts.JobCallbackRequest;
import org.springframework.stereotype.Component;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-08-08
 * @since 3.0.0
 */
@Component("sendMailJob")
public class SendMailJob implements PiscesJob {

    @Override
    public void run(JobCallRequest jobCallRequest, JobCallbackRequest jobCallbackRequest) {
        jobCallbackRequest.setTemplate("send_mail");
        jobCallbackRequest.putArg("date", Calendars.datetime());
    }
}
