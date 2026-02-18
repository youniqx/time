package com.youniqx.time.domain.models

sealed interface NamespaceEntry {

    fun isOfSameType(other: NamespaceEntry): Boolean

    data class FrecentGroup(
        override val name: String?,
        override val fullPath: String,
        override val iterationCadences: List<IterationCadenceMarker>?
    ) : Namespace, NamespaceEntry {
        override fun isOfSameType(other: NamespaceEntry) = other is FrecentGroup
    }

    data class Group(
        override val name: String?,
        override val fullPath: String,
        override val iterationCadences: List<IterationCadenceMarker>?
    ) : Namespace, NamespaceEntry {
        override fun isOfSameType(other: NamespaceEntry) = other is Group
    }

    data class User(
        override val name: String?,
        override val fullPath: String,
        override val iterationCadences: List<IterationCadenceMarker>?
    ) : Namespace, NamespaceEntry {
        override fun isOfSameType(other: NamespaceEntry) = other is User
    }

    data class SelectedSearch(
        override val name: String?,
        override val fullPath: String,
        override val iterationCadences: List<IterationCadenceMarker>?
    ) : Namespace, NamespaceEntry {
        override fun isOfSameType(other: NamespaceEntry) = other is SelectedSearch
    }

    object Separator : NamespaceEntry {
        override fun isOfSameType(other: NamespaceEntry) = other is Separator
    }
}