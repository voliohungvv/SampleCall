package com.volio.vn.common.utils.copy

import android.app.Application
import android.media.MediaScannerConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object CopyFile {

    private var isCancelCopy = false
    private var isDeleteWhenCopySuccess = true
    private var application: Application? = null

    private val callBackCopy = mutableListOf<CopyFileCallback>()

    fun init(activity: Application) {
        application = activity
    }

    fun addCallback(callback: CopyFileCallback) {
        callBackCopy.add(callback)
    }

    fun removeCallback(callback: CopyFileCallback) {
        callBackCopy.remove(callback)
    }

//    fun createFileHide(fileInput: File, nameFolderHide: String): File {
//        val folder = File(activityCopy?.filesDir, "$nameFolderHide")
//        if (!folder.exists()) {
//            folder.mkdirs()
//        }
//
//        return File(activityCopy?.filesDir, "$nameFolderHide/${fileInput.name}")
//    }

    suspend fun copyListFile(
        listFileInput: List<File>, folderFileOutput: File
    ) = withContext(Dispatchers.IO) {
        var index = 0
        isCancelCopy = false

        val listFileOutputSuccess = mutableListOf<File>()
        val listFileOriginSuccess = mutableListOf<File>()

        while (!isCancelCopy && index < listFileInput.size) {

            val outputFile = File(folderFileOutput, listFileInput[index].name)
            copyFile(listFileInput[index], outputFile) {
                it?.let {
                    listFileOutputSuccess.add(it)
                    listFileOriginSuccess.add(listFileInput[index])

                    callBackCopy.forEach { callBack ->
                        callBack.updateProcessTotalSizeListFileCopy(
                            listFileOutputSuccess.size, listFileInput.size
                        )
                    }
                }
            }
            index++
        }

        if (isCancelCopy) {
            val listNonCopy = listFileInput.filter { !listFileOriginSuccess.contains(it) }

            callBackCopy.forEach {
                it.cancelCopyFile(listFileOutputSuccess, listNonCopy)
            }
        } else {
            callBackCopy.forEach {
                it.copyListFileSuccess(listFileOutputSuccess)
            }
        }
    }

    private fun copyFile(
        fileInput: File, fileOutput: File, onCallback: (file: File?) -> Unit = {}
    ) {
        var bytesCopied: Long = 0

        callBackCopy.forEach {
            it.startCopyFile(fileInput)
        }

        fileInput.inputStream().use { input ->
            fileOutput.outputStream().use { output ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                val totalBytes = input.read(buffer)
                var bytes = input.read(buffer)
                while (bytes >= 0 && !isCancelCopy) {

                    output.write(buffer, 0, bytes)
                    bytesCopied += bytes
                    bytes = input.read(buffer)

                    callBackCopy.forEach { callBack ->
                        callBack.updateProcessFileCopyCurrent(bytesCopied, totalBytes.toLong())
                    }
                }
            }
        }

        if (isCancelCopy) {
            fileOutput.delete()
            onCallback(null)
        } else {
            if (isDeleteWhenCopySuccess) {
                fileInput.delete()
            }
            callBackCopy.forEach {
                it.copyFileCurrentSuccess(fileOutput)
            }
            MediaScannerConnection.scanFile(application, arrayOf(fileOutput.path), null, null)
            onCallback(fileOutput)
        }
    }

    fun cancelCopy() {
        isCancelCopy = true
    }

    private fun removeFile(listFile: List<File>) {
        listFile.forEach {
            it.delete()
        }
    }

    private fun removeFile(file: File) {
        file.delete()
    }

}
