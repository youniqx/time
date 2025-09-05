package com.youniqx.time

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform