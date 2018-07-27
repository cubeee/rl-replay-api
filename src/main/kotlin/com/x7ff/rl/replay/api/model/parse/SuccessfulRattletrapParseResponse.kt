package com.x7ff.rl.replay.api.model.parse

import com.x7ff.rl.replay.api.model.replay.rattletrap.RattletrapReplay

data class SuccessfulRattletrapParseResponse(val replay: RattletrapReplay) : ParseResult