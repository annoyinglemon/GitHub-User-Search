package lemond.kurt.githubprofilesearch.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Profile (
    val name: String,
    @Json(name = "avatar_url") val avatarUrl: String
)