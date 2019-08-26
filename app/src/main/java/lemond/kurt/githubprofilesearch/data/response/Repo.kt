package lemond.kurt.githubprofilesearch.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Repo (
    val name: String,
    val description: String? = null,
    @Json(name = "updated_at") val lastUpdateTime: String,
    @Json(name = "stargazers_count") val stargazerCount: Int,
    @Json(name = "forks") val forksCount: Int
)