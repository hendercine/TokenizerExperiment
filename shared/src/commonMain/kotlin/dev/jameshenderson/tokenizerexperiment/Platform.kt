package dev.jameshenderson.tokenizerexperiment

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform