/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.components

import android.content.Context
import com.movtery.zalithlauncher.path.PathManager
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.util.zip.ZipInputStream

private const val MODPACK_ASSET = "整合包.zip"
private const val INSTALL_MARKER = ".zalith_builtin_modpack_installed"
private val WINDOWS_ZIP_CHARSET: Charset = Charset.forName("GBK")

/** Installs the modpack bundled in the APK into the default Minecraft directory. */
class UnpackGameResourcesTask(private val context: Context) : AbstractUnpackTask() {
    private val targetDirectory = File(PathManager.DIR_FILES_EXTERNAL, ".minecraft")
    private val markerFile = File(targetDirectory, INSTALL_MARKER)

    fun assetExists(): Boolean = runCatching {
        context.assets.open(MODPACK_ASSET).use { }
        true
    }.getOrDefault(false)

    override fun checkState(): InstallableItem.State =
        if (markerFile.isFile) InstallableItem.State.FINISHED
        else InstallableItem.State.NOT_STARTED

    override suspend fun run() {
        targetDirectory.mkdirs()
        val canonicalRoot = targetDirectory.canonicalFile

        context.assets.open(MODPACK_ASSET).buffered().use { assetInput ->
            // ZIPs created by some Windows archivers store entry names in the
            // system code page without the UTF-8 flag. Android otherwise tries
            // UTF-8 and throws MALFORMED before the first entry can be read.
            // ZipInputStream still honors UTF-8 for entries carrying that flag.
            ZipInputStream(assetInput, WINDOWS_ZIP_CHARSET).use { zipInput ->
                var entry = zipInput.nextEntry
                while (entry != null) {
                    val output = File(canonicalRoot, entry.name).canonicalFile
                    val isInsideTarget = output.path == canonicalRoot.path ||
                        output.path.startsWith(canonicalRoot.path + File.separator)
                    require(isInsideTarget) { "Invalid ZIP entry path: ${entry.name}" }

                    updateMessage(entry.name)
                    if (entry.isDirectory) {
                        output.mkdirs()
                    } else {
                        output.parentFile?.mkdirs()
                        FileOutputStream(output).buffered().use { fileOutput ->
                            zipInput.copyTo(fileOutput)
                        }
                    }
                    zipInput.closeEntry()
                    entry = zipInput.nextEntry
                }
            }
        }

        markerFile.writeText("installed")
        updateMessage(null)
    }
}
