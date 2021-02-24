package info.hannes.liveedgedetection

import android.graphics.Bitmap

/**
 * This class defines constants
 */
object ScanConstants {
    var IMAGE_NAME: String = "image"
    lateinit var croppedBitmap: Bitmap
    const val SCANNED_RESULT = "scannedResult"
    var IMAGE_PATH = "image_path"
    const val TIME_HOLD_STILL = "time_hold"
    internal const val INTERNAL_IMAGE_DIR = "imageDir"
    const val HIGHER_SAMPLING_THRESHOLD = 2200
    @JvmField
    var KSIZE_BLUR = 3
    @JvmField
    var KSIZE_CLOSE = 10
    const val CANNY_THRESH_L = 85
    const val CANNY_THRESH_U = 185
    const val TRUNC_THRESH = 150
    const val CUTOFF_THRESH = 155
}