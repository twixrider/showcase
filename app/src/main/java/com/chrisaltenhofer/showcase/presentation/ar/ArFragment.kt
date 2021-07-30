package com.chrisaltenhofer.showcase.presentation.ar

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chrisaltenhofer.showcase.databinding.FragmentArBinding
import com.google.android.filament.Skybox
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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        arViewModel =
                ViewModelProvider(this).get(ArViewModel::class.java)

        _binding = FragmentArBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAr
        arViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        surfaceView = SurfaceView(context).apply { activity?.setContentView(this) }
        choreographer = Choreographer.getInstance()
        modelViewer = ModelViewer(surfaceView)

        surfaceView.setOnTouchListener(modelViewer)

        loadGlb("DamagedHelmet")
        // modelViewer.scene.skybox = Skybox.Builder().build(modelViewer.engine)
        // loadGltf("BusterDrone")
        loadEnvironment("venetian_crossroads_2k")

        return root
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

    private fun loadGltf(name: String) {
        val buffer = readAsset("models/${name}.gltf")
        modelViewer.loadModelGltf(buffer) { uri -> readAsset("models/$uri") }
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