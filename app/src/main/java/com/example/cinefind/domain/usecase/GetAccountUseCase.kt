package com.example.cinefind.domain.usecase

import com.example.cinefind.data.model.AccountResponse
import com.example.cinefind.data.repository.MovieRepository
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val repository: MovieRepository
) : BaseUseCaseNoParams<AccountResponse>() {

    override suspend fun invoke(): AccountResponse = repository.getAccount()
}
