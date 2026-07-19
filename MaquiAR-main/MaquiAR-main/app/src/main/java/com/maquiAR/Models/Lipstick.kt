package com.maquiAR.Models

import com.maquiAR.R
import java.util.ArrayList

class Lipstick(
    id: Int,
    name: String,
    description: String,
    images: ArrayList<String>,
    ecommerceLink: String,
    color: FloatArray,
    colorName: String
) : Product(id, name, description, images, ecommerceLink, "models/lipstick.png", R.drawable.icon_lipstick, color, colorName)