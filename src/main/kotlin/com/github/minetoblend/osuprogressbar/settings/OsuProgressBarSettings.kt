package com.github.minetoblend.osuprogressbar.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.components.Service.Level
import java.awt.Color


@Service(Level.APP)
@State(
    name = "com.osucad.plugin.progressbar.settings.OsuProgressBarSettingsState",
    storages = [Storage("OsuProgressBarSettings.xml")]
)
class OsuProgressBarSettings : SimplePersistentStateComponent<OsuProgressBarSettings.State>(State()) {

    companion object {
        fun getInstance() = service<OsuProgressBarSettings>()

        @Suppress("UseJBColor")
        val defaultComboColor = Color(235, 99, 89)
    }

    var comboColor: Color
        get() = runCatching {
            Color.decode(state.comboColor)
        }.getOrElse { defaultComboColor }
        set(value) {
            state.comboColor = value.hexString
        }

    var skinDirectory: String
        get() = state.skinDirectory ?: ""
        set(value) {
            state.skinDirectory = value
        }

    class State : BaseState() {
        var skinDirectory by string("")
        var comboColor by string()
    }
}

private val Color.hexString get() = "#%06X".format(rgb and 0xFFFFFF)