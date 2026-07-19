package com.example.frontend.feature.arcore

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.example.frontend.R
import com.example.frontend.data.remote.NetworkResult
import com.example.frontend.feature.ar.data.ArShade
import com.example.frontend.feature.ar.ui.ArTryOnViewModel
import com.google.android.material.button.MaterialButton
import java.util.ArrayList
import java.util.Locale

class ArCoreTryOnActivity : AppCompatActivity(), AugmentedFaceListener {

    private lateinit var augmentedFaceFragment: AugmentedFaceFragment
    private lateinit var imageSlider: ImageSlider
    private lateinit var viewModel: ArTryOnViewModel
    
    private lateinit var tvVariantName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvFinishAndStock: TextView
    private lateinit var btnAddToCart: MaterialButton
    
    private var currentTexturePath = "models/lipstick.png"
    private var currentColor = floatArrayOf(0f, 0f, 0f, 0f)
    private var productChanged = false
    private var shadesList: List<ArShade> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arcore_tryon)

        augmentedFaceFragment = supportFragmentManager.findFragmentById(R.id.face_view) as AugmentedFaceFragment
        augmentedFaceFragment.setAugmentedFaceListener(this)

        imageSlider = findViewById(R.id.image_slider)
        tvVariantName = findViewById(R.id.tvVariantName)
        tvPrice = findViewById(R.id.tvPrice)
        tvFinishAndStock = findViewById(R.id.tvFinishAndStock)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        
        viewModel = ViewModelProvider(this).get(ArTryOnViewModel::class.java)

        btnAddToCart.setOnClickListener {
            btnAddToCart.isEnabled = false
            viewModel.addToCart()
        }

        setupViewModel()

        val productId = intent.getStringExtra("product_id")
        val variantId = intent.getStringExtra("variant_id")
        if (productId != null) {
            viewModel.loadArConfig(productId, variantId)
        }
    }

    private fun setupViewModel() {
        viewModel.shades.observe(this) { shades ->
            if (shades != null && shades.isNotEmpty()) {
                this.shadesList = shades
                val imageList = ArrayList<SlideModel>()
                
                for (shade in shades) {
                    val imgUrl = shade.thumbnailUrl ?: "https://cdn-icons-png.flaticon.com/512/1024/1024505.png"
                    imageList.add(SlideModel(imgUrl, shade.variantName ?: "Màu sắc"))
                }

                imageSlider.setImageList(imageList)
                
                imageSlider.setItemClickListener(object : ItemClickListener {
                    override fun onItemSelected(position: Int) {
                        val selectedShade = shades[position]
                        viewModel.selectShade(selectedShade)
                    }

                    override fun doubleClick(position: Int) {}
                })
            }
        }

        viewModel.selectedShade.observe(this) { shade ->
            if (shade != null) {
                applyShadeConfig(shade)
                
                // Update UI
                tvVariantName.text = shade.variantName
                if (shade.price != null) {
                    tvPrice.text = String.format(Locale.US, "%,dđ", shade.price).replace(",", ".")
                } else {
                    tvPrice.text = ""
                }
                
                val finishText = shade.finishType ?: "MATTE"
                val stockText = if (shade.inStock) "Còn hàng" else "Hết hàng"
                tvFinishAndStock.text = "$finishText • $stockText"
                
                btnAddToCart.isEnabled = shade.inStock
                btnAddToCart.text = if (shade.inStock) "Thêm vào giỏ hàng" else "Hết hàng"
            }
        }
        
        viewModel.arType.observe(this) { type ->
            if (type != null) {
                currentTexturePath = when (type.uppercase(Locale.US)) {
                    "LIPS" -> "models/lipstick.png"
                    "CHEEKS" -> "models/blush.png"
                    "EYES" -> "models/eyeShadow.png"
                    else -> "models/lipstick.png"
                }
                productChanged = true
            }
        }
        
        viewModel.addToCartResult.observe(this) { result ->
            val selected = viewModel.selectedShade.value
            btnAddToCart.isEnabled = selected != null && selected.inStock
            if (result != null) {
                if (result.status == NetworkResult.Status.SUCCESS) {
                    Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                } else if (result.status == NetworkResult.Status.ERROR) {
                    Toast.makeText(this, "Lỗi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun applyShadeConfig(shade: ArShade) {
        val colorInt = try {
            Color.parseColor(shade.shadeHex ?: "#000000")
        } catch (e: Exception) {
            Color.BLACK
        }
        val r = Color.red(colorInt) / 255f
        val g = Color.green(colorInt) / 255f
        val b = Color.blue(colorInt) / 255f
        val a = (shade.opacity ?: 0.6f).coerceIn(0f, 1f)

        currentColor = floatArrayOf(r, g, b, a)
        productChanged = true

        // Push color update to all currently tracked face nodes (runs on UI thread;
        // AugmentedFaceNode will apply on GL thread via pendingColor in onDraw)
        for (node in augmentedFaceFragment.faceNodeMap.values) {
            node.setTextureColor(currentColor)
        }
    }


    override fun onFaceAdded(face: AugmentedFaceNode) {
        face.setFaceMeshTexture(currentTexturePath)
        face.setTextureColor(currentColor)
    }

    override fun onFaceUpdate(face: AugmentedFaceNode) {
        if (productChanged) {
            productChanged = false
            face.setFaceMeshTexture(currentTexturePath)
            face.setTextureColor(currentColor)
        }
    }
}
