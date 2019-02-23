package sanskritCode.staticSites

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.google.gson.JsonParser
import sanskritCode.downloaderFlow.ArchiveIndexStore
import sanskritCode.downloaderFlow.ArchiveStatus
import java.io.File
import java.nio.charset.StandardCharsets


/**
 * Extracts all selected archives sequentially in ONE Async task (doBackground() being run outside the UI thread).
 */
internal class BaseUrlReplacer(@SuppressLint("StaticFieldLeak") private val activity:ReplaceBaseUrlActivity,
                               private val archiveIndexStore: ArchiveIndexStore): AsyncTask<Void, String, Void?> /* params, progress, result */() {
    private val LOGGER_TAG=javaClass.getSimpleName()

    override fun onPostExecute(result: Void?) {
        activity.whenAllExtracted()
    }

    override fun onProgressUpdate(vararg values: String) {
        super.onProgressUpdate(*values)
//        activity.updateProgressBar(Integer.parseInt(values[0]), Integer.parseInt(values[1]))
        activity.setTopTextWhileExtracting(values[0])
    }

    fun walkAndReplace(root: File, oldValue: String, newValue: String) {
        val list = root.listFiles()
        for (f in list) {
            if (f.isDirectory) {
                Log.d(LOGGER_TAG, "Dir: " + f.absoluteFile)
                walkAndReplace(f, oldValue, newValue)
            } else {
                Log.d(LOGGER_TAG, "File: ${f.absoluteFile} - Replacing ${oldValue} with ${newValue}")
                if (f.extension in listOf<String>("js", "html", "css")) {
                    val charset = StandardCharsets.UTF_8
                    var content = String(f.readBytes(), charset)
                    content = content.replace(oldValue, newValue)
                    f.writeText(content)
                }
            }
        }
    }

    override fun doInBackground(vararg params: Void): Void? {
        val sdcard = Environment.getExternalStorageDirectory()
        val destDir = File(sdcard.absolutePath, activity.getString(sanskritCode.downloaderFlow.R.string.df_destination_sdcard_directory))
        for (archiveInfo in archiveIndexStore.archivesSelectedMap.values) {
            if (archiveInfo.status == ArchiveStatus.EXTRACTION_SUCCESS) {
                val extractionDirOriginal = File(destDir, archiveInfo.getDownloadedArchiveBasename())
                // TODO : For each js, html and css file, replace baseURL with destDir.toURI.
                val baseURL = JsonParser().parse(archiveInfo.archiveInfoJsonStr).asJsonObject.get("baseUrl").asString
                val extractionDir = File(destDir, baseURL.replace(regex = Regex("^https?://"), replacement = "/"))
                Log.i(LOGGER_TAG, "Renaming ${extractionDirOriginal} to ${extractionDir}")
                extractionDirOriginal.renameTo(extractionDir)
                walkAndReplace(extractionDir, baseURL, extractionDir.toURI().toString())
                publishProgress(archiveInfo.getDownloadedArchiveBasename())
            }
        }
        return null
    }
}