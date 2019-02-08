package sanskritCode.staticSites

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView

import java.io.File
import java.io.IOException

abstract class BaseActivity : AppCompatActivity() {

    protected val version: String
        get() {
            val LOGGER_TAG = localClassName
            try {
                val pInfo    = this.packageManager.getPackageInfo(packageName, 0)
                return pInfo.versionName
            } catch (e: NameNotFoundException) {
                Log.w(LOGGER_TAG, "getVersion:" + "Version getting failed.")
                e.printStackTrace()
                return "Version getting failed."
            }

        }

    fun getPermission(permissionString: String, activity: Activity) {
        val LOGGER_TAG = localClassName
        if (ContextCompat.checkSelfPermission(activity, permissionString) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOGGER_TAG, "getPermission: Got permission: $permissionString")
        } else {
            Log.w(LOGGER_TAG, "getPermission: Don't have permission: $permissionString")
            ActivityCompat.requestPermissions(activity, arrayOf(permissionString), 101)
            Log.i(LOGGER_TAG, "getPermission: new permission: " + ContextCompat.checkSelfPermission(activity, permissionString))
        }
    }

    fun sendLoagcatMail() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS) == PackageManager.PERMISSION_GRANTED) {
            Log.d(this.localClassName, "SendLoagcatMail: " + "Got READ_LOGS permissions")
        } else {
            Log.e(this.localClassName, "SendLoagcatMail: " + "Don't have READ_LOGS permissions")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_LOGS), 103)
            Log.i(this.localClassName, "SendLoagcatMail: " + "new READ_LOGS permission: " + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS))
        }
        // save logcat in file
        val outputFile = File(Environment.getExternalStorageDirectory().absolutePath,
                "logcat.txt")
        Log.i(this.localClassName, "SendLoagcatMail: " + "logcat file is " + outputFile.absolutePath)


        var deviceDetails = "Device details:"
        deviceDetails += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")"
        deviceDetails += "\n OS API Level: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.VERSION.SDK_INT + ")"
        deviceDetails += "\n Device: " + android.os.Build.DEVICE
        deviceDetails += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")"
        Log.i(this.localClassName, "SendLoagcatMail: deviceDetails: $deviceDetails")

        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        Log.i(this.localClassName, "SendLoagcatMail: App version: $versionName with id $versionCode")

        try {
            Runtime.getRuntime().exec(
                    "logcat -f " + outputFile.absolutePath)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            Log.e(this.localClassName, "SendLoagcatMail: " + "Alas error! ", e)
        }

        //send file using email
        val emailIntent = Intent(Intent.ACTION_SEND)
        // Set type to "email"
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("vishvas.vasuki+STARDICTAPP@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outputFile))
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Stardict Updater App Failure report.")
        this.startActivity(Intent.createChooser(emailIntent, "Email failure report to maker?..."))
    }

    companion object {


        fun largeLog(tag: String, content: String) {
            if (content.length > 4000) {
                Log.v(tag, tag + ":" + content.substring(0, 4000))
                largeLog(tag, content.substring(4000))
            } else {
                Log.v(tag, "$tag:$content")
            }
        }
    }

}
