package com.github.minetoblend.osuprogressbar.skinning

import com.github.minetoblend.osuprogressbar.utils.withTint
import java.awt.Color
import java.awt.image.BufferedImage

class Skin(
    val config: SkinConfig,
    private val textures: TextureStore
) {
    val hitCircle = textures.get("hitcircle")
    val hitCircleOverlay = textures.get("hitcircleoverlay")
    val sliderbND = textures.get("sliderb-nd")
    val sliderFollowCircle = textures.get("sliderfollowcircle")
    val reverseArrow = textures.get("reversearrow")
    val sliderEndCircle = textures.get("sliderendcircle")
    val sliderEndCircleOverlay = textures.get("sliderendcircleoverlay")

    val sliderb: List<Texture>? = run {
        textures.get("sliderb")?.let { return@run listOf(it) }

        var index = 0

        val frames = mutableListOf<Texture>()

        while (true) {
            frames += textures.get("sliderb$index") ?: break

            index++
        }

        frames.takeIf { it.isNotEmpty() }
    }

}