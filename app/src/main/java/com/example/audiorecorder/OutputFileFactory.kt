package com.example.audiorecorder

import java.io.File

interface OutputFileFactory {
    fun nextFile(): File
}