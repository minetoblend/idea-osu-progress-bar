package com.github.minetoblend.osuprogressbar.skinning

import com.jetbrains.rd.util.ConcurrentHashMap
import java.awt.image.BufferedImage
import java.util.*
import kotlin.jvm.optionals.getOrNull

abstract class TextureStore {
    private val textures = ConcurrentHashMap<String, Optional<Texture>>()

    protected abstract fun load(name: String): BufferedImage?

    fun get(name: String): Texture? {
        val result = textures.computeIfAbsent(name) {
            val texture = load(name)?.toTexture() ?: load("$name@2x")?.toTexture(2)

            Optional.ofNullable(texture)
        }

        return result.getOrNull()
    }
}