/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent a configuration manager that handles reading, writing,
 * and manipulating configuration data through various types of input sources.
 */
@SuppressWarnings("SuspiciousMethodCalls")
public class Config {

    /**
     * Method to copy this Config, but setting sectionName.optionName to be value.
     * If <code>sectionName</code> does not already exist, it will be added to the result.
     *
     * @param sectionName the section name.
     * @param optionName  the option name.
     * @param value       the new value.
     * @return a new Config as described.
     */
    public Config copy(String sectionName, String optionName, String value) {
        Config result = new Config(copyIni());
        Profile.Section section = result.ini.get(sectionName);
        if (section == null) {
            result.ini.add(sectionName);
            section = result.ini.get(sectionName);
        }
        section.put(optionName, value);
        result.ini.replace(sectionName, section);
        return result;
    }

    /**
     * Retrieves the value of the specified option from the given section as a String.
     *
     * @param sectionName the name of the section from which to retrieve the option.
     * @param optionName  the name of the option to retrieve.
     * @return the value of the specified option as a String.
     */
    public String get(Object sectionName, Object optionName) {
        return get(sectionName, optionName, String.class);
    }

    /**
     * Retrieves the value of the specified option from the given section and converts it to the specified class type.
     *
     * @param <T>         the type of the value being returned.
     * @param sectionName the name of the section from which to retrieve the option.
     * @param optionName  the name of the option to retrieve.
     * @param clazz       the Class type to which the value should be converted.
     * @return the value of the specified option, converted to the specified class type.
     */
    public <T> T get(Object sectionName, Object optionName, Class<T> clazz) {
        final T t = ini.get(sectionName, optionName, clazz);
        if (unLogged(sectionName + "." + optionName))
            logger.debug(() -> "Config.get(" + sectionName + ", " + optionName + ") = " + t);
        return t;
    }

    /**
     * Retrieves the value of the specified option from the given section as a boolean.
     *
     * @param sectionName the name of the section from which to retrieve the option.
     * @param optionName  the name of the option to retrieve.
     * @return the value of the specified option as a boolean.
     */
    public boolean getBoolean(String sectionName, String optionName) {
        return get(sectionName, optionName, boolean.class);
    }

    /**
     * Retrieves the value of the specified option from the given section and converts it to an integer.
     * If the value is not found or cannot be retrieved, the specified default value is returned.
     *
     * @param sectionName  the name of the section from which to retrieve the option.
     * @param optionName   the name of the option to retrieve.
     * @param defaultValue the default value to return if the option is not found or cannot be retrieved.
     * @return the value of the specified option as an integer, or the default value if not found.
     */
    public int getInt(final String sectionName, final String optionName, final int defaultValue) {
        final String s = get(sectionName, optionName);
        if (s == null || s.isEmpty()) return defaultValue;
        return Integer.parseInt(s);
    }

    /**
     * Retrieves the value of the specified option from the given section and converts it to a long.
     * If the value is not found or cannot be retrieved, the specified default value is returned.
     *
     * @param sectionName   the name of the section from which to retrieve the option.
     * @param optionName    the name of the option to retrieve.
     * @param defaultValue  the default value to return if the option is not found or cannot be retrieved.
     * @return the value of the specified option as a long, or the default value if not found.
     */
    public long getLong(final String sectionName, final String optionName, final long defaultValue) {
        final String s = get(sectionName, optionName);
        if (s == null || s.isEmpty()) return defaultValue;
        return Long.parseLong(s);
    }

    /**
     * Retrieves the value of the specified option from the given section as a String.
     * If the value is null or empty, returns the provided default value.
     *
     * @param sectionName the name of the section from which to retrieve the option.
     * @param optionName  the name of the option to retrieve.
     * @param defaultValue the default value to return if the option is not found or is empty.
     * @return the value of the specified option as a String, or the default value if not found or empty.
     */
    public String getString(String sectionName, String optionName, String defaultValue) {
        final String s = get(sectionName, optionName);
        if (s == null || s.isEmpty()) return defaultValue;
        return s;
    }

    /**
     * Retrieves the comment associated with the specified key from the configuration.
     * If the key has not been logged, logs a debug message containing the key and its associated comment.
     *
     * @param key the key for which the comment is to be retrieved.
     * @return the comment associated with the specified key, or null if no comment exists.
     */
    public String getComment(String key) {
        final String comment = ini.getComment(key);
        if (unLogged(key))
            logger.debug(() -> "Config.getComment(" + key + ") = " + comment);
        return comment;
    }

    /**
     * Retrieves all sections associated with the specified key.
     *
     * @param key the key for which the associated sections are to be retrieved.
     * @return a collection of Profile.Section objects associated with the specified key.
     */
    public Collection<Profile.Section> getAll(Object key) {
        return ini.getAll(key);
    }

    /**
     * Retrieves the section associated with the specified key from the configuration.
     *
     * @param key the key for which the associated section is to be retrieved.
     * @return the Profile.Section object associated with the specified key, or null if no section exists for the key.
     */
    public Profile.Section get(Object key) {
        return ini.get(key);
    }

    /**
     * Retrieves the section associated with the specified key and index from the configuration.
     *
     * @param key   the key for which the associated section is to be retrieved.
     * @param index the index of the section to retrieve.
     * @return the Profile.Section object associated with the specified key and index, or null if no section exists for the key and index.
     */
    public Profile.Section get(Object key, int index) {
        return ini.get(key, index);
    }

    /**
     * Primary constructor: instantiates a Config object using the provided Ini configuration.
     *
     * @param ini the Ini object containing the configuration details.
     */
    public Config(Ini ini) {
        this.ini = ini;
    }

    /**
     * Secondary constructor: instantiates a Config instance by initializing it with
     * the provided Reader.
     *
     * @param reader the Reader object from which configuration data is read.
     * @throws IOException if an error occurs during reading from the Reader.
     */
    public Config(Reader reader) throws IOException {
        this(new Ini(reader));
    }

    /**
     * Secondary constructor: instantiates a Config instance by initializing it with
     * the provided InputStream.
     *
     * @param stream the InputStream object from which configuration data is read.
     * @throws IOException if an error occurs during reading from the InputStream.
     */
    public Config(InputStream stream) throws IOException {
        this(new Ini(stream));
    }

    /**
     * Secondary constructor: instantiates a Config instance by initializing it with
     * the configuration data provided by the specified URL resource.
     *
     * @param resource the URL from which the configuration data is read.
     * @throws IOException if an error occurs during reading from the URL resource.
     */
    public Config(URL resource) throws IOException {
        this(new Ini(resource));
    }

    /**
     * Secondary constructor: instantiates a Config object by reading the configuration data from the provided File.
     *
     * @param input the File object from which configuration data is read.
     * @throws IOException if an error occurs during reading from the file.
     */
    public Config(File input) throws IOException {
        this(new Ini(input));
    }

    /**
     * Secondary constructor: instantiates a Config object by reading the configuration data
     * from the specified file path.
     *
     * @param file the path of the file from which configuration data is read.
     * @throws IOException if an error occurs during reading from the file.
     */
    public Config(String file) throws IOException {
        this(new File(file));
    }

    /**
     * Method to load the appropriate configuration.
     * <p>
     * If clazz is not null, then we look for config.ini relative to the given class.
     * If clazz is null, or if the resource cannot be found relative to the class,
     * then we look in the root directory.
     *
     * @param clazz the Class in which to look for the config.ini file (may be null).
     * @return a new Config.
     * @throws IOException if config.ini cannot be found using the locations described above.
     */
    public static Config load(final Class<?> clazz) throws IOException {
        final String name = "config.ini";
        URL resource = null;
        if (clazz != null) resource = clazz.getResource(name);
        if (resource == null)
            resource = Config.class.getResource("/" + name);
        if (resource != null) return new Config(resource);
        throw new IOException("resource " + name + " not found for " + clazz);
    }

    /**
     * Loads the default configuration file.
     * <p>
     * This method is a shorthand for loading the configuration using the {@code load(Class<?> clazz)}
     * method with a {@code null} parameter. It looks for the {@code config.ini} file in the root
     * directory if it cannot be found relative to a class.
     *
     * @return the loaded Config instance.
     * @throws IOException if the {@code config.ini} file cannot be found in the expected locations.
     */
    public static Config load() throws IOException {
        return load(null);
    }

    /**
     * Checks if the given string has been logged, and if not, marks it as logged.
     *
     * @param s the string to be checked and potentially marked as logged.
     * @return true if the string was not previously logged, false otherwise.
     */
    private boolean unLogged(String s) {
        Boolean value = logged.get(s);
        if (value == null) {
            logged.put(s, true);
            return true;
        }
        return !value;
    }

    /**
     * Creates a deep copy of the current Ini configuration.
     * For each section and its associated key-value pairs in the original Ini,
     * the method duplicates them into a new Ini instance.
     *
     * @return a new Ini instance containing a deep copy of the sections and key-value pairs
     *         from the original Ini configuration.
     */
    private Ini copyIni() {
        Ini result = new Ini();
        // CONSIDER using result.putAll(this.result)...
        // XXX ... but only if Ini4J fixes the bug (unlikely since it hasn't been touched in more than 6 years!)
        for (Map.Entry<String, Profile.Section> entry : this.ini.entrySet())
            for (Map.Entry<String, String> x : entry.getValue().entrySet())
                result.put(entry.getKey(), x.getKey(), x.getValue());
        return result;
    }

    final static LazyLogger logger = new LazyLogger(Config.class);

    // NOTE this is static because, otherwise, we get too much logging when we copy a Config that hasn't had all enquiries made yet.
    private static final Map<String, Boolean> logged = new HashMap<>();

    private final Ini ini;
}