package com.example.cinefind.domain.usecase

abstract class BaseUseCase<Result, in Params> {
    abstract suspend fun invoke(params: Params): Result
    suspend fun execute(params: Params): Result = invoke(params)
}

abstract class BaseUseCaseNoParams<Result> {
    abstract suspend fun invoke(): Result
    suspend fun execute(): Result = invoke()
}
