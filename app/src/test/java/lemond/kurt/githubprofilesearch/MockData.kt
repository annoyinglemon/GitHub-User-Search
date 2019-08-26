package lemond.kurt.githubprofilesearch

import lemond.kurt.githubprofilesearch.data.repo_impl.GitHubRepositoryImpl
import lemond.kurt.githubprofilesearch.data.response.Profile
import lemond.kurt.githubprofilesearch.data.response.Repo
import lemond.kurt.githubprofilesearch.domain.model.GitHubProfile
import lemond.kurt.githubprofilesearch.domain.model.GitHubRepo
import java.text.SimpleDateFormat
import java.util.*

fun mockGitHubProfile(repoCount: Int = 5): GitHubProfile {
    val ghRepos = ArrayList<GitHubRepo>()
    for (i in 0 until repoCount) {
        ghRepos.add(mockGitHubRepo())
    }
    return GitHubProfile(randomString(), randomString(), ghRepos)
}

fun mockGitHubRepo(): GitHubRepo {
    return GitHubRepo(randomString(), randomString(), randomString(), randomInt(), randomInt())
}

fun mockProfile(): Profile {
    return Profile(randomString(), randomString())
}

fun mockRepos(count: Int = 5, date: Date): List<Repo> {
    val repos = ArrayList<Repo>()
    for (i in 0 until count) {
        val repoDateFormat = SimpleDateFormat(GitHubRepositoryImpl.REPO_UPDATE_TIME_PARSER, Locale.getDefault())
        val dateString = repoDateFormat.format(date)
        val repo = Repo(randomString(), randomString(), dateString ,randomInt(), randomInt())
        repos.add(repo)
    }
    return repos
}

fun randomString(): String {
    return UUID.randomUUID().toString()
}

fun randomInt(): Int {
    return Random().nextInt()
}