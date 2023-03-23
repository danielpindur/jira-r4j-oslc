package cz.vutbr.fit.danielpindur.oslc.shared.errors;

import org.slf4j.Logger;
import org.eclipse.lyo.oslc4j.core.model.Error;

import javax.servlet.ServletException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;

public class ErrorHandler {

    private final Logger logger;

    public ErrorHandler(Logger logger) {
        this.logger = logger;
    }

    public Response Execute(final String name, final Callable<Response> code) throws ServletException {
        try { return code.call(); }
        catch (WebApplicationException e) {
            Error errorResource = new Error();
            var statusCode = e.getResponse().getStatus();
            errorResource.setStatusCode(Integer.toString(statusCode));
            errorResource.setMessage(e.getMessage());
            return Response.status(statusCode).entity(errorResource).build();
        }
        catch (Exception e) {
            logger.error("ERROR: " + name, e);
            throw new ServletException(e);
        }
    }
}
