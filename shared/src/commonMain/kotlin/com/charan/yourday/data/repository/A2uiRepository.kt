package com.charan.yourday.data.repository

import com.charan.yourday.data.model.PlatformSurfaceModel
import kotlinx.coroutines.flow.StateFlow

interface A2uiRepository {
    val surfaces: StateFlow<List<PlatformSurfaceModel>>

    fun process(json: String)
}
