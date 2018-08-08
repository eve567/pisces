package net.ufrog.pisces.server;

import net.ufrog.aries.common.contract.Response;
import net.ufrog.aries.common.exception.AriesException;
import net.ufrog.common.Logger;
import net.ufrog.common.data.exception.DataNotFoundException;
import net.ufrog.pisces.client.ResultCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.0.0, 2018-07-16
 * @since 3.0.0
 */
@ControllerAdvice
public class PiscesControllerAdvice {

    @ExceptionHandler(AriesException.class)
    @ResponseBody
    public Response handleAriesException(AriesException e) {
        Logger.error(e.getMessage(), e);
        return Response.createResponse(e.getResultCode(), Response.class);
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseBody
    public Response handleDataNotFoundException(DataNotFoundException e) {
        Logger.error(e.getMessage(), e);
        return Response.createResponse(ResultCode.DATA_NOT_FOUND, Response.class);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Response handleThrowable(Throwable e) {
        Logger.error(e.getMessage(), e);
        return Response.createResponse(ResultCode.UNBEKNOWN, Response.class);
    }
}
