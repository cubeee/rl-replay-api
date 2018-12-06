package com.x7ff.rl.replay.api.transformer

import com.x7ff.rl.replay.api.model.replay.Replay

interface ReplayTransformer<in R> {

    fun transform(fileName: String, parsedReplay: R): Replay

}