package com.lastdance.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lastdance.app.R

val AstaSans = FontFamily(
    Font(R.font.asta_sans_400, FontWeight.Normal),
    Font(R.font.asta_sans_500, FontWeight.Medium),
    Font(R.font.asta_sans_600, FontWeight.SemiBold),
    Font(R.font.asta_sans_700, FontWeight.Bold),
    Font(R.font.asta_sans_800, FontWeight.ExtraBold),
)

private fun t(size: Int, line: Int, w: FontWeight) =
    TextStyle(fontFamily = AstaSans, fontSize = size.sp, lineHeight = line.sp, fontWeight = w)

/** 타입 램프 — 웹 `typography.css`와 동일한 이름/값 */
object LDType {
    val headlineLBold = t(60, 80, FontWeight.Bold)
    val headlineSBold = t(48, 64, FontWeight.Bold)
    val headlineSXBold = t(48, 64, FontWeight.ExtraBold)
    val titleLBold = t(40, 52, FontWeight.Bold)
    val titleLXBold = t(40, 52, FontWeight.ExtraBold)
    val titleMBold = t(36, 44, FontWeight.Bold)
    val titleMSemibold = t(36, 44, FontWeight.SemiBold)
    val titleMXBold = t(36, 44, FontWeight.ExtraBold)
    val titleSRegular = t(32, 40, FontWeight.Normal)
    val titleSBold = t(32, 40, FontWeight.Bold)
    val titleSXBold = t(32, 40, FontWeight.ExtraBold)
    val bodyLRegular = t(30, 38, FontWeight.Normal)
    val bodyLBold = t(30, 38, FontWeight.Bold)
    val bodyMRegular = t(28, 36, FontWeight.Normal)
    val bodyMSemibold = t(28, 36, FontWeight.SemiBold)
    val bodyMBold = t(28, 36, FontWeight.Bold)
    val bodySRegular = t(26, 34, FontWeight.Normal)
    val bodySSemibold = t(26, 34, FontWeight.SemiBold)
    val bodySBold = t(26, 34, FontWeight.Bold)
    val labelMRegular = t(24, 28, FontWeight.Normal)
    val labelMMedium = t(24, 28, FontWeight.Medium)
    val labelMBold = t(24, 28, FontWeight.Bold)
    val labelSMedium = t(22, 26, FontWeight.Medium)
    val labelSRegular = t(20, 24, FontWeight.Normal)
    val captionMedium = t(18, 22, FontWeight.Medium)
}
