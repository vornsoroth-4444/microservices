package org.apache.kafka.common.utils;

public class Sanitizer {
    public static String sanitize(String name) {
        return org.apache.kafka.common.utils.internals.Sanitizer.sanitize(name);
    }
    public static String desanitize(String name) {
        return org.apache.kafka.common.utils.internals.Sanitizer.desanitize(name);
    }
    public static String jmxSanitize(String name) {
        return org.apache.kafka.common.utils.internals.Sanitizer.jmxSanitize(name);
    }
}
