package pl.example.app.ui.main

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import pl.example.app.R

class ImageAnalyzeFragment : BaseCognitiveFragment() {

    override fun handlePhoto(bitmap: Bitmap) {
        view?.findViewById<ImageView>(R.id.image_view)?.run {
            setImageBitmap(bitmap)
        }

        cognitiveService.analyzeImage(bitmap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, error ->
                if (error == null) {
                    view?.findViewById<TextView>(R.id.textView)?.run {
                        JSONObject(result).let { array ->
                            text = array.toString(4)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }.addTo(subscriptions)
    }
}