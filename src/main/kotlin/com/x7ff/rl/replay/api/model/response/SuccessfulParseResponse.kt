package com.x7ff.rl.replay.api.model.response

data class SuccessfulParseResponse<R>(override val replay: R?) : ParseResponse<R>
