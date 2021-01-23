package pl.example.app

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.thorny.photoeasy.OnPictureReady
import com.thorny.photoeasy.PhotoEasy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import pl.example.app.ui.main.SectionsPagerAdapter
import java.util.jar.Manifest


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    val photoSubject = BehaviorSubject.create<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 999)
    }

    fun takePhoto(){
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ActivityResult","Outside")

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            photoSubject.onNext(imageBitmap)
        }
    }
}