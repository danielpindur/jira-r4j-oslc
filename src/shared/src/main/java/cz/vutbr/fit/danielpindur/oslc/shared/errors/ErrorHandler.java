/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.errors;

import org.slf4j.Logger;
import org.eclipse.lyo.oslc4j.core.model.Error;

import javax.servlet.ServletException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;

/**
 * Error handler for the adaptors.
 */
public class ErrorHandler {

    private final Logger logger;

    public ErrorHandler(Logger logger) {
        this.logger = logger;
    }

    /**
     * Executes the code and handles the exceptions.
     * 
     * @param name Name of the code.
     * @param code Code to execute.
     * 
     * @return Response from the code.
     * 
     * @throws ServletException If the code throws an exception.
     */
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
