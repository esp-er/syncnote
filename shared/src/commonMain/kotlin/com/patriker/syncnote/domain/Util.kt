package com.patriker.syncnote.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.periodUntil
import kotlinx.datetime.TimeZone

object Util {
    fun timeAgoString(past: Instant, now: Instant): String {
        val timeAgo = past.periodUntil(now, TimeZone.UTC)
        if(timeAgo.seconds < 0) return ""

        val weeksAgo = if(timeAgo.days >= 7) timeAgo.days / 7 else 0

        return when{
            timeAgo.years == 1   -> "${timeAgo.years} yr"
            timeAgo.years > 1    -> "${timeAgo.years} yrs"
            timeAgo.months >= 1  -> "${timeAgo.months} mo"
            timeAgo.days >= 14   -> "$weeksAgo w"
            timeAgo.days >= 1    -> "${timeAgo.days} d"
            timeAgo.hours == 1   -> "${timeAgo.hours} hr"
            timeAgo.hours > 1    -> "${timeAgo.hours} hrs"
            timeAgo.minutes >= 1 -> "${timeAgo.minutes} min"
            else -> "now" //less than 1 min should be caught
        }

    }
}