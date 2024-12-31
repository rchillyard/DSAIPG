/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * LazyLogger is an extension of the {@link Logger} class providing lazy logging capabilities.
 * This class enhances the performance of logging by deferring the evaluation of log messages
 * until it is determined that the log level is enabled.
 * <p>
 * The primary features of LazyLogger include:
 * - Lazy evaluation of log messages using {@link Supplier<String>}.
 * - Standard logging methods that don't defer the evaluation of messages.
 * - Utility methods inherited from the {@link Logger} class for additional functionality.
 * <p>
 * The class supports both lazy and standard logging methods for TRACE and DEBUG log levels.
 */
@SuppressWarnings("rawtypes")
public class LazyLogger extends Logger {

    /**
     * Constructs a LazyLogger instance for a specific class.
     *
     * @param clazz the class for which the logger is created.
     */
    public LazyLogger(Class<?> clazz) {
        super("LazyLogger");
        logger = Logger.getLogger(clazz);
    }

    /**
     * Logs a trace message if trace logging is enabled. The message is generated lazily by invoking the supplied {@code Supplier}.
     *
     * @param fMessage a {@code Supplier<String>} that provides the message to log.
     */
    public void trace(Supplier<String> fMessage) {
        if (logger.isTraceEnabled())
            logger.trace(fMessage.get());
    }

    /**
     * Logs a trace message using a supplier for the message and an associated throwable, if trace level logging is enabled.
     *
     * @param fMessage a supplier providing the message to be logged.
     * @param t        the throwable to be logged alongside the message.
     */
    public void trace(Supplier<String> fMessage, Throwable t) {
        if (logger.isTraceEnabled())
            logger.trace(fMessage.get(), t);
    }

    /**
     * Logs a debug message using a lazy evaluation approach.
     *
     * @param fMessage a supplier that provides the debug message. The message will only be computed
     *                 and logged if debug logging is enabled.
     */
    public void debug(Supplier<String> fMessage) {
        if (logger.isDebugEnabled())
            logger.debug(fMessage.get());
    }

    /**
     * Logs a debug message and an associated throwable if the debug logging level is enabled.
     *
     * @param fMessage the supplier providing the debug message to be logged.
     * @param t the throwable to be logged alongside the debug message.
     */
    public void debug(Supplier<String> fMessage, Throwable t) {
        if (logger.isDebugEnabled())
            logger.debug(fMessage.get(), t);
    }

    /**
     * Checks if trace level logging is enabled for the underlying logger.
     *
     * @return true if trace logging is enabled, false otherwise.
     */
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    /**
     * Checks if debug level logging is enabled.
     *
     * @return true if debug logging is enabled, false otherwise.
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * Logs a trace message at the TRACE level without deferring message evaluation.
     *
     * @param message the non-lazy message object to be logged at the TRACE level.
     */
    public void trace(Object message) {
        logger.trace("NOT lazy: " + message);
    }

    /**
     * Logs a trace-level message along with a throwable.
     *
     * @param message the message object to be logged.
     * @param t       the throwable associated with the message.
     */
    public void trace(Object message, Throwable t) {
        logger.trace("NOT lazy: " + message, t);
    }

    /**
     * Logs a debug message without lazy evaluation of the provided message object.
     *
     * @param message an object containing the message to be logged.
     */
    public void debug(Object message) {
        logger.debug("NOT lazy: " + message);
    }

    /**
     * Logs a debug message along with an accompanying throwable.
     *
     * @param message the message object to log.
     * @param t       the throwable associated with the message, providing further details about an error or exception.
     */
    public void debug(Object message, Throwable t) {
        logger.debug("NOT lazy: " + message, t);
    }

    /**
     * Retrieves a logger by its name.
     *
     * @param name the name of the logger to retrieve.
     * @return the logger associated with the specified name.
     */
    public static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }

    /**
     * Retrieves a Logger instance associated with the specified class.
     *
     * @param clazz the class for which the Logger instance is requested.
     * @return the Logger instance corresponding to the provided class.
     */
    public static Logger getLogger(Class clazz) {
        return Logger.getLogger(clazz);
    }

    /**
     * Retrieves the root logger instance.
     *
     * @return the root {@link Logger} instance.
     */
    public static Logger getRootLogger() {
        return Logger.getRootLogger();
    }

    /**
     * Retrieves a logger instance by name using a specified LoggerFactory.
     *
     * @param name    the name of the logger to retrieve.
     * @param factory the LoggerFactory to use for creating or retrieving the logger.
     * @return the Logger instance associated with the specified name and created using the provided LoggerFactory.
     */
    public static Logger getLogger(String name, LoggerFactory factory) {
        return Logger.getLogger(name, factory);
    }

    /**
     * Adds a new appender to the logger instance associated with this LazyLogger.
     *
     * @param newAppender the appender to be added to the logger. It must not be null and should implement the Appender interface.
     */
    public void addAppender(Appender newAppender) {
        logger.addAppender(newAppender);
    }

    /**
     * Logs an assertion message based on the assertion condition.
     *
     * @param assertion the boolean condition to test; if false, an assertion message is logged.
     * @param msg       the message to log if the assertion fails.
     */
    public void assertLog(boolean assertion, String msg) {
        logger.assertLog(assertion, msg);
    }

    /**
     * Forwards the given logging event to all the appenders associated with the underlying logger.
     *
     * @param event the LoggingEvent to be processed by the appenders.
     */
    public void callAppenders(LoggingEvent event) {
        logger.callAppenders(event);
    }

    /**
     * Logs an error-level message.
     *
     * @param message the message object to log.
     */
    public void error(Object message) {
        logger.error(message);
    }

    /**
     * Logs an error message, along with a throwable for debugging or tracking purposes.
     *
     * @param message the error message to log. This can be any object, typically a string, containing details about the error.
     * @param t       the throwable associated with the error, providing stack trace information for deeper analysis.
     */
    public void error(Object message, Throwable t) {
        logger.error(message, t);
    }

    /**
     * Logs a fatal level message using the underlying logger.
     *
     * @param message the message to log. The message is of type Object, and its string representation will be logged.
     */
    public void fatal(Object message) {
        logger.fatal(message);
    }

    /**
     * Logs a fatal level message along with a throwable.
     *
     * @param message the message object to log.
     * @param t       the throwable associated with the message.
     */
    public void fatal(Object message, Throwable t) {
        logger.fatal(message, t);
    }

    /**
     * Retrieves the additivity setting of the underlying logger.
     *
     * @return true if additivity is enabled for the logger, false otherwise.
     */
    public boolean getAdditivity() {
        return logger.getAdditivity();
    }

    /**
     * Retrieves all the appenders associated with the logger.
     *
     * @return an Enumeration of all appenders currently attached to the logger.
     */
    public Enumeration getAllAppenders() {
        return logger.getAllAppenders();
    }

    /**
     * Retrieves an Appender by its name.
     *
     * @param name the name of the appender to be retrieved.
     * @return the Appender associated with the specified name, or null if no appender is found.
     */
    public Appender getAppender(String name) {
        return logger.getAppender(name);
    }

    /**
     * Retrieves the effective logging level for the underlying logger.
     *
     * @return the effective {@code Level} of the logger, representing the current logging threshold.
     */
    public Level getEffectiveLevel() {
        return logger.getEffectiveLevel();
    }

    /**
     * Retrieves the LoggerRepository associated with this logger instance.
     *
     * @return the LoggerRepository object used by this logger.
     */
    public LoggerRepository getLoggerRepository() {
        return logger.getLoggerRepository();
    }

    /**
     * Gets the resource bundle associated with this logger.
     *
     * @return the resource bundle for this logger, or null if no resource bundle is set.
     */
    public ResourceBundle getResourceBundle() {
        return logger.getResourceBundle();
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param message the message to be logged
     */
    public void info(Object message) {
        logger.info(message);
    }

    /**
     * Logs an informational message along with an associated throwable.
     *
     * @param message the informational message to log.
     * @param t       the throwable to include in the log entry, which provides additional context such as stack trace.
     */
    public void info(Object message, Throwable t) {
        logger.info(message, t);
    }

    /**
     * Checks if the specified appender is attached to the logger.
     *
     * @param appender the appender to be checked.
     * @return true if the appender is attached, false otherwise.
     */
    public boolean isAttached(Appender appender) {
        return logger.isAttached(appender);
    }

    /**
     * Determines whether the logging instance is enabled for the specified priority level.
     *
     * @param level the priority level to check.
     * @return true if the logger is enabled for the specified priority level; false otherwise.
     */
    public boolean isEnabledFor(Priority level) {
        return logger.isEnabledFor(level);
    }

    /**
     * Checks if the logging level "INFO" is enabled for the logger.
     *
     * @return true if the "INFO" level is enabled; otherwise, false.
     */
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * Logs a localized message associated with the specified key and priority, along with the provided throwable.
     *
     * @param priority the logging level indicating the importance or severity of the message being logged.
     * @param key      the key used to retrieve the localized message from a resource bundle.
     * @param t        the throwable associated with the log, providing details about an exception or error.
     */
    public void l7dlog(Priority priority, String key, Throwable t) {
        logger.l7dlog(priority, key, t);
    }

    /**
     * Logs a localized message with the specified priority, resource bundle key, optional parameter array, and an optional throwable.
     *
     * @param priority the priority level of the log, representing the severity of the message.
     * @param key the resource bundle key for the localized message.
     * @param params an array of objects to be used as parameters in the localized message, can be null if no parameters are needed.
     * @param t an optional Throwable associated with the log message, can be null if no exception information is to be logged.
     */
    public void l7dlog(Priority priority, String key, Object[] params, Throwable t) {
        logger.l7dlog(priority, key, params, t);
    }

    /**
     * Logs a message along with a specific priority level and an associated Throwable instance.
     * This method provides a mechanism to log detailed information with the specified priority,
     * message, and an optional exception for better tracking and debugging.
     *
     * @param priority the priority level of the log message (e.g., DEBUG, INFO, WARN, ERROR).
     * @param message  the message object to log, which can be any object that provides information.
     * @param t        the Throwable instance to be logged, representing an exception or error, or null if no exception is to be logged.
     */
    public void log(Priority priority, Object message, Throwable t) {
        logger.log(priority, message, t);
    }

    /**
     * Logs a message at the specified priority level using the underlying logger.
     *
     * @param priority the priority level at which the message should be logged.
     * @param message  the message to be logged.
     */
    public void log(Priority priority, Object message) {
        logger.log(priority, message);
    }

    /**
     * Logs a message with a specific priority level and optional throwable information.
     *
     * @param callerFQCN the fully qualified class name of the class requesting the log operation.
     * @param level the priority level of the log message.
     * @param message the message object to log.
     * @param t an optional throwable to log, or null if no throwable is provided.
     */
    public void log(String callerFQCN, Priority level, Object message, Throwable t) {
        logger.log(callerFQCN, level, message, t);
    }

    /**
     * Removes all appenders from the underlying logger.
     *
     * This method detaches and clears all previously added appenders from the internal logger configuration.
     * Use this method when you need to reset or reconfigure the logger with a clean slate of appenders.
     */
    public void removeAllAppenders() {
        logger.removeAllAppenders();
    }

    /**
     * Removes the specified appender from the logger's list of appenders.
     *
     * @param appender the appender to remove from the logger.
     */
    public void removeAppender(Appender appender) {
        logger.removeAppender(appender);
    }

    /**
     * Removes an appender with the specified name from the attached appenders.
     *
     * @param name the name of the appender to be removed.
     */
    public void removeAppender(String name) {
        logger.removeAppender(name);
    }

    /**
     * Sets the additivity flag for the logger.
     *
     * @param additive a boolean value indicating whether logging events
     *                 generated by this logger should also be forwarded
     *                 to the parent logger.
     */
    public void setAdditivity(boolean additive) {
        logger.setAdditivity(additive);
    }

    /**
     * Sets the logging level for the logger.
     *
     * @param level the desired logging level to be set.
     */
    public void setLevel(Level level) {
        logger.setLevel(level);
    }

    /**
     * Sets the resource bundle for logging.
     *
     * @param bundle the ResourceBundle to be used for logging messages.
     */
    public void setResourceBundle(ResourceBundle bundle) {
        logger.setResourceBundle(bundle);
    }

    /**
     * Logs a warning-level message.
     *
     * @param message the message object to log.
     */
    public void warn(Object message) {
        logger.warn(message);
    }

    /**
     * Logs a warning message, along with a throwable cause if provided.
     *
     * @param message the warning message to log.
     * @param t       the throwable associated with the warning message, can be null.
     */
    public void warn(Object message, Throwable t) {
        logger.warn(message, t);
    }

    private final Logger logger;
}