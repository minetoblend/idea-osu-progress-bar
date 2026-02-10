package com.github.minetoblend.osuprogressbar.ui

import com.github.minetoblend.osuprogressbar.skinning.Texture
import com.intellij.util.ui.GraphicsUtil
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JComponent
import kotlin.math.roundToInt

fun draw(
    graphics: Graphics2D,
    component: JComponent,
    block: DrawScope.() -> Unit,
) {
    val config = GraphicsUtil.setupAAPainting(graphics)
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)

    DrawScope(graphics, component).block()

    config.restore()
}

class DrawScope(
    val graphics: Graphics2D,
    val component: JComponent,
) {
    fun sprite(
        texture: Texture?,
        block: SpriteOptions.() -> Unit = {}
    ) {
        if (texture == null) return

        val options = SpriteOptions().apply(block)

        val width = texture.width * options.scale * if (options.flipX) -1 else 1
        val height = texture.width * options.scale

        val image = when (val tint = options.tint) {
            null -> texture.image
            else -> texture.withTint(tint)
        }

        graphics.drawImage(
            image,
            (options.x - width / 2).roundToInt(),
            (options.y - height / 2).roundToInt(),
            width.roundToInt(),
            height.roundToInt(),
            component,
        )
    }
}

class SpriteOptions(
    var scale: Float = 1f,
    var flipX: Boolean = false,
    var x: Int = 0,
    var y: Int = 0,
    var tint: Color? = null,
)