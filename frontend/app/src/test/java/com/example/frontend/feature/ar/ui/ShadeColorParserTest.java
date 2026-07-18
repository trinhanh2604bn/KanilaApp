package com.example.frontend.feature.ar.ui;

import android.graphics.Color;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ShadeColorParserTest {

    private static final int FALLBACK = Color.RED;

    @Test
    public void parseOrFallback_validUppercaseHex_returnsColor() {
        int color = ShadeColorParser.parseOrFallback("#FF0000");
        assertEquals(Color.parseColor("#FF0000"), color);
    }

    @Test
    public void parseOrFallback_validLowercaseHex_returnsColor() {
        int color = ShadeColorParser.parseOrFallback("#00ff00");
        assertEquals(Color.parseColor("#00ff00"), color);
    }

    @Test
    public void parseOrFallback_missingHash_addsHashAndReturnsColor() {
        int color = ShadeColorParser.parseOrFallback("0000FF");
        assertEquals(Color.parseColor("#0000FF"), color);
    }

    @Test
    public void parseOrFallback_invalidHex_returnsFallback() {
        int color = ShadeColorParser.parseOrFallback("#ZZZZZZ");
        assertEquals(FALLBACK, color);
    }

    @Test
    public void parseOrFallback_null_returnsFallback() {
        int color = ShadeColorParser.parseOrFallback(null);
        assertEquals(FALLBACK, color);
    }

    @Test
    public void parseOrFallback_emptyString_returnsFallback() {
        int color = ShadeColorParser.parseOrFallback("   ");
        assertEquals(FALLBACK, color);
    }
}
