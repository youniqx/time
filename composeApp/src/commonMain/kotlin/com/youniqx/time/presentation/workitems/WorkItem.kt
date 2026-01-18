package com.youniqx.time.presentation.workitems

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.youniqx.time.AddedTextVisualTransformation
import com.youniqx.time.AdditionalActions
import com.youniqx.time.components.SimpleTooltip
import com.youniqx.time.domain.models.OpenTracking
import com.youniqx.time.domain.models.toTimelog
import com.youniqx.time.gitlab.models.fragment.BareWorkItem
import com.youniqx.time.gitlab.models.type.WorkItemState
import com.youniqx.time.labels
import com.youniqx.time.modifier.adaptivePadding
import com.youniqx.time.modifier.changeFocusOnTab
import com.youniqx.time.modifier.onCtrlOrMetaEnter
import com.youniqx.time.presentation.opentracking.RepresentingIndicator
import com.youniqx.time.presentation.opentracking.customTimeSpentHasErrorMessage
import com.youniqx.time.presentation.opentracking.representingColors
import com.youniqx.time.refresh
import com.youniqx.time.theme.LocalSpacing
import com.youniqx.time.timelogs
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
operator fun BareWorkItem.invoke(
    startTracking: () -> Unit,
    currentUserId: String?,
    showLabelsByDefault: Boolean,
    useLabelColors: Boolean,
    openTracking: OpenTracking?,
    onOpenTrackingChange: (openTracking: OpenTracking?) -> Unit,
    pinned: Boolean,
    togglePinned: () -> Unit,
    disableGlobalSearchIfFocused: Modifier.() -> Modifier,
    commitTimeTrackingEnabled: Boolean,
    commitTimeTracking: () -> Unit,
    modifier: Modifier = Modifier,
    additionalContent: (@Composable () -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val open = openTracking?.workItemId == id
    var showTimelogs by remember { mutableStateOf(false) }
    val openTrackingAsTimelog = remember(
        openTracking, currentUserId, open, refresh(every = 1.seconds)
    ) { openTracking.takeIf { open }?.toTimelog(currentUserId = currentUserId.orEmpty()) }
    val allTimelogs = listOfNotNull(openTrackingAsTimelog) + timelogs
    val myTimelogs = allTimelogs.filter { it.user.id == currentUserId }
    val myTotalTime = myTimelogs.fold(0) { acc, timelog -> acc + timelog.timeSpent }
    val totalMinutes = myTotalTime / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val myTotalTimeString = "$hours:${minutes.toString().padStart(length = 2, padChar = '0')}"
    val spacing = LocalSpacing.current

    SwipeableWorkItemCard(
        modifier = modifier
            .padding(vertical = 4.dp)
            .adaptivePadding(minWidth = 500.dp, horizontalPadding = 40.dp)
            .padding(horizontal = 12.dp),
        isPinned = pinned,
        isTracking = open,
        onStartTracking = startTracking,
        onTogglePin = togglePinned
    ) {
        Surface(
            modifier = Modifier
                .then(
                    if (open) Modifier.border(
                        width = 2.dp,
                        color = openTracking.representingColors.color,
                        shape = RoundedCornerShape(spacing.cardRadius)
                    ) else Modifier
                )
                .clip(RoundedCornerShape(spacing.cardRadius))
                .clickable(
                    enabled = !open,
                    onClickLabel = "Work on work item",
                    role = Role.Switch
                ) {
                    startTracking()
                },
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(spacing.cardRadius),
            shadowElevation = if (open) 4.dp else 0.dp
        ) {
            Column(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .padding(spacing.cardPadding)
            ) {
                val labels = if (showLabelsByDefault) this@invoke.labels?.nodes else null
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = if (labels.isNullOrEmpty()) 48.dp else 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkItemTypeIcon(workItemType.name)
                    Text(
                        text = buildAnnotatedString {
                            this.append(title)
                            if (myTotalTime > 0) {
                                this.append(" ")
                                appendInlineContent("time", myTotalTimeString)
                            }
                            if (promotedToEpicUrl != null) {
                                this.append(" ")
                                appendInlineContent("promoted", "(promoted)")
                            } else if (state == WorkItemState.CLOSED) {
                                this.append(" ")
                                appendInlineContent("closed", "(closed)")
                            }
                        },
                        inlineContent = mapOf(
                            "time" to InlineTextContent(
                                placeholder = rememberTimeBadgePlaceholder(
                                    time = myTotalTimeString,
                                    trailingIconSize = if (open) 16.dp else null
                                )
                            ) {
                                SimpleTooltip(
                                    text = "Timelog Sum\nClick to ${if (showTimelogs) "hide" else "see"} details."
                                ) {
                                    openTracking.takeIf { open }?.run {
                                        val representingColors = representingColors
                                        TimeBadge(
                                            time = myTotalTimeString,
                                            backgroundColor = representingColors.container.copy(alpha = 0.7f),
                                            color = representingColors.onContainer,
                                            trailingIcon = {
                                                RepresentingIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = representingColors.onContainer,
                                                )
                                            },
                                            onClick = { showTimelogs = !showTimelogs }
                                        )
                                    } ?: TimeBadge(
                                        time = myTotalTimeString,
                                        backgroundColor =
                                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        onClick = { showTimelogs = !showTimelogs }
                                    )
                                }
                            },
                            "closed" to InlineTextContent(
                                Placeholder(
                                    width = 16.sp,
                                    height = 16.sp,
                                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                )
                            ) {
                                SimpleTooltip("closed") {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "closed",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            "promoted" to InlineTextContent(
                                Placeholder(
                                    width = 16.sp,
                                    height = 16.sp,
                                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                )
                            ) {
                                SimpleTooltip("promoted") {
                                    Icon(
                                        imageVector = Icons.Default.ArrowCircleUp,
                                        contentDescription = "promoted",
                                        modifier = Modifier.size(16.dp).clickable {
                                            promotedToEpicUrl?.let { uri -> uriHandler.openUri(uri) }
                                        }
                                    )
                                }
                            },
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                labels(useLabelColors)
                AnimatedVisibility(showTimelogs) {
                    myTimelogs(openTracking)
                }
                AnimatedVisibility(visible = open) {
                    if (!open) return@AnimatedVisibility
                    val timeSinceOpen = remember(openTracking, refresh(every = 1.seconds)) {
                        Clock.System.now() - openTracking.timeOfOpen
                    }
                    val timeSinceOpenInWholeMinutes = timeSinceOpen.inWholeMinutes.minutes
                    val customTimeSpent = openTracking.customTimeSpent
                    val focusRequester = remember { FocusRequester() }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth()
                                .disableGlobalSearchIfFocused()
                                .focusRequester(focusRequester)
                                .changeFocusOnTab()
                                .onCtrlOrMetaEnter(commitTimeTracking)
                                .onKeyEvent { true }, // https://github.com/JetBrains/compose-multiplatform/issues/4612,
                            value = openTracking.summary.orEmpty(),
                            label = { Text("What have I achieved? (optional)") },
                            onValueChange = { text -> onOpenTrackingChange(openTracking.copy(summary = text)) },
                            leadingIcon = { Icon(Icons.Outlined.Summarize, contentDescription = null) },
                        )
                        val timeSinceOpenString = timeSinceOpenInWholeMinutes.toComponents { hours, minutes, _, _ ->
                            "${hours}h ${minutes}m"
                        }
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth()
                                .disableGlobalSearchIfFocused()
                                .changeFocusOnTab()
                                .onCtrlOrMetaEnter(commitTimeTracking)
                                .onKeyEvent { true }, // https://github.com/JetBrains/compose-multiplatform/issues/4612
                            value = customTimeSpent ?: timeSinceOpenString,
                            onValueChange = { text -> onOpenTrackingChange(openTracking.copy(customTimeSpent = text)) },
                            label = { Text("Time spent") },
                            placeholder = { Text("Example: 1h 30m") },
                            visualTransformation = customTimeSpent?.let { VisualTransformation.None }
                                ?: AddedTextVisualTransformation(
                                    addedText = buildAnnotatedString {
                                        withStyle(SpanStyle(color = LocalContentColor.current.copy(alpha = 0.5f))) {
                                            this.append(" ${(timeSinceOpen - timeSinceOpenInWholeMinutes).inWholeSeconds}s")
                                        }
                                    }
                                ),
                            leadingIcon = {
                                openTracking.RepresentingIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = openTracking.representingColors.color
                                )
                            },
                            trailingIcon = {
                                when {
                                    customTimeSpent != null -> Row {
                                        val customTimeSpentDuration = Duration.parseOrNull(customTimeSpent.trim())
                                        customTimeSpentDuration?.let {
                                            SimpleTooltip("Continue timer\n(overwrites $timeSinceOpenString)") {
                                                IconButton(
                                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                                                    onClick = {
                                                        onOpenTrackingChange(
                                                            openTracking.copy(
                                                                timeOfOpen = Clock.System.now() - customTimeSpentDuration,
                                                                customTimeSpent = null,
                                                            )
                                                        )
                                                    }
                                                ) {
                                                    Icon(
                                                        Icons.Default.Start,
                                                        contentDescription = "Continue timer from entered time"
                                                    )
                                                }
                                            }
                                        }
                                        SimpleTooltip("Reset to running timer\n($timeSinceOpenString)") {
                                            IconButton(
                                                modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                                                onClick = { onOpenTrackingChange(openTracking.copy(customTimeSpent = null)) }
                                            ) {
                                                Icon(
                                                    Icons.Default.History,
                                                    contentDescription = "Reset to running timer"
                                                )
                                            }
                                        }
                                    }

                                    else -> SimpleTooltip("Restart time spent timer") {
                                        IconButton(
                                            modifier = Modifier.pointerHoverIcon(PointerIcon.Default),
                                            onClick = { onOpenTrackingChange(openTracking.copy(timeOfOpen = Clock.System.now())) }
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Restart time spent timer"
                                            )
                                        }
                                    }
                                }
                            },
                            isError = openTracking.customTimeSpentHasError,
                            supportingText = if (openTracking.customTimeSpentHasError) {
                                { Text(customTimeSpentHasErrorMessage) }
                            } else null
                        )
                        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            AdditionalActions(this@invoke, pinned, togglePinned)
                            SimpleTooltip("Discard time tracking") {
                                IconButton(onClick = { onOpenTrackingChange(null) }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteForever,
                                        contentDescription = "Discard time tracking"
                                    )
                                }
                            }
                            SimpleTooltip("Commit time tracking") {
                                FilledTonalIconButton(
                                    modifier = Modifier.padding(start = 4.dp),
                                    onClick = commitTimeTracking,
                                    enabled = !openTracking.customTimeSpentHasError
                                ) {
                                    val x by rememberInfiniteTransition().animateValue(
                                        initialValue = if (commitTimeTrackingEnabled) 0.dp else (-35).dp,
                                        targetValue = if (commitTimeTrackingEnabled) 0.dp else 35.dp,
                                        typeConverter = Dp.VectorConverter,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1500),
                                            repeatMode = RepeatMode.Restart,
                                            initialStartOffset = StartOffset(
                                                offsetMillis = 750,
                                                offsetType = StartOffsetType.FastForward
                                            )
                                        ),
                                    )
                                    Icon(
                                        modifier = Modifier.offset(x = x, y = -x / 10).rotate(-x.value / 8),
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Commit time tracking"
                                    )
                                }
                            }
                        }
                    }
                    LaunchedEffect(open) {
                        if (!open) return@LaunchedEffect
                        focusRequester.requestFocus()
                    }
                }
                additionalContent?.invoke()
            }
        }
    }
}
