package com.youniqx.time

import android.app.Application
import com.youniqx.time.di.AndroidAppGraph
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class Application :
    Application(),
    MetroApplication {
    override val appComponentProviders: MetroAppComponentProviders by lazy { createGraph<AndroidAppGraph>() }
}
