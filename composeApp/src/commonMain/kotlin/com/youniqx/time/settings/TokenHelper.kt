package com.youniqx.time.settings

import io.ktor.http.appendPathSegments
import io.ktor.http.buildUrl
import io.ktor.http.takeFrom

fun createTokenUrl(fromInstanceUrl: String) = buildUrl {
    takeFrom(fromInstanceUrl)
    appendPathSegments("-", "user_settings", "personal_access_tokens")
    parameters.append("name", "Time")
    parameters.append("scopes", "api")
    parameters.append(
        "description",
        "Token used by the Time app to help you track time on GitLab."
    )
}