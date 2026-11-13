package com.vauth.foxyvpn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vauth.foxyvpn.R
import com.vauth.foxyvpn.data.ProxyStateStore
import com.vauth.foxyvpn.data.ThemeMode
import com.vauth.foxyvpn.data.formatBytes
import com.vauth.foxyvpn.data.model.ConnectionState
import com.vauth.foxyvpn.data.model.displayName
import com.vauth.foxyvpn.data.model.emojiFlag
import com.vauth.foxyvpn.ui.HomeViewModel
import com.vauth.foxyvpn.ui.theme.ThemeController
import com.vauth.foxyvpn.vpn.FoxyVpnService
import com.vauth.foxyvpn.vpn.IpLocationInfo
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private val SkyTop = Color(0xFFDCEBFF)
private val SkyBottom = Color(0xFFF5F8FF)
private val Navy = Color(0xFF092D83)
private val Blue = Color(0xFF0878F9)
private val CardWhite = Color(0xDDFBFDFF)
private val MutedBlue = Color(0xFF456BA9)

@Composable
fun HomeScreen(
    proxyStateStore: ProxyStateStore,
    homeModel: HomeViewModel,
    themeController: ThemeController,
    onRequestConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onOpenServers: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val state by FoxyVpnService.state.collectAsStateWithLifecycle()
    val proxyOnlyActive by FoxyVpnService.proxyOnlyActive.collectAsStateWithLifecycle()
    val lastError by FoxyVpnService.lastError.collectAsStateWithLifecycle()
    val liveStats by FoxyVpnService.vpnLiveStats.collectAsStateWithLifecycle()
    val connectedAtElapsedMs by FoxyVpnService.connectedAtElapsedMs.collectAsStateWithLifecycle()
    val pingLatency by FoxyVpnService.pingLatencyMs.collectAsStateWithLifecycle()
    val vpnEdgeIp by FoxyVpnService.vpnEdgeIp.collectAsStateWithLifecycle()
    val isPinging by homeModel.isPinging.collectAsStateWithLifecycle()

    val selectedProxy by proxyStateStore.selectedProxyFlow.collectAsStateWithLifecycle()
    val activeProxy by FoxyVpnService.activeCandidate.collectAsStateWithLifecycle()
    val displayProxy =
        if (state == ConnectionState.DISCONNECTED) selectedProxy else activeProxy ?: selectedProxy

    val connected = state == ConnectionState.CONNECTED
    val connecting = state == ConnectionState.CONNECTING
    val haptics = LocalHapticFeedback.current
    val systemDark = isSystemInDarkTheme()

    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    val account by homeModel.account.collectAsStateWithLifecycle()
    val realIp = account.realIp?.let { IpLocationInfo(it, account.country.orEmpty()) }
    val quotaRemaining = account.quotaRemaining
    val quotaMax = account.quotaMax
    val isUnlimited = account.unlimited

    LaunchedEffect(Unit) { homeModel.refreshAccount() }
    LaunchedEffect(state) {
        if (state == ConnectionState.CONNECTED) homeModel.refreshAccount()
    }
    LaunchedEffect(state, connectedAtElapsedMs) {
        val startEpoch = connectedAtElapsedMs
        if (connected && startEpoch != null) {
            while (isActive) {
                elapsedSeconds =
                    ((android.os.SystemClock.elapsedRealtime() - startEpoch) / 1000L)
                        .coerceAtLeast(0L)
                delay(1000L)
            }
        } else {
            elapsedSeconds = 0L
        }
    }

    Scaffold(containerColor = Color.Transparent) { padding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(Brush.verticalGradient(listOf(SkyTop, SkyBottom, Color.White))),
            contentAlignment = Alignment.TopCenter,
        ) {
            val scroll = rememberScrollState()
            Column(
                modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth()
                    .verticalScroll(scroll)
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Blue, modifier = Modifier.size(30.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Security, contentDescription = null, tint = Blue, modifier = Modifier.size(54.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            color = Navy,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp,
                        )
                        Text("FAST  •  SECURE  •  PRIVATE", color = MutedBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = { themeController.toggle(systemDark) }) {
                        val mode = themeController.mode
                        Icon(
                            imageVector = when (mode) {
                                ThemeMode.SYSTEM -> Icons.Filled.BrightnessAuto
                                ThemeMode.LIGHT -> Icons.Filled.LightMode
                                ThemeMode.DARK -> Icons.Filled.DarkMode
                            },
                            contentDescription = "Change appearance",
                            tint = Blue,
                            modifier = Modifier.size(27.dp),
                        )
                    }
                }

                Spacer(Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier.weight(1f).height(1.dp).background(Color(0x6683B8FF))
                    )
                    Text(
                        when {
                            connected -> "YOUR CONNECTION IS PROTECTED"
                            connecting -> "SECURE CONNECTION IN PROGRESS"
                            else -> "YOUR PRIVACY STARTS HERE"
                        },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MutedBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Box(Modifier.weight(1f).height(1.dp).background(Color(0x6683B8FF)))
                }

                Spacer(Modifier.height(22.dp))

                var dragDelta by remember { mutableFloatStateOf(0f) }
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth().height(154.dp),
                ) {
                    val knobSize = 132.dp
                    val horizontalInset = 8.dp
                    val maxTravelDp = (maxWidth - knobSize - horizontalInset * 2).coerceAtLeast(0.dp)
                    val density = LocalDensity.current
                    val maxTravelPx = with(density) { maxTravelDp.toPx() }
                    val targetOffset = if (connected) maxTravelPx else 0f
                    val animatedOffset by animateFloatAsState(
                        targetValue = (targetOffset + dragDelta).coerceIn(0f, maxTravelPx),
                        animationSpec = spring(dampingRatio = 0.72f, stiffness = 420f),
                        label = "sliding-power-knob",
                    )
                    val capsuleModifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(100.dp),
                            ambientColor = Color(0xFF00BFFF).copy(alpha = 0.42f),
                            spotColor = Color(0xFF00BFFF).copy(alpha = 0.42f),
                        )
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF078CFF), Color(0xFF0756D7), Color(0xFF052D91))
                            )
                        )
                        .pointerInput(state, maxTravelPx) {
                            detectDragGestures(
                                onDragEnd = {
                                    val destination = (targetOffset + dragDelta).coerceIn(0f, maxTravelPx)
                                    dragDelta = 0f
                                    if (!connected && !connecting && destination > maxTravelPx * 0.48f) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onRequestConnect()
                                    } else if ((connected || connecting) && destination < maxTravelPx * 0.52f) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onDisconnect()
                                    }
                                },
                                onDragCancel = { dragDelta = 0f },
                            ) { change, amount ->
                                change.consume()
                                dragDelta = (dragDelta + amount.x).coerceIn(-targetOffset, maxTravelPx - targetOffset)
                            }
                        }
                        .pointerInput(state) {
                            detectTapGestures {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (connected || connecting) onDisconnect() else onRequestConnect()
                            }
                        }

                    Box(modifier = capsuleModifier) {
                        // The label moves to the opposite side of the sliding power control.
                        Column(
                            modifier = Modifier
                                .align(if (connected) Alignment.CenterStart else Alignment.CenterEnd)
                                .padding(horizontal = 24.dp)
                                .widthIn(max = maxWidth - knobSize - 28.dp),
                            horizontalAlignment = if (connected) Alignment.Start else Alignment.End,
                        ) {
                            Text(
                                when {
                                    connected -> "متصل"
                                    connecting -> "در حال اتصال"
                                    else -> "اتصال"
                                },
                                color = Color.White,
                                fontSize = 27.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = if (connected) TextAlign.Start else TextAlign.End,
                            )
                            Text(
                                when {
                                    connected -> "برای قطع اتصال بکشید"
                                    connecting -> "برای لغو اتصال بکشید"
                                    else -> "برای اتصال کلیک کنید"
                                },
                                color = Color(0xFFD8EAFF),
                                fontSize = 12.sp,
                                textAlign = if (connected) TextAlign.Start else TextAlign.End,
                            )
                        }
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .align(if (connected) Alignment.CenterStart else Alignment.CenterEnd)
                                .padding(horizontal = 8.dp)
                                .size(34.dp),
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = horizontalInset)
                                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                                .size(knobSize)
                                .shadow(
                                    elevation = 10.dp,
                                    shape = CircleShape,
                                    ambientColor = Color(0xFF00DFFF),
                                    spotColor = Color(0xFF00DFFF),
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFF087DFB), Color(0xFF0342B5), Color(0xFF03277D))
                                    )
                                )
                                .padding(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0750C9))
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFF168FFF), Color(0xFF0340A8))
                                    )
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                                    .border(2.dp, Color(0xFF43E9FF), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Filled.Power,
                                    contentDescription = if (connected) "Disconnect" else "Connect",
                                    tint = Color.White,
                                    modifier = Modifier.size(59.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))
                Text(
                    if (connected) "Connected for ${formatDuration(elapsedSeconds)}" else "Tap to connect",
                    color = MutedBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (connecting) {
                    Text(
                        if (lastError != null) "Retrying connection…" else "Preparing secure tunnel…",
                        color = MutedBlue,
                        fontSize = 12.sp,
                    )
                }
                if (!lastError.isNullOrBlank() && !connecting) {
                    Text(
                        lastError.orEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        color = Color(0xFFB3261E),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                    )
                }

                Spacer(Modifier.height(26.dp))

                Card(
                    onClick = onOpenServers,
                    modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(34.dp), spotColor = Blue.copy(alpha = 0.10f)),
                    shape = RoundedCornerShape(34.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val flag = displayProxy?.emojiFlag
                        if (!flag.isNullOrBlank()) {
                            Text(flag, fontSize = 42.sp)
                        } else {
                            Icon(Icons.Filled.Public, contentDescription = null, tint = Blue, modifier = Modifier.size(42.dp))
                        }
                        Spacer(Modifier.width(18.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SELECT SERVER", color = MutedBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                displayProxy?.displayName?.ifBlank { displayProxy.countryCode }
                                    ?: stringResource(R.string.home_recommended),
                                color = Navy,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            if (!connected && activeProxy != null && selectedProxy != null && activeProxy != selectedProxy) {
                                Text(
                                    stringResource(R.string.home_next_connection, selectedProxy?.displayName ?: ""),
                                    color = MutedBlue,
                                    fontSize = 11.sp,
                                )
                            }
                        }
                        Icon(Icons.Filled.ExpandMore, contentDescription = "Choose server", tint = Navy, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(30.dp), spotColor = Blue.copy(alpha = 0.10f)),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                ) {
                    val remaining = quotaRemaining
                    val max = quotaMax
                    val quotaText = when {
                        remaining != null && max != null -> "${formatBytes(remaining)} / ${formatBytes(max)}"
                        remaining != null -> formatBytes(remaining)
                        isUnlimited -> "Unlimited"
                        max != null -> "Limited to ${formatBytes(max)}"
                        else -> "—"
                    }
                    val fraction = when {
                        max != null && max > 0L && remaining != null ->
                            (remaining.toFloat() / max.toFloat()).coerceIn(0f, 1f)
                        isUnlimited -> 1f
                        remaining != null && remaining > 0L -> 1f
                        else -> 0f
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Storage, contentDescription = null, tint = Blue, modifier = Modifier.size(30.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("DATA REMAINING", color = MutedBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(quotaText, color = Navy, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 5.dp))
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(8.dp).clip(CircleShape),
                                color = Blue,
                                trackColor = Color(0xFFD5E3F9),
                            )
                        }
                        Box(Modifier.padding(horizontal = 14.dp).width(1.dp).height(90.dp).background(Color(0x889CC2FF)))
                        Column(modifier = Modifier.weight(0.9f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.AccessTime, contentDescription = null, tint = Blue, modifier = Modifier.size(30.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("CONNECTED TIME", color = MutedBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                if (connected) formatDuration(elapsedSeconds) else "00:00:00",
                                color = Navy,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (connected) "Protected" else "Not connected",
                            color = if (connected) Color(0xFF16834B) else MutedBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                        val ip = if (connected) vpnEdgeIp else realIp
                        Text(
                            ip?.let { if (it.countryCode.isNotEmpty()) "${it.ip} • ${it.countryCode}" else it.ip } ?: "IP address unavailable",
                            color = MutedBlue,
                            fontSize = 11.sp,
                        )
                        Text(
                            "Session data: ${formatBytes(liveStats.sessionBytes)}",
                            color = MutedBlue,
                            fontSize = 11.sp,
                        )
                    }
                    Column(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = connected && !isPinging, role = Role.Button) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                homeModel.triggerPing()
                            }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            when {
                                isPinging -> "Checking…"
                                connected && pingLatency != null -> "${pingLatency} ms"
                                else -> if (proxyOnlyActive) "Proxy mode" else "Secure VPN"
                            },
                            color = Blue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (connected) Text("Tap to test latency", color = MutedBlue, fontSize = 10.sp)
                    }
                }
                Spacer(Modifier.height(26.dp))
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
}
