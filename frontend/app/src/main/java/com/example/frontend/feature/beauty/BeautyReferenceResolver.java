package com.example.frontend.feature.beauty;

import com.example.frontend.data.model.beauty.BeautyReferenceDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeautyReferenceResolver {
    private final Map<String, BeautyReferenceDto> referenceMap = new HashMap<>();

    public BeautyReferenceResolver(List<BeautyReferenceDto> references) {
        if (references != null) {
            for (BeautyReferenceDto ref : references) {
                referenceMap.put(ref.getReferenceCode(), ref);
            }
        }
    }

    public String resolveName(String code) {
        if (code == null) return null;
        BeautyReferenceDto ref = referenceMap.get(code);
        return ref != null ? ref.getDisplayNameVi() : code;
    }
    
    public BeautyReferenceDto getReference(String code) {
        return referenceMap.get(code);
    }
}
