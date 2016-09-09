package com.gallery.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * The PhotoControllerAdvice class provide a consistent response
 * when Exceptions are thrown from <code>@RequestMapping</code> Controller methods.
 */
@ControllerAdvice
public class PhotoControllerAdvice {

    /**
     * Logging system for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PhotoControllerAdvice.class);

    /**
     * Handles <code>IllegalArgumentException</code> thrown from web service controller methods.
     * @param ex A <code>IllegalArgumentException</code> instance.
     * @return response with HTTP status code 400 and exception message.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView illegalArgumentException(final IllegalArgumentException ex) {
        return getErrorModel(HttpStatus.BAD_REQUEST, ex);
    }

    /**
     * Handles <code>NullPointerException</code> thrown from web service controller methods.
     * @param ex A <code>NullPointerException</code> instance.
     * @return response with HTTP status code 404 and exception message.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NullPointerException.class)
    public ModelAndView nullPointerException(final NullPointerException ex) {
        return getErrorModel(HttpStatus.NOT_FOUND, ex);
    }

    /**
     * Handles <code>NullPointerException</code> thrown from web service controller methods.
     * @param ex A <code>NullPointerException</code> instance.
     * @return response with HTTP status code 500 and exception message.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView exception(final Exception ex) {
        return getErrorModel(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    /**
     * Creates error model for error page view.
     * @param status response status code for given exception.
     * @param e exception that needs to be handled.
     * @param <E> instance of the Exception class.
     * @return complete error model for the view.
     */
    private <E extends Exception> ModelAndView getErrorModel(final HttpStatus status, final E e) {
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