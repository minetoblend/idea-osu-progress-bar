package com.github.minetoblend.osuprogressbar.skinning

import com.github.minetoblend.osuprogressbar.utils.withTint
import java.awt.Color
import java.awt.image.BufferedImage

class Texture(
    val image: BufferedImage,
    val resolution: Int = 1
) {
    private val tintMap = mutableMapOf<Color, BufferedImage>()

    fun withTint(tint: Color): BufferedImage {
        return tintMap.computeIfAbsent(tint) {
            image.withTint(tint)
        }
    }

    val width get() = image.width / resolution.toFloat()
    val height get() = image.height / resolution.toFloat()
}

fun BufferedImage.toTexture(resolution: Int = 1) = Texture(this, resolution)