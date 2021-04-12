package info.hannes.liveedgedetection.activity

import com.google.gson.annotations.SerializedName

class pojo {
    @SerializedName("response")
    private var Response: String? = null

    @SerializedName("filename")
    private val fname: String? = null

    @SerializedName("sym_conf")
    private val symbol: String? = null

    @SerializedName("request_id")
    private val req: String? = null

    @SerializedName("vertical")
    private val vertical: String? = null

    @SerializedName("vehicle_id")
    private val vehicle: String? = null

    @SerializedName("make")
    private val make: String? = null

    @SerializedName("model")
    private val model: String? = null

    @SerializedName("variant")
    private val variant: String? = null

    @SerializedName("fuel")
    private val fuel: String? = null

    @SerializedName("cc")
    private val cc: String? = null

    @SerializedName("prev_insurer")
    private val ins: String? = null

    @SerializedName("mfg_yr")
    private val mfg: String? = null

    @SerializedName("registration_date")
    private val reg: String? = null

    @SerializedName("expiry_date")
    private val exp: String? = null

    @SerializedName("block_conf")
    private val block: String? = null

    fun getResponse(): String? {
        return Response
    }

    fun getfname(): String? {
        return fname
    }

    fun getvertical(): String? {
        return vertical
    }

    fun getsymbol(): String? {
        return symbol
    }

    fun getblock(): String? {
        return block
    }

    fun getcc(): String? {
        return cc
    }

    fun getprevinsu(): String? {
        return ins
    }

    fun getmake(): String? {
        return make
    }

    fun getregdate(): String? {
        return reg
    }

    fun getexpdate(): String? {
        return exp
    }

    fun getmanuf(): String? {
        return mfg
    }

    fun getreqid(): String? {
        return req
    }

    fun getvehicleid(): String? {
        return vehicle
    }

    fun getfuel(): String? {
        return fuel
    }

    fun getvariant(): String? {
        return variant
    }

    fun getmodel(): String? {
        return model
    }

}