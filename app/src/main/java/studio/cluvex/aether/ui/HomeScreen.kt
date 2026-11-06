package studio.cluvex.aether.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import studio.cluvex.aether.R
import studio.cluvex.aether.core.IpEndpoint
import studio.cluvex.aether.model.ConnectionProfile
import studio.cluvex.aether.model.ConnectionState
import studio.cluvex.aether.model.isBusy
import studio.cluvex.aether.model.isConnected
import studio.cluvex.aether.ui.components.AmbientBackground
import studio.cluvex.aether.ui.components.ButtonMode
import studio.cluvex.aether.ui.components.ConnectButton
import studio.cluvex.aether.ui.components.ConnectionCard
import studio.cluvex.aether.ui.components.FitToHeight
import studio.cluvex.aether.ui.settings.CustomConfigsPage
import studio.cluvex.aether.ui.settings.RowDivider
import studio.cluvex.aether.ui.settings.SettingsGroup
import studio.cluvex.aether.ui.settings.SettingsHost
import studio.cluvex.aether.ui.settings.SettingsNavRow
import studio.cluvex.aether.ui.settings.SettingsRoute
import studio.cluvex.aether.ui.theme.AetherBlue
import studio.cluvex.aether.ui.theme.AetherMint
import studio.cluvex.aether.ui.theme.AetherViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: ConnectionState,
    profile: ConnectionProfile,
    connectedSince: Long?,
    ipInfo: IpEndpoint?,
    ipLoading: Boolean,
    onProfileChange: (ConnectionProfile) -> Unit,
    onToggleConnection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // UI-SPEED, and the single biggest win in this release: settings used to be a
    // ModalBottomSheet holding a ~900-line, ~50-control card, AND the same card
    // was a child of the navigation drawer. Now settings is its own screen and
    // the home screen is DISPOSED while it is open, so the connect button, the
    // connection card and its animated edge stop composing and animating
    // entirely while the user is in settings - instead of running behind a sheet
    // and competing for the same frame budget.
    var settingsRoute by remember { mutableStateOf<SettingsRoute?>(null) }
    // Bottom tabs: 0 home, 1 stats (diagnostics), 2 V2Ray configs, 3 settings, 4 about.
    var tab by remember { mutableIntStateOf(0) }

    val openRoute = settingsRoute
    if (openRoute != null) {
        SettingsHost(
            start = openRoute,
            state = state,
            profile = profile,
            onProfileChange = onProfileChange,
            onClose = { settingsRoute = null },
            modifier = modifier,
        )
        return
    }

    if (tab != 0) {
        Column(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (tab) {
                    1 -> SettingsHost(start = SettingsRoute.DIAGNOSTICS, state = state, profile = profile, onProfileChange = onProfileChange, onClose = { tab = 0 })
                    2 -> CustomConfigsPage(editable = true, onBack = { tab = 0 })
                    3 -> SettingsHost(start = SettingsRoute.HOME, state = state, profile = profile, onProfileChange = onProfileChange, onClose = { tab = 0 })
                    else -> SettingsHost(start = SettingsRoute.ABOUT, state = state, profile = profile, onProfileChange = onProfileChange, onClose = { tab = 0 })
                }
            }
            AetherTabBar(selected = tab, onSelect = { tab = it })
        }
        return
    }

    val mode = when {
        state.isConnected -> ButtonMode.CONNECTED
        state.isBusy -> ButtonMode.BUSY
        state is ConnectionState.Error -> ButtonMode.ERROR
        else -> ButtonMode.IDLE
    }

    val accent = when (mode) {
        // Brand mint, the same accent the connection card and its animated edge
        // use, so the whole screen reads as one palette.
        ButtonMode.CONNECTED -> AetherMint
        ButtonMode.ERROR -> Color(0xFFFF5C7A)
        else -> Color(0xFF1E63F0)
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // The drawer is a MENU now, not a container for every panel in the
            // app. It used to compose the diagnostics, share, advanced and about
            // cards - all four, including the whole settings card - even while
            // closed, which is why the first drawer swipe stuttered and why every
            // engine log line recomposed something behind a panel nobody was
            // looking at. Four rows cost nothing.
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(0.86f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = stringResource(R.string.tagline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(Modifier.height(22.dp))

                    val go: (SettingsRoute) -> Unit = { route ->
                        settingsRoute = route
                        drawerScope.launch { drawerState.close() }
                    }

                    // 1.2.9 AI: its own group at the top of the menu. The
                    // assistant is not a setting, it is a place you go - and a
                    // feature nobody can find is a feature that does not exist.
                    SettingsGroup {
                        SettingsNavRow(
                            title = stringResource(R.string.ai_title),
                            summary = stringResource(R.string.ai_subtitle),
                            icon = Icons.Rounded.AutoAwesome,
                            onClick = { go(SettingsRoute.AI) },
                        )
                        RowDivider()
                        SettingsNavRow(
                            title = stringResource(R.string.ai_chat_open),
                            summary = stringResource(R.string.ai_chat_subtitle),
                            icon = Icons.Rounded.Chat,
                            onClick = { go(SettingsRoute.AI_CHAT) },
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    SettingsGroup {
                        SettingsNavRow(
                            title = stringResource(R.string.settings_title),
                            summary = stringResource(R.string.settings_subtitle),
                            icon = Icons.Rounded.Settings,
                            onClick = { go(SettingsRoute.HOME) },
                        )
                        RowDivider()
                        SettingsNavRow(
                            title = stringResource(R.string.diag_title),
                            summary = stringResource(R.string.diag_subtitle),
                            icon = Icons.Rounded.BugReport,
                            onClick = { go(SettingsRoute.DIAGNOSTICS) },
                        )
                        RowDivider()
                        SettingsNavRow(
                            title = stringResource(R.string.share_title),
                            summary = stringResource(R.string.share_subtitle),
                            icon = Icons.Rounded.Wifi,
                            onClick = { go(SettingsRoute.SHARE) },
                        )
                        RowDivider()
                        SettingsNavRow(
                            title = stringResource(R.string.about_title),
                            summary = stringResource(R.string.about_subtitle),
                            icon = Icons.Rounded.Info,
                            onClick = { go(SettingsRoute.ABOUT) },
                        )
                    }
                }
            }
        },
    ) {
        Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AmbientBackground(accent = accent, active = state.isConnected)

            // NO SCROLLING ON THE HOME SCREEN, BY CONSTRUCTION.
            //
            // FitToHeight measures what this content naturally wants and, if the
            // viewport is smaller, scales the whole subtree's density down by one
            // measured factor - type, paddings, icons, radii and stroke widths
            // together - until it fits exactly. Everything stays on screen on
            // every device, at full rendering sharpness, and there is nothing
            // left to scroll. See FitToHeight.
            FitToHeight(
                // r2: every connection-state change re-opens the fit, so a size
                // forced down by a transient is restored as soon as it passes.
                epochKey = state::class,
                modifier = Modifier
                    .fillMaxSize()
                    // Insets are applied OUTSIDE the scaled subtree: system bars
                    // are a physical size and must not shrink with the content.
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    // Brand badge: a glossy blue shield-ish disc with the P mark.
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF3FA2FF), Color(0xFF0B1F5C)))),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("P", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = stringResource(R.string.tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(18.dp))

                    ConnectButton(
                        mode = mode,
                        onClick = onToggleConnection,
                        stateText = stateTitle(state),
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = stateSubtitle(state),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(14.dp))

                    // Server pill: where the tunnel currently goes out.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Wifi, contentDescription = null, tint = AetherBlue)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = profile.backend.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Icon(Icons.Rounded.Settings, contentDescription = null, tint = AetherBlue)
                    }

                    Spacer(Modifier.height(10.dp))

                    ConnectionCard(
                        connected = state.isConnected,
                        statusTitle = stateTitle(state),
                        statusCaption = stateSubtitle(state),
                        connectedSince = connectedSince,
                        ipInfo = ipInfo,
                        ipLoading = ipLoading,
                        error = state is ConnectionState.Error,
                        backend = profile.backend,
                    )
                }
            }

            IconButton(
                onClick = { drawerScope.launch { drawerState.open() } },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = stringResource(R.string.menu_open),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }

            // Top-end corner: the AI chat and the tunnel shortcut, in that order.
            //
            // 1.2.9: the SHORTCUT to the tunnel settings - Connection, Transport &
            // anti-DPI, DNS & routing, Upstream proxy, and the reset action. The
            // full settings tree lives in the drawer (top start), which is where
            // every other group belongs; this icon used to open that same tree.
            //
            // The AI button sits beside it rather than anywhere in the middle of the
            // screen on purpose: the home screen is measured by FitToHeight and
            // scaled to fit exactly, so ANY new element inside that subtree shrinks
            // the connect button on small phones. Both icons live outside it, in the
            // inset padding, where they cost the layout nothing.
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { settingsRoute = SettingsRoute.AI_CHAT }) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = stringResource(R.string.ai_title),
                        tint = AetherViolet,
                    )
                }
                IconButton(onClick = { settingsRoute = SettingsRoute.QUICK }) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = stringResource(R.string.advanced_open),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
        AetherTabBar(selected = tab, onSelect = { tab = it })
        }
    }
}

@Composable
private fun AetherTabBar(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf(
        Triple(stringResource(R.string.tab_home), Icons.Rounded.Home, 0),
        Triple(stringResource(R.string.tab_stats), Icons.Rounded.BarChart, 1),
        Triple(stringResource(R.string.tab_v2ray), Icons.Rounded.Send, 2),
        Triple(stringResource(R.string.tab_settings), Icons.Rounded.Settings, 3),
        Triple(stringResource(R.string.tab_about), Icons.Rounded.Info, 4),
    )
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                selected = selected == index,
                onClick = { onSelect(index) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
            )
        }
    }
}

@Composable
private fun stateTitle(state: ConnectionState): String = when (state) {
    is ConnectionState.Idle -> stringResource(R.string.state_idle)
    is ConnectionState.Launching -> stringResource(R.string.state_launching)
    is ConnectionState.Connecting -> stringResource(R.string.state_connecting)
    is ConnectionState.Verifying -> stringResource(R.string.state_verifying)
    is ConnectionState.Connected -> stringResource(R.string.state_connected)
    is ConnectionState.Reconnecting -> stringResource(R.string.state_reconnecting)
    is ConnectionState.Disconnecting -> stringResource(R.string.state_disconnecting)
    is ConnectionState.Error -> stringResource(R.string.state_error)
}

@Composable
private fun stateSubtitle(state: ConnectionState): String = when (state) {
    is ConnectionState.Idle -> stringResource(R.string.tap_to_connect)
    // The exit IP + flag is shown inside the card, so keep the subtitle generic
    // instead of leaking the internal 127.0.0.1:port address.
    is ConnectionState.Connected -> stringResource(R.string.tap_to_disconnect)
    is ConnectionState.Reconnecting ->
        stringResource(R.string.reconnect_attempt, state.attempt, state.maxAttempts)
    is ConnectionState.Error -> state.message
    else -> stringResource(R.string.tap_to_disconnect)
}
