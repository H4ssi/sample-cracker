package pos1_2ahif.sample_cracker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) throws NumberFormatException,
            IOException {

        // try cracking the safe
        int result = test(Arrays.asList(0001, 0002, 0003));

        // test() returns:
        // -1 ... when cracked!
        // 0 .... error after first pin
        // 1 .... error after second pin
        // 2 .... error after third pin

        System.out.println("result: " + result);

        // now write your crack logic!

    }

    private static final ByteBuffer bb = ByteBuffer.allocate(1024);
    private static final String[] args = new String[] {};
    private static final List<Integer> bytes = new ArrayList<Integer>();
    private static final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private static int test(List<Integer> code) throws NumberFormatException,
            IOException {
        PrintStream oout = System.out;
        InputStream oin = System.in;
        try {
            bb.clear();
            bytes.clear();
            try (PrintWriter pw = new PrintWriter(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    bb.put((byte) b);
                }

                @Override
                public void write(byte[] b) throws IOException {
                    bb.put(b);
                }

                @Override
                public void write(byte[] b, int off, int len)
                        throws IOException {
                    bb.put(b, off, len);
                }
            }, true)) {
                for (Integer i : code) {
                    pw.printf("%05d%n", i);
                    bytes.add(bb.position());
                }
            }

            bb.flip();

            System.setIn(new InputStream() {
                private int cur = 0;

                @Override
                public int read() throws IOException {
                    return bb.get();
                }

                @Override
                public int read(byte[] b) throws IOException {
                    return read(b, 0, b.length);
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    if (cur == bytes.size()) {
                        return -1;
                    }

                    int limit = len;
                    if (limit > bytes.get(cur) - bb.position()) {
                        limit = bytes.get(cur) - bb.position();
                        cur++;
                    }

                    for (int i = off; i < off + limit; ++i) {
                        b[i] = bb.get();
                    }

                    return limit;
                }

            });
            baos.reset();
            try (PrintStream ps = new PrintStream(baos, true)) {
                System.setOut(ps);
                pos1_2ahif.sample_safe.Main.main(args);
            }

            if (baos.toString().startsWith("correct!")) {
                return -1;
            }

            return Collections.binarySearch(bytes, bb.position());
        } finally {
            System.setOut(oout);
            System.setIn(oin);
        }
    }
}
