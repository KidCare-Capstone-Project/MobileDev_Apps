package com.callcenter.kidcare.ui.home.mainfeaturesgrid.predict.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.callcenter.kidcare.data.ChildProfile
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PredictViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private var tflite: Interpreter? = null

    /**
     * Memuat model TensorFlow Lite dari aset sebagai MappedByteBuffer.
     */
    @Throws(IOException::class)
    private fun loadModelFile(context: Context): MappedByteBuffer {
        val assetManager = context.assets
        assetManager.openFd("kidcare.tflite").use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel: FileChannel = inputStream.channel
                val startOffset: Long = fileDescriptor.startOffset
                val declaredLength: Long = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    /**
     * Menginisialisasi Interpreter TensorFlow Lite dengan model.
     */
    fun loadModel(context: Context) {
        try {
            val modelBuffer = loadModelFile(context)
            tflite = Interpreter(modelBuffer)
            Log.d("PredictViewModel", "Model berhasil dimuat")
        } catch (e: IOException) {
            Log.e("PredictViewModel", "Error saat memuat model", e)
        }
    }

    /**
     * Mengambil data anak dari Firestore.
     */
    fun fetchChildrenData(uid: String, onResult: (List<ChildProfile>) -> Unit) {
        firestore.collection("users").document(uid).collection("children")
            .get()
            .addOnSuccessListener { documents ->
                val children = documents.map { doc ->
                    doc.toObject(ChildProfile::class.java)
                }
                onResult(children)
            }
            .addOnFailureListener { e ->
                Log.e("PredictViewModel", "Error saat mengambil data", e)
                onResult(emptyList())
            }
    }

    fun predict(child: ChildProfile): FloatArray? {
        tflite?.let { interpreter ->

            val heightValue = child.height.replace(",", ".").toFloatOrNull()
            val weightValue = child.weight.replace(",", ".").toFloatOrNull()
            val headCircumferenceValue = child.headCircumference.replace(",", ".").toFloatOrNull()
            val ageValue = child.getAge()

            if (heightValue == null || weightValue == null || headCircumferenceValue == null || ageValue == null) {
                Log.e("PredictViewModel", "Data input tidak valid")
                return null
            }

            val gender = if (child.gender.lowercase() == "perempuan") 0.0f else 1.0f

            val inputSize = 5
            val inputBuffer =
                ByteBuffer.allocateDirect(4 * inputSize).order(ByteOrder.nativeOrder())
            inputBuffer.putFloat(gender)
            inputBuffer.putFloat(ageValue)
            inputBuffer.putFloat(heightValue)
            inputBuffer.putFloat(weightValue)
            inputBuffer.putFloat(headCircumferenceValue)
            inputBuffer.rewind()

            val outputTensor = interpreter.getOutputTensor(0)
            val outputShape = outputTensor.shape()
            val outputSize = outputShape[1]
            val outputBuffer =
                ByteBuffer.allocateDirect(4 * outputSize).order(ByteOrder.nativeOrder())

            interpreter.run(inputBuffer, outputBuffer)
            outputBuffer.rewind()

            val predictions = FloatArray(outputSize)
            outputBuffer.asFloatBuffer().get(predictions)

            Log.d("PredictViewModel", "Prediksi stunting: ${predictions.joinToString()}")

            return predictions
        } ?: run {
            Log.e("PredictViewModel", "Interpreter belum diinisialisasi")
            return null
        }
    }

    /**
     * Membersihkan Interpreter ketika ViewModel dihapus.
     */
    override fun onCleared() {
        super.onCleared()
        tflite?.close()
    }
}
