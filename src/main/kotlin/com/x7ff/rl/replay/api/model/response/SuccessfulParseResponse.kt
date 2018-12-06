package com.x7ff.rl.replay.api.model.response

data class SuccessfulParseResponse<R>(val replay: R) : ParseResponse<R>