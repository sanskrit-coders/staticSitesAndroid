package sanskritCode.staticSites

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import sanskritCode.downloaderFlow.*
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
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
        val baseUrlReplacer = BaseUrlReplacer(this, archiveIndexStore!!)
        baseUrlReplacer.execute()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.i(LOGGER_TAG, "Back pressed")
        val intent = Intent(this, GetUrlActivity::class.java)
        intent.putExtra("archiveIndexStore", archiveIndexStore)
        // intent.putStringArrayListExtra();
        startActivity(intent)
    }

    fun whenAllExtracted() {
        // TODO: Transition to a browser view which lists all installed static sites.
        val intent = Intent(this, FinalActivity::class.java)
        intent.putExtra("archiveIndexStore", archiveIndexStore)
        // intent.putStringArrayListExtra();
        startActivity(intent)
    }

    fun setTopTextWhileExtracting(siteName: String) {
        val updateTextView = findViewById<TextView>(R.id.ss_replace_baseurl_textView_update)
        updateTextView.setText(siteName)
    }


}
