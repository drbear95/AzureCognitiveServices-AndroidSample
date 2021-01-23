package pl.example.app.ui.main

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import pl.example.app.R

class FaceRecognitionFragment : BaseCognitiveFragment() {
    override fun handlePhoto(bitmap: Bitmap) {
        view?.findViewById<ImageView>(R.id.image_view)?.run {
            setImageBitmap(bitmap)
        }

        cognitiveService.faceRecognition(bitmap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, error ->
                if (error == null) {
                    view?.findViewById<TextView>(R.id.textView)?.run {
                        JSONArray(result).let { array ->
                            drawRectangle(array)
                            text = array.toString(4)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }.addTo(subscriptions)
    }

    private fun drawRectangle(result: JSONArray) {
        view?.findViewById<ImageView>(R.id.image_view)?.let { imageView ->
            (imageView.drawable as? BitmapDrawable?)?.bitmap?.let { image ->
                if (!result.isNull(0)){
                    result.getJSONObject(0).getJSONObject("faceRectangle").let { area ->
                        imageView.setImageBitmap(
                            drawFaceRectangleOnBitmap(
                                image,
                                area.getString("top").toFloat(),
                                area.getString("left").toFloat(),
                                area.getString("width").toFloat(),
                                area.getString("height").toFloat(),
                            )
                        )
                    }
                }
            }
        }
    }
}