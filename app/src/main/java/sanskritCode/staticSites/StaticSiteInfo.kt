package sanskritCode.staticSites

import sanskritCode.downloaderFlow.ArchiveInfo
import java.io.File
import java.io.Serializable

/*
Expected data -
  {
    "url": "https://github.com/vvasuki/saMskAra/archive/offline_android.zip",
    "baseUrlStringsToReplace": ["/storage/emulated/0/notesData/saMskAra", "\/storage\/emulated\/0\/notesData\/saMskAra"],
    "replacementBaseUrlSuffix": "vvasuki.github.io/saMskAra/saMskAra-offline_android/",
    "startPage": "vvasuki.github.io/saMskAra/saMskAra-offline_android/index.html",
    "destinationPathSuffix": "vvasuki.github.io/saMskAra/"
  }
 */
// jsonStrIn argument below cannot be replaced with 'override var jsonStr'. Kotlin goes bonkers.
class StaticSiteInfo (jsonStrIn: String) : ArchiveInfo(jsonStrIn) {
    fun getExtractionDirectory(destDirBase: File) = File(destDirBase, getJsonObject().get("destinationPathSuffix").asString)
    fun getReplacementBaseUrlMinusProtocol(destDirBase: File) =
            File(getExtractionDirectory(destDirBase),
                    getJsonObject().get("replacementBaseUrlSuffix").asString).toString()
    fun getStartPageUrl(destDirBase: File) =
            File(getExtractionDirectory(destDirBase),
                    getJsonObject().get("startPage").asString).toURI().toURL()
    companion object {
        fun fromArchiveInfo(archiveInfo: ArchiveInfo) = StaticSiteInfo(archiveInfo.jsonStr)
    }
}