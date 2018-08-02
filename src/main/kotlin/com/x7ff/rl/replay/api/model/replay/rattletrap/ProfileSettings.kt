package com.x7ff.rl.replay.api.model.replay.rattletrap

import com.fasterxml.jackson.annotation.JsonProperty

data class ProfileSettings (
    @JsonProperty("cam_settings") val cameraSettings: RattletrapCameraSettings
)