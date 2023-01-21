package com.example.ledscontroller.models.patterns

import kotlinx.serialization.Serializable


@Serializable
data class SolidPatternModel(
    val id: UByte,
    val hue: UByte,
    val saturation: UByte,
    val value: UByte
)
