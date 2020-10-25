package com.x7ff.rl.replay.api.forwarder

abstract class ReplayForwarder {
    abstract fun forwardReplay(fileName: String, bytes: ByteArray)
}
