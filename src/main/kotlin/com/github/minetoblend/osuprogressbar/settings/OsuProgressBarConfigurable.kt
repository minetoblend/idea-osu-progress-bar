package com.github.minetoblend.osuprogressbar.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.ColorPanel
import com.intellij.ui.components.textFieldWithBrowseButton
import com.intellij.ui.dsl.builder.*
import com.sun.java.accessibility.util.AWTEventMonitor.addActionListener
import java.awt.Color

class OsuProgressBarConfigurable : BoundConfigurable("osu! Progress Bar") {

    override fun createPanel(): DialogPanel {
        val skinDirectoryField = createSkinDirectoryField()

        val settings = OsuProgressBarSettings.getInstance()

        return panel {
            group(displayName) {
                row("Skin directory:") {
                    cell(skinDirectoryField)
                        .setupSkinDirectoryField()
                        .align(AlignX.FILL)
                }
                row("Combo color:") {
                    cell(ColorPanel())
                        .bind(
                            componentGet = { it.selectedColor ?: OsuProgressBarSettings.defaultComboColor },
                            componentSet = ColorPanel::setSelectedColor,
                            prop = settings::comboColor.toMutableProperty(),
                        )
                        .setupComboColorField()
                }
            }
        }
    }

    private fun createSkinDirectoryField(): TextFieldWithBrowseButton {
        return textFieldWithBrowseButton(
            project = null,
            fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor(),

            )
    }
}

private fun Cell<TextFieldWithBrowseButton>.setupSkinDirectoryField(): Cell<TextFieldWithBrowseButton> =
    apply {
        val settings = OsuProgressBarSettings.getInstance()

        bindText(settings::skinDirectory)
    }

private fun Cell<ColorPanel>.setupComboColorField(): Cell<ColorPanel> =
    applyToComponent {
        addActionListener { e ->

        }
    }