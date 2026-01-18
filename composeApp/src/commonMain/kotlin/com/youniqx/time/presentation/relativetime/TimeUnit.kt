package com.youniqx.time.presentation.relativetime

import io.github.skeptick.libres.strings.PluralForms
import io.github.skeptick.libres.strings.VoidPluralString

fun plurals(forms: PluralForms = PluralForms()) = VoidPluralString(forms = forms, languageCode = "en")

internal enum class TimeUnit(
    val past: () -> VoidPluralString,
    val present: () -> VoidPluralString,
    val future: () -> VoidPluralString
) {
    Seconds(
        past = { plurals() },
        present = { plurals(PluralForms(one = "second", other = "seconds")) },
        future = { plurals() }
    ),
    Minutes(
        past = { plurals() },
        present = { plurals(PluralForms(one = "minute", other = "minutes")) },
        future = { plurals() }
    ),
    Hours(
        past = { plurals() },
        present = { plurals(PluralForms(one = "hour", other = "hours")) },
        future = { plurals() }
    ),
    Days(
        past = { plurals() },
        present = { plurals(PluralForms(one = "day", other = "days")) },
        future = { plurals() }
    ),
    Weeks(
        past = { plurals() },
        present = { plurals(PluralForms(one = "week", other = "weeks")) },
        future = { plurals() }
    ),
    Months(
        past = { plurals() },
        present = { plurals(PluralForms(one = "month", other = "months")) },
        future = { plurals() }
    ),
    Years(
        past = { plurals() },
        present = { plurals(PluralForms(one = "year", other = "years")) },
        future = { plurals() }
    );

    fun format(value: Int, relativeTime: RelativeTime): String {
        return when (relativeTime) {
            RelativeTime.Past -> past().optionallyFormat(value) ?: present().format(value)
            RelativeTime.Present -> present().format(value)
            RelativeTime.Future -> future().optionallyFormat(value) ?: present().format(value)
        }
    }
}

/**
 * @return `null` if the given resource doesn't exist in the current locale.
 */
internal fun VoidPluralString.optionallyFormat(number: Int): String? {
    return try {
        format(number).takeIf { it.isNotEmpty() }
    } catch (e: IllegalStateException) {
        return null
    }
}
