package com.volio.vn.callscreen.helpers

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.provider.MediaStore
import android.util.Log
import com.volio.vn.callscreen.extensions.PERMISSION_READ_CONTACTS
import com.volio.vn.callscreen.extensions.getStringValue
import com.volio.vn.callscreen.extensions.hasPermission
import java.io.File


private const val TAG = "CallRingtoneManager"

fun setPhoto(context: Context, phoneNumber: String, absolutePathAudio: String){
    val lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, phoneNumber)

    val projection = arrayOf<String>(
        ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
    )

    val data: Cursor = context.contentResolver.query(lookupUri, projection, null, null, null)!!
    data.moveToFirst()
    try {
        // Get the contact lookup Uri
        val contactId = data.getLong(0)
        val lookupKey = data.getString(1)
        val contactUri: Uri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)
            ?: // Invalid arguments
            return

        val values = ContentValues(1)
        values.put(
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            getRingtoneUriFromPath(context, absolutePathAudio)
        )
        context.contentResolver.update(contactUri, values, null, null)
    } catch (ex: Exception) {
        Log.e(TAG, "setRingtone: Failed ${ex.toString()}")
    } finally {
        // Don't forget to close your Cursor
        data.close()
    }
}

fun setRingtone(context: Context, phoneNumber: String, absolutePathAudio: String) {


    val lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, phoneNumber)

    val projection = arrayOf<String>(
        ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
    )

    val data: Cursor = context.contentResolver.query(lookupUri, projection, null, null, null)!!
    data.moveToFirst()
    try {
        // Get the contact lookup Uri
        val contactId = data.getLong(0)
        val lookupKey = data.getString(1)
        val contactUri: Uri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)
            ?: // Invalid arguments
            return

        // Get the path of ringtone you'd like to use
        /*   val storage = Environment.getExternalStorageDirectory().path
           val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
           val file = File(path, "ringtone.mp3")
           val value = Uri.fromFile(file).toString()*/

        // file.absolutePath
        // Apply the custom ringtone
        getPhotoUriFromPhoneNumber(context,phoneNumber)
        val values = ContentValues(1)
        values.put(
            ContactsContract.Contacts.CUSTOM_RINGTONE,
            getRingtoneUriFromPath(context, absolutePathAudio)
        )
        context.contentResolver.update(contactUri, values, null, null)
    } catch (ex: Exception) {
        Log.e(TAG, "setRingtone: Failed ${ex.toString()}")
    } finally {
        // Don't forget to close your Cursor
        data.close()
    }

}

fun getPhotoUriFromPhoneNumber(context: Context, number: String): String {
    if (!context.hasPermission(PERMISSION_READ_CONTACTS)) {
        return ""
    }

    val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
    val projection = arrayOf(
        PhoneLookup.CUSTOM_RINGTONE
    )
    val storage = Environment.getExternalStorageDirectory().path
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
    val file = File(path, "ringtone.mp3")
    val value = Uri.fromFile(file)

    val pathMedia = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
    val media = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    val item = path.listFiles().map { it.path }
    Log.e(TAG, "all item in path  ${getRingtonePathFromContentUri(context, value)}")
    Log.e(
        TAG,
        "all item in path absolutePath  ${getRingtoneUriFromPath(context, file.absolutePath)}"
    )

    Log.e(TAG, "all item in pathMedia  ${pathMedia}")
    Log.e(TAG, "all item in media new version  ${media}")
    Log.e(
        TAG,
        "all item in default  ${RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)}"
    )

    try {
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor.use {
            if (cursor?.moveToFirst() == true) {
                return cursor.getStringValue(PhoneLookup.CUSTOM_RINGTONE) ?: ""
            }
        }
    } catch (ignored: Exception) {
    }

    return ""
}

fun getRingtonePathFromContentUri(
    context: Context,
    contentUri: Uri
): String? {
    val proj = arrayOf(MediaStore.Audio.Media.DATA)
    val ringtoneCursor = context.contentResolver.query(
        contentUri,
        proj, null, null, null
    )
    ringtoneCursor?.moveToFirst()
    val path = ringtoneCursor?.getString(
        ringtoneCursor
            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
    )
    ringtoneCursor?.close()
    return path
}

@SuppressLint("Range")
private fun getRingtoneUriFromPath(context: Context, path: String): String? {
    val ringtonesUri = MediaStore.Audio.Media.getContentUriForPath(path)
    val ringtoneCursor = context.contentResolver.query(
        ringtonesUri!!, null,
        MediaStore.Audio.Media.DATA + "='" + path + "'", null, null
    )
    ringtoneCursor!!.moveToFirst()
    val id = ringtoneCursor.getLong(
        ringtoneCursor
            .getColumnIndex(MediaStore.Audio.Media._ID)
    )
    ringtoneCursor.close()
    return if (!ringtonesUri.toString().endsWith(id.toString())) {
        "$ringtonesUri/$id"
    } else ringtonesUri.toString()
}

fun adjustAudio(context: Context, setMute: Boolean) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val adJustMute = if (setMute) {
            AudioManager.ADJUST_MUTE
        } else {
            AudioManager.ADJUST_UNMUTE
        }
        //audioManager!!.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, adJustMute, 0)
        // audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, adJustMute, 0)
        //   audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, adJustMute, 0)
        audioManager!!.adjustStreamVolume(AudioManager.STREAM_RING, adJustMute, 0)
        // audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, adJustMute, 0)
    } else {
        // audioManager!!.setStreamMute(AudioManager.STREAM_NOTIFICATION, setMute)
        // audioManager.setStreamMute(AudioManager.STREAM_ALARM, setMute)
        // audioManager.setStreamMute(AudioManager.STREAM_MUSIC, setMute)
        audioManager!!.setStreamMute(AudioManager.STREAM_RING, setMute)
        // audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, setMute)
    }
}



