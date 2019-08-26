package lemond.kurt.githubprofilesearch.data.endpoint

import io.reactivex.Single
import lemond.kurt.githubprofilesearch.data.response.Profile
import lemond.kurt.githubprofilesearch.data.response.Repo
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {

    @GET("{name}")
    fun getProfile(@Path("name") userName: String): Single<Profile>

    @GET("{name}/repos")
    fun getProfileRepos(@Path("name") userName: String): Single<List<Repo>>

}