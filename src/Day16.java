import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day16 {
    private static final String EX_LITERAL = "D2FE28";
    private static final String EX_OPERATOR = "38006F45291200";
    private static final String EX_OPERATOR2 = "EE00D40C823060";
    private static final String EXAMPLE1 = "8A004A801A8002F478";
    private static final String EXAMPLE2 = "620080001611562C8802118E34";
    private static final String EXAMPLE3 = "C0015000016115A2E0802F182340";
    private static final String EXAMPLE4 = "A0016C880162017C3686B18A3D4780";
    private static final String ACTUAL =
            "805311100469800804A3E488ACC0B10055D8009548874F65665AD42F60073E7338E7E5C538D820114AEA1A19927797976F8F43CD7354D66747B3005B401397C6CBA2FCEEE7AACDECC017938B3F802E000854488F70FC401F8BD09E199005B3600BCBFEEE12FFBB84FC8466B515E92B79B1003C797AEBAF53917E99FF2E953D0D284359CA0CB80193D12B3005B4017968D77EB224B46BBF591E7BEBD2FA00100622B4ED64773D0CF7816600B68020000874718E715C0010D8AF1E61CC946FB99FC2C20098275EBC0109FA14CAEDC20EB8033389531AAB14C72162492DE33AE0118012C05EEB801C0054F880102007A01192C040E100ED20035DA8018402BE20099A0020CB801AE0049801E800DD10021E4002DC7D30046C0160004323E42C8EA200DC5A87D06250C50015097FB2CFC93A101006F532EB600849634912799EF7BF609270D0802B59876F004246941091A5040402C9BD4DF654967BFDE4A6432769CED4EC3C4F04C000A895B8E98013246A6016CB3CCC94C9144A03CFAB9002033E7B24A24016DD802933AFAE48EAA3335A632013BC401D8850863A8803D1C61447A00042E3647B83F313674009E6533E158C3351F94C9902803D35C869865D564690103004E74CB001F39BEFFAAD37DFF558C012D005A5A9E851D25F76DD88A5F4BC600ACB6E1322B004E5FE1F2FF0E3005EC017969EB7AE4D1A53D07B918C0B1802F088B2C810326215CCBB6BC140C0149EE87780233E0D298C33B008C52763C9C94BF8DC886504E1ECD4E75C7E4EA00284180371362C44320043E2EC258F24008747785D10C001039F80644F201217401500043A2244B8D200085C3F8690BA78F08018394079A7A996D200806647A49E249C675C0802609D66B004658BA7F1562500366279CCBEB2600ACCA6D802C00085C658BD1DC401A8EB136100";

    public static void main(String[] args) throws IOException {
        String s = ACTUAL;
        BitReader br = new BitReader(s);

        int bitLen = s.length() * 4;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bitLen; i++) {
            sb.append(br.readChar());
        }
        System.out.println(sb);
        br.reset();

        Packet decoded = parse(br);
        System.out.println(decoded);
        System.out.println();
        System.out.println(decoded.versionSum());
        System.out.println(decoded.value());
    }

    private static Packet parse(BitReader br) {
        int version = br.readBits(3);
        int typeId = br.readBits(3);

        if (typeId == 4) {
            return new Literal(version, br);
        }
        return new Operator(version, typeId, br);
    }

    private static abstract class Packet {
        int version;

        public Packet(int version) {
            this.version = version;
        }

        public abstract int versionSum();

        public abstract long value();
    }

    private static class Literal extends Packet {
        long value;

        public Literal(int version, BitReader br) {
            super(version);
            int firstBit;
            do {
                firstBit = br.readBit();
                value = value << 4;
                value += br.readBits(4);
            } while (firstBit == 1);
        }

        @Override
        public int versionSum() {
            return version;
        }

        @Override
        public long value() {
            return value;
        }

        @Override
        public String toString() {
            return "Literal{" +
                    "\nversion=" + version +
                    ", value=" + value +
                    "\n}";
        }
    }

    private static class Operator extends Packet {
        int typeId;
        List<Packet> subPackets = new ArrayList<>();

        public Operator(int version, int typeId, BitReader br) {
            super(version);
            this.typeId = typeId;
            int lengthTypeId = br.readBit();
            if (lengthTypeId == 0) {
                int bitCount = br.readBits(15);
                int start = br.position;
                while (br.position < start + bitCount) {
                    subPackets.add(parse(br));
                }
            } else {
                int packetCount = br.readBits(11);
                for (int i = 0; i < packetCount; i++) {
                    subPackets.add(parse(br));
                }
            }
        }

        @Override
        public long value() {
            switch (typeId) {
                case 0:
                    long sum = 0;
                    for (Packet p : subPackets) {
                        sum += p.value();
                    }
                    return sum;
                case 1:
                    long prod = 1;
                    for (Packet p : subPackets) {
                        prod *= p.value();
                    }
                    return prod;
                case 2:
                    long min = Long.MAX_VALUE;
                    for (Packet p : subPackets) {
                        min = Math.min(min, p.value());
                    }
                    return min;
                case 3:
                    long max = Long.MIN_VALUE;
                    for (Packet p : subPackets) {
                        max = Math.max(max, p.value());
                    }
                    return max;
                case 5:
                    return subPackets.get(0).value() > subPackets.get(1).value() ? 1 : 0;
                case 6:
                    return subPackets.get(0).value() < subPackets.get(1).value() ? 1 : 0;
                case 7:
                    return subPackets.get(0).value() == subPackets.get(1).value() ? 1 : 0;
                default:
                    throw new IllegalStateException("unknown typeId " + typeId);
            }
        }

        @Override
        public int versionSum() {
            int sum = version;
            for (Packet packet : subPackets) {
                sum += packet.versionSum();
            }
            return sum;
        }

        @Override
        public String toString() {
            return "Operator{" +
                    "\nversion=" + version +
                    ", typeId=" + typeId +
                    "\n, subPackets=" + subPackets +
                    "\n}";
        }
    }

    private static class BitReader {
        private final String src;
        int position;

        public BitReader(String src) {
            this.src = src;
        }

        public char readChar() {
            return next() == 0 ? '0' : '1';
        }

        public int readBit() {
            return next() == 0 ? 0 : 1;
        }

        private int readBits(int count) {
            int ret = 0;
            while (count > 0) {
                ret = ret << 1;
                ret += readBit();
                count--;
            }
            return ret;
        }

        private int next() {
            int pos = position / 4;
            String one = src.substring(pos, pos + 1);
            int mask = 1 << (3 - (position % 4));
            int i = Integer.parseInt(one, 16);
            int res = i & mask;
            position++;
            return res;
        }

        public void reset() {
            position = 0;
        }
    }
}
