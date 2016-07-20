package org.jboss.fuse.largefile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateCsvFile {

    private static final int ITERATIONS = 10;
    private static final double MEG = (Math.pow(1024, 2));
    private static final int RECORD_COUNT = 50000;
    private static Random r = new Random();
    private static final String domain = "@acme.com";
    private static String targetDir = "target/data";

    private static String[] FirstName = { "Charles", "James", "Claus", "Jeff", "Rachel", "Sylvie", "Pauline",
            "Marc", "Eric", "John", "Keith", "Ken", "Rob", "Bruno", "Isabelle","Eleonor","Chad","Nandan","Satya" };
    private static String[] LastName = { "Moulliard", "Cassidy", "Yordan", "Bailly", "Baboo", "Johnson",
            "Britton", "Ibsen", "Davies", "Strachan", "Rawling", "Sativa", "Joshi", "Bride" };

    public static void main(String[] args) throws Exception {
        List<String> records = new ArrayList<String>(RECORD_COUNT);
        StringBuffer buffer;

        boolean isDirCreated = new File(targetDir).mkdirs();

        if (isDirCreated) {
            int size = 0;
            for (int i = 0; i < RECORD_COUNT; i++) {
                buffer = new StringBuffer();
                String name = generateName();
                buffer.append(i).append(",").append(name).append(",").append(getEmail(name)).append(",")
                        .append(getIpAddress()).append("\n");
                String record = buffer.toString();
                records.add(record);
                size += record.getBytes().length;
            }
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
            File file = File.createTempFile("foo", ".txt", new File(targetDir));
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
        return FirstName[r.nextInt(FirstName.length)] + "," + LastName[r.nextInt(LastName.length)];
    }

    public static String getEmail(String name) {
        String[] parts = name.split(",");
        StringBuffer buffer = new StringBuffer();
        return buffer.append(parts[0].toLowerCase()).append(".").append(parts[1].toLowerCase()).append(domain)
                .toString();
    }

    private static String getIpAddress() {
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
}