package info.hannes.liveedgedetection.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PointF
import android.os.*
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import info.hannes.liveedgedetection.*
import info.hannes.liveedgedetection.FileUtils
import info.hannes.liveedgedetection.view.ScanSurfaceView
import kotlinx.android.synthetic.main.activity_scan.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


/**
 * This class initiates camera and detects edges on live view
 */
class ScanActivity : AppCompatActivity(), IScanner, View.OnClickListener {
    lateinit var croppedBitmap: Bitmap
    private var imageSurfaceView: ScanSurfaceView? = null
    private var isCameraPermissionGranted = true
    private var isExternalStorageStatsPermissionGranted = true
    private var copyBitmap: Bitmap? = null
    private var timeHoldStill: Long = ScanSurfaceView.DEFAULT_TIME_POST_PICTURE
    private var imageFileName: String? = null
    private var filepath: String? = null
    lateinit var capture: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timeHoldStill = intent.getLongExtra(ScanConstants.TIME_HOLD_STILL, ScanSurfaceView.DEFAULT_TIME_POST_PICTURE)
        setContentView(R.layout.activity_scan)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        capture = findViewById(R.id.btnCapture)

        capture.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Do some work here
            }

        })

        crop_accept_btn.setOnClickListener(this)
        crop_reject_btn.setOnClickListener {
            TransitionManager.beginDelayedTransition(container_scan)
            crop_layout.visibility = View.GONE
            imageSurfaceView?.setPreviewCallback()
        }
        checkCameraPermissions()
        if (intent.hasExtra(ScanConstants.IMAGE_PATH))
            checkExternalStoragePermissions()
    }

    private fun checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            isCameraPermissionGranted = false
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Enable camera permission from settings", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_CAMERA)
            }
        } else {
            if (isCameraPermissionGranted) {
                imageSurfaceView = ScanSurfaceView(this@ScanActivity, this, timeHoldStill)
                camera_preview.addView(imageSurfaceView)
            } else {
                isCameraPermissionGranted = true
            }
        }
    }

    private fun checkExternalStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            isExternalStorageStatsPermissionGranted = false
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Enable external storage permission", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_EXTERNAL_STORAGE)
            }
        } else {
            if (!isExternalStorageStatsPermissionGranted) {
                isExternalStorageStatsPermissionGranted = true
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA -> onRequestCamera(grantResults)
            PERMISSIONS_REQUEST_EXTERNAL_STORAGE -> onRequestExternalStorage(grantResults)
            else -> {
            }
        }
    }

    private fun onRequestCamera(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Handler(Looper.getMainLooper()).post {
                imageSurfaceView = ScanSurfaceView(this@ScanActivity, this@ScanActivity, timeHoldStill)
                camera_preview.addView(imageSurfaceView)
            }
        } else {
            Toast.makeText(this, getString(R.string.permission_denied_camera_toast), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun onRequestExternalStorage(grantResults: IntArray) {
        if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permission_denied_external_storage_toast), Toast.LENGTH_SHORT).show()
        }
    }

    override fun displayHint(scanHint: ScanHint) {
        capture_hint_layout.visibility = View.VISIBLE
        when (scanHint) {
            ScanHint.MOVE_CLOSER -> {
                capture_hint_text.text = resources.getString(R.string.move_closer)
                capture_hint_layout.background = ContextCompat.getDrawable(this, R.drawable.hint_red)
            }
            ScanHint.MOVE_AWAY -> {
                capture_hint_text.text = resources.getString(R.string.move_away)
                capture_hint_layout.background = ContextCompat.getDrawable(this, R.drawable.hint_red)
            }
            ScanHint.ADJUST_ANGLE -> {
                capture_hint_text.text = resources.getString(R.string.adjust_angle)
                capture_hint_layout.background = ContextCompat.getDrawable(this, R.drawable.hint_red)
            }
            ScanHint.FIND_RECT -> {
                capture_hint_text.text = resources.getString(R.string.finding_rect)
                capture_hint_layout.background = ContextCompat.getDrawable(this, R.drawable.hint_white)
            }
            ScanHint.CAPTURING_IMAGE -> {
                capture_hint_text.text = resources.getString(R.string.hold_still)
                capture_hint_layout.background = ContextCompat.getDrawable(this, R.drawable.hint_green)
            }
            ScanHint.NO_MESSAGE -> capture_hint_layout.visibility = View.GONE
        }
    }

    override fun onPictureClicked(bitmap: Bitmap) {
        try {
            copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val height = window.findViewById<View>(Window.ID_ANDROID_CONTENT).height
            val width = window.findViewById<View>(Window.ID_ANDROID_CONTENT).width
            copyBitmap = copyBitmap?.resizeToScreenContentSize(width, height)
            copyBitmap?.let {
                val originalMat = Mat(it.height, it.width, CvType.CV_8UC1)
                Utils.bitmapToMat(copyBitmap, originalMat)
                val points: ArrayList<PointF>
                val pointFs: MutableMap<Int, PointF> = HashMap()
                try {
                    val quad = ScanUtils.detectLargestQuadrilateral(originalMat)
                    if (null != quad) {
                        val resultArea = abs(Imgproc.contourArea(quad.contour))
                        val previewArea = originalMat.rows() * originalMat.cols().toDouble()
                        if (resultArea > previewArea * 0.08) {
                            points = ArrayList()
                            points.add(PointF(quad.points[0].x.toFloat(), quad.points[0].y.toFloat()))
                            points.add(PointF(quad.points[1].x.toFloat(), quad.points[1].y.toFloat()))
                            points.add(PointF(quad.points[3].x.toFloat(), quad.points[3].y.toFloat()))
                            points.add(PointF(quad.points[2].x.toFloat(), quad.points[2].y.toFloat()))
                        } else {
                            points = it.getPolygonDefaultPoints()
                        }
                    } else {
                        points = it.getPolygonDefaultPoints()
                    }
                    var index = -1
                    for (pointF in points) {
                        pointFs[++index] = pointF
                    }
                    polygon_view.points = pointFs
                    val padding = resources.getDimension(R.dimen.scan_padding).toInt()
                    val layoutParams = FrameLayout.LayoutParams(it.width + 2 * padding, it.height + 2 * padding)
                    layoutParams.gravity = Gravity.CENTER
                    polygon_view.layoutParams = layoutParams
                    TransitionManager.beginDelayedTransition(container_scan)
                    crop_layout.visibility = View.VISIBLE
                    crop_image_view.setImageBitmap(copyBitmap)
                    crop_image_view.scaleType = ImageView.ScaleType.FIT_XY

                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(view: View) {
        val points = polygon_view.points
        val croppedBitmap: Bitmap?
        croppedBitmap = if (ScanUtils.isScanPointsValid(points)) {
            val point1 = Point(points.getValue(0).x.toDouble(), points.getValue(0).y.toDouble())
            val point2 = Point(points.getValue(1).x.toDouble(), points.getValue(1).y.toDouble())
            val point3 = Point(points.getValue(2).x.toDouble(), points.getValue(2).y.toDouble())
            val point4 = Point(points.getValue(3).x.toDouble(), points.getValue(3).y.toDouble())
            copyBitmap?.enhanceReceipt(point1, point2, point3, point4)
        } else {
            copyBitmap
        }
        croppedBitmap?.let { bitmap ->
            val imageName = ScanConstants.IMAGE_NAME + SimpleDateFormat("-yyyy-MM-dd_HHmmss").format(Date()) + ".png"
            var path: String? = null
            intent.getStringExtra(ScanConstants.IMAGE_PATH)?.let {
                path = FileUtils.saveToExternalMemory(bitmap, it, imageName, 100).first
            } ?: run {
                path = FileUtils.saveToInternalMemory(bitmap, ScanConstants.INTERNAL_IMAGE_DIR, imageName, this@ScanActivity, 100).first
            }
            setResult(Activity.RESULT_OK, Intent().putExtra(ScanConstants.SCANNED_RESULT, path + File.separator + imageName))

        }

        System.gc()
        if (croppedBitmap != null) {
            filepath = saveToInternalStorage(croppedBitmap)
            ScanConstants.IMAGE_PATH = filepath.toString()
            ScanConstants.croppedBitmap = croppedBitmap
        }
        sharpen()
        ScanConstants.IMAGE_NAME = imageFileName.toString()
        val intent = Intent(this, data::class.java)
        intent.putExtra("fname", imageFileName)
        startActivityForResult(intent, 101)
        finish()
    }

    private fun sharpen() {
            try {
                val bmpMonochrome = Bitmap.createBitmap(croppedBitmap.getWidth(), croppedBitmap.getHeight(), Bitmap.Config.ARGB_8888)
                val source = Mat()
                Utils.bitmapToMat(bmpMonochrome, source)
                val destination = Mat(source.rows(), source.cols(), source.type())

                // filtering
                Imgproc.GaussianBlur(source, destination, Size(0.0, 0.0), 10.0)
                Core.addWeighted(source, 1.5, destination, -0.5, 0.0, destination)
                Utils.bitmapToMat(bmpMonochrome, destination)
            } catch (e: java.lang.Exception) {
            }
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File("$root/EdgeDetection")
        if (!myDir.exists()) myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        imageFileName = "$timeStamp.jpg"

        val mypath = File(myDir, imageFileName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return myDir.absolutePath + imageFileName
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CAMERA = 101
        private const val PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 102
        private const val openCvLibrary = "opencv_java4"
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

        val allDraggedPointsStack = Stack<PolygonPoints>()

        init {
            System.loadLibrary(openCvLibrary)
        }
    }

}