package com.cnam.medic_assist.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import androidx.core.content.FileProvider
import com.cnam.medic_assist.datas.models.RendezVous
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CalendarHelper(private val context: Context) : ICalendarHelper {

    override fun addEventToCalendar(rdv: RendezVous) {
        try {
            // Convertir la date et l'heure en millisecondes
            val startMillis = parseDateTime("${rdv.daterdv}T${rdv.horaire}")
            val endMillis = startMillis + 60 * 60 * 1000 // Ajouter 1 heure par défaut

            // Intent pour ajouter un événement au calendrier
            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = "vnd.android.cursor.item/event"
                putExtra(CalendarContract.Events.TITLE, rdv.intitule)
                putExtra(CalendarContract.Events.EVENT_LOCATION, "${rdv.nom}, ${rdv.numero_rue} ${rdv.rue}, ${rdv.codepostal} ${rdv.ville}")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                putExtra(CalendarContract.Events.DESCRIPTION, "Ajouté via MedicAssist App")
            }

            // Vérifier qu'une application est disponible pour gérer l'Intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Aucune application d'agenda trouvée", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur lors de l'ajout à l'agenda : ${e.message}", Toast.LENGTH_SHORT).show()
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
            DTSTART:${getFormattedDateTime(parseDateTime("${rdv.daterdv}T${rdv.horaire}"))}
            DTEND:${getFormattedDateTime(parseDateTime("${rdv.daterdv}T${rdv.horaire}") + 60 * 60 * 1000)}
            SUMMARY:${rdv.intitule}
            LOCATION:${formatLocation(rdv)}
            END:VEVENT
            END:VCALENDAR
        """.trimIndent()
    }

    private fun getFormattedDateTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timeInMillis))
    }

    fun parseDateTime(dateTime: String): Long {
        // Le format attendu est "yyyy-MM-ddTHH:mm:ss"
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        dateTimeFormat.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            dateTimeFormat.parse(dateTime)?.time ?: throw ParseException("Unable to parse date-time", 0)
        } catch (e: Exception) {
            throw ParseException("Invalid date-time format: $dateTime", 0)
        }
    }

    private fun saveIcsFile(content: String): Uri? {
        return try {
            val fileName = "event_${System.currentTimeMillis()}.ics"
            val file = File(context.cacheDir, fileName)
            file.writeText(content)

            // Obtenir l'URI en utilisant FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
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

    private fun formatLocation(rdv: RendezVous): String {
        return "${rdv.nom}, ${rdv.numero_rue} ${rdv.rue}, ${rdv.codepostal} ${rdv.ville}"
    }
}
