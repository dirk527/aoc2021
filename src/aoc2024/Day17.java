package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day17 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("17-in"));
        long[] registers = new long[3];
        for (int i = 0; i < 3; i++) {
            registers[i] = Integer.parseInt(br.readLine().split(": ")[1]);
        }
        br.readLine();
        String[] pStrings = (br.readLine().split(": ")[1]).split(",");
        int[] program = new int[pStrings.length];
        System.out.print("Program: ");
        for (int i = 0; i < pStrings.length; i++) {
            program[i] = Integer.parseInt(pStrings[i]);
            System.out.print(program[i] + ", ");
        }
        System.out.printf("a=%d %s%n%n", registers[0], Long.toBinaryString(registers[0]));

        List<Integer> out = simulate(registers, program, false);
        System.out.println("p1:" + String.join(",", out.stream().map(String::valueOf).toList()));
        System.out.println();

        // For Part 2, I disassembled and analyzed my input program. It turns out that the first output
        // depends only on the rightmost 10 bits of the initial value of register a. So first, look for all
        // 10-bit numbers that output program[0].
        List<Long> possibilites = new ArrayList<>();
        for (long aCandidate = 0; aCandidate < 1 << 10; aCandidate++) {
            registers[0] = aCandidate;
            registers[1] = 0;
            registers[2] = 0;
            out = simulate(registers, program, false);
            if (out.get(0) == program[0]) {
                possibilites.add(aCandidate);
                String bin = Long.toBinaryString(aCandidate);
                bin = "0".repeat(10 - bin.length()) + bin;
                String result = String.join(",", out.stream().map(String::valueOf).toList());
                System.out.printf("%10d %s: %s%n", aCandidate, bin, result);
            }
        }

        // Now, iteratively find longer possible numbers that produce the first offset+1 numbers of program.
        // We always have the possible numbers (that produce the first offset numbers of program) in
        // possibilities. The next possibilities are then dependent on the next 3 bits on the left. So try all
        // possible firstThree with all current possibilities, simulate and see which ones are now possible.
        for (int offset = 1; offset < program.length; offset++) {
            System.out.printf("*** looking for offset %d%n", offset);
            List<Long> newPoss = new ArrayList<>();
            for (long initial : possibilites) {
                for (long firstThree = 0; firstThree < 8; firstThree++) {
                    int leftShift = 10 + 3 * (offset - 1);
                    long aCandidate = initial + (firstThree << leftShift);

                    /*
                    String b1 = Long.toBinaryString(initial);
                    b1 = "0".repeat(10 - b1.length()) + b1;
                    String b2 = Long.toBinaryString(firstThree);
                    b2 = "0".repeat(3 - b2.length()) + b2;
                    String b3 = Long.toBinaryString(aCandidate);
                    b3 = "0".repeat(13 - b3.length()) + b3;
                    System.out.printf("%s %s %s%n", b1, b2, b3);
                     */

                    registers[0] = aCandidate;
                    registers[1] = 0;
                    registers[2] = 0;
                    out = simulate(registers, program, false);
                    boolean isPossible;
                    // for the last iteration only, check that the output is not longer than program; for the
                    // previous iterations, longer output may be modified by later iterations, so they remain
                    // possible.
                    if (offset < program.length - 1) {
                        isPossible = out.size() > offset && out.get(offset) == program[offset];
                    } else {
                        isPossible = out.size() == program.length && out.get(offset) == program[offset];
                    }
                    if (isPossible) {
                        newPoss.add(aCandidate);
//                        String bin = Long.toBinaryString(aCandidate);
//                        bin = "0".repeat(leftShift + 3 - bin.length()) + bin;
//                        String result = String.join(",", out.stream().map(String::valueOf).toList());
//                        System.out.printf("%20d %s: %s%n", aCandidate, bin, result);
                    }
                }
            }
            possibilites = newPoss;
            System.out.printf("found %d possibilites%n", possibilites.size());
        }

        Collections.sort(possibilites);
        System.out.println("p2: " + possibilites.getFirst());
    }

    private static List<Integer> simulate(long[] registers, int[] program, boolean print) {
        List<Integer> out = new ArrayList<>();
        int pc = 0;
        while (pc < program.length) {
            int opcode = program[pc];
            int operand = program[pc + 1];
            if (print) {
                System.out.printf("%na=%-8d b=%-8d c=%-8d pc=%2d opcode=%d operand=%d", registers[0], registers[1], registers[2], pc, opcode, operand);
            }
            switch (opcode) {
                case 1:
                    /* The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's literal operand, then stores the result in register B. */
                    registers[1] = registers[1] ^ operand;
                    break;
                case 2:
                    /* The bst instruction (opcode 2) calculates the value of its combo operand modulo 8 (thereby keeping only its lowest 3 bits), then writes that value to the B register. */
                    registers[1] = combo(operand, registers) % 8;
                    break;
                case 3:
                    /* The jnz instruction (opcode 3) does nothing if the A register is 0. However, if the A register is not zero, it jumps by setting the instruction pointer to the value of its literal operand; if this instruction jumps, the instruction pointer is not increased by 2 after this instruction. */
                    if (registers[0] != 0) {
                        pc = operand;
                        continue;
                    }
                case 4:
                    /* The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C, then stores the result in register B. (For legacy reasons, this instruction reads an operand but ignores it.) */
                    registers[1] = registers[1] ^ registers[2];
                    break;
                case 5:
                    /* The out instruction (opcode 5) calculates the value of its combo operand modulo 8, then outputs that value. (If a program outputs multiple values, they are separated by commas.) */
                    out.add((int) (combo(operand, registers) % 8));
                    if (print) {
                        System.out.print(" *** " + out.getLast());
                    }
                    break;
                case 0:
                case 6:
                case 7:
                    /*
                    The adv instruction (opcode 0) performs division. The numerator is the value in the A register. The denominator is found by raising 2 to the power of the instruction's combo operand. (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.) The result of the division operation is truncated to an integer and then written to the A register.
                    The bdv instruction (opcode 6) works exactly like the adv instruction except that the result is stored in the B register. (The numerator is still read from the A register.)
                    The cdv instruction (opcode 7) works exactly like the adv instruction except that the result is stored in the C register. (The numerator is still read from the A register.)
                    */
                    int reg = opcode == 0 ? 0 : opcode - 5;
                    int denominator = 1 << combo(operand, registers);
                    registers[reg] = registers[0] / denominator;
                    break;
                default:
                    throw new IllegalStateException("unknown opcode: " + opcode);
            }
            pc += 2;
        }
        return out;
    }

    private static long combo(int operand, long[] registers) {
        /*
        Combo operands 0 through 3 represent literal values 0 through 3.
        Combo operand 4 represents the value of register A.
        Combo operand 5 represents the value of register B.
        Combo operand 6 represents the value of register C.
        Combo operand 7 is reserved and will not appear in valid programs.
         */
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4, 5, 6 -> registers[operand - 4];
            default -> throw new IllegalArgumentException("unknown " + operand);
        };
    }

}

/*
Pseudocode for my input:

b = a % 8       // b is the last 3 bits from a
b = b xor NNN   // b is changed, all values from 000 to 111 are possible. NNN depends on your input.
c = a / 2**b    // c is a part of a, starting from 0 to 7 bits from the right. Only the rightmost 3 bits are relevant due to the output definition
b = b xor MMM   // b is changed, all values from 000 to 111 are possible. MMM depends on your input.
a = a / 2**3    // a is shifted right by 3 bits
b = b xor c     // b now depends on c as well, and c is a part of a up to bit 10 from the right
output b % 8    // output last 3 bits of b
jnz 0           // stop if a ==0, otherwise start at beginning
 */