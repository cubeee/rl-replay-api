package com.x7ff.rl.replay.api.model.response

data class PartiallySuccessfulParseResponse<R>(override val replay: R) : ParseResponse<R>