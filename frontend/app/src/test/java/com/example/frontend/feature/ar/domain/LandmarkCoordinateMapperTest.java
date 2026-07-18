package com.example.frontend.feature.ar.domain;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LandmarkCoordinateMapperTest {

    @Test
    public void testCoordinateMappingBackCamera() {
        LandmarkCoordinateMapper mapper = new LandmarkCoordinateMapper(1080, 1920, 1080, 1920, false);
        
        LandmarkPoint mapped = mapper.map(new LandmarkPoint(100f, 200f));
        assertEquals(100f, mapped.x, 0.01f);
        assertEquals(200f, mapped.y, 0.01f);
    }

    @Test
    public void testCoordinateMappingFrontCameraMirrored() {
        LandmarkCoordinateMapper mapper = new LandmarkCoordinateMapper(1080, 1920, 1080, 1920, true);
        
        LandmarkPoint mapped = mapper.map(new LandmarkPoint(100f, 200f));
        // Mirrored x = 1080 - 100 = 980
        assertEquals(980f, mapped.x, 0.01f);
        assertEquals(200f, mapped.y, 0.01f);
    }

    @Test
    public void testCoordinateMappingCenterCrop() {
        // Image is 100x100, View is 200x100 (wider).
        // It must scale up to fill width (scale=2.0) -> scaled image is 200x200
        // Y is offset by (100 - 200) / 2 = -50
        LandmarkCoordinateMapper mapper = new LandmarkCoordinateMapper(100, 100, 200, 100, false);
        
        LandmarkPoint input = new LandmarkPoint(50f, 50f);
        LandmarkPoint result = mapper.map(input);
        
        assertEquals(100f, result.x, 0.01f);
        assertEquals(50f, result.y, 0.01f);
    }
}
