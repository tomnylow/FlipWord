package com.tomnylow.flipword.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

object customIcons {


    val TablerCards: ImageVector
        get() {
            if (_TablerCards != null) return _TablerCards!!

            _TablerCards = ImageVector.Builder(
                name = "cards",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(3.604f, 7.197f)
                    lineToRelative(7.138f, -3.109f)
                    arcToRelative(0.96f, 0.96f, 0f, false, true, 1.27f, 0.527f)
                    lineToRelative(4.924f, 11.902f)
                    arcToRelative(1f, 1f, 0f, false, true, -0.514f, 1.304f)
                    lineToRelative(-7.137f, 3.109f)
                    arcToRelative(0.96f, 0.96f, 0f, false, true, -1.271f, -0.527f)
                    lineToRelative(-4.924f, -11.903f)
                    arcToRelative(1f, 1f, 0f, false, true, 0.514f, -1.304f)
                    close()
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(15f, 4f)
                    horizontalLineToRelative(1f)
                    arcToRelative(1f, 1f, 0f, false, true, 1f, 1f)
                    verticalLineToRelative(3.5f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(20f, 6f)
                    curveToRelative(0.264f, 0.112f, 0.52f, 0.217f, 0.768f, 0.315f)
                    arcToRelative(1f, 1f, 0f, false, true, 0.53f, 1.311f)
                    lineToRelative(-2.298f, 5.374f)
                }
            }.build()

            return _TablerCards!!
        }

    private var _TablerCards: ImageVector? = null


    val FontAwesomeMagic: ImageVector
        get() {
            if (_FontAwesomeMagic != null) return _FontAwesomeMagic!!

            _FontAwesomeMagic = ImageVector.Builder(
                name = "magic",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 512f,
                viewportHeight = 512f
            ).apply {
                path(
                    fill = SolidColor(Color.Black)
                ) {
                    moveTo(224f, 96f)
                    lineToRelative(16f, -32f)
                    lineToRelative(32f, -16f)
                    lineToRelative(-32f, -16f)
                    lineToRelative(-16f, -32f)
                    lineToRelative(-16f, 32f)
                    lineToRelative(-32f, 16f)
                    lineToRelative(32f, 16f)
                    lineToRelative(16f, 32f)
                    close()
                    moveTo(80f, 160f)
                    lineToRelative(26.66f, -53.33f)
                    lineTo(160f, 80f)
                    lineToRelative(-53.34f, -26.67f)
                    lineTo(80f, 0f)
                    lineTo(53.34f, 53.33f)
                    lineTo(0f, 80f)
                    lineToRelative(53.34f, 26.67f)
                    lineTo(80f, 160f)
                    close()
                    moveToRelative(352f, 128f)
                    lineToRelative(-26.66f, 53.33f)
                    lineTo(352f, 368f)
                    lineToRelative(53.34f, 26.67f)
                    lineTo(432f, 448f)
                    lineToRelative(26.66f, -53.33f)
                    lineTo(512f, 368f)
                    lineToRelative(-53.34f, -26.67f)
                    lineTo(432f, 288f)
                    close()
                    moveToRelative(70.62f, -193.77f)
                    lineTo(417.77f, 9.38f)
                    curveTo(411.53f, 3.12f, 403.34f, 0f, 395.15f, 0f)
                    curveToRelative(-8.19f, 0f, -16.38f, 3.12f, -22.63f, 9.38f)
                    lineTo(9.38f, 372.52f)
                    curveToRelative(-12.5f, 12.5f, -12.5f, 32.76f, 0f, 45.25f)
                    lineToRelative(84.85f, 84.85f)
                    curveToRelative(6.25f, 6.25f, 14.44f, 9.37f, 22.62f, 9.37f)
                    curveToRelative(8.19f, 0f, 16.38f, -3.12f, 22.63f, -9.37f)
                    lineToRelative(363.14f, -363.15f)
                    curveToRelative(12.5f, -12.48f, 12.5f, -32.75f, 0f, -45.24f)
                    close()
                    moveTo(359.45f, 203.46f)
                    lineToRelative(-50.91f, -50.91f)
                    lineToRelative(86.6f, -86.6f)
                    lineToRelative(50.91f, 50.91f)
                    lineToRelative(-86.6f, 86.6f)
                    close()
                }
            }.build()

            return _FontAwesomeMagic!!
        }

    private var _FontAwesomeMagic: ImageVector? = null
}