package info.hannes.liveedgedetection.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import info.hannes.liveedgedetection.R
import info.hannes.liveedgedetection.ScanConstants
import kotlinx.android.synthetic.main.activity_data.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException

class uploadData : AppCompatActivity() {
    var imagename: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        imagename = intent.extras!!.getString("filename")
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        upload()
        btnDone.setOnClickListener(View.OnClickListener {
            setResult(RESULT_OK)
            finish()
        })
    }

    object ServiceGenerator2 {
        private const val BASE_URL = "http://192.180.1.169:7000/api/qis/android_generate_quote/"
        private val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
        private val retrofit = builder.build()
        private val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BASIC)
        private val httpClient = OkHttpClient.Builder()
        fun <S> createService(serviceClass: Class<S>?): S {
            if (!httpClient.interceptors().contains(loggingInterceptor)) {
                httpClient.addInterceptor(loggingInterceptor)
                builder.client(httpClient.build())
            }
            return retrofit.create(serviceClass)
        }
    }

    private fun upload() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ScanConstants.croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageByte = byteArrayOutputStream.toByteArray()
        val mediaType = "image/*".toMediaTypeOrNull()
        val requestFile = imageByte.toRequestBody(mediaType)
        val body = MultipartBody.Part.createFormData("image", imagename, requestFile)
        val call = ServiceGenerator2.createService(FileUploadService::class.java).upload(body)
        call!!.enqueue(object : Callback<pojo?> {
            override fun onResponse(call: Call<pojo?>, response: Response<pojo?>) {
                try {
                    btnDone.visibility = View.VISIBLE
                    fname.setText(response.body()?.getfname())
                    block.setText(response.body()?.getblock())
                    symbol.setText(response.body()?.getsymbol())
                    reqid.setText(response.body()?.getreqid())
                    vertical.setText(response.body()?.getvertical())
                    vehicleid.setText(response.body()?.getvehicleid())
                    make.setText(response.body()?.getmake())
                    model.setText(response.body()?.getmodel())
                    variant.setText(response.body()?.getvariant())
                    fuel.setText(response.body()?.getfuel())
                    cc.setText(response.body()?.getcc())
                    expdate.setText(response.body()?.getexpdate())
                    previnsu.setText(response.body()?.getprevinsu())
                    regdate.setText(response.body()?.getregdate())
                    mfg.setText(response.body()?.getmanuf())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (reqid.text == "" || previnsu.text == "" || vertical.text == ""){
                    waitmsg.setText("Capture Image Again.")
                }
            }

            override fun onFailure(call: Call<pojo?>, t: Throwable) {
                btnDone.visibility = View.VISIBLE
                waitmsg.setText(t.message)
            }
        })
    }
}

private fun MultipartBody.Part.Companion.createFormData(name: String, filename: String?, body: Unit) {

}

private fun RequestBody.Companion.create(imageByte: ByteArray, parse: MediaType?) {

}
