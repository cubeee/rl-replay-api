package com.x7ff.rl.replay.api.model.parse

import com.x7ff.rl.replay.api.model.replay.parsed.ParsedReplay

data class SuccessfulParseResponse(val replay: ParsedReplay) : ParseResult