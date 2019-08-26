package lemond.kurt.githubprofilesearch.app

import android.app.Application
import lemond.kurt.githubprofilesearch.BuildConfig
import lemond.kurt.githubprofilesearch.data.endpoint.GitHubService
import lemond.kurt.githubprofilesearch.data.repo_impl.GitHubRepositoryImpl
import lemond.kurt.githubprofilesearch.domain.repository.GitHubRepository
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class GitHubUserSearchApp: Application() {

    fun createGitHubRepository(): GitHubRepository {
        return GitHubRepositoryImpl(createGitHubService())
    }

    private fun createGitHubService(): GitHubService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(GitHubService::class.java)
    }

}