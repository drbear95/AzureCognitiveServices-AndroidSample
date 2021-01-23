package pl.example.app.ui.main

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint


fun drawFaceRectangleOnBitmap(
    originalBitmap: Bitmap, top: Float, left: Float, width: Float, height: Float
): Bitmap? {
    val bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.isAntiAlias = true
    paint.style = Paint.Style.STROKE
    paint.color = Color.RED
    paint.strokeWidth = 1f
    canvas.drawRect(
        left,
        top,
        left + width,
        top + height,
        paint
    )
    return bitmap
}