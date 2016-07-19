package org.jboss.fuse.largefile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    public static String getNumberFromThreadName() {
        String response;
        String threadName = Thread.currentThread().getName();
        Pattern r = Pattern.compile("thread-\\d+");
        Matcher m = r.matcher(threadName);
        if (m.find()) {
            response = m.group().replace("thread-","");
        } else {
            response = "NOT_FOUND";
        }
        return response;
    }

    public static String getNumberFromCamelThreadName() {
        String response;
        String threadName = Thread.currentThread().getName();
        Pattern r = Pattern.compile("\\#\\d+");
        Matcher m = r.matcher(threadName);
        if (m.find()) {
            response = m.group().replace("#","");
        } else {
            response = "NOT_FOUND";
        }
        return response;
    }

}