package com.youniqx.time.presentation.workitems

import com.youniqx.time.gitlab.models.fragment.BareWorkItem

val BareWorkItem.assignees get() =
    widgets
        ?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetAssignees != null }
        ?.bareWorkItemWidgets
        ?.onWorkItemWidgetAssignees
        ?.assignees
val BareWorkItem.labels get() =
    widgets
        ?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetLabels != null }
        ?.bareWorkItemWidgets
        ?.onWorkItemWidgetLabels
        ?.labels
val BareWorkItem.timelogs get() =
    widgets
        ?.firstOrNull { it.bareWorkItemWidgets.onWorkItemWidgetTimeTracking != null }
        ?.bareWorkItemWidgets
        ?.onWorkItemWidgetTimeTracking
        ?.timelogs
        ?.nodes
        ?.filterNotNull()
        .orEmpty()
