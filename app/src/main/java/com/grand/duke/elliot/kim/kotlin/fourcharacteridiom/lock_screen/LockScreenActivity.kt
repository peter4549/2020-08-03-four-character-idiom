package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.lock_screen

import android.Manifest
import android.app.KeyguardManager
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.R
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import timber.log.Timber


class LockScreenActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true) // check. true or false.
            setTurnScreenOn(true)
            //Toast.makeText(this, "LLLLLL", Toast.LENGTH_SHORT).show()
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            // TODO. low version test..
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val gifImage = findViewById<GifImageView>(R.id.gifImage)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        val s = getLockScreenWallpaper(this, gifImage)
        Toast.makeText(this, s.toString(), Toast.LENGTH_SHORT).show()
        //constraintLayout.background = s
        gifImage.setImageDrawable(s)// 이래도 안돔. 코드 정제할것.

        findViewById<Button>(R.id.button).setOnClickListener {
            finish()
        }
    }

    private fun getLockScreenWallpaper(context: Context, gif: GifImageView): Drawable? {
        val wallpaperManager = WallpaperManager.getInstance(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (permission == PackageManager.PERMISSION_GRANTED) {
                val parcelFileDescriptor = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_LOCK)
                parcelFileDescriptor?.let {

                    val gifFromFd = GifDrawable(it.fileDescriptor)
                    gif.background = gifFromFd
                    Toast.makeText(context, "QQPQPQPQQQPQQ" + gifFromFd, Toast.LENGTH_SHORT).show()

                    val bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.fileDescriptor)

                    try {
                        parcelFileDescriptor.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    return BitmapDrawable(resources, bitmap)
                }
            } else {
                Timber.e("Permission denied: READ_EXTERNAL_STORAGE")
                return null
            }
        }

        return wallpaperManager.drawable
    }
}