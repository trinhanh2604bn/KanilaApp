package com.maquiAR.Models

import com.maquiAR.R
import java.util.ArrayList

class EyeShadow(
    id: Int,
    name: String,
    description: String,
    images: ArrayList<String>,
    ecommerceLink: String,
    color: FloatArray,
    colorName: String
) : Product(id, name, description, images, ecommerceLink, "models/eyeShadow.png", R.drawable.icon_eye_makeup, color, colorName)