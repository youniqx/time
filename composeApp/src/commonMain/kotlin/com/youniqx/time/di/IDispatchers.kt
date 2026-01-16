package com.youniqx.time.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers as KDispatchers

@Suppress("PropertyName", "VariableNaming")
interface IDispatchers {
    val Default: CoroutineDispatcher
    val Main: CoroutineDispatcher
    val Unconfined: CoroutineDispatcher
    val IO: CoroutineDispatcher
}

expect val IoDispatcher: CoroutineDispatcher

@Suppress("InjectDispatcher", "ktlint:standard:property-naming")
object Dispatchers : IDispatchers {
    override val Default = KDispatchers.Default
    override val Main = KDispatchers.Main
    override val Unconfined = KDispatchers.Unconfined
    override val IO = IoDispatcher
}
