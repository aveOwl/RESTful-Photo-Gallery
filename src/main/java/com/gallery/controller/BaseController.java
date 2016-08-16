package com.gallery.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * The BaseController class implements common functionality for all Controller
 * classes. The <code>@ExceptionHandler</code> method provide a consistent response
 * when Exceptions are thrown.
 */
public class BaseController {
    /**
     * Logging system for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

    /**
     * Handles Exception providing corresponding exception message
     * and HTTP status code.
     *
     * @param e An Exception instance.
     * @return An Error page containing the Exception message and
     * a HTTP status code.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleException(final Exception e) {
        return getErrorModel(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    /**
     * Creates error model for error page view.
     * @param status response status for given exception.
     * @param e exception that needs to be handled.
     * @return complete error model for the view.
     */
    private ModelAndView getErrorModel(final HttpStatus status, final Exception e) {
        LOG.error(e.getMessage());

        ModelAndView model = new ModelAndView();

        final String desc = "There is no content available. " + e.getMessage();

        model.addObject("code", status.value());
        model.addObject("reason", status.getReasonPhrase());
        model.addObject("description", desc);

        model.setViewName("error");

        return model;
    }
}
