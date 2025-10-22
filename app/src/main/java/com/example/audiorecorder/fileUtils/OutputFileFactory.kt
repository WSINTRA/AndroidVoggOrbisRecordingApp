package com.example.audiorecorder.fileUtils

import java.io.File

interface OutputFileFactory {
    fun nextFile(): File
}