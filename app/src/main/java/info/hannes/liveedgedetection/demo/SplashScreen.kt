package info.hannes.liveedgedetection.demo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            val intent = Intent(this@SplashScreen, StartActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DISPLAY_TIME.toLong())
    }

    companion object {
        private const val SPLASH_DISPLAY_TIME = 1000
    }
}