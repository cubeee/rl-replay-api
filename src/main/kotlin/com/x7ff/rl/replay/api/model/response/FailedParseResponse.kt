package com.x7ff.rl.replay.api.model.response

data class FailedParseResponse<R>(val error: String): ParseResponse<R>