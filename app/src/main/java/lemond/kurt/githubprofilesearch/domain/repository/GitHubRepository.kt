package lemond.kurt.githubprofilesearch.domain.repository

import io.reactivex.Single
import lemond.kurt.githubprofilesearch.domain.model.GitHubProfile

interface GitHubRepository {

    fun getGithubProfile(userName: String): Single<GitHubProfile>

}