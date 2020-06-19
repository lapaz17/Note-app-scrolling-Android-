package com.noto.data.source.remote

import com.noto.domain.model.User
import com.noto.domain.schema.ResponseSchema
import com.noto.domain.schema.UserResponse

interface UserRemoteDataSource {

    val baseUrl: String
        get() = "/user"

    suspend fun createUser(user: User): ResponseSchema<UserResponse>

    suspend fun loginUser(user: User): ResponseSchema<UserResponse>

}