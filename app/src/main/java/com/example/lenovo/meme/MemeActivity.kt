package com.example.lenovo.meme

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_meme.*

class MemeActivity : AppCompatActivity(), View.OnClickListener {

    private var state = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)

        btnPick.setOnClickListener(this)
        btnPreview.setOnClickListener(this)
        btnSave.setOnClickListener(this)

        setLoading()

        requestPermissions()
    }

    override fun onStart() {
        super.onStart()

        revealUI(state)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        setupAnimation(ivMemePreview, 500, 500)

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
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

        doStepOne()
        doStepTwo()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)//do nothing
            } else {
                Toast.makeText(this, "No Permission Granted!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setLoading() {

        tvInstructionHint1.alpha = 0f
        tvInstructionHint2.alpha = 0f
        btnPick.alpha = 0f
        ivMemePreview.alpha = 0f
        tvInstructionHint3.alpha = 0f
        etTopText.alpha = 0f
        tvInstructionHint4.alpha = 0f
        etBottomText.alpha = 0f
        btnPreview.alpha = 0f
        btnSave.alpha = 0f
    }

    private fun setupAnimation(viewElement: View,
                               startDelay: Long = 0,
                               duration: Long = 1000) {
        viewElement.animate()
                .setStartDelay(startDelay)
                .alpha(1f)
                .duration = duration
    }

    private fun revealUI(level: Int) {
        when (level) {
            0 -> doStepZero()
            /* 1 -> setTickVisible(ivTick1)
             2 -> setTickVisible(ivTick2)
             3 -> {
                 setButtonText(R.string.setup_button_finish)
                 setTickVisible(ivTick3)
             }*/
        }
        state++
    }

    private fun doStepZero() {
        setupAnimation(tvInstructionHint1)
        setupAnimation(tvInstructionHint2, 1000, 500)
        setupAnimation(btnPick, 1500, 500)
    }

    private fun doStepOne() {
        setupAnimation(tvInstructionHint3, 1000, 500)
        setupAnimation(etTopText, 500, 500)
    }

    private fun doStepTwo() {
        setupAnimation(tvInstructionHint4, 1000, 500)
        setupAnimation(etBottomText, 500, 500)
        setupAnimation(btnPreview, 500, 500)
    }

    private fun doFinalStep() {
        setupAnimation(btnSave, 500, 500)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnPick -> {
                val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i, RESULT_LOAD_IMAGE)
            }

            R.id.btnPreview -> {
                tvMemeTop.text = etTopText.text.toString()
                tvMemeBottom.text = etBottomText.text.toString()

                doFinalStep()
            }

            R.id.btnSave -> {
                val content = findViewById<View>(R.id.rlMemeLayout)
                val bitmap = MemeTools.getScreenShot(content)
                val dirPath = baseContext.getExternalFilesDir(MEME_DIR)
                if (MemeTools.storeMeme(bitmap, dirPath)) {
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            }
        }
    }

    companion object {

        private const val MY_PERMISSION_REQUEST = 1
        private const val RESULT_LOAD_IMAGE = 2
        private const val MEME_DIR = "MEME"
    }
}
