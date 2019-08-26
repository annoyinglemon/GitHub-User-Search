package lemond.kurt.githubprofilesearch.domain.model

class GitHubRepo (
    val name: String,
    val description: String,
    val lastUpdated: String,
    val stargazerCount: Int,
    val forksCount: Int
)