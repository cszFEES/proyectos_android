package com.example.reconocerimagenes

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling


// extension function to get bitmap from assets
fun Context.assetsToBitmap(fileName: String): Bitmap?{
    return try {
        with(assets.open(fileName)){
            BitmapFactory.decodeStream(this)
        }
    } catch (e: IOException) { null }
}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val xmlImagenParaMostrar: ImageView = findViewById(R.id.imagenParaMostrar)
        var imagenCargada: String = "euro.png"
        var imagenMapeada = assetsToBitmap(imagenCargada)
        imagenMapeada?.apply {
            xmlImagenParaMostrar.setImageBitmap(this)
        }

        val xmlTextoCuadroPrincipalTexto: TextView = findViewById(R.id.cuadroPrincipalTexto)
        val xmlBotonComputerVision: Button = findViewById(R.id.computerVision)

        xmlBotonComputerVision.setOnClickListener {
            val clasificador = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            var imagenParaExaminar = InputImage.fromBitmap(imagenMapeada!!, 0)
            var textoFinal = ""
            clasificador.process(imagenParaExaminar)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        var text = label.text
                        var confidence = label.confidence
                        textoFinal += "Predicción: $text      Probabilidad: $confidence\n"
                    }
                    xmlTextoCuadroPrincipalTexto.text = textoFinal
                }
                .addOnFailureListener { e ->
                }
        }

        val xmlAlternadorCambiarImagen: Switch = findViewById(R.id.cambiarImagen)
        xmlAlternadorCambiarImagen.setOnCheckedChangeListener { _, isChecked ->
            imagenCargada = if (imagenCargada == "arbol.png") "euro.png" else "arbol.png"
            imagenMapeada = assetsToBitmap(imagenCargada)
            imagenMapeada?.let { bitmap ->
                xmlImagenParaMostrar.setImageBitmap(bitmap)
            }
        }

        val xmlBotonCerrar: Button = findViewById(R.id.cerrar)
        xmlBotonCerrar.setOnClickListener { finishAffinity() }


        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data

                if (uri != null) {
                    try {
                        imagenMapeada = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        imagenMapeada?.let {
                            xmlImagenParaMostrar.setImageBitmap(it)
                        }
                        xmlImagenParaMostrar.scaleType = ImageView.ScaleType.FIT_XY
                        xmlImagenParaMostrar.adjustViewBounds = true
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No image URI found", Toast.LENGTH_SHORT).show()
                }

            }
        }

        val xmlBotonUsarGaleria: Button = findViewById(R.id.usarGaleria)
        xmlBotonUsarGaleria.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent) // Now getContent is in scope
        }

    }}