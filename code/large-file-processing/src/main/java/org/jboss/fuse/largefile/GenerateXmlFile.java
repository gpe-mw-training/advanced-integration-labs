package org.jboss.fuse.largefile;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateXmlFile {

    private static final int ITERATIONS = 10;
    private static final double MEG = (Math.pow(1024, 2));
    private static final int RECORD_COUNT = 100000;
    private static Random r = new Random();
    private static final String domain = "@acme.com";
    private static String targetDir = "target/data";

    private static String[] FirstName = { "Charles", "James", "Claus", "Jeff", "Rachel", "Sylvie", "Pauline",
            "Marc", "Eric", "John", "Keith", "Ken", "Rob", "Bruno", "Isabelle","Eleonor","Chad","Nandan","Satya" };
    private static String[] LastName = { "Moulliard", "Cassidy", "Yordan", "Bailly", "Baboo", "Johnson",
            "Britton", "Ibsen", "Davies", "Strachan", "Rawling", "Sativa", "Joshi", "Bride" };

    public static void main(String[] args) throws Exception {
        List<String> records = new ArrayList<String>(RECORD_COUNT);
        StringBuffer b;

        boolean isDirCreated = new File(targetDir).mkdirs();

        if (isDirCreated) {
            int size = 0;
            records.add("<records xmlns=\"http:acme.com\">\n");
            for (int i = 0; i < RECORD_COUNT; i++) {
                b = new StringBuffer();
                String name = generateName();

                b.append("<record>");
                b.append("<id>" + i + "</id>");
                b.append(name);
                b.append("<email>" + getEmail(name) + "</email>");
                b.append("<ip>" + getIpAddress() + "</ip>");
                b.append("</record>\n");

                String record = b.toString();
                records.add(record);
                size += record.getBytes().length;
            }
            records.add("</records>");

            System.out.println(records.size() + " 'records'");
            System.out.println(size / MEG + " MB");

            for (int i = 0; i < ITERATIONS; i++) {
                System.out.println("\nIteration " + i);
                writeRaw(records);
            }
        }
    }

    private static void writeRaw(List<String> records) throws IOException {
        try {
            File file = File.createTempFile("foo", ".xml", new File(targetDir));
            FileWriter writer = new FileWriter(file);
            System.out.print("Writing raw... ");
            write(records, writer);
        } finally {
            // comment this out if you want to inspect the files afterward
            //file.delete();
        }
    }

    private static void writeBuffered(List<String> records, int bufSize) throws IOException {
        File file = File.createTempFile("foo", ".txt");
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);

            System.out.print("Writing buffered (buffer size: " + bufSize + ")... ");
            write(records, bufferedWriter);
        } finally {
            // comment this out if you want to inspect the files afterward
            file.delete();
        }
    }

    private static void write(List<String> records, Writer writer) throws IOException {
        long start = System.currentTimeMillis();
        for (String record : records) {
            writer.write(record);
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000f + " seconds");
    }

    public static String generateName() {
        return "<firstname>" + FirstName[r.nextInt(FirstName.length)] + "</firstname><lastname>" + LastName[r.nextInt(LastName.length)] + "</lastname>";
    }

    public static String getEmail(String name) throws Exception {
        StringBuffer buffer = new StringBuffer();
        String fn = "";
        String ln = "";

        Matcher fnMatcher = Pattern.compile("<firstname>(.+?)</firstname>").matcher(name);
        Matcher lnMatcher = Pattern.compile("<lastname>(.+?)</lastname>").matcher(name);

        while(fnMatcher.find() && lnMatcher.find()) {
            fn = fnMatcher.group(1);
            ln = lnMatcher.group(1);
        }

        return buffer.append(fn.toLowerCase())
                .append(".")
                .append(ln.toLowerCase())
                .append(domain)
                .toString();
    }

    private static String getIpAddress() {
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
}