package info.hannes.liveedgedetection.activity

import androidx.annotation.Nullable
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileUploadService {
    @Multipart
    @Nullable
    @POST("http://192.180.1.169:7000/api/qis/android_generate_quote/")
    fun upload(
            @Part body: MultipartBody.Part?
    ): Call<pojo?>?
}

