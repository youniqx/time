package com.youniqx.time

import com.youniqx.time.gitlab.models.IterationCadencesQuery
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets
import com.youniqx.time.gitlab.models.type.WorkItemState
import kotlin.random.Random

val previewUserId by lazy { "gid://gitlab/User/123" }

val previewIterationCadences: List<IterationCadencesQuery.Node>? by lazy {
    listOf(
        IterationCadencesQuery.Node(__typename = "", title = "Awesome Team Sprint", id = "123"),
        IterationCadencesQuery.Node(__typename = "", title = "Performing Team Sprint", id = "435"),
    )
}

val previewIssues: List<BareWorkItem> by lazy {

    val titles = listOf(
        "Android | Aut aut minima quidem occaecati ea consequuntur est",
        "iOS | Et corporis veniam labore quia in ut velit qui.",
        "iOS | Laudantium quo repudiandae quam quae saepe esse voluptatum consequuntur, Qui numquam optio commodi",
        "Special offer",
        "Spike: Quos assumenda minus et consequatur officia reprehenderit, Atque quia est et: Minima aut labore nostrum",
    )

    val workItemTypeNames = listOf("Issue", "Epic", "Task")

    val labels = listOf(
        "Android" to "#a4c639",
        "customer::spectacular" to "#003da5",
        "iOS" to "#9400d3",
        "team::awesome" to "#cd5b45",
        "timetracking" to "#dc143c",
        "type::story" to "#5CB85C",
        "product::time" to "#006BB6"
    )

    val timelogs = listOf(
        Triple("2025-11-17T16:45:11+01:00", "Setting up amazing feature", 17940),
        Triple("2025-11-17T13:55:59+01:00", null, 18000),
        Triple("2025-11-14T14:17:22+01:00", "Finishing this stuff", 16200),
    )

    val users = listOf(
        "gid://gitlab/User/123",
        "gid://gitlab/User/534"
    )

    buildList {
        repeat(20) {
            add(
                BareWorkItem(
                    id = it.toString(),
                    iid = it.toString(),
                    title = titles[it % titles.size],
                    webUrl = "",
                    state = listOf(WorkItemState.OPEN, WorkItemState.CLOSED).random(),
                    workItemType = BareWorkItem.WorkItemType(
                        __typename = "",
                        id = "0",
                        name = workItemTypeNames.random()
                    ),
                    promotedToEpicUrl = listOf("https://example.com", null, null, null, null, null, null).random(),
                    widgets = listOf(
                        BareWorkItem.Widget(
                            __typename = "WorkItemWidgetLabels",
                            bareWorkItemWidgets = BareWorkItemWidgets(
                                __typename = "WorkItemWidgetLabels",
                                onWorkItemWidgetLabels = BareWorkItemWidgets.OnWorkItemWidgetLabels(
                                    labels = BareWorkItemWidgets.Labels(
                                        __typename = "",
                                        nodes = labels.shuffled().take(Random.nextInt(1, labels.size))
                                            .map { (title, color) ->
                                                BareWorkItemWidgets.Node(
                                                    __typename = "",
                                                    title = title,
                                                    id = "0",
                                                    color = color,
                                                )
                                            }
                                    )
                                ),
                                onWorkItemWidgetAssignees = BareWorkItemWidgets.OnWorkItemWidgetAssignees(
                                    assignees = null
                                ),
                                onWorkItemWidgetTimeTracking = BareWorkItemWidgets.OnWorkItemWidgetTimeTracking(
                                    timelogs = BareWorkItemWidgets.Timelogs(
                                        __typename = "",
                                        nodes = timelogs.shuffled().take(Random.nextInt(0, timelogs.size))
                                            .map { (spentAt, summary, timeSpent) ->
                                                BareWorkItemWidgets.Node2(
                                                    __typename = "",
                                                    id = "0",
                                                    spentAt = spentAt,
                                                    summary = summary,
                                                    timeSpent = timeSpent,
                                                    user = users.random().let {
                                                        BareWorkItemWidgets.User(
                                                            __typename = "",
                                                            id = it
                                                        )
                                                    }
                                                )
                                            }
                                    )
                                )
                            )
                        )
                    ),
                    __typename = "WorkItem"
                )
            )
        }
    }
}

val loremIpsum = """
    Aut aut minima quidem occaecati ea consequuntur est. Iure velit minus enim id sit explicabo nulla dolorem. Alias officiis quia et exercitationem.
    Doloribus adipisci fugit molestias illum. Quos assumenda minus et consequatur officia reprehenderit. Atque quia est et. Minima aut labore nostrum. Omnis voluptates occaecati molestias assumenda. Dolorum quia at soluta sequi vero saepe.
    Non distinctio qui placeat dolores ab voluptatum ea. Et corporis veniam labore quia in ut velit qui. Laudantium quo repudiandae quam quae saepe esse voluptatum consequuntur. Qui numquam optio commodi.
""".trimIndent()
