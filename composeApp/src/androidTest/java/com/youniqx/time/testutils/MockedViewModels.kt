package com.youniqx.time.testutils

import androidx.lifecycle.ViewModel
import com.youniqx.time.di.AppGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import io.mockk.MockK
import io.mockk.MockKGateway
import kotlin.reflect.KClass

class CachedViewModelFactory :
    MetroViewModelFactory(),
    Map<KClass<out ViewModel>, ViewModel> by emptyMap() {
    private val cache = mutableMapOf<KClass<out ViewModel>, ViewModel>()

    override fun get(key: KClass<out ViewModel>) =
        (
            cache[key] ?: MockK.useImpl {
                MockKGateway.implementation().mockFactory.mockk(
                    mockType = key,
                    name = null,
                    relaxed = true,
                    moreInterfaces = emptyArray(),
                    relaxUnitFun = true,
                )
            }
        ).also {
            cache[key] = it
        }

    override val viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel> =
        object : Map<KClass<out ViewModel>, () -> ViewModel> by emptyMap() {
            override fun get(key: KClass<out ViewModel>): (() -> ViewModel) =
                {
                    this@CachedViewModelFactory[key]
                }
        }
}

inline fun <reified T : ViewModel> AppGraph.vm(): T = (metroViewModelFactory as CachedViewModelFactory)[T::class] as T

@BindingContainer
class ViewModelBindings {
    @Provides
    @SingleIn(AppScope::class)
    fun providesViewModelFactory(): MetroViewModelFactory = CachedViewModelFactory()
}
