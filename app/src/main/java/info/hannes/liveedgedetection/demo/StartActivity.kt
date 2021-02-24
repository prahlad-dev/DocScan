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
    lateinit var btnPick: Button
    lateinit var mCurrentPhotoPath: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
//        btnPick = findViewById(R.id.buttonScan)
//        setView()

        buttonScan.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }

    fun setView() {
        btnPick.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this@StartActivity)
            builder.setMessage("Select an image!")
            builder.setPositiveButton("Gallery") { dialog, which ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 123)
            }
            builder.setNegativeButton("Camera") { dialog, which ->
                dialog.dismiss()
                val cameraIntent = Intent(
                        StartActivity@ this,
                        MainActivity::class.java
                ) //MediaStore.ACTION_IMAGE_CAPTURE is used to bring default camera
                cameraIntent.putExtra(ScanConstants.IMAGE_PATH, getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString())
                startActivityForResult(cameraIntent, 101)
            }
            builder.setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            var selectedImage = data.data
            var btimap: Bitmap? = null
            try {
                val inputStream = selectedImage?.let { contentResolver.openInputStream(it) }
                btimap = BitmapFactory.decodeStream(inputStream)
                ScanConstants.croppedBitmap = btimap
                startActivityForResult(
                        Intent(StartActivity@ this, ScanActivity::class.java),
                        123)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            ScanConstants.croppedBitmap = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    Uri.parse(mCurrentPhotoPath)
            )
            var selectedImage = data?.data
            val f = File(selectedImage.toString())
            val imageName: String = f.getName()
            val intent = Intent(this, data!!::class.java)
            intent.putExtra("uri",imageName)
            startActivityForResult(intent,
                    101)
        }
    }

}
