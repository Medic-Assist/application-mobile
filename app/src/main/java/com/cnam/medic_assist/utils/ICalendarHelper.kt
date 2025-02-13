package com.cnam.medic_assist.utils

import com.cnam.medic_assist.datas.models.RendezVous

interface ICalendarHelper {
    fun addEventToCalendar(rdv: RendezVous)
}
