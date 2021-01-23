package pl.example.app

import android.graphics.Bitmap
import io.reactivex.Single
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


class CognitiveService {
    fun faceRecognition(bitmap: Bitmap): Single<String> {
        return Single.fromCallable {
            val url = HttpUrl.Builder()
                .scheme("https")
                .host("todsc-face.cognitiveservices.azure.com")
                .addPathSegments("face/v1.0/detect")
                .addQueryParameter("returnFaceAttributes", "age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise")
                .build()

            val requestBody = bitmap.bitmapToByteArray().toRequestBody("application/octet-stream".toMediaType())

            url to requestBody
        }.flatMap {
            sendRequest("bd8975e716454b72af65a0e4e87081fa", it.first.toString(), it.second)
        }
    }

    fun analyzeImage(bitmap: Bitmap): Single<String> {
        return Single.fromCallable {
            val url = HttpUrl.Builder()
                .scheme("https")
                .host("todscimageanalyze.cognitiveservices.azure.com")
                .addPathSegments("vision/v3.1/analyze")
                .addQueryParameter("visualFeatures", "adult,brands,categories,color,description,faces,imagetype,objects,tags")
                .build()

            val requestBody = bitmap.bitmapToByteArray().toRequestBody("application/octet-stream".toMediaType())

            url to requestBody
        }.flatMap {
            sendRequest("8556dc9bdbcc4a029552d06160062c64", it.first.toString(), it.second )
        }
    }

    private fun sendRequest(subscriptionKey: String, url: String, requestBody: RequestBody): Single<String>{
        return Single.fromCallable {
            OkHttpClient().let { client ->
                val request = Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                    .url(url)
                    .post(requestBody)
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

    private fun Bitmap.bitmapToByteArray(): ByteArray{
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}