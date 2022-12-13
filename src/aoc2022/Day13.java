package aoc2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static aoc2022.Day13.Type.INT;
import static aoc2022.Day13.Type.LIST;

public class Day13 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("day13.txt"));
        String s;
        List<Data> allPackets = new ArrayList<>();
        int part1 = 0;
        int idx = 0;
        while ((s = br.readLine()) != null) {
            idx++;
            ListData one = new ListData(new StringCharacterIterator(s));
            ListData two = new ListData(new StringCharacterIterator(br.readLine()));
            int cmp = compare(one, two);
            if (cmp < 0) {
                part1 += idx;
            }
            allPackets.add(one);
            allPackets.add(two);
            br.readLine();
        }
        System.out.println("part 1: " + part1);

        ListData div1 = new ListData(new StringCharacterIterator("[[2]]"));
        ListData div2 = new ListData(new StringCharacterIterator("[[6]]"));
        allPackets.add(div1);
        allPackets.add(div2);
        allPackets.sort(Day13::compare);
        int decoderKey = (allPackets.indexOf(div1) + 1) * (allPackets.indexOf(div2) + 1);
        System.out.println("part 2: " + decoderKey);
    }

    public static int compare(Data one, Data two) {
        Type t1 = one.getType();
        Type t2 = two.getType();
        if (t1 == INT && t2 == INT) {
            return ((IntData) one).data - ((IntData) two).data;
        }
        if (t1 == LIST && t2 == LIST) {
            ListData l1 = (ListData) one;
            ListData l2 = (ListData) two;
            for (int i = 0; i < l1.elements.size(); i++) {
                if (i == l2.elements.size()) {
                    return 1;
                }
                int cmp = compare(l1.elements.get(i), l2.elements.get(i));
                if (cmp != 0) {
                    return cmp;
                }
            }
            if (l1.elements.size() == l2.elements.size()) {
                return 0;
            }
            return -1;
        }
        if (t1 == INT && t2 == LIST) {
            return compare(new ListData((IntData) one), two);
        }
        return compare(one, new ListData((IntData) two));
    }

    enum Type {LIST, INT}

    private static abstract class Data {
        abstract Type getType();
    }

    private static class ListData extends Day13.Data {
        List<Day13.Data> elements = new ArrayList<>();

        public ListData(StringCharacterIterator sci) {
            assert (sci.current() == '[');
            char c = sci.next();
            while ((c = sci.current()) != ']') {
                if ('0' <= c && c <= '9') {
                    elements.add(new IntData(sci));
                } else if (c == '[') {
                    elements.add(new ListData(sci));
                } else {
                    assert (c == ',');
                    c = sci.next();
                }
            }
            sci.next();
        }

        public ListData(IntData data) {
            elements.add(data);
        }

        @Override
        Type getType() {
            return LIST;
        }

        @Override
        public String toString() {
            return '[' + elements.stream().map(Object::toString).collect(Collectors.joining(",")) + ']';
        }
    }

    private static class IntData extends Day13.Data {
        int data;

        public IntData(StringCharacterIterator sci) {
            while (sci.current() >= '0' && sci.current() <= '9') {
                data = 10 * data + sci.current() - '0';
                sci.next();
            }
        }

        @Override
        Type getType() {
            return INT;
        }

        @Override
        public String toString() {
            return Integer.toString(data);
        }
    }

}
