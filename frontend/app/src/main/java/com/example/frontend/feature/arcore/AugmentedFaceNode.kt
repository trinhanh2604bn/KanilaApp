package com.example.frontend.feature.arcore

import android.content.Context
import com.google.ar.core.AugmentedFace
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState

class AugmentedFaceNode(private val augmentedFace: AugmentedFace?, val context: Context) {

    private val augmentedFaceRenderer = AugmentedFaceRenderer()
    private val faceLandmarks = HashMap<FaceLandmark, FaceRegion>()
    private var renderFaceMesh: Boolean = false

    companion object {
        enum class FaceLandmark {
            FOREHEAD_RIGHT,
            FOREHEAD_LEFT,
            NOSE_TIP
        }
    }

    init {
        renderFaceMesh = false
        augmentedFaceRenderer.setMaterialProperties(0.0f, 1.0f, 0.1f, 6.0f)
    }

    fun setRegionModel(faceLandmark: FaceLandmark, modelName: String, modelTexture: String) {
        val faceRegion  = FaceRegion(faceLandmark)
        faceRegion.setRenderable(context, modelName, modelTexture)
        faceLandmarks[faceLandmark] = faceRegion
    }

    fun setFaceMeshTexture(assetName: String) {
        pendingTextureName = assetName
    }

    fun setTextureColor(color: FloatArray) {
        pendingColor = color.copyOf()
    }

    // Pending updates applied on GL thread
    @Volatile private var pendingTextureName: String? = null
    @Volatile private var pendingColor: FloatArray? = null

    fun onDraw(projectionMatrix: FloatArray, viewMatrix: FloatArray, colorCorrectionRgba: FloatArray) {
        // Apply pending texture change on the GL thread
        pendingTextureName?.let {
            augmentedFaceRenderer.createOnGlThread(context, it)
            renderFaceMesh = true
            pendingTextureName = null
        }
        // Apply pending color change on the GL thread
        pendingColor?.let {
            augmentedFaceRenderer.setTintColor(it)
            pendingColor = null
        }

        augmentedFace?.let { face ->
            if (face.trackingState != TrackingState.TRACKING) {
                return
            }

            if (renderFaceMesh) {
                val modelMatrix = FloatArray(16)
                face.centerPose.toMatrix(modelMatrix, 0)
                augmentedFaceRenderer.draw(
                        projectionMatrix, viewMatrix, modelMatrix, colorCorrectionRgba, face
                )
            }

            for (region in faceLandmarks.values) {
                val objectMatrix = FloatArray(16)
                getRegionPose(region.faceLandmark)?.toMatrix(objectMatrix, 0)
                region.draw(objectMatrix, viewMatrix, projectionMatrix, colorCorrectionRgba)
            }
        }
    }

    private fun getRegionPose(faceLandmark: FaceLandmark) : Pose? {
        return when (faceLandmark) {
            FaceLandmark.NOSE_TIP -> augmentedFace?.getRegionPose(AugmentedFace.RegionType.NOSE_TIP)
            FaceLandmark.FOREHEAD_LEFT -> augmentedFace?.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT)
            FaceLandmark.FOREHEAD_RIGHT -> augmentedFace?.getRegionPose(AugmentedFace.RegionType.FOREHEAD_RIGHT)
        }
    }
}
