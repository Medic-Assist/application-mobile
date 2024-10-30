package com.cnam.medic_assist.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.cnam.medic_assist.models.RendezVous
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CalendarHelper(private val context: Context) : ICalendarHelper {

    override fun addEventToCalendar(rdv: RendezVous) {
        val icsContent = generateIcsContent(rdv)
        val fileUri = saveIcsFile(icsContent)

        if (fileUri != null) {
            openIcsFile(fileUri)
        } else {
            Toast.makeText(context, "Erreur lors de la création du fichier .ics", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateIcsContent(rdv: RendezVous): String {
        return """
            BEGIN:VCALENDAR
            VERSION:2.0
            PRODID:-//MedicAssistApp//EN
            BEGIN:VEVENT
            UID:${rdv.idRDV}@medicassistapp.com
            DTSTAMP:${getFormattedDateTime(System.currentTimeMillis())}
            DTSTART:${getFormattedDateTime(parseDateTime("${rdv.dateRDV} ${rdv.horaire}"))}
            DTEND:${getFormattedDateTime(parseDateTime("${rdv.dateRDV} ${rdv.horaire}") + 60 * 60 * 1000)}
            SUMMARY:${rdv.intitule}
            LOCATION: HELLO
            END:VEVENT
            END:VCALENDAR
        """.trimIndent()
    }

    private fun getFormattedDateTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timeInMillis))
    }

    private fun parseDateTime(dateTimeString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = format.parse(dateTimeString)
        return date?.time ?: System.currentTimeMillis()
    }

    private fun saveIcsFile(content: String): Uri? {
        return try {
            val fileName = "event_${System.currentTimeMillis()}.ics"
            val file = File(context.cacheDir, fileName)
            file.writeText(content)

            // Obtenir l'URI en utilisant FileProvider
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun openIcsFile(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/calendar"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Vérifier qu'il y a une application pour gérer l'Intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(intent, "Ouvrir avec"))
        } else {
            Toast.makeText(context, "Aucune application pour ouvrir les fichiers .ics", Toast.LENGTH_SHORT).show()
        }
    }
}
