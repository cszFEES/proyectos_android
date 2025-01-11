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
        val bitmap: Bitmap? = assetsToBitmap("euro.png")
        bitmap?.apply {
            xmlImagenParaMostrar.setImageBitmap(this)
        }

        val xmlTextoCuadroPrincipalTexto : TextView = findViewById(R.id.cuadroPrincipalTexto)
        val xmlBotonComputerVision : Button = findViewById(R.id.computerVision)

        xmlBotonComputerVision.setOnClickListener {
            val clasificador = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap!!, 0)
            var textoFinal = ""
            clasificador.process(image)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        textoFinal += "$text : $confidence\n"
                    }
                    xmlTextoCuadroPrincipalTexto.text = textoFinal
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        }

        val xmlBotonCerrar: Button = findViewById(R.id.cerrar)
        xmlBotonCerrar.setOnClickListener { finishAffinity() }
    }
}