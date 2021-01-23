package pl.example.app

import android.graphics.Bitmap
import io.reactivex.Single
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


class CognitiveService {
    private val subscriptionKey = "bd8975e716454b72af65a0e4e87081fa"
    val endpoint = HttpUrl.Builder()
        .scheme("https")
        .host("todsc-face.cognitiveservices.azure.com")
        .addPathSegments("face/v1.0/detect")

    fun faceRecognition(bitmap: Bitmap): Single<String> {
        return Single.fromCallable {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = stream.toByteArray()

            val url = endpoint
                .addQueryParameter("returnFaceAttributes", "age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise")
                .build()

            OkHttpClient().let { client ->
                val request = Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                    .url(url)
                    .post(image.toRequestBody("application/octet-stream".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    response.body?.string()
                } else {
                    error(response.message)
                }
            }
        }
    }
}