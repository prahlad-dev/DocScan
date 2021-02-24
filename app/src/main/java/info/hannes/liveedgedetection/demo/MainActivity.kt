package info.hannes.liveedgedetection.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import info.hannes.liveedgedetection.FileUtils
import info.hannes.liveedgedetection.ScanConstants
import info.hannes.liveedgedetection.activity.ScanActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startScan()
    }

    private fun startScan() {
        val intent = Intent(this, ScanActivity::class.java)
        // optional, otherwise it's stored internal
        intent.putExtra(ScanConstants.IMAGE_PATH, getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString())
        intent.putExtra(ScanConstants.TIME_HOLD_STILL, 700L)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.extras?.let { bundle ->
                    val filePath = bundle.getString(ScanConstants.SCANNED_RESULT)
                    filePath?.let {
                        val baseBitmap = FileUtils.decodeBitmapFromFile(it)
                        scanned_image.setImageBitmap(baseBitmap)
                        scanned_image.scaleType = ImageView.ScaleType.FIT_CENTER

                        showSnackbar(ScanConstants.IMAGE_PATH)
                        Timber.i(filePath)
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish()
            }
        }
    }

    private fun showSnackbar(text: String) {
        var viewPos: View? = findViewById(R.id.coordinatorLayout)
        if (viewPos == null) {
            viewPos = findViewById(android.R.id.content)
        }
        val snackbar = Snackbar.make(viewPos!!, text, Snackbar.LENGTH_INDEFINITE)
        val view = snackbar.view
        when (val params = view.layoutParams) {
            is CoordinatorLayout.LayoutParams -> {
                val paramsC = view.layoutParams as CoordinatorLayout.LayoutParams
                paramsC.gravity = Gravity.CENTER_VERTICAL
                view.layoutParams = paramsC
                snackbar.show()
            }
            is FrameLayout.LayoutParams -> {
                val paramsC = view.layoutParams as FrameLayout.LayoutParams
                paramsC.gravity = Gravity.BOTTOM
                view.layoutParams = paramsC
                snackbar.show()
            }
            else -> {
                Toast.makeText(this, text + " " + params.javaClass.simpleName, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 101
    }
}