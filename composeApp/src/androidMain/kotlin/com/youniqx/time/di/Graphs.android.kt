@file:Suppress("ktlint:standard:filename")

package com.youniqx.time.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders

@DependencyGraph(AppScope::class)
interface AndroidAppGraph :
    AppGraph,
    MetroAppComponentProviders {
    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides context: Context,
        ): AndroidAppGraph
    }
}
