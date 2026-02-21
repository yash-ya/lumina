package com.example.lumina.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Quote(
    val id: Int,
    val text: String,
    val author: String
)
