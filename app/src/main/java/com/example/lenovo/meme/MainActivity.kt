package com.example.lenovo.meme

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.lenovo.meme.MemeTools.Companion.getScreenShot
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var btnLoad: Button
    private lateinit var btnSave: Button
    private lateinit var btnShare: Button
    private lateinit var btnGo: Button
    private lateinit var btnClear: Button
    private lateinit var prevTextTop: TextView
    private lateinit var preTextBottom: TextView
    private lateinit var inputTextTop: EditText
    private lateinit var inputTextBottom: EditText
    private lateinit var ivMemePreview: ImageView
    private var imageLoaded = false
    private var textAdded = false

    //region Life-Cycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            }
        }

        ivMemePreview = findViewById(R.id.imageView)

        prevTextTop = findViewById(R.id.textView1)
        preTextBottom = findViewById(R.id.textView2)

        inputTextTop = findViewById(R.id.editText1)
        inputTextBottom = findViewById(R.id.editText2)

        btnGo = findViewById(R.id.go)

        btnLoad = findViewById(R.id.load)
        btnSave = findViewById(R.id.save)
        btnShare = findViewById(R.id.share)
        btnClear = findViewById(R.id.btnClear)

        btnSave.isEnabled = false
        btnShare.isEnabled = false
        btnGo.isEnabled = false
        btnGo.text = getString(R.string.load_image_first_msg)

        btnLoad.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, RESULT_LOAD_IMAGE)
        }

        btnSave.setOnClickListener {
            val content = findViewById<View>(R.id.meme_preview)
            val bitmap = getScreenShot(content)
            val dirPath = baseContext.getExternalFilesDir(MEME_DIR)
            if (MemeTools.storeMeme(bitmap, dirPath)) {
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
            }
            btnShare.isEnabled = true
        }

        btnShare.setOnClickListener {
            val content = findViewById<View>(R.id.meme_preview)
            val dirPath = baseContext.getExternalFilesDir(MEME_DIR)
            shareImage(MemeTools.createShareableMeme(dirPath, content))
        }

        btnGo.setOnClickListener {
            prevTextTop.text = inputTextTop.text.toString()
            preTextBottom.text = inputTextBottom.text.toString()

            //Forces user to enter at least one line of text
            if (inputTextTop.text.toString() != "" || inputTextBottom.text.toString() != "") {
                textAdded = true
            } else {
                Toast.makeText(applicationContext, "Enter some text first!",
                        Toast.LENGTH_SHORT).show()
                textAdded = false
                btnShare.isEnabled = false
                btnSave.isEnabled = false
            }
            if (imageLoaded && textAdded) {
                btnShare.isEnabled = true
                btnSave.isEnabled = true
            }
        }

        btnClear.setOnClickListener {
            inputTextTop.setText("")
            inputTextBottom.setText("")
            btnShare.isEnabled = false
            btnSave.isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            if (selectedImage != null) {
                cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
            }
            if (cursor != null) {
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val picturePath = cursor.getString(columnIndex)
                cursor.close()
                ivMemePreview.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                //Ensures image can't be saved/shared until text has been added
                imageLoaded = true
                btnGo.isEnabled = true
                btnGo.text = getString(R.string.btn_try)
                if (textAdded) {
                    btnShare.isEnabled = true
                    btnSave.isEnabled = true
                    btnShare.isEnabled = true
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)//do nothing
            } else {
                Toast.makeText(this, "No Permission Granted!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //endregion

    //region utils
    private fun shareImage(imageFile: File) {
        val uri = FileProvider.getUriForFile(this@MainActivity, "com.example.lenovo.meme.provider", imageFile)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/*"

        try {
            startActivity(Intent.createChooser(intent, "Share Via"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No sharing app found", Toast.LENGTH_SHORT).show()
        }
    }
    //endregion

    companion object {

        private const val MY_PERMISSION_REQUEST = 1
        private const val RESULT_LOAD_IMAGE = 2
        private const val MEME_DIR = "MEME"
    }
}