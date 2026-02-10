@file:Suppress("UseJBColor")

package com.github.minetoblend.osuprogressbar.ui

import com.github.minetoblend.osuprogressbar.settings.OsuProgressBarSettings
import com.github.minetoblend.osuprogressbar.skinning.Skin
import com.github.minetoblend.osuprogressbar.skinning.SkinSource
import com.github.minetoblend.osuprogressbar.skinning.Texture
import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicProgressBarUI
import kotlin.math.min
import kotlin.math.roundToInt

class OsuProgressbarUI : BasicProgressBarUI() {
    companion object {
        @JvmStatic
        @Suppress("ACCIDENTAL_OVERRIDE")
        fun createUI(c: JComponent): ComponentUI {
            c.border = JBUI.Borders.empty().asUIResource()
            return OsuProgressbarUI()
        }
    }

    override fun getPreferredSize(c: JComponent): Dimension {
        return Dimension(super.getPreferredSize(c).width, JBUI.scale(30))
    }

    override fun installListeners() {
        super.installListeners()

        progressBar.addComponentListener(object : ComponentAdapter() {
            override fun componentShown(e: ComponentEvent?) {
                super.componentShown(e)
            }

            override fun componentHidden(e: ComponentEvent?) {
                super.componentHidden(e)
            }
        })
    }

    override fun paintIndeterminate(g: Graphics, c: JComponent) {
        if (g !is Graphics2D)
            return

        if (progressBar.orientation != SwingConstants.HORIZONTAL || !c.componentOrientation.isLeftToRight)
            return super.paintIndeterminate(g, c)

        val settings = OsuProgressBarSettings.getInstance()
        val width = progressBar.width
        val height = progressBar.preferredSize.height - 2

        draw(g, c) {
            val drawScale = height / 128f

            boxRect = getBox(boxRect)

            val middleFrame = frameCount / 2

            val isReverse = animationIndex >= middleFrame

            val sliderb = SkinSource.get { sliderb }

            var sliderBIndex = animationIndex % sliderb.size
            if (isReverse)
                sliderBIndex = sliderb.lastIndex - sliderBIndex

            var spanProgress = animationIndex.toFloat() / frameCount * 2f
            if (isReverse)
                spanProgress -= 1

            val centerY = c.height / 2

            sliderBody(
                accentColor = SkinSource.get { config }.sliderTrackOverride ?: settings.comboColor,
                x = 0,
                y = (c.height - height) / 2,
                width = width,
                height = height,
            )

            hitCircle(
                reverseArrow = SkinSource.get { reverseArrow },
                x = height / 2,
                y = centerY,
                scale = drawScale,
                spanProgress = spanProgress.takeIf { !isReverse },
                arrowDirection = ArrowDirection.Right,
            )

            hitCircle(
                getCircleTexture = { sliderEndCircle },
                getOverlayTexture = { sliderEndCircleOverlay },
                reverseArrow = SkinSource.get { reverseArrow },
                x = width - height / 2,
                y = centerY,
                scale = drawScale,
                spanProgress = spanProgress.takeIf { isReverse },
                arrowDirection = ArrowDirection.Left,
            )

            sprite(SkinSource.getProvider { it.sliderb != null }?.sliderbND) {
                tint = Color(5, 5, 5, 255)
                scale = drawScale
                x = boxRect.centerX.toInt()
                y = boxRect.centerY.toInt()
            }

            sprite(sliderb[sliderBIndex]) {
                tint = when {
                    SkinSource.get { config }.allowSliderBallTint -> settings.comboColor
                    else -> null
                }
                scale = drawScale
                x = boxRect.centerX.toInt()
                y = boxRect.centerY.toInt()
            }

            sprite(SkinSource.get { sliderFollowCircle }) {
                scale = drawScale
                x = boxRect.centerX.toInt()
                y = boxRect.centerY.toInt()
            }
        }
    }

    override fun paintDeterminate(g: Graphics, c: JComponent) {
        if (g !is Graphics2D)
            return

        if (progressBar.orientation != SwingConstants.HORIZONTAL || !c.componentOrientation.isLeftToRight)
            return super.paintDeterminate(g, c)

        val settings = OsuProgressBarSettings.getInstance()

        val width = progressBar.width
        val height = progressBar.preferredSize.height - 2

        val centerY = c.height / 2

        val amountFull = ((width - height) * progressBar.percentComplete).toInt()

        draw(g, c) {
            sliderBody(
                accentColor = SkinSource.get { config }.sliderTrackOverride ?: settings.comboColor,
                x = 0,
                y = (c.height - height) / 2,
                width = width,
                height = height
            )

            val drawScale = height / 128f

            hitCircle(
                reverseArrow = null,
                x = height / 2 + amountFull,
                y = centerY,
                scale = drawScale,
                spanProgress = null,
                arrowDirection = ArrowDirection.Right,
            )
            hitCircle(
                getCircleTexture = { sliderEndCircle },
                getOverlayTexture = { sliderEndCircleOverlay },
                reverseArrow = null,
                x = width - height / 2,
                y = centerY,
                scale = drawScale,
                spanProgress = null,
                arrowDirection = ArrowDirection.Left,
            )

            val sliderb = SkinSource.get { sliderb }

            val sliderBIndex = (amountFull / 4) % sliderb.size


            sprite(SkinSource.getProvider { it.sliderb != null }?.sliderbND) {
                tint = Color(5, 5, 5, 255)
                scale = drawScale
                x = amountFull + height / 2
                y = centerY
            }

            sprite(sliderb[sliderBIndex]) {
                scale = drawScale
                x = amountFull + height / 2
                y = centerY
            }

            sprite(SkinSource.get { sliderFollowCircle }) {
                scale = drawScale
                x = amountFull + height / 2
                y = centerY
            }
        }
    }

    private fun DrawScope.hitCircle(
        getCircleTexture: Skin.() -> Texture? = { hitCircle },
        getOverlayTexture: Skin.() -> Texture? = { hitCircleOverlay },
        reverseArrow: Texture? = null,
        x: Int,
        y: Int,
        scale: Float,
        spanProgress: Float?,
        arrowDirection: ArrowDirection = ArrowDirection.Left
    ) {
        val provider = SkinSource.getProvider { getCircleTexture(it) != null }
            ?: SkinSource.getProvider { it.hitCircle != null }!!

        var hasCircleSprite = true

        val circleTexture =
            (provider.getCircleTexture() ?: SkinSource.get { hitCircle }.also { hasCircleSprite = false })

        val overlayTexture = if (hasCircleSprite) provider.getOverlayTexture() else SkinSource.get { hitCircleOverlay }

        sprite(circleTexture) {
            this.scale = scale
            this.x = x
            this.y = y
            tint = OsuProgressBarSettings.getInstance().comboColor
        }

        sprite(overlayTexture) {
            this.scale = scale
            this.x = x
            this.y = y
        }

        sprite(reverseArrow) {
            this.scale = scale
            this.x = x
            this.y = y
            flipX = arrowDirection == ArrowDirection.Left
        }

        if (spanProgress == null)
            return

        val progress = (spanProgress * 4).coerceIn(0f, 1f)

        if (progress >= 1)
            return

        val alpha = 1 - progress
        val circleScale = scale * 1f.lerp(1.4f, easeOut(progress))

        withComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)) {
            sprite(circleTexture) {
                this.scale = circleScale
                this.x = x
                this.y = y
            }

            sprite(overlayTexture) {
                this.scale = circleScale
                this.x = x
                this.y = y
            }

            sprite(reverseArrow) {
                this.scale = circleScale
                this.x = x
                this.y = y
                flipX = arrowDirection == ArrowDirection.Left
            }
        }
    }

    private inline fun DrawScope.withComposite(composite: Composite, block: () -> Unit) {
        val oldComposite = graphics.composite

        try {
            graphics.composite = composite
            block()
        } finally {
            graphics.composite = oldComposite
        }
    }


    private fun DrawScope.sliderBody(
        accentColor: Color,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) {
        val (positions, colors) = sliderBodyGradientStops(accentColor)

        graphics.paint = LinearGradientPaint(
            0f,
            y.toFloat(),
            0f,
            y + height.toFloat(),
            positions,
            colors,
        )

        graphics.fillRect(x + height / 2, y, width - height, height)

        graphics.paint = RadialGradientPaint(
            Point2D.Float(
                x + height / 2f,
                y + height / 2f
            ),
            height / 2f,
            positions.take(6).map { 1 - it * 2 }.reversed().toFloatArray(),
            colors.take(6).reversed().toTypedArray(),
        )

        graphics.fillRect(x, y, height / 2, height)

        graphics.paint = RadialGradientPaint(
            Point2D.Float(
                x + width - height / 2f,
                y + height / 2f
            ),
            height / 2f,
            positions.take(6).map { 1 - it * 2 }.reversed().toFloatArray(),
            colors.take(6).reversed().toTypedArray(),
        )

        graphics.fillRect(x + width - height / 2, y, height / 2, height)
    }

    private fun sliderBodyGradientStops(accentColor: Color): Pair<FloatArray, Array<Color>> {
        val shadowPortion = 1f - (59f / 64f)
        val borderPortion = 0.1875f

        val outerShadowColor = Color.BLACK.opacity(0f)
        val innerShadowColor = Color.BLACK.opacity(0.25f)
        val borderColor = SkinSource.get { config }.sliderBorder

        val outerColor = accentColor.darken(0.1f).opacity(0.7f)
        val innerColor = accentColor.lighten(0.5f).opacity(0.7f)
        val aaWidth = 0.01f

        val stops = floatArrayOf(
            0f,
            shadowPortion / 2,
            shadowPortion / 2 + aaWidth,
            borderPortion / 2,
            borderPortion / 2 + aaWidth,
            0.5f,
            1f - borderPortion / 2 - aaWidth,
            1f - borderPortion / 2,
            1f - shadowPortion / 2 - aaWidth,
            1f - shadowPortion / 2,
            1f,
        )
        val colors = arrayOf(
            outerShadowColor,
            innerShadowColor,
            borderColor,
            borderColor,
            outerColor,
            innerColor,
            outerColor,
            borderColor,
            borderColor,
            innerShadowColor,
            outerShadowColor,
        )

        return Pair(stops, colors)
    }

    private fun Color.opacity(alpha: Float) = Color(red, green, blue, (alpha * 255).toInt())

    private operator fun Color.times(amount: Float) = Color(
        red / 255f * amount,
        green / 255f * amount,
        blue / 255f * amount,
        alpha / 255f
    )

    private fun Color.darken(amount: Float) = this * (1 / (1 + amount))

    private fun Color.lighten(amount: Float): Color {
        val x = amount * 0.5f

        return Color(
            min(1f, red / 255f * (1 + 0.5f * x) + x),
            min(1f, green / 255f * (1 + 0.5f * x) + x),
            min(1f, blue / 255f * (1 + 0.5f * x) + x),
            alpha / 255f
        )
    }

    override fun getBoxLength(availableLength: Int, otherDimension: Int): Int = otherDimension

    private fun Float.lerp(other: Float, factor: Float) = this + (other - this) * factor

    private fun easeOut(x: Float) = 1 - ((1 - x) * (1 - x))

    override fun incrementAnimationIndex() {
        super.incrementAnimationIndex()
        // full repaint required due to transparency being a thing
        progressBar.repaint()
    }

    private enum class ArrowDirection {
        Left,
        Right,
    }
}