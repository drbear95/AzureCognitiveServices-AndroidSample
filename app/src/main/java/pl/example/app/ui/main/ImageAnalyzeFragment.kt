package pl.example.app.ui.main

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import pl.example.app.CognitiveService
import pl.example.app.MainActivity
import pl.example.app.R

class ImageAnalyzeFragment : Fragment(R.layout.fragment_one) {

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

            cognitiveService.analyzeImage(it)
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
                }
        }.addTo(subscriptions)
    }

    override fun onPause() {
        super.onPause()

        subscriptions.clear()
    }

}