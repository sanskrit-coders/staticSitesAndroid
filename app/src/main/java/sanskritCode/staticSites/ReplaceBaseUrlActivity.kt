package sanskritCode.staticSites

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import sanskritCode.downloaderFlow.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Predicate

class ReplaceBaseUrlActivity : BaseActivity() {
    internal val LOGGER_TAG = javaClass.getSimpleName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOGGER_TAG, "onCreate:" + "************************STARTS****************************")
        if (archiveIndexStore == null) {
            archiveIndexStore = intent.getSerializableExtra("archiveIndexStore") as ArchiveIndexStore
        }
        setContentView(R.layout.activity_replace_base_url)
        val sdcard = Environment.getExternalStorageDirectory()
        val destDir = File(sdcard.absolutePath, getString(sanskritCode.downloaderFlow.R.string.df_destination_sdcard_directory))
        for (archiveInfo in archiveIndexStore!!.archivesSelectedMap.values) {
            if (archiveInfo.status == ArchiveStatus.EXTRACTION_SUCCESS) {
                val extractionDir = File(destDir, archiveInfo.getDownloadedArchiveBasename())
                // TODO : For each js, html and css file, replace baseURL with destDir.toURI.
            }
        }
        // TODO: Transition to a browser view which lists all installed static sites.
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.i(LOGGER_TAG, "Back pressed")
        val intent = Intent(this, GetUrlActivity::class.java)
        intent.putExtra("archiveIndexStore", archiveIndexStore)
        // intent.putStringArrayListExtra();
        startActivity(intent)
    }


}
