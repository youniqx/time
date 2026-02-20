@file:Suppress("ktlint:standard:filename")

package com.youniqx.time.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface WasmAppGraph : AppGraph
