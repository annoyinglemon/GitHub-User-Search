package lemond.kurt.githubprofilesearch.domain.model

data class GitHubProfile(
    val name: String,
    val avatarUrl: String,
    val repositories: List<GitHubRepo>
)