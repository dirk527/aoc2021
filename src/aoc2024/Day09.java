package aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Day09 {
    public static void main(String[] args) throws IOException {
        String s = new BufferedReader(new FileReader("09-in")).readLine();

        Disk disk = new Disk(s);
        disk.compact();
        System.out.println(disk.checksum());

        disk = new Disk(s);
        disk.noFragCompact();
        System.out.println(disk.checksum());
    }

    static class Part {
        int id;
        int length;
        Part prev;
        Part next;

        public Part(int id, int length, Part prev, Part next) {
            this.id = id;
            this.length = length;
            this.prev = prev;
            this.next = next;
        }

        public long checksum(int n) {
            long ret = 0;
            for (int i = 0; i < length; i++) {
                long tmp = (long) (n + i) * id;
                ret += tmp;
            }
            return ret;
        }
    }

    static class Disk {
        Part first;
        Part last;

        public Disk(String repr) {
            boolean file = true;
            for (int i = 0; i < repr.length(); i++) {
                int len = repr.charAt(i) - '0';
                if (len > 0) {
                    if (file) {
                        addFile(i / 2, len);
                    } else {
                        addFree(len);
                    }
                }
                file = !file;
            }
        }

        private void addFile(int id, int len) {
            Part p;
            if (first == null) {
                p = new Part(id, len, null, null);
                first = p;
            } else {
                p = new Part(id, len, last, null);
                last.next = p;
            }
            last = p;
        }

        private void addFree(int len) {
            if (last.id == -1) {
                last.length += len;
            } else {
                Part p = new Part(-1, len, last, null);
                last.next = p;
                last = p;
            }
        }

        public void println() {
            Part p = first;
            Part prev = null;
            while (p != null) {
                if (p.prev != prev) {
                    System.out.println("*** error");
                    System.exit(1);
                }
                for (int i = 0; i < p.length; i++) {
                    if (p.id == -1) {
                        System.out.print('.');
                    } else {
                        System.out.print(p.id);
                    }
                }
                prev = p;
                p = p.next;
            }
            System.out.println();
        }

        public void compact() {
            Part firstFree = first;
            while (firstFree.id != -1) {
                firstFree = firstFree.next;
            }
            Part lastFile = last;
            while (lastFile.id == -1) {
                lastFile = lastFile.prev;
            }

            while (firstFree != null && lastFile != null) {
                if (lastFile.length >= firstFree.length) {
                    // replace first free
                    firstFree.id = lastFile.id;
                    lastFile.length -= firstFree.length;
                    do {
                        firstFree = firstFree.next;
                    } while (firstFree != null && firstFree.id != -1);
                    if (lastFile.length == 0) {
                        // moved it completely
                        do {
                            lastFile = lastFile.prev;
                        } while (lastFile.length == 0 || lastFile.id == -1);
                        lastFile.next = null;
                    }
                } else {
                    // move lastFile
                    Part moving = lastFile;

                    do {
                        lastFile = lastFile.prev;
                    } while ((lastFile.length == 0 || lastFile.id == -1) && lastFile != firstFree);
                    lastFile.next = null;

                    moving.prev = firstFree.prev;
                    moving.next = firstFree;
                    firstFree.prev.next = moving;
                    firstFree.prev = moving;
                    firstFree.length -= moving.length;
                }
            }
        }

        public void noFragCompact() {
            List<Part> filesToTry = new LinkedList<>();
            for (Part part = last; part != null; part = part.prev) {
                if (part.id != -1) {
                    filesToTry.add(part);
                }
            }
            while (!filesToTry.isEmpty()) {
                Part file = filesToTry.remove(0);
                Part bigEnoughFree = null;
                for (Part part = first; part != null && part != file; part = part.next) {
                    if (part.id == -1 && part.length >= file.length) {
                        bigEnoughFree = part;
                        break;
                    }
                }
                if (bigEnoughFree != null) {
                    int id = file.id;
                    int len = file.length;

                    file.id = -1;
                    unifyIfFree(file);
                    unifyIfFree(file.prev);

                    if (bigEnoughFree.length == len) {
                        bigEnoughFree.id = id;
                    } else {
                        bigEnoughFree.length -= len;
                        Part ins = new Part(id, len, bigEnoughFree.prev, bigEnoughFree);
                        bigEnoughFree.prev.next = ins;
                        bigEnoughFree.prev = ins;
                    }
                }
            }
        }

        private void unifyIfFree(Part part) {
            Part next = part.next;
            if (next == null) {
                return;
            }
            if (part.id == -1 && next.id == -1) {
                part.length += next.length;
                part.next = next.next;
                if (part.next != null) {
                    part.next.prev = part;
                }
            }
        }

        public long checksum() {
            long checksum = 0L;
            int n = 0;
            for (Part part = first; part != null; part = part.next) {
                if (part.id != -1) {
                    checksum += part.checksum(n);
                }
                n += part.length;
            }
            return checksum;
        }
    }
}