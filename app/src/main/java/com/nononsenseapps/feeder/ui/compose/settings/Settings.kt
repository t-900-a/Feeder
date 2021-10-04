package com.nononsenseapps.feeder.ui.compose.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.nononsenseapps.feeder.R
import com.nononsenseapps.feeder.archmodel.FeedItemStyle
import com.nononsenseapps.feeder.archmodel.ItemOpener
import com.nononsenseapps.feeder.archmodel.LinkOpener
import com.nononsenseapps.feeder.archmodel.SortingOptions
import com.nononsenseapps.feeder.archmodel.SyncFrequency
import com.nononsenseapps.feeder.archmodel.ThemeOptions
import com.nononsenseapps.feeder.ui.compose.theme.FeederTheme
import com.nononsenseapps.feeder.ui.compose.theme.LocalDimens
import com.nononsenseapps.feeder.ui.compose.theme.keyline1Padding
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    val viewState by settingsViewModel.viewState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_activity_settings),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                contentPadding = rememberInsetsPaddingValues(
                    LocalWindowInsets.current.statusBars,
                    applyBottom = false,
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        SettingsList(
            modifier = Modifier.padding(padding),
            currentThemeValue = viewState.currentTheme.asThemeOption(),
            onThemeChanged = { value ->
                settingsViewModel.setCurrentTheme(value.currentTheme)
            },
            currentSortingValue = viewState.currentSorting.asSortOption(),
            onSortingChanged = { value ->
                settingsViewModel.setCurrentSorting(value.currentSorting)
            },
            showFabValue = viewState.showFab,
            onShowFabChanged = { value ->
                settingsViewModel.setShowFab(value)
            },
            feedItemStyleValue = viewState.feedItemStyle,
            onFeedItemStyleChanged = { value ->
                settingsViewModel.setFeedItemStyle(value)
            },
            syncOnStartupValue = viewState.syncOnResume,
            onSyncOnStartupChanged = {
                settingsViewModel.setSyncOnResume(it)
            },
            syncOnlyOnWifiValue = viewState.syncOnlyOnWifi,
            onSyncOnlyOnWifiChanged = {
                settingsViewModel.setSyncOnlyOnWifi(it)
            },
            syncOnlyWhenChargingValue = viewState.syncOnlyWhenCharging,
            onSyncOnlyWhenChargingChanged = {
                settingsViewModel.setSyncOnlyWhenCharging(it)
            },
            loadImageOnlyOnWifiValue = viewState.loadImageOnlyOnWifi,
            onLoadImageOnlyOnWifiChanged = {
                settingsViewModel.setLoadImageOnlyOnWifi(it)
            },
            showThumbnailsValue = viewState.showThumbnails,
            onShowThumbnailsChanged = {
                settingsViewModel.setShowThumbnails(it)
            },
            maxItemsPerFeedValue = viewState.maximumCountPerFeed,
            onMaxItemsPerFeedChanged = {
                settingsViewModel.setMaxCountPerFeed(it)
            },
            currentItemOpenerValue = viewState.itemOpener,
            onItemOpenerChanged = {
                settingsViewModel.setItemOpener(it)
            },
            currentLinkOpenerValue = viewState.linkOpener,
            onLinkOpenerChanged = {
                settingsViewModel.setLinkOpener(it)
            },
            currentSyncFrequencyValue = viewState.syncFrequency,
            onSyncFrequencyChanged = {
                settingsViewModel.setSyncFrequency(it)
            },
            batteryOptimizationIgnoredValue = viewState.batteryOptimizationIgnored,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    FeederTheme(ThemeOptions.DAY) {
        Surface {
            SettingsList(
                modifier = Modifier,
                currentThemeValue = ThemeOptions.SYSTEM.asThemeOption(),
                onThemeChanged = {},
                currentSortingValue = SortingOptions.NEWEST_FIRST.asSortOption(),
                onSortingChanged = {},
                showFabValue = true,
                onShowFabChanged = {},
                feedItemStyleValue = FeedItemStyle.CARD,
                onFeedItemStyleChanged = {},
                syncOnStartupValue = true,
                onSyncOnStartupChanged = {},
                syncOnlyOnWifiValue = true,
                onSyncOnlyOnWifiChanged = {},
                syncOnlyWhenChargingValue = true,
                onSyncOnlyWhenChargingChanged = {},
                loadImageOnlyOnWifiValue = true,
                onLoadImageOnlyOnWifiChanged = {},
                showThumbnailsValue = true,
                onShowThumbnailsChanged = {},
                maxItemsPerFeedValue = 101,
                onMaxItemsPerFeedChanged = {},
                currentItemOpenerValue = ItemOpener.CUSTOM_TAB,
                onItemOpenerChanged = {},
                currentLinkOpenerValue = LinkOpener.DEFAULT_BROWSER,
                onLinkOpenerChanged = {},
                currentSyncFrequencyValue = SyncFrequency.EVERY_12_HOURS,
                onSyncFrequencyChanged = {},
                batteryOptimizationIgnoredValue = false
            )
        }
    }
}

@Composable
fun SettingsList(
    modifier: Modifier,
    currentThemeValue: ThemeOption,
    onThemeChanged: (ThemeOption) -> Unit,
    currentSortingValue: SortOption,
    onSortingChanged: (SortOption) -> Unit,
    showFabValue: Boolean,
    onShowFabChanged: (Boolean) -> Unit,
    feedItemStyleValue: FeedItemStyle,
    onFeedItemStyleChanged: (FeedItemStyle) -> Unit,
    syncOnStartupValue: Boolean,
    onSyncOnStartupChanged: (Boolean) -> Unit,
    syncOnlyOnWifiValue: Boolean,
    onSyncOnlyOnWifiChanged: (Boolean) -> Unit,
    syncOnlyWhenChargingValue: Boolean,
    onSyncOnlyWhenChargingChanged: (Boolean) -> Unit,
    loadImageOnlyOnWifiValue: Boolean,
    onLoadImageOnlyOnWifiChanged: (Boolean) -> Unit,
    showThumbnailsValue: Boolean,
    onShowThumbnailsChanged: (Boolean) -> Unit,
    maxItemsPerFeedValue: Int,
    onMaxItemsPerFeedChanged: (Int) -> Unit,
    currentItemOpenerValue: ItemOpener,
    onItemOpenerChanged: (ItemOpener) -> Unit,
    currentLinkOpenerValue: LinkOpener,
    onLinkOpenerChanged: (LinkOpener) -> Unit,
    currentSyncFrequencyValue: SyncFrequency,
    onSyncFrequencyChanged: (SyncFrequency) -> Unit,
    batteryOptimizationIgnoredValue: Boolean,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val dimens = LocalDimens.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = keyline1Padding)
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        MenuSetting(
            currentValue = currentThemeValue,
            values = listOf(
                ThemeOptions.SYSTEM.asThemeOption(),
                ThemeOptions.DAY.asThemeOption(),
                ThemeOptions.NIGHT.asThemeOption()
            ),
            title = stringResource(id = R.string.theme),
            onSelection = onThemeChanged
        )

        MenuSetting(
            currentValue = currentSortingValue,
            values = listOf(
                SortingOptions.NEWEST_FIRST.asSortOption(),
                SortingOptions.OLDEST_FIRST.asSortOption()
            ),
            title = stringResource(id = R.string.sort),
            onSelection = onSortingChanged
        )

        SwitchSetting(
            checked = showFabValue,
            onCheckedChanged = onShowFabChanged,
            title = stringResource(id = R.string.show_fab)
        )

        MenuSetting(
            title = stringResource(id = R.string.feed_item_style),
            currentValue = feedItemStyleValue.asFeedItemStyleOption(),
            values = FeedItemStyle.values().map { it.asFeedItemStyleOption() },
            onSelection = {
                onFeedItemStyleChanged(it.feedItemStyle)
            },
        )

        Divider(modifier = Modifier.width(dimens.maxContentWidth))

        GroupTitle {
            Text(stringResource(id = R.string.synchronization))
        }

        MenuSetting(
            currentValue = currentSyncFrequencyValue.asSyncFreqOption(),
            values = SyncFrequency.values().map {
                it.asSyncFreqOption()
            },
            title = stringResource(id = R.string.check_for_updates),
            onSelection = {
                onSyncFrequencyChanged(it.syncFrequency)
            }
        )

        SwitchSetting(
            checked = syncOnStartupValue,
            onCheckedChanged = onSyncOnStartupChanged,
            title = stringResource(id = R.string.on_startup)
        )

        SwitchSetting(
            checked = syncOnlyOnWifiValue,
            onCheckedChanged = onSyncOnlyOnWifiChanged,
            title = stringResource(id = R.string.only_on_wifi)
        )

        SwitchSetting(
            checked = syncOnlyWhenChargingValue,
            onCheckedChanged = onSyncOnlyWhenChargingChanged,
            title = stringResource(id = R.string.only_when_charging)
        )

        MenuSetting(
            currentValue = maxItemsPerFeedValue,
            values = listOf(
                50,
                100,
                200,
                500,
                1000
            ),
            title = stringResource(id = R.string.max_feed_items),
            onSelection = onMaxItemsPerFeedChanged
        )

        ExternalSetting(
            currentValue = when (batteryOptimizationIgnoredValue) {
                true -> stringResource(id = R.string.battery_optimization_disabled)
                false -> stringResource(id = R.string.battery_optimization_enabled)
            },
            title = stringResource(id = R.string.battery_optimization)
        ) {
            context.startActivity(
                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            )
        }

        Divider(modifier = Modifier.width(dimens.maxContentWidth))

        GroupTitle {
            Text(stringResource(id = R.string.image_loading))
        }

        SwitchSetting(
            checked = loadImageOnlyOnWifiValue,
            onCheckedChanged = onLoadImageOnlyOnWifiChanged,
            title = stringResource(id = R.string.only_on_wifi)
        )

        SwitchSetting(
            checked = showThumbnailsValue,
            onCheckedChanged = onShowThumbnailsChanged,
            title = stringResource(id = R.string.show_thumbnails)
        )

        Divider(modifier = Modifier.width(dimens.maxContentWidth))

        GroupTitle {
            Text(stringResource(id = R.string.reader_settings))
        }

        MenuSetting(
            currentValue = currentItemOpenerValue.asItemOpenerOption(),
            values = listOf(
                ItemOpener.READER.asItemOpenerOption(),
                ItemOpener.CUSTOM_TAB.asItemOpenerOption(),
                ItemOpener.DEFAULT_BROWSER.asItemOpenerOption()
            ),
            title = stringResource(id = R.string.open_item_by_default_with),
            onSelection = {
                onItemOpenerChanged(it.itemOpener)
            }
        )

        MenuSetting(
            currentValue = currentLinkOpenerValue.asLinkOpenerOption(),
            values = listOf(
                LinkOpener.CUSTOM_TAB.asLinkOpenerOption(),
                LinkOpener.DEFAULT_BROWSER.asLinkOpenerOption(),
                LinkOpener.READER_VIEWER.asLinkOpenerOption(),
            ),
            title = stringResource(id = R.string.open_links_with),
            onSelection = {
                onLinkOpenerChanged(it.linkOpener)
            }
        )

        Spacer(modifier = Modifier.navigationBarsHeight())
    }
}

@Composable
fun GroupTitle(
    startingSpace: Boolean = true,
    height: Dp = 64.dp,
    title: @Composable () -> Unit,
) {
    val dimens = LocalDimens.current
    Row(
        modifier = Modifier
            .width(dimens.maxContentWidth),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (startingSpace) {
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(height)
            )
        }
        Box(
            modifier = Modifier
                .height(height),
            contentAlignment = Alignment.CenterStart
        ) {
            ProvideTextStyle(
                value = MaterialTheme.typography.caption.merge(
                    TextStyle(color = MaterialTheme.colors.primary)
                )
            ) {
                title()
            }
        }
    }
}

@Composable
fun ExternalSetting(
    currentValue: String,
    title: String,
    icon: @Composable () -> Unit = {},
    onClick: () -> Unit,
) {
    val dimens = LocalDimens.current
    Row(
        modifier = Modifier
            .width(dimens.maxContentWidth)
            .clickable { onClick() }
            .semantics {
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                icon()
            }
        }

        TitleAndSubtitle(
            title = {
                Text(title)
            },
            subtitle = {
                Text(currentValue)
            }
        )
    }
}

@Composable
fun <T> MenuSetting(
    title: String,
    currentValue: T,
    values: Iterable<T>,
    icon: @Composable () -> Unit = {},
    onSelection: (T) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val dimens = LocalDimens.current
    Row(
        modifier = Modifier
            .width(dimens.maxContentWidth)
            .clickable { expanded = !expanded }
            .semantics {
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                icon()
            }
        }

        TitleAndSubtitle(
            title = {
                Text(title)
            },
            subtitle = {
                Text(currentValue.toString())
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (value in values) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSelection(value)
                    }
                ) {
                    val style = if (value==currentValue) {
                        MaterialTheme.typography.body1.merge(
                            TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.secondary
                            )
                        )
                    } else {
                        MaterialTheme.typography.body1
                    }
                    Text(
                        value.toString(),
                        style = style
                    )
                }
            }
        }
    }
}

@Composable
fun RadioButtonSetting(
    title: String,
    selected: Boolean,
    icon: (@Composable () -> Unit)? = {},
    minHeight: Dp = 64.dp,
    onClick: () -> Unit,
) {
    val stateLabel = if (selected) {
        stringResource(R.string.selected)
    } else {
        stringResource(R.string.not_selected)
    }
    val dimens = LocalDimens.current
    Row(
        modifier = Modifier
            .width(dimens.maxContentWidth)
            .heightIn(min = minHeight)
            .clickable { onClick() }
            .semantics(mergeDescendants = true) {
                role = Role.RadioButton
                stateDescription = stateLabel
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon!=null) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    icon()
                }
            }
        }

        TitleAndSubtitle(
            title = {
                Text(title)
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = Modifier.clearAndSetSemantics { }
        )
    }
}

@Composable
fun SwitchSetting(
    title: String,
    checked: Boolean,
    icon: (@Composable () -> Unit)? = {},
    onCheckedChanged: (Boolean) -> Unit,
) {
    val stateLabel = if (checked) {
        stringResource(R.string.on)
    } else {
        stringResource(R.string.off)
    }
    val dimens = LocalDimens.current
    Row(
        modifier = Modifier
            .width(dimens.maxContentWidth)
            .heightIn(min = 64.dp)
            .clickable { onCheckedChanged(!checked) }
            .semantics(mergeDescendants = true) {
                stateDescription = stateLabel
                role = Role.Switch
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon!=null) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    icon()
                }
            }
        }

        TitleAndSubtitle(
            title = {
                Text(title)
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChanged,
            modifier = Modifier.clearAndSetSemantics { }
        )
    }
}

@Composable
private fun RowScope.TitleAndSubtitle(
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
            title()
        }
        if (subtitle!=null) {
            Spacer(modifier = Modifier.size(2.dp))
            ProvideTextStyle(value = MaterialTheme.typography.caption) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.medium,
                    content = subtitle
                )
            }
        }
    }
}

@Composable
fun ThemeOptions.asThemeOption() =
    ThemeOption(
        currentTheme = this,
        name = stringResource(id = stringId)
    )

@Immutable
data class ThemeOption(
    val currentTheme: ThemeOptions,
    val name: String,
) {
    override fun toString() = name
}

@Composable
fun SortingOptions.asSortOption() =
    SortOption(
        currentSorting = this,
        name = stringResource(id = stringId)
    )

@Immutable
data class SortOption(
    val currentSorting: SortingOptions,
    val name: String,
) {
    override fun toString() = name
}

@Immutable
data class FeedItemStyleOption(
    val feedItemStyle: FeedItemStyle,
    val name: String,
) {
    override fun toString() = name
}

@Immutable
data class SyncFreqOption(
    val syncFrequency: SyncFrequency,
    val name: String,
) {
    override fun toString() = name
}

@Composable
fun SyncFrequency.asSyncFreqOption() =
    SyncFreqOption(
        syncFrequency = this,
        name = stringResource(id = stringId)
    )

@Immutable
data class ItemOpenerOption(
    val itemOpener: ItemOpener,
    val name: String,
) {
    override fun toString() = name
}

@Composable
fun ItemOpener.asItemOpenerOption() =
    ItemOpenerOption(
        itemOpener = this,
        name = stringResource(id = stringId)
    )

@Immutable
data class LinkOpenerOption(
    val linkOpener: LinkOpener,
    val name: String,
) {
    override fun toString() = name
}

@Composable
fun LinkOpener.asLinkOpenerOption() =
    LinkOpenerOption(
        linkOpener = this,
        name = stringResource(id = stringId)
    )

@Composable
fun FeedItemStyle.asFeedItemStyleOption() =
    FeedItemStyleOption(
        feedItemStyle = this,
        name = stringResource(id = stringId)
    )
