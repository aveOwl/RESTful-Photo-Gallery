package com.gallery.controller;

import com.gallery.util.StorageException;
import com.gallery.util.StorageFileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * The PhotoControllerAdvice class provides a consistent response
 * when Exceptions are thrown from <code>@RequestMapping</code> controller methods.
 */
@ControllerAdvice
public class PhotoControllerAdvice {

    /**
     * Handles <code>StorageException</code> thrown from web service controller methods.
     *
     * @param ex A <code>StorageException</code> instance.
     * @return response with HTTP status code 400 and exception message.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StorageException.class)
    public ModelAndView storageException(final StorageException ex) {
        return this.getDefaultErrorModel(HttpStatus.BAD_REQUEST, ex);
    }

    /**
     * Handles <code>StorageFileNotFoundException</code> thrown from web service controller methods.
     *
     * @param ex A <code>StorageFileNotFoundException</code> instance.
     * @return response with HTTP status code 404 and exception message.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ModelAndView storageFileNotFoundException(final StorageFileNotFoundException ex) {
        return this.getDefaultErrorModel(HttpStatus.NOT_FOUND, ex);
    }

    /**
     * Handles <code>Exception</code> thrown from web service controller methods.
     *
     * @param ex A <code>Exception</code> instance.
     * @return response with HTTP status code 500 and exception message.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView exception(final Exception ex) {
        return this.getDefaultErrorModel(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    /**
     * Creates error model for the error page view.
     *
     * @param status response status code for given exception.
     * @param e      exception that needs to be handled.
     * @param <E>    instance of the Exception class.
     * @return complete error model for the view.
     */
    private <E extends Exception> ModelAndView getDefaultErrorModel(final HttpStatus status, final E e) {
        final ModelAndView model = new ModelAndView("error");

        final String desc = "There is no content available. " + e.getMessage();

        model.addObject("code", status.value());
        model.addObject("reason", status.getReasonPhrase());
        model.addObject("description", desc);

        return model;
    }
}
