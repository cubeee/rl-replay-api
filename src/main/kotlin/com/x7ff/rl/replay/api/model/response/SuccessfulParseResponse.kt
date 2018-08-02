package com.x7ff.rl.replay.api.model.response

import com.x7ff.rl.replay.api.model.replay.rattletrap.RattletrapReplay

data class SuccessfulParseResponse(val replay: RattletrapReplay): ParseResponse