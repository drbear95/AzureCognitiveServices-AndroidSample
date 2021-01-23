package pl.example.app.ui.main

import android.graphics.drawable.BitmapDrawable
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import pl.example.app.CognitiveService
import pl.example.app.MainActivity
import pl.example.app.R


class FaceRecognitionFragment : Fragment(R.layout.fragment_one) {

    private val cognitiveService = CognitiveService()
    private val subscriptions = CompositeDisposable()

    override fun onResume() {
        super.onResume()

        view?.findViewById<Button>(R.id.open_button)?.setOnClickListener {
            (requireActivity() as MainActivity).takePhoto()
        }

        (requireActivity() as MainActivity).photoSubject.subscribe {

            view?.findViewById<ImageView>(R.id.image_view)?.run {
                setImageBitmap(it)
            }

            cognitiveService.faceRecognition(it)
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

    override fun onPause() {
        super.onPause()

        subscriptions.clear()
    }

}