package com.offload.companion.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offload.companion.R
import com.offload.companion.state.Coach
import com.offload.companion.ui.theme.BrandColors
import com.offload.companion.ui.theme.OffloadFonts

private val DeepGreen = Color(0xFF062617)
private val PanelGreen = Color(0xFF08351F)
private val PanelGreen2 = Color(0xFF0B4328)
private val PanelBlack = Color(0xFF041811)
private val RedDark = Color(0xFF4C160F)
private val RedMid = Color(0xFF8C291D)
private val BlueDark = Color(0xFF002A3D)
private val BlueMid = Color(0xFF005879)
private val SoftGold = Color(0xFFDDBB58)

private data class UiMetrics(val scale: Float) {
    fun dp(value: Float): Dp = (value * scale).dp
    fun sp(value: Float): TextUnit = (value * scale).sp
}

@Composable
fun FoundationScreen() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepGreen)
    ) {
        val metrics = UiMetrics((maxHeight.value / 410f).coerceIn(0.78f, 1.12f))
        val gap = metrics.dp(5f)

        FieldTexture()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = metrics.dp(6f), vertical = metrics.dp(5f)),
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            AppHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.12f),
                metrics = metrics
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.34f),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                CoachPanel(
                    coach = Coach.RED,
                    active = false,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    metrics = metrics
                )
                CoachPanel(
                    coach = Coach.BLUE,
                    active = false,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    metrics = metrics
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.17f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                OffloadTrack(
                    coach = Coach.RED,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    metrics = metrics
                )
                RestControl(
                    modifier = Modifier
                        .width(metrics.dp(62f))
                        .fillMaxHeight(),
                    metrics = metrics
                )
                OffloadTrack(
                    coach = Coach.BLUE,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    metrics = metrics
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.16f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                TurnTrack(
                    coach = Coach.RED,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    metrics = metrics
                )
                HalfControl(
                    modifier = Modifier
                        .width(metrics.dp(78f))
                        .fillMaxHeight(),
                    metrics = metrics
                )
                TurnTrack(
                    coach = Coach.BLUE,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    metrics = metrics
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.21f),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                BottomNavigationControl(
                    title = "PHASES OF PLAY",
                    subtitle = "Tackle • Offload • Scrum • And more...",
                    icon = NavIcon.PLAYBOOK,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    metrics = metrics
                )
                BottomNavigationControl(
                    title = "ACTIONS",
                    subtitle = "Pass • Offload • Kick • And more...",
                    icon = NavIcon.ACTION,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    metrics = metrics
                )
                BottomNavigationControl(
                    title = "DICE ROLLER",
                    subtitle = "Roll 1 or 2 dice • Reroll option",
                    icon = NavIcon.DICE,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    metrics = metrics
                )
                BottomNavigationControl(
                    title = "RULES",
                    subtitle = "Open the Offload rulebook (PDF)",
                    icon = NavIcon.BOOK,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    metrics = metrics
                )
            }
        }
    }
}

@Composable
private fun FieldTexture() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF071E14), DeepGreen, Color(0xFF04160F))
            )
        )
        val step = size.minDimension / 20f
        var x = -size.height
        while (x < size.width + size.height) {
            drawLine(
                color = Color.White.copy(alpha = 0.018f),
                start = androidx.compose.ui.geometry.Offset(x, 0f),
                end = androidx.compose.ui.geometry.Offset(x + size.height, size.height),
                strokeWidth = 1f
            )
            x += step
        }
    }
}

@Composable
private fun AppHeader(modifier: Modifier, metrics: UiMetrics) {
    Panel(
        modifier = modifier,
        borderColor = BrandColors.TealGreen.copy(alpha = 0.75f),
        background = Brush.horizontalGradient(listOf(Color(0xFF07321E), Color(0xFF052717), Color(0xFF07321E))),
        metrics = metrics
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = metrics.dp(9f), vertical = metrics.dp(4f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.offload_logo),
                contentDescription = "Offload",
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .width(metrics.dp(180f)),
                contentScale = ContentScale.Fit
            )

            BasicText(
                text = "THE RUGBY
BOARD GAME",
                style = TextStyle(
                    color = BrandColors.Doctor,
                    fontFamily = OffloadFonts.PoppinsLight,
                    fontSize = metrics.sp(9f),
                    letterSpacing = metrics.sp(1.4f),
                    lineHeight = metrics.sp(13f)
                ),
                modifier = Modifier.padding(start = metrics.dp(10f))
            )

            Spacer(modifier = Modifier.weight(1f))
            HeaderFieldArt(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .width(metrics.dp(160f))
            )
            Spacer(modifier = Modifier.width(metrics.dp(8f)))
            DisabledHeaderIcon("⚙", metrics)
            Spacer(modifier = Modifier.width(metrics.dp(5f)))
            DisabledHeaderIcon("▥", metrics)
            Spacer(modifier = Modifier.width(metrics.dp(5f)))
            DisabledHeaderIcon("?", metrics)
        }
    }
}

@Composable
private fun HeaderFieldArt(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val c = BrandColors.Doctor.copy(alpha = 0.15f)
        drawLine(c, androidx.compose.ui.geometry.Offset(size.width * 0.04f, size.height), androidx.compose.ui.geometry.Offset(size.width * 0.33f, 0f), 2f)
        drawLine(c, androidx.compose.ui.geometry.Offset(size.width * 0.27f, size.height), androidx.compose.ui.geometry.Offset(size.width * 0.56f, 0f), 2f)
        drawLine(c, androidx.compose.ui.geometry.Offset(size.width * 0.48f, size.height), androidx.compose.ui.geometry.Offset(size.width * 0.77f, 0f), 2f)
        drawOval(
            color = BrandColors.Doctor.copy(alpha = 0.14f),
            topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.75f, size.height * 0.18f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.18f, size.height * 0.64f)
        )
    }
}

@Composable
private fun DisabledHeaderIcon(label: String, metrics: UiMetrics) {
    val shape = RoundedCornerShape(metrics.dp(7f))
    Box(
        modifier = Modifier
            .size(metrics.dp(34f))
            .clip(shape)
            .background(Color(0xFF0A3422))
            .border(BorderStroke(metrics.dp(1f), SoftGold), shape),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = label,
            style = TextStyle(
                color = BrandColors.Doctor.copy(alpha = 0.9f),
                fontSize = metrics.sp(15f),
                fontFamily = OffloadFonts.PoppinsLight,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun CoachPanel(coach: Coach, active: Boolean, modifier: Modifier, metrics: UiMetrics) {
    val accent = if (coach == Coach.RED) BrandColors.BloodySalmon else BrandColors.BrilliantBlue
    val dark = if (coach == Coach.RED) RedDark else BlueDark
    val mid = if (coach == Coach.RED) RedMid else BlueMid
    val border = if (active) BrandColors.PortOfGold else accent.copy(alpha = 0.9f)
    val title = if (coach == Coach.RED) "RED COACH" else "BLUE COACH"

    Panel(
        modifier = modifier,
        borderColor = border,
        borderWidth = if (active) metrics.dp(2f) else metrics.dp(1.2f),
        background = Brush.horizontalGradient(listOf(mid, dark, PanelBlack)),
        metrics = metrics
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.32f)
                    .padding(horizontal = metrics.dp(10f), vertical = metrics.dp(4f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RugbyBallIcon(
                    color = accent,
                    modifier = Modifier.size(metrics.dp(24f))
                )
                BasicText(
                    text = title,
                    style = TextStyle(
                        color = BrandColors.Doctor,
                        fontFamily = OffloadFonts.ComicSerifPro,
                        fontWeight = FontWeight.Bold,
                        fontSize = metrics.sp(24f)
                    ),
                    modifier = Modifier.padding(start = metrics.dp(7f))
                )
                Spacer(modifier = Modifier.weight(1f))
                if (active) ActiveBadge(metrics)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.68f)
                    .padding(horizontal = metrics.dp(9f), vertical = metrics.dp(5f)),
                horizontalArrangement = Arrangement.spacedBy(metrics.dp(8f))
            ) {
                Column(
                    modifier = Modifier.weight(1.35f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(metrics.dp(5f))
                ) {
                    ScoreActionButton(
                        label = "+ Try",
                        icon = ActionIcon.BALL,
                        accent = accent,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        metrics = metrics
                    )
                    ScoreActionButton(
                        label = "+ Conversion",
                        icon = ActionIcon.GOAL,
                        accent = accent,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        metrics = metrics
                    )
                }
                ScoreDisplay(
                    accent = accent,
                    modifier = Modifier.weight(0.78f).fillMaxHeight(),
                    metrics = metrics
                )
            }
        }
    }
}

@Composable
private fun ActiveBadge(metrics: UiMetrics) {
    val shape = RoundedCornerShape(metrics.dp(10f))
    Box(
        modifier = Modifier
            .clip(shape)
            .background(Color(0xFF493610))
            .border(metrics.dp(1.2f), BrandColors.PortOfGold, shape)
            .padding(horizontal = metrics.dp(9f), vertical = metrics.dp(4f)),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            "ACTIVE",
            style = TextStyle(
                color = BrandColors.Doctor,
                fontFamily = OffloadFonts.ComicSerifPro,
                fontSize = metrics.sp(10f),
                fontWeight = FontWeight.Bold
            )
        )
    }
}

private enum class ActionIcon { BALL, GOAL }

@Composable
private fun ScoreActionButton(label: String, icon: ActionIcon, accent: Color, modifier: Modifier, metrics: UiMetrics) {
    val shape = RoundedCornerShape(metrics.dp(7f))
    Row(
        modifier = modifier
            .clip(shape)
            .background(accent.copy(alpha = 0.18f))
            .border(metrics.dp(1f), accent.copy(alpha = 0.95f), shape)
            .padding(horizontal = metrics.dp(8f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon == ActionIcon.BALL) {
            RugbyBallIcon(BrandColors.Doctor, Modifier.size(metrics.dp(24f)))
        } else {
            GoalpostIcon(BrandColors.Doctor, Modifier.size(metrics.dp(24f)))
        }
        BasicText(
            text = label,
            style = TextStyle(
                color = BrandColors.Doctor,
                fontFamily = OffloadFonts.ComicSerifPro,
                fontWeight = FontWeight.Bold,
                fontSize = metrics.sp(17f)
            ),
            modifier = Modifier.padding(start = metrics.dp(9f))
        )
    }
}

@Composable
private fun ScoreDisplay(accent: Color, modifier: Modifier, metrics: UiMetrics) {
    val shape = RoundedCornerShape(metrics.dp(7f))
    Column(
        modifier = modifier
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.28f))
            .border(metrics.dp(1.3f), accent, shape)
            .padding(metrics.dp(4f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BasicText(
            "SCORE",
            style = TextStyle(
                color = BrandColors.Doctor,
                fontFamily = OffloadFonts.ComicSerifPro,
                fontSize = metrics.sp(12f),
                fontWeight = FontWeight.Bold
            )
        )
        BasicText(
            "0",
            style = TextStyle(
                color = BrandColors.Doctor,
                fontFamily = OffloadFonts.ComicSerifPro,
                fontSize = metrics.sp(42f),
                lineHeight = metrics.sp(42f),
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun OffloadTrack(coach: Coach, modifier: Modifier, metrics: UiMetrics) {
    val accent = if (coach == Coach.RED) BrandColors.BloodySalmon else BrandColors.BrilliantBlue
    Panel(
        modifier = modifier,
        borderColor = BrandColors.TealGreen.copy(alpha = 0.75f),
        background = Brush.verticalGradient(listOf(PanelGreen2, PanelGreen)),
        metrics = metrics
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = metrics.dp(6f), vertical = metrics.dp(3f))
        ) {
            Row(
                modifier = Modifier.weight(0.35f).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RugbyBallIcon(accent, Modifier.size(metrics.dp(18f)))
                BasicText(
                    "OFFLOAD TRACK",
                    style = TextStyle(
                        color = BrandColors.Doctor,
                        fontFamily = OffloadFonts.ComicSerifPro,
                        fontWeight = FontWeight.Bold,
                        fontSize = metrics.sp(14f)
                    ),
                    modifier = Modifier.padding(start = metrics.dp(5f))
                )
            }
            Row(
                modifier = Modifier.weight(0.65f).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(metrics.dp(5f))
            ) {
                TrackSquare("−", modifier = Modifier.weight(0.72f).fillMaxHeight(), metrics = metrics)
                listOf("2+", "3+", "4+", "5+", "6+").forEachIndexed { index, label ->
                    OffloadMarker(
                        label = label,
                        selected = index == 0,
                        accent = accent,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        metrics = metrics
                    )
                }
                TrackSquare("+", modifier = Modifier.weight(0.72f).fillMaxHeight(), metrics = metrics)
            }
        }
    }
}

@Composable
private fun TrackSquare(label: String, modifier: Modifier, metrics: UiMetrics) {
    val shape = RoundedCornerShape(metrics.dp(7f))
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xFF0A3022))
            .border(metrics.dp(1f), SoftGold, shape),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            label,
            style = TextStyle(
                color = BrandColors.Doctor,
                fontFamily = OffloadFonts.PoppinsLight,
                fontWeight = FontWeight.Bold,
                fontSize = metrics.sp(20f),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun OffloadMarker(label: String, selected: Boolean, accent: Color, modifier: Modifier, metrics: UiMetrics) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val bg = if (selected) accent.copy(alpha = 0.92f) else Color(0xFF0B3024)
        Box(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(bg)
                .border(metrics.dp(1.2f), SoftGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                label,
                style = TextStyle(
                    color = BrandColors.Doctor,
                    fontFamily = OffloadFonts.ComicSerifPro,
                    fontWeight = FontWeight.Bold,
                    fontSize = metrics.sp(14f),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun RestControl(modifier: Modifier, metrics: UiMetrics) {
    val shape = RoundedCornerShape(metrics.dp(7f))
    Box(
        modifier = modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF143D28), Color(0xFF082A1D))))
            .border(metrics.dp(1.2f), BrandColors.PortOfGold, shape),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            "REST",
            style = TextStyle(
                color = BrandColors.PortOfGold,
                fontFamily = OffloadFonts.ComicSerifPro,
                fontWeight = FontWeight.Bold,
                fontSize = metrics.sp(12f),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun TurnTrack(coach: Coach, modifier: Modifier, metrics: UiMetrics) {
    val accent = if (coach == Coach.RED) BrandColors.BloodySalmon else BrandColors.BrilliantBlue
    Panel(
        modifier = modifier,
        borderColor = BrandColors.TealGreen.copy(alpha = 0.75f),
        background = Brush.verticalGradient(listOf(PanelGreen2, PanelGreen)),
        metrics = metrics
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = metrics.dp(5f), vertical = metrics.dp(3f))) {
            Row(
                modifier = Modifier.weight(0.32f).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(metrics.dp(12f), metrics.dp(3f)).background(accent, RoundedCornerShape(50)))
                BasicText(
                    "TURN TRACK",
                    style = TextStyle(
                        color = BrandColors.Doctor,
                        fontFamily = OffloadFonts.ComicSerifPro,
                        fontWeight = FontWeight.Bold,
                        fontSize = metrics.sp(11.5f)
                    ),
                    modifier = Modifier.padding(start = metrics.dp(5f))
                )
            }
            Row(
                modifier = Modifier.weight(0.2f).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    "START TURN",
                    style = TextStyle(color = BrandColors.Doctor.copy(alpha = 0.85f), fontFamily = OffloadFonts.PoppinsLight, fontSize = metrics.sp(7.5f))
                )
                Spacer(Modifier.weight(1f))
                BasicText(
                    "END TURN",
                    style = TextStyle(color = BrandColors.Doctor.copy(alpha = 0.85f), fontFamily = OffloadFonts.PoppinsLight, fontSize = metrics.sp(7.5f))
                )
            }
            Row(
                modifier = Modifier.weight(0.48f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(metrics.dp(2.5f))
            ) {
                listOf("START", "1", "2", "3", "4", "5", "6", "7", "OT").forEachIndexed { index, label ->
                    TurnCell(
                        label = label,
                        selected = index == 0,
                        accent = accent,
                        modifier = Modifier.weight(if (label == "START") 1.35f else 1f).fillMaxHeight(),
                        metrics = metrics
                    )
                }
            }
        }
    }
}

@Composable
private fun TurnCell(label: String, selected: Boolean, accent: Color, modifier: Modifier, metrics: UiMetrics) {
    val shape = RoundedCornerShape(metrics.dp(3.5f))
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) accent.copy(alpha = 0.82f) else Color(0xFF0A2A20))
            .border(metrics.dp(0.8f), if (selected) BrandColors.Doctor else SoftGold.copy(alpha = 0.75f), shape),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            label,
            style = TextStyle(
                color = BrandColors.Doctor,
                fontFamily = if (label == "START") OffloadFonts.ComicSerifPro else OffloadFonts.PoppinsLight,
                fontWeight = FontWeight.Bold,
                fontSize = metrics.sp(if (label == "START") 9f else 10f),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun HalfControl(modifier: Modifier, metrics: UiMetrics) {
    Panel(
        modifier = modifier,
        borderColor = BrandColors.TealGreen.copy(alpha = 0.9f),
        background = Brush.verticalGradient(listOf(PanelGreen2, PanelGreen)),
        metrics = metrics
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(metrics.dp(4f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(metrics.dp(3f))
        ) {
            BasicText(
                "HALF",
                style = TextStyle(
                    color = BrandColors.Doctor,
                    fontFamily = OffloadFonts.ComicSerifPro,
                    fontWeight = FontWeight.Bold,
                    fontSize = metrics.sp(10f)
                )
            )
            HalfState("H1", selected = true, modifier = Modifier.weight(1f).fillMaxWidth(), metrics = metrics)
            HalfState("H2", selected = false, modifier = Modifier.weight(1f).fillMaxWidth(), metrics = metrics)
        }
    }
}

@Composable
private fun HalfState(label: String, selected: Boolean, modifier: Modifier, metrics: UiMetrics) {
    val shape = RoundedCornerShape(metrics.dp(4f))
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) Color(0xFF5A4610) else Color(0xFF08251B))
            .border(metrics.dp(1f), if (selected) BrandColors.PortOfGold else Color.White.copy(alpha = 0.18f), shape),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            label,
            style = TextStyle(
                color = if (selected) BrandColors.Doctor else BrandColors.Doctor.copy(alpha = 0.35f),
                fontFamily = OffloadFonts.ComicSerifPro,
                fontWeight = FontWeight.Bold,
                fontSize = metrics.sp(11f)
            )
        )
    }
}

private enum class NavIcon { PLAYBOOK, ACTION, DICE, BOOK }

@Composable
private fun BottomNavigationControl(title: String, subtitle: String, icon: NavIcon, modifier: Modifier, metrics: UiMetrics) {
    Panel(
        modifier = modifier,
        borderColor = SoftGold.copy(alpha = 0.9f),
        background = Brush.verticalGradient(listOf(Color(0xFF0A4729), Color(0xFF07311F))),
        metrics = metrics
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = metrics.dp(8f), vertical = metrics.dp(5f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationIcon(icon, Modifier.size(metrics.dp(35f)), BrandColors.Doctor)
            Column(
                modifier = Modifier.weight(1f).padding(start = metrics.dp(7f)),
                verticalArrangement = Arrangement.Center
            ) {
                BasicText(
                    title,
                    style = TextStyle(
                        color = BrandColors.Doctor,
                        fontFamily = OffloadFonts.ComicSerifPro,
                        fontWeight = FontWeight.Bold,
                        fontSize = metrics.sp(13f)
                    )
                )
                BasicText(
                    subtitle,
                    style = TextStyle(
                        color = BrandColors.Doctor.copy(alpha = 0.83f),
                        fontFamily = OffloadFonts.PoppinsLight,
                        fontSize = metrics.sp(7.8f),
                        lineHeight = metrics.sp(9f)
                    ),
                    modifier = Modifier.padding(top = metrics.dp(2f))
                )
            }
            BasicText(
                "›",
                style = TextStyle(
                    color = BrandColors.PortOfGold,
                    fontFamily = OffloadFonts.PoppinsLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = metrics.sp(24f)
                )
            )
        }
    }
}

@Composable
private fun NavigationIcon(icon: NavIcon, modifier: Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val sw = size.minDimension * 0.07f
        when (icon) {
            NavIcon.PLAYBOOK -> {
                drawRoundRect(color, style = Stroke(sw), cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.08f), topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.18f, size.height * 0.1f), size = androidx.compose.ui.geometry.Size(size.width * 0.64f, size.height * 0.8f))
                for (i in 0..3) drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.33f, size.height * (0.28f + i * 0.14f)), androidx.compose.ui.geometry.Offset(size.width * 0.68f, size.height * (0.28f + i * 0.14f)), sw * 0.65f, StrokeCap.Round)
            }
            NavIcon.ACTION -> {
                drawCircle(color, size.minDimension * 0.1f, androidx.compose.ui.geometry.Offset(size.width * 0.65f, size.height * 0.2f))
                drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.58f, size.height * 0.34f), androidx.compose.ui.geometry.Offset(size.width * 0.45f, size.height * 0.55f), sw, StrokeCap.Round)
                drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.48f, size.height * 0.48f), androidx.compose.ui.geometry.Offset(size.width * 0.72f, size.height * 0.52f), sw, StrokeCap.Round)
                drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.46f, size.height * 0.55f), androidx.compose.ui.geometry.Offset(size.width * 0.3f, size.height * 0.82f), sw, StrokeCap.Round)
                drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.47f, size.height * 0.56f), androidx.compose.ui.geometry.Offset(size.width * 0.63f, size.height * 0.82f), sw, StrokeCap.Round)
            }
            NavIcon.DICE -> {
                drawRoundRect(color, style = Stroke(sw), cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.08f), topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.2f), size = androidx.compose.ui.geometry.Size(size.width * 0.45f, size.height * 0.55f))
                drawRoundRect(color, style = Stroke(sw), cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.08f), topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.45f, size.height * 0.35f), size = androidx.compose.ui.geometry.Size(size.width * 0.42f, size.height * 0.5f))
                drawCircle(color, sw * 0.8f, androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.45f))
                drawCircle(color, sw * 0.8f, androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.58f))
            }
            NavIcon.BOOK -> {
                val p = Path().apply {
                    moveTo(size.width * 0.12f, size.height * 0.22f)
                    quadraticBezierTo(size.width * 0.33f, size.height * 0.13f, size.width * 0.5f, size.height * 0.27f)
                    quadraticBezierTo(size.width * 0.67f, size.height * 0.13f, size.width * 0.88f, size.height * 0.22f)
                    lineTo(size.width * 0.88f, size.height * 0.78f)
                    quadraticBezierTo(size.width * 0.67f, size.height * 0.7f, size.width * 0.5f, size.height * 0.83f)
                    quadraticBezierTo(size.width * 0.33f, size.height * 0.7f, size.width * 0.12f, size.height * 0.78f)
                    close()
                }
                drawPath(p, color, style = Stroke(sw))
                drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.27f), androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.83f), sw)
            }
        }
    }
}

@Composable
private fun RugbyBallIcon(color: Color, modifier: Modifier) {
    Canvas(modifier = modifier) {
        drawOval(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.18f, size.height * 0.05f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.64f, size.height * 0.9f),
            style = Stroke(size.minDimension * 0.09f)
        )
        drawLine(
            color,
            androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.78f),
            androidx.compose.ui.geometry.Offset(size.width * 0.72f, size.height * 0.22f),
            size.minDimension * 0.07f,
            StrokeCap.Round
        )
    }
}

@Composable
private fun GoalpostIcon(color: Color, modifier: Modifier) {
    Canvas(modifier = modifier) {
        val sw = size.minDimension * 0.08f
        drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.25f, size.height * 0.18f), androidx.compose.ui.geometry.Offset(size.width * 0.25f, size.height * 0.64f), sw, StrokeCap.Round)
        drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.75f, size.height * 0.18f), androidx.compose.ui.geometry.Offset(size.width * 0.75f, size.height * 0.64f), sw, StrokeCap.Round)
        drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.25f, size.height * 0.55f), androidx.compose.ui.geometry.Offset(size.width * 0.75f, size.height * 0.55f), sw, StrokeCap.Round)
        drawLine(color, androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.55f), androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.9f), sw, StrokeCap.Round)
    }
}

@Composable
private fun Panel(
    modifier: Modifier,
    borderColor: Color,
    background: Brush,
    metrics: UiMetrics,
    borderWidth: Dp = metrics.dp(1f),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(metrics.dp(7f))
    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(BorderStroke(borderWidth, borderColor), shape)
    ) {
        content()
    }
}
