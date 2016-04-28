package com.oloftus.fbarchiveprocessor;

import java.io.File;

public class Utils {

    public static String joinPathWithSeparator(String... components) {

        StringBuilder sb = new StringBuilder();

        for (String component : components) {
            sb.append(File.separator);
            sb.append(component);
        }

        return sb.toString();
    }
}
