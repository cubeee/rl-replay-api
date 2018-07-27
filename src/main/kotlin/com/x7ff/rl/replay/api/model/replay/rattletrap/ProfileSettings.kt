package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.squareup.moshi.Json

data class ProfileSettings (
    @Json(name = "cam_settings") val cameraSettings: CameraSettings
)