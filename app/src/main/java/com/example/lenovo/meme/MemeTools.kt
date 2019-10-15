package com.example.lenovo.meme

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MemeTools {

    companion object {

        fun storeMeme(bm: Bitmap, dirPath: File?): Boolean {
            val fileName = "meme" + System.currentTimeMillis() + ".png"
            val file = File(dirPath, fileName)
            Log.d("PATH", file.absolutePath)
            return try {
                val fos = FileOutputStream(file)
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
                true
            } catch (e: Exception) {
                false
            }
        }

        fun createShareableMeme(dirPath: File?, content: View): File {
            val imageName = "meme" + System.currentTimeMillis() + ".png"
            val bitmap = getScreenShot(content)
            val imageFile = File(dirPath, imageName)
            val os: OutputStream
            try {
                os = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return imageFile
        }

        fun getScreenShot(view: View): Bitmap {
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            return bitmap
        }
    }
}