package pl.example.app.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import pl.example.app.CognitiveService
import pl.example.app.R

abstract class BaseCognitiveFragment: Fragment(R.layout.fragment_one) {
    protected val cognitiveService = CognitiveService()
    protected val subscriptions = CompositeDisposable()

    override fun onResume() {
        super.onResume()

        view?.findViewById<Button>(R.id.open_button)?.setOnClickListener {
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            (data?.extras?.get("data") as? Bitmap?)?.run(::handlePhoto)
        }
    }

    abstract fun handlePhoto(bitmap: Bitmap)

    override fun onPause() {
        super.onPause()

        subscriptions.clear()
    }
}