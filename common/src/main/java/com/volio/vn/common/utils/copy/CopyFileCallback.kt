package com.volio.vn.common.utils.copy

import java.io.File

interface CopyFileCallback {
    fun startCopyFile(file: File){}
    fun cancelCopyFile(listFileSuccess: List<File>, listFileNonSuccess: List<File>){}
    fun updateProcessFileCopyCurrent(bytesCopied: Long, bytesFile: Long){}
    fun updateProcessTotalSizeListFileCopy(totalSizeCopied: Int, totalSizeFile: Int){}
    fun copyListFileSuccess(listFileOutput: List<File>){}
    fun copyFileCurrentSuccess(file: File){}
}