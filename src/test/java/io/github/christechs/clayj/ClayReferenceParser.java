/*
 * This is free and unencumbered software released into the public domain.
 *
 * See the LICENSE.md file for more information, or visit <https://unlicense.org/>
 */

package io.github.christechs.clayj;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class ClayReferenceParser {

    private ClayReferenceParser() {
    }

    public static List<TestCase> parse(InputStream in) throws IOException {
        List<String> lines = new String(in.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
        List<TestCase> tests = new ArrayList<>();
        int i = 0;

        while (i < lines.size()) {
            String l = lines.get(i).trim();
            if (l.startsWith("TEST: ") || l.equals("TEST_SUITE_END")) break;
            i++;
        }

        while (i < lines.size()) {
            String line = lines.get(i);
            if (line.equals("TEST_SUITE_END")) break;
            if (!line.startsWith("TEST: ")) {
                i++;
                continue;
            }

            ParseResult pr = parseTest(lines, i);
            tests.add(pr.test);
            i = pr.nextIndex;
        }
        return tests;
    }

    private static ParseResult parseTest(List<String> lines, int start) {
        String name = lines.get(start).substring(6).trim();
        int i = start + 1;

        float vw = 0, vh = 0;
        Map<String, String> extras = new LinkedHashMap<>();

        while (i < lines.size()) {
            String l = lines.get(i);
            if (l.equals("SPEC")) {
                i++;
                break;
            }
            int eq = l.indexOf('=');
            if (eq > 0) {
                String k = l.substring(0, eq);
                String v = l.substring(eq + 1);
                if (k.equals("viewport")) {
                    String[] p = v.split(",");
                    vw = Float.parseFloat(p[0]);
                    vh = Float.parseFloat(p[1]);
                } else {
                    extras.put(k, v);
                }
            }
            i++;
        }

        List<ElementSpec> roots = new ArrayList<>();
        Deque<ElementSpec> stack = new ArrayDeque<>();
        while (i < lines.size() && !lines.get(i).equals("OUTPUT")) {
            String l = lines.get(i);
            i++;
            if (l.isBlank()) continue;

            int depth = 0;
            while (depth < l.length() && l.charAt(depth) == ' ') depth++;
            depth /= 2;

            ElementSpec el = parseElementLine(l.substring(depth * 2));
            while (stack.size() > depth) stack.pop();
            if (stack.isEmpty()) roots.add(el);
            else stack.peek().children.add(el);
            stack.push(el);
        }
        i++;

        List<CmdSpec> cmds = new ArrayList<>();
        int expectedCount = -1;
        while (i < lines.size() && !lines.get(i).equals("END")) {
            String l = lines.get(i);
            i++;
            if (l.startsWith("CMD|")) cmds.add(parseCmdLine(l));
            else if (l.startsWith("count=")) expectedCount = Integer.parseInt(l.substring(6).trim());
            else {
                int eq = l.indexOf('=');
                if (eq > 0) extras.put(l.substring(0, eq), l.substring(eq + 1));
            }
        }
        i++;

        return new ParseResult(
                new TestCase(name, vw, vh, roots, expectedCount, cmds, extras), i);
    }

    private static ElementSpec parseElementLine(String line) {
        List<String> tokens = tokenize(line);
        String type = tokens.get(0);
        String textContent = null;
        int attrStart = 1;
        if (type.equals("TEXT") && tokens.size() > 1 && tokens.get(1).startsWith("\"")) {
            textContent = unquote(tokens.get(1));
            attrStart = 2;
        }
        Map<String, String> attrs = new LinkedHashMap<>();
        for (int j = attrStart; j < tokens.size(); j++) {
            String t = tokens.get(j);
            int eq = t.indexOf('=');
            if (eq < 0) continue;
            attrs.put(t.substring(0, eq), t.substring(eq + 1));
        }
        return new ElementSpec(type, textContent, attrs, new ArrayList<>());
    }

    private static CmdSpec parseCmdLine(String line) {
        String[] parts = line.split("\\|", -1);
        int index = Integer.parseInt(parts[1]);
        String type = parts[2];
        Map<String, String> fields = new LinkedHashMap<>();
        for (int j = 3; j < parts.length; j++) {
            int eq = parts[j].indexOf('=');
            if (eq < 0) continue;
            String v = parts[j].substring(eq + 1);
            if (parts[j].startsWith("text=") && v.length() >= 2 && v.charAt(0) == '"') {
                v = unescape(v.substring(1, v.length() - 1));
            }
            fields.put(parts[j].substring(0, eq), v);
        }
        return new CmdSpec(index, type, fields);
    }

    private static List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false, escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escaped) {
                sb.append(c);
                escaped = false;
            } else if (c == '\\' && inQuotes) {
                sb.append(c);
                escaped = true;
            } else if (c == '"') {
                sb.append(c);
                inQuotes = !inQuotes;
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (sb.length() > 0) {
                    tokens.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) tokens.add(sb.toString());
        return tokens;
    }

    private static String unquote(String s) {
        if (s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"')
            return unescape(s.substring(1, s.length() - 1));
        return s;
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                switch (n) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '\\' -> sb.append('\\');
                    case '"' -> sb.append('"');
                    case '|' -> sb.append('|');
                    default -> {
                        sb.append('\\');
                        sb.append(n);
                    }
                }
            } else sb.append(c);
        }
        return sb.toString();
    }

    public record TestCase(
            String name,
            float viewportW,
            float viewportH,
            List<ElementSpec> roots,
            int expectedCount,
            List<CmdSpec> expectedCommands,
            Map<String, String> extras
    ) {
        @Override
        public String toString() {
            return name;
        }
    }

    public record ElementSpec(
            String type,
            String textContent,
            Map<String, String> attrs,
            List<ElementSpec> children
    ) {
    }

    public record CmdSpec(int index, String type, Map<String, String> fields) {
    }

    private static final class ParseResult {
        final TestCase test;
        final int nextIndex;

        ParseResult(TestCase t, int n) {
            test = t;
            nextIndex = n;
        }
    }
}