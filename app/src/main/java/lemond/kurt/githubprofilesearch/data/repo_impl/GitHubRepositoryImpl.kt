package lemond.kurt.githubprofilesearch.data.repo_impl

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import lemond.kurt.githubprofilesearch.data.endpoint.GitHubService
import lemond.kurt.githubprofilesearch.data.response.Profile
import lemond.kurt.githubprofilesearch.data.response.Repo
import lemond.kurt.githubprofilesearch.domain.model.GitHubProfile
import lemond.kurt.githubprofilesearch.domain.model.GitHubRepo
import lemond.kurt.githubprofilesearch.domain.repository.GitHubRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GitHubRepositoryImpl(private val gitHubService: GitHubService): GitHubRepository {

    override fun getGithubProfile(userName: String): Single<GitHubProfile> {
        return Single.zip(gitHubService.getProfile(userName), gitHubService.getProfileRepos(userName), BiFunction { profile, repos ->
            return@BiFunction mergeToGitHubProfile(profile, repos)
        })
    }

    private fun mergeToGitHubProfile(profile: Profile, repos: List<Repo>): GitHubProfile {
        val gitHubRepos = repos.map {
            val description = it.description ?: ""
            val reposParser = SimpleDateFormat(REPO_UPDATE_TIME_PARSER, Locale.getDefault())
            val ghFormatter = SimpleDateFormat(GH_REPO_UPDATE_TIME_FORMATTER, Locale.getDefault())
            val formattedDate = try {
                ghFormatter.format(reposParser.parse(it.lastUpdateTime) ?: "")
            } catch (e: ParseException) {
                ""
            }
            GitHubRepo(it.name, description, formattedDate, it.stargazerCount, it.forksCount)
        }
        return GitHubProfile(profile.name, profile.avatarUrl, gitHubRepos)
    }

    companion object {
        const val REPO_UPDATE_TIME_PARSER = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val GH_REPO_UPDATE_TIME_FORMATTER = "MMM dd, yyyy hh:mm:ss aa"
    }
}