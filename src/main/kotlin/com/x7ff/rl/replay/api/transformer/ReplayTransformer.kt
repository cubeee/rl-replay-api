package com.x7ff.rl.replay.api.transformer

import com.x7ff.rl.replay.api.model.replay.Replay
import com.x7ff.rl.replay.api.model.replay.rattletrap.RattletrapReplay

interface ReplayTransformer {

    fun transform(parsedReplay: RattletrapReplay): Replay

}