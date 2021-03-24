package info.hannes.liveedgedetection.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import info.hannes.liveedgedetection.ScanConstants
import info.hannes.liveedgedetection.activity.ScanActivity
import kotlinx.android.synthetic.main.activity_start.*
import java.io.File

class StartActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        buttonScan.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}
