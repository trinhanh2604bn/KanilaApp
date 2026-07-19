package com.example.frontend.feature.arcore

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.R
import com.example.frontend.data.remote.NetworkResult
import com.example.frontend.feature.ar.data.ArShade
import com.example.frontend.feature.ar.ui.ArTryOnViewModel
import com.google.android.material.button.MaterialButton
import java.util.ArrayList
import java.util.Locale

class ArCoreTryOnActivity : AppCompatActivity(), AugmentedFaceListener {

    private lateinit var augmentedFaceFragment: AugmentedFaceFragment
    private lateinit var rvColors: RecyclerView
    private lateinit var colorAdapter: ArColorAdapter
    private lateinit var viewModel: ArTryOnViewModel
    
    private lateinit var tvVariantName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvFinishAndStock: TextView
    private lateinit var btnAddToCart: MaterialButton
    private lateinit var btnBuyNow: MaterialButton
    
    private var currentTexturePath = "models/lipstick.png"
    private var currentColor = floatArrayOf(0f, 0f, 0f, 0f)
    private var productChanged = false
    private var shadesList: List<ArShade> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()
        setContentView(R.layout.activity_arcore_tryon)

        augmentedFaceFragment = supportFragmentManager.findFragmentById(R.id.face_view) as AugmentedFaceFragment
        augmentedFaceFragment.setAugmentedFaceListener(this)

        rvColors = findViewById(R.id.rvColors)
        tvVariantName = findViewById(R.id.tvVariantName)
        tvPrice = findViewById(R.id.tvPrice)
        tvFinishAndStock = findViewById(R.id.tvFinishAndStock)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)
        
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }
        
        viewModel = ViewModelProvider(this).get(ArTryOnViewModel::class.java)

        // Setup RecyclerView
        colorAdapter = ArColorAdapter { selectedShade ->
            viewModel.selectShade(selectedShade)
        }
        rvColors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvColors.adapter = colorAdapter

        btnAddToCart.setOnClickListener {
            btnAddToCart.isEnabled = false
            viewModel.addToCart()
        }

        btnBuyNow.setOnClickListener {
            val shade = viewModel.selectedShade.value
            if (shade != null) {
                val cartItem = com.example.frontend.data.model.cart.CartItemDto.createMock(
                    "buy_now_" + System.currentTimeMillis(),
                    tvVariantName.text.toString(),
                    shade.variantName ?: "",
                    shade.price?.toDouble() ?: 0.0,
                    1,
                    true,
                    ""
                )
                cartItem.productId = intent.getStringExtra("product_id") ?: "mock_id"
                cartItem.variantId = shade.variantId

                val selectedItems = java.util.ArrayList<com.example.frontend.data.model.cart.CartItemDto>()
                selectedItems.add(cartItem)

                val mainIntent = android.content.Intent(this, com.example.frontend.MainActivity::class.java).apply {
                    putExtra("TARGET_FRAGMENT", "checkout")
                    putExtra("selected_items", selectedItems)
                    flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(mainIntent)
                finish()
            } else {
                Toast.makeText(this, "Vui lòng chọn màu trước", Toast.LENGTH_SHORT).show()
            }
        }

        setupViewModel()

        val productId = intent.getStringExtra("product_id")
        val variantId = intent.getStringExtra("variant_id")
        if (productId != null) {
            viewModel.loadArConfig(productId, variantId)
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun setupViewModel() {
        viewModel.shades.observe(this) { shades ->
            if (shades != null && shades.isNotEmpty()) {
                this.shadesList = shades
                colorAdapter.submitList(shades)
            }
        }

        viewModel.selectedShade.observe(this) { shade ->
            if (shade != null) {
                applyShadeConfig(shade)
                colorAdapter.setSelectedShade(shade)
                
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
                btnAddToCart.text = if (shade.inStock) "Giỏ hàng" else "Hết hàng"
                
                btnBuyNow.isEnabled = shade.inStock
                btnBuyNow.visibility = if (shade.inStock) android.view.View.VISIBLE else android.view.View.GONE
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
            val inStock = selected?.inStock ?: false
            btnAddToCart.isEnabled = inStock
            btnBuyNow.isEnabled = inStock
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
