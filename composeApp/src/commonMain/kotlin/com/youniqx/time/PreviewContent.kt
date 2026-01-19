package com.youniqx.time

import com.youniqx.time.gitlab.models.NamespaceQuery
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.fragment.BareWorkItemWidgets
import com.youniqx.time.gitlab.models.fragment.GroupWithIterationCadences
import com.youniqx.time.gitlab.models.type.WorkItemState
import com.youniqx.time.presentation.Label
import kotlin.random.Random

val previewUserId by lazy { "gid://gitlab/User/123" }

val previewNamespaces: NamespaceQuery.Data by lazy {

    val groups = listOf(
        GroupWithIterationCadences(
            __typename = "",
            id = "834",
            name = "Lovely Group",
            archived = false,
            fullPath = "lovely-group",
            iterationCadences = GroupWithIterationCadences.IterationCadences(
                __typename = "",
                nodes = listOf(
                    GroupWithIterationCadences.Node(__typename = "", title = "Awesome Team Sprint", id = "123"),
                    GroupWithIterationCadences.Node(__typename = "", title = "Performing Team Sprint", id = "435"),
                )
            )
        ),
        GroupWithIterationCadences(
            __typename = "",
            id = "368",
            name = "Archived Group",
            archived = true,
            fullPath = "archived-group",
            iterationCadences = GroupWithIterationCadences.IterationCadences(
                __typename = "",
                nodes = listOf()
            )
        ),
        GroupWithIterationCadences(
            __typename = "",
            id = "109",
            name = "Fancy Group",
            archived = false,
            fullPath = "lovely-group/fancy-group",
            iterationCadences = GroupWithIterationCadences.IterationCadences(
                __typename = "",
                nodes = listOf()
            )
        ),
        GroupWithIterationCadences(
            __typename = "",
            id = "273",
            name = "Obscure Group",
            archived = false,
            fullPath = "obscure-group",
            iterationCadences = GroupWithIterationCadences.IterationCadences(
                __typename = "",
                nodes = listOf()
            )
        ),
        GroupWithIterationCadences(
            __typename = "",
            id = "194",
            name = "Hidden Group",
            archived = false,
            fullPath = "hidden-group",
            iterationCadences = GroupWithIterationCadences.IterationCadences(
                __typename = "",
                nodes = listOf()
            )
        )
    )
    NamespaceQuery.Data(
        currentUser = NamespaceQuery.CurrentUser(
            __typename = "",
            namespace = NamespaceQuery.Namespace(
                __typename = "",
                id = "gid://gitlab/Namespaces::UserNamespace/832",
                name = "Diar User",
                fullPath = "diar"
            )
        ),
        frecentGroups = groups.shuffled().take(5).map {
            NamespaceQuery.FrecentGroup(
                __typename = "",
                groupWithIterationCadences = it
            )
        },
        groups = NamespaceQuery.Groups(
            __typename = "",
            nodes = groups.map {
                NamespaceQuery.Node(
                    __typename = "",
                    groupWithIterationCadences = it
                )
            },
            pageInfo = NamespaceQuery.PageInfo(
                __typename = "",
                hasPreviousPage = true,
                hasNextPage = true,
            )
        )
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
                                                Label(
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
