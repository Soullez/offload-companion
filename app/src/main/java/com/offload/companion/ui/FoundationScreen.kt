package com.offload.companion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import com.offload.companion.BuildConfig
import com.offload.companion.R
import com.offload.companion.ui.theme.BrandColors
import com.offload.companion.ui.theme.OffloadFonts

@Composable
fun FoundationScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF092A19))
            .padding(horizontal = 40.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.offload_logo),
                contentDescription = "Offload",
                modifier = Modifier.sizeIn(maxWidth = 520.dp, maxHeight = 150.dp),
                contentScale = ContentScale.Fit
            )

            BasicText(
                text = "OFFLOAD COMPANION",
                style = TextStyle(
                    color = BrandColors.Doctor,
                    fontFamily = OffloadFonts.Moderniz,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )
            )

            BasicText(
                text = BuildConfig.APP_VERSION,
                style = TextStyle(
                    color = BrandColors.PortOfGold,
                    fontFamily = OffloadFonts.PoppinsLight,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
