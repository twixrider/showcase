package com.chrisaltenhofer.showcase.presentation.ar

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chrisaltenhofer.showcase.databinding.FragmentArBinding
import com.google.android.filament.utils.*
import java.nio.ByteBuffer

class ArFragment : Fragment() {

    companion object {
        init { Utils.init() }
    }

    private lateinit var surfaceView: SurfaceView
    private lateinit var choreographer: Choreographer
    private lateinit var modelViewer: ModelViewer

    private lateinit var arViewModel: ArViewModel
    private var _binding: FragmentArBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arViewModel = ViewModelProvider(this).get(ArViewModel::class.java)

        _binding = FragmentArBinding.inflate(inflater, container, false)

        choreographer = Choreographer.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViews()
        setListeners()

        loadGlb("DamagedHelmet")
        loadEnvironment("venetian_crossroads_2k")
    }

    private fun setViews() {
        surfaceView = binding.surfaceView
        modelViewer = ModelViewer(surfaceView)
    }

    private fun setListeners() {
        surfaceView.setOnTouchListener(modelViewer)
    }

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(currentTime: Long) {
            choreographer.postFrameCallback(this)
            modelViewer.render(currentTime)
        }
    }

    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(frameCallback)
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        choreographer.removeFrameCallback(frameCallback)
        _binding = null
    }

    private fun loadGlb(name: String) {
        val buffer = readAsset("models/${name}.glb")
        modelViewer.loadModelGlb(buffer)
        modelViewer.transformToUnitCube()
    }

    private fun readAsset(assetName: String): ByteBuffer {
        val input = activity?.assets?.open(assetName)
        val bytes = input?.let { ByteArray(it.available()) }
        input?.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    private fun loadEnvironment(ibl: String) {
        // Create the indirect light source and add it to the scene.
        var buffer = readAsset("envs/$ibl/${ibl}_ibl.ktx")
        KTXLoader.createIndirectLight(modelViewer.engine, buffer).apply {
            intensity = 50_000f
            modelViewer.scene.indirectLight = this
        }

        // Create the sky box and add it to the scene.
        buffer = readAsset("envs/$ibl/${ibl}_skybox.ktx")
        KTXLoader.createSkybox(modelViewer.engine, buffer).apply {
            modelViewer.scene.skybox = this
        }
    }
}