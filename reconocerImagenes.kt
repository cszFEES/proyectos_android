package com.example.reconocerimagenes

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
        var imagenCargada : String = "euro.png"
        var imagenMapeada = assetsToBitmap(imagenCargada)
        imagenMapeada?.apply {
            xmlImagenParaMostrar.setImageBitmap(this)
        }

        val xmlTextoCuadroPrincipalTexto : TextView = findViewById(R.id.cuadroPrincipalTexto)
        val xmlBotonComputerVision : Button = findViewById(R.id.computerVision)

        xmlBotonComputerVision.setOnClickListener {
            val clasificador = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            var image = InputImage.fromBitmap(imagenMapeada!!, 0)
            var textoFinal = ""
            clasificador.process(image)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    for (label in labels) {
                        var text = label.text
                        var confidence = label.confidence
                        textoFinal += "Predicción: $text      Probabilidad: $confidence\n"
                    }
                    xmlTextoCuadroPrincipalTexto.text = textoFinal
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
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
    }
}