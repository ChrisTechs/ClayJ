/*
 * This is free and unencumbered software released into the public domain.
 *
 * See the LICENSE.md file for more information, or visit <https://unlicense.org/>
 */

package io.github.christechs.clayj;

import io.github.christechs.clayj.config.*;
import io.github.christechs.clayj.core.ElementId;
import io.github.christechs.clayj.core.RenderCommand;
import io.github.christechs.clayj.enums.*;
import io.github.christechs.clayj.math.*;
import io.github.christechs.clayj.util.HashUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ClayReferenceTest {

    private static final float FLOAT_TOL = 0.01f;

    static Stream<ClayReferenceParser.TestCase> cases() throws IOException {
        Path p = Path.of("src/test/resources/clay_reference.txt");
        try (var in = Files.newInputStream(p)) {
            return ClayReferenceParser.parse(in).stream();
        }
    }

    private static void buildElement(ClayReferenceParser.ElementSpec spec) {
        if (spec.type().equals("TEXT")) {
            ClayJ.text(spec.textContent(), buildTextConfig(spec.attrs()));
            return;
        }

        ElementDeclBuilder decl = ClayJ.decl();
        applyId(decl, spec.attrs());
        applyLayout(decl, spec.attrs());
        applyShared(decl, spec.attrs());
        applyBorder(decl, spec.attrs());
        applyClip(decl, spec.attrs());
        applyFloating(decl, spec.attrs());
        applyImage(decl, spec.attrs());
        applyCustom(decl, spec.attrs());
        applyAspectRatio(decl, spec.attrs());

        ClayJ.beginEl(decl);
        for (var child : spec.children()) buildElement(child);
        ClayJ.endEl();
    }

    private static void applyId(ElementDeclBuilder decl, Map<String, String> a) {
        if (a.containsKey("id")) {
            ElementId eid = new ElementId();
            HashUtil.hashString(a.get("id"), 0, eid);
            decl.id(eid);
        } else if (a.containsKey("idi")) {
            String[] parts = a.get("idi").split(",");
            String base = parts[0];
            int index = Integer.parseInt(parts[1]);
            ElementId eid = new ElementId();
            HashUtil.hashStringWithOffset(base, index, 0, eid);
            decl.id(eid);
        }
    }

    private static void applyLayout(ElementDeclBuilder decl, Map<String, String> a) {
        boolean hasLayout = a.containsKey("w") || a.containsKey("h")
                || a.containsKey("pad") || a.containsKey("gap")
                || a.containsKey("dir") || a.containsKey("ax") || a.containsKey("ay");
        if (!hasLayout) return;

        LayoutConfigBuilder layout = ClayJ.layout();

        if (a.containsKey("w")) applySizing(layout, true, a.get("w"));
        if (a.containsKey("h")) applySizing(layout, false, a.get("h"));

        if (a.containsKey("pad")) {
            float[] p = parseFloats(a.get("pad"));
            layout.padding((int) p[0], (int) p[1], (int) p[2], (int) p[3]);
        }
        if (a.containsKey("gap")) {
            layout.gap((int) Short.parseShort(a.get("gap")));
        }
        if (a.containsKey("dir")) {
            if (a.get("dir").equals("TB")) {
                layout.dirTopToBottom();
            } else {
                layout.dirLeftToRight();
            }
        }
        if (a.containsKey("ax")) {
            switch (a.get("ax")) {
                case "CENTER" -> layout.alignCenterX();
                case "RIGHT" -> layout.alignRight();
                default -> layout.alignLeft();
            }
        }
        if (a.containsKey("ay")) {
            switch (a.get("ay")) {
                case "CENTER" -> layout.alignCenterY();
                case "BOTTOM" -> layout.alignBottom();
                default -> layout.alignTop();
            }
        }

        decl.layout = layout;
    }

    private static void applySizing(LayoutConfigBuilder layout, boolean isWidth, String expr) {
        if (expr.startsWith("FIXED(")) {
            float v = Float.parseFloat(inner(expr));
            if (isWidth) layout.widthFixed(v);
            else layout.heightFixed(v);
        } else if (expr.startsWith("PERCENT(")) {
            float v = Float.parseFloat(inner(expr));
            if (isWidth) layout.widthPercent(v);
            else layout.heightPercent(v);
        } else if (expr.equals("GROW")) {
            if (isWidth) layout.widthGrow();
            else layout.heightGrow();
        } else if (expr.startsWith("GROW(")) {
            float[] v = parseFloats(inner(expr));
            float min = v.length > 0 ? v[0] : 0f;
            float max = v.length > 1 ? v[1] : 0f;
            SizingAxis axis = new SizingAxis(SizingType.GROW, min, max);
            if (isWidth) layout.sizing(axis, layout.sizing.height());
            else layout.sizing(layout.sizing.width(), axis);
        } else if (expr.equals("FIT")) {
            if (isWidth) layout.widthFit();
            else layout.heightFit();
        } else if (expr.startsWith("FIT(")) {
            float[] v = parseFloats(inner(expr));
            float min = v.length > 0 ? v[0] : 0f;
            float max = v.length > 1 ? v[1] : 0f;
            SizingAxis axis = new SizingAxis(SizingType.FIT, min, max);
            if (isWidth) layout.sizing(axis, layout.sizing.height());
            else layout.sizing(layout.sizing.width(), axis);
        } else {
            throw new IllegalArgumentException("Unknown sizing: " + expr);
        }
    }

    private static void applyShared(ElementDeclBuilder decl, Map<String, String> a) {
        if (a.containsKey("bg")) {
            decl.bg(parseColor(a.get("bg")));
        }
        if (a.containsKey("cr")) {
            decl.radius(parseCornerRadius(a.get("cr")));
        }
        if (a.containsKey("overlay")) {
            decl.overlay(parseColor(a.get("overlay")));
        }
        if (a.containsKey("ud")) {
            decl.userData = new Object();
        }
    }

    private static void applyBorder(ElementDeclBuilder decl, Map<String, String> a) {
        if (!a.containsKey("bw") && !a.containsKey("bcolor")) return;

        Color color = a.containsKey("bcolor") ? parseColor(a.get("bcolor")) : new Color(0f, 0f, 0f, 0f);
        if (a.containsKey("bw")) {
            float[] w = parseFloats(a.get("bw"));
            int between = w.length > 4 ? (int) w[4] : 0;
            decl.border(color, (int) w[0], (int) w[1], (int) w[2], (int) w[3], between);
        } else {
            decl.border(color, 0);
        }
    }

    private static void applyFloating(ElementDeclBuilder decl, Map<String, String> a) {
        if (!a.containsKey("fTo")) return;

        AttachToElement attachTo = switch (a.get("fTo")) {
            case "PARENT" -> AttachToElement.PARENT;
            case "ROOT" -> AttachToElement.ROOT;
            case "ID" -> AttachToElement.ELEMENT_WITH_ID;
            default -> AttachToElement.NONE;
        };
        int parentId = a.containsKey("fPid") ? hashId(a.get("fPid")) : 0;

        FloatingAttachPoint attachElement = FloatingAttachPoint.LEFT_TOP;
        FloatingAttachPoint attachParent = FloatingAttachPoint.LEFT_TOP;
        if (a.containsKey("fAp")) {
            String[] pts = a.get("fAp").split(",");
            attachElement = parseAttachPoint(pts[0]);
            attachParent = parseAttachPoint(pts[1]);
        }

        Vector2 offset = new Vector2(0f, 0f);
        if (a.containsKey("fOff")) {
            float[] o = parseFloats(a.get("fOff"));
            offset = new Vector2(o[0], o[1]);
        }

        int zIndex = a.containsKey("fZ") ? Integer.parseInt(a.get("fZ")) : 0;
        decl.floating(attachTo, parentId, attachElement, attachParent, offset, zIndex);

        if ("PASSTHROUGH".equals(a.get("fCap"))) {
            decl.floating.captureMode(PointerCaptureMode.PASSTHROUGH);
        }
    }

    private static void applyClip(ElementDeclBuilder decl, Map<String, String> a) {
        boolean h = "1".equals(a.get("clipH"));
        boolean v = "1".equals(a.get("clipV"));
        if (!h && !v) return;

        ScrollConfigBuilder sc = new ScrollConfigBuilder();
        sc.horizontal(h);
        sc.vertical(v);
        decl.scroll(sc);
    }

    private static FloatingAttachPoint parseAttachPoint(String code) {
        return switch (code) {
            case "LT" -> FloatingAttachPoint.LEFT_TOP;
            case "LC" -> FloatingAttachPoint.LEFT_CENTER;
            case "LB" -> FloatingAttachPoint.LEFT_BOTTOM;
            case "CT" -> FloatingAttachPoint.CENTER_TOP;
            case "CC" -> FloatingAttachPoint.CENTER_CENTER;
            case "CB" -> FloatingAttachPoint.CENTER_BOTTOM;
            case "RT" -> FloatingAttachPoint.RIGHT_TOP;
            case "RC" -> FloatingAttachPoint.RIGHT_CENTER;
            case "RB" -> FloatingAttachPoint.RIGHT_BOTTOM;
            default -> FloatingAttachPoint.LEFT_TOP;
        };
    }

    private static void applyImage(ElementDeclBuilder decl, Map<String, String> a) {
        if (!a.containsKey("img")) return;
        ImageConfigBuilder img = new ImageConfigBuilder();
        img.data(new Object());
        decl.image(img);
    }

    private static void applyCustom(ElementDeclBuilder decl, Map<String, String> a) {
        if (!a.containsKey("custom")) return;
        CustomConfigBuilder c = new CustomConfigBuilder();
        c.customData(new Object());
        decl.custom(c);
    }

    private static void applyAspectRatio(ElementDeclBuilder decl, Map<String, String> a) {
        if (!a.containsKey("ar")) return;
        decl.aspectRatio(Float.parseFloat(a.get("ar")));
    }

    private static TextConfigBuilder buildTextConfig(Map<String, String> a) {
        TextConfigBuilder t = ClayJ.txt();
        if (a.containsKey("textColor")) t.color(parseColor(a.get("textColor")));
        if (a.containsKey("fontId")) t.fontId((short) Short.parseShort(a.get("fontId")));
        if (a.containsKey("fontSize")) t.size((short) Short.parseShort(a.get("fontSize")));
        if (a.containsKey("letterSpacing")) t.letterSpacing((short) Short.parseShort(a.get("letterSpacing")));
        if (a.containsKey("lineHeight")) t.lineHeight((short) Short.parseShort(a.get("lineHeight")));
        if (a.containsKey("wrap")) {
            t.wrap(switch (a.get("wrap")) {
                case "NEWLINES" -> TextWrapMode.NEWLINES;
                case "NONE" -> TextWrapMode.NONE;
                default -> TextWrapMode.WORDS;
            });
        }
        if (a.containsKey("textAlign")) {
            t.align(switch (a.get("textAlign")) {
                case "CENTER" -> TextAlignment.CENTER;
                case "RIGHT" -> TextAlignment.RIGHT;
                default -> TextAlignment.LEFT;
            });
        }
        return t;
    }

    private static void assertCommand(RenderCommand actual,
                                      ClayReferenceParser.CmdSpec expected,
                                      String testName, int index) {
        String where = testName + "[" + index + "]";

        assertThat(actual.commandType.name())
                .as("%s .type", where)
                .isEqualTo(expected.type());

        String bbox = expected.fields().get("bbox");
        if (bbox != null) {
            float[] v = parseFloats(bbox);
            assertThat(actual.boundingBox.x()).as("%s .bbox.x", where).isCloseTo(v[0], within(FLOAT_TOL));
            assertThat(actual.boundingBox.y()).as("%s .bbox.y", where).isCloseTo(v[1], within(FLOAT_TOL));
            assertThat(actual.boundingBox.width()).as("%s .bbox.width", where).isCloseTo(v[2], within(FLOAT_TOL));
            assertThat(actual.boundingBox.height()).as("%s .bbox.height", where).isCloseTo(v[3], within(FLOAT_TOL));
        }

        String idStr = expected.fields().get("id");
        if (idStr != null) {
            long expectedId = Long.parseUnsignedLong(idStr);
            assertThat(Integer.toUnsignedLong(actual.id))
                    .as("%s .id", where)
                    .isEqualTo(expectedId);
        }

        String zStr = expected.fields().get("z");
        if (zStr != null) {
            assertThat((int) actual.zIndex).as("%s .zIndex", where).isEqualTo(Integer.parseInt(zStr));
        }

        String udStr = expected.fields().get("userData");
        if (udStr != null) {
            assertThat(actual.userData != null).as("%s .userData present", where)
                    .isEqualTo(udStr.equals("yes"));
        }

        switch (actual.commandType) {
            case RECTANGLE -> {
                compareColor(actual.renderData.backgroundColor, expected.fields().get("bg"), where, "bg");
                compareCornerRadius(actual.renderData.cornerRadius, expected.fields().get("cr"), where);
            }
            case BORDER -> {
                compareColor(actual.renderData.borderColor, expected.fields().get("color"), where, "color");
                compareCornerRadius(actual.renderData.cornerRadius, expected.fields().get("cr"), where);
                String w = expected.fields().get("w");
                if (w != null) {
                    float[] ws = parseFloats(w);
                    assertThat((int) actual.renderData.borderWidth.left()).as("%s .border.width.left").isEqualTo((int) ws[0]);
                    assertThat((int) actual.renderData.borderWidth.right()).as("%s .border.width.right").isEqualTo((int) ws[1]);
                    assertThat((int) actual.renderData.borderWidth.top()).as("%s .border.width.top").isEqualTo((int) ws[2]);
                    assertThat((int) actual.renderData.borderWidth.bottom()).as("%s .border.width.bottom").isEqualTo((int) ws[3]);
                    assertThat((int) actual.renderData.borderWidth.betweenChildren()).as("%s .border.width.betweenChildren").isEqualTo((int) ws[4]);
                }
            }
            case TEXT -> {
                compareColor(actual.renderData.textColor, expected.fields().get("color"), where, "color");
                assertThat((int) actual.renderData.fontId).as("%s .fontId")
                        .isEqualTo(Integer.parseInt(expected.fields().getOrDefault("fontId", "0")));
                assertThat((int) actual.renderData.fontSize).as("%s .fontSize")
                        .isEqualTo(Integer.parseInt(expected.fields().getOrDefault("fontSize", "0")));
                assertThat((int) actual.renderData.letterSpacing).as("%s .letterSpacing")
                        .isEqualTo(Integer.parseInt(expected.fields().getOrDefault("letterSpacing", "0")));
                assertThat((int) actual.renderData.lineHeight).as("%s .lineHeight")
                        .isEqualTo(Integer.parseInt(expected.fields().getOrDefault("lineHeight", "0")));
                String expectedText = expected.fields().get("text");
                String actualText = String.valueOf(actual.renderData.text)
                        .substring(actual.renderData.textStart, actual.renderData.textStart + actual.renderData.textLength);
                assertThat(actualText).as("%s .text", where).isEqualTo(expectedText);
            }
            case IMAGE -> {
                compareColor(actual.renderData.backgroundColor, expected.fields().get("bg"), where, "bg");
                compareCornerRadius(actual.renderData.cornerRadius, expected.fields().get("cr"), where);
                assertThat(actual.renderData.imageData != null).as("%s .hasImage", where)
                        .isEqualTo("yes".equals(expected.fields().get("hasImage")));
            }
            case CUSTOM -> {
                compareColor(actual.renderData.backgroundColor, expected.fields().get("bg"), where, "bg");
                compareCornerRadius(actual.renderData.cornerRadius, expected.fields().get("cr"), where);
                assertThat(actual.renderData.customData != null).as("%s .hasCustomData", where)
                        .isEqualTo("yes".equals(expected.fields().get("hasCustomData")));
            }
            case OVERLAY_COLOR_START -> {
                compareColor(actual.renderData.backgroundColor, expected.fields().get("color"), where, "color");
            }
            default -> {
            }
        }
    }

    private static void compareColor(Color actual, String expected, String where, String field) {
        if (expected == null || actual == null) return;
        float[] v = parseFloats(expected);
        assertThat(actual.r()).as("%s .%s.r", where, field).isCloseTo(v[0], within(FLOAT_TOL));
        assertThat(actual.g()).as("%s .%s.g", where, field).isCloseTo(v[1], within(FLOAT_TOL));
        assertThat(actual.b()).as("%s .%s.b", where, field).isCloseTo(v[2], within(FLOAT_TOL));
        assertThat(actual.a()).as("%s .%s.a", where, field).isCloseTo(v[3], within(FLOAT_TOL));
    }

    private static void compareCornerRadius(CornerRadius actual, String expected, String where) {
        if (expected == null || actual == null) return;
        float[] v = parseFloats(expected);
        assertThat(actual.topLeft()).as("%s .cr.topLeft").isCloseTo(v[0], within(FLOAT_TOL));
        assertThat(actual.topRight()).as("%s .cr.topRight").isCloseTo(v[1], within(FLOAT_TOL));
        assertThat(actual.bottomLeft()).as("%s .cr.bottomLeft").isCloseTo(v[2], within(FLOAT_TOL));
        assertThat(actual.bottomRight()).as("%s .cr.bottomRight").isCloseTo(v[3], within(FLOAT_TOL));
    }

    private static float[] parseFloats(String s) {
        String[] parts = s.split(",");
        float[] out = new float[parts.length];
        for (int i = 0; i < parts.length; i++) out[i] = Float.parseFloat(parts[i].trim());
        return out;
    }

    private static String inner(String expr) {
        return expr.substring(expr.indexOf('(') + 1, expr.length() - 1);
    }

    private static Color parseColor(String s) {
        float[] v = parseFloats(s);
        float a = v.length > 3 ? v[3] : 255f;
        return new Color(v[0], v[1], v[2], a);
    }

    private static CornerRadius parseCornerRadius(String s) {
        float[] v = parseFloats(s);
        if (v.length == 1) {
            return new CornerRadius(v[0]);
        } else {
            return new CornerRadius(v[0], v[1], v[2], v[3]);
        }
    }

    private static int hashId(String name) {
        return HashUtil.hashString(name, 0);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void differentialTest(ClayReferenceParser.TestCase test) {
        ClayJContext context = ClayJContext.createFixed(8192);
        context.layoutDimensions = new Dimensions(test.viewportW(), test.viewportH());

        LayoutResults[] layoutResultsWrapper = new LayoutResults[1];

        ClayJ.runLayout(context, () -> {
            ClayJ.setMeasureTextFunction((text, start, len, config) ->
                    new Dimensions((float) len * config.fontSize, (float) config.fontSize)
            );

            ClayJ.setPointerState(new Vector2(-100000f, -100000f), false);
            ClayJ.updateScrollContainers(false, new Vector2(0f, 0f), 0f);

            ClayJ.beginLayout();
            for (var root : test.roots()) {
                buildElement(root);
            }
            layoutResultsWrapper[0] = ClayJ.endLayout();
        });

        LayoutResults results = layoutResultsWrapper[0];
        List<RenderCommand> actual = new ArrayList<>();
        for (var c : results) actual.add(c);

        assertThat(actual)
                .as("command count for test '%s'", test.name())
                .hasSize(test.expectedCommands().size());

        for (int i = 0; i < test.expectedCommands().size(); i++) {
            assertCommand(actual.get(i), test.expectedCommands().get(i), test.name(), i);
        }
    }
}