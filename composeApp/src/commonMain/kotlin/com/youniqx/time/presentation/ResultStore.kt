/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youniqx.time.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.savedstate.compose.serialization.serializers.SnapshotStateMapSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Local for storing results in a [ResultStore]
 */
object LocalResultStore {
    private val LocalResultStore: ProvidableCompositionLocal<ResultStore?> =
        compositionLocalOf { null }

    /**
     * The current [ResultStore]
     */
    val current: ResultStore
        @Composable
        get() = LocalResultStore.current ?: error("No ResultStore has been provided")

    /**
     * Provides a [ResultStore] to the composition
     */
    infix fun provides(
        store: ResultStore
    ): ProvidedValue<ResultStore?> {
        return LocalResultStore.provides(store)
    }
}

interface ResultStoreValue

private class ResultStoreSerializer(elementSerializer: KSerializer<ResultStoreValue>) :
    KSerializer<ResultStore> {
    val keySerializer: KSerializer<String> = String.serializer()
    private val delegate =
        SnapshotStateMapSerializer(keySerializer = keySerializer, valueSerializer = elementSerializer)

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ResultStore", delegate.descriptor)

    override fun serialize(encoder: Encoder, value: ResultStore) {
        encoder.encodeSerializableValue(serializer = delegate, value = value.resultStateMap)
    }

    override fun deserialize(decoder: Decoder): ResultStore {
        return ResultStore().apply {
            resultStateMap.putAll(decoder.decodeSerializableValue(deserializer = delegate))
        }
    }
}

/**
 * Provides a [ResultStore] that will be remembered across configuration changes.
 */
@Composable
fun rememberResultStore(configuration: SavedStateConfiguration) : ResultStore {
    require(configuration.serializersModule != SavedStateConfiguration.DEFAULT.serializersModule) {
        "You must pass a `SavedStateConfiguration.serializersModule` configured to handle " +
                "`ResultStoreValue` open polymorphism. Define it with: `polymorphic(ResultStoreValue::class) { ... }`"
    }
    return rememberSerializable(
        configuration = configuration,
        serializer = ResultStoreSerializer(PolymorphicSerializer(ResultStoreValue::class)),
    ) {
        ResultStore()
    }
}

/**
 * A store for passing results between multiple sets of screens.
 *
 * It provides a solution for state based results.
 */
class ResultStore {

    /**
     * Map from the result key to a mutable state of the result.
     */
    val resultStateMap: SnapshotStateMap<String, ResultStoreValue> = mutableStateMapOf()

    /**
     * Retrieves the current result of the given resultKey.
     */
    inline fun <reified T : ResultStoreValue?> getResultState(resultKey: String = T::class.toString()): T =
        resultStateMap[resultKey] as T

    /**
     * Sets the result for the given resultKey.
     */
    inline fun <reified T : ResultStoreValue> setResult(resultKey: String = T::class.toString(), result: T) {
        resultStateMap[resultKey] = result
    }

    /**
     * Removes all results associated with the given key from the store.
     */
    inline fun <reified T : ResultStoreValue> removeResult(resultKey: String = T::class.toString()) {
        resultStateMap.remove(resultKey)
    }
}
