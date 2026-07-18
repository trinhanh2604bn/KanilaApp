package com.example.frontend.feature.ar.domain;

import android.graphics.Path;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LipPathBuilderTest {

    @Test
    public void testBuildLipPathWithValidPoints() {
        LipPathBuilder builder = new LipPathBuilder();
        List<LandmarkPoint> points = new ArrayList<>();
        for (int i = 0; i < 468; i++) {
            points.add(new LandmarkPoint(i, i));
        }
        
        Path mockPath = Mockito.mock(Path.class);
        builder.buildLipPath(mockPath, points);
        
        // Verify path building operations were called
        verify(mockPath, atLeastOnce()).moveTo(anyFloat(), anyFloat());
        verify(mockPath, atLeastOnce()).lineTo(anyFloat(), anyFloat());
        verify(mockPath, atLeastOnce()).close();
    }

    @Test
    public void testBuildLipPathWithInvalidPoints() {
        LipPathBuilder builder = new LipPathBuilder();
        List<LandmarkPoint> points = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            points.add(new LandmarkPoint(i, i));
        }
        
        Path mockPath = Mockito.mock(Path.class);
        builder.buildLipPath(mockPath, points);
        
        // If invalid, reset is not called
        verify(mockPath, never()).reset();
    }
}
