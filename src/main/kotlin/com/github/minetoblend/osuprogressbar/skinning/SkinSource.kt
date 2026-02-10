package com.github.minetoblend.osuprogressbar.skinning

import com.github.minetoblend.osuprogressbar.settings.OsuProgressBarSettings
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object SkinSource {
    private val defaultSkin = Skin(
        config = SkinConfig(),
        textures = object : TextureStore() {
            override fun load(name: String): BufferedImage? =
                runCatching { javaClass.getResource("/icons/$name.png")?.let(ImageIO::read) }.getOrNull()
        }
    )

    private var _customSkin: Skin? = null
    private var _skinPath: String? = null

    private val customSkin: Skin?
        get() {
            val skinPath = OsuProgressBarSettings.getInstance().skinDirectory

            if (skinPath != _skinPath) {
                _skinPath = skinPath

                _customSkin = runCatching {
                    File(skinPath).takeIf { it.exists() && it.isDirectory }
                        ?.let { directory ->
                            Skin(
                                config = SkinConfig.fromFile(directory.resolve("skin.ini")),
                                textures = object : TextureStore() {
                                    override fun load(name: String): BufferedImage? {
                                        return runCatching {
                                            val file = directory.resolve("$name.png").takeIf { it.exists() }

                                            file?.let(ImageIO::read)
                                        }.getOrNull()
                                    }

                                }
                            )
                        }
                }.getOrNull()
            }

            return _customSkin
        }

    val allSources
        get() = sequence {
            customSkin?.let { yield(it) }

            yield(defaultSkin)
        }

    fun getProvider(predicate: (Skin) -> Boolean): Skin? =
        allSources.find(predicate)

    fun <T> getOrNull(lookup: Skin.() -> T?): T? {
        return customSkin?.lookup() ?: defaultSkin.lookup()
    }

    fun <T : Any> get(lookup: Skin.() -> T?): T = getOrNull(lookup)!!
}