package com.jarvis.assistant.domain.model

sealed class AIError(val message: String) {
    class RateLimited(val retryAfterSeconds: Int) : AIError("Rate limited. Retry after $retryAfterSeconds seconds.")
    class AuthFailed(message: String) : AIError(message)
    class NetworkError(cause: Throwable) : AIError("Network error: ${cause.message}")
    class ContextTooLong(val maxTokens: Int) : AIError("Context too long. Max tokens: $maxTokens")
    class ModelNotFound(val modelId: String) : AIError("Model not found: $modelId")
    class Unknown(message: String) : AIError(message)
}
