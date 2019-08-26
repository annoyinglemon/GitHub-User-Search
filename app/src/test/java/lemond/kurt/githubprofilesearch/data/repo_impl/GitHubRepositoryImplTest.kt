package lemond.kurt.githubprofilesearch.data.repo_impl

import io.reactivex.Single
import lemond.kurt.githubprofilesearch.data.endpoint.GitHubService
import lemond.kurt.githubprofilesearch.domain.model.GitHubProfile
import lemond.kurt.githubprofilesearch.mockProfile
import lemond.kurt.githubprofilesearch.mockRepos
import lemond.kurt.githubprofilesearch.randomString
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito
import java.text.SimpleDateFormat
import java.util.*

class GitHubRepositoryImplTest {

    private lateinit var gitHubService: GitHubService
    private lateinit var gitHubRepositoryImpl: GitHubRepositoryImpl

    @Before
    fun setUp() {
        gitHubService = Mockito.mock(GitHubService::class.java)
        gitHubRepositoryImpl = GitHubRepositoryImpl(gitHubService)
    }

    @Test
    fun getGithubProfile() {
        val userName = randomString()

        val currentDateTime = Date()
        val ghFormatter = SimpleDateFormat(GitHubRepositoryImpl.GH_REPO_UPDATE_TIME_FORMATTER, Locale.getDefault())
        val expectedGHDateString = ghFormatter.format(currentDateTime)

        val profile = mockProfile()

        val repos = mockRepos(date = currentDateTime)

        Mockito.`when`(gitHubService.getProfile(userName)).thenReturn(Single.just(profile))
        Mockito.`when`(gitHubService.getProfileRepos(userName)).thenReturn(Single.just(repos))

        val testObserver = gitHubRepositoryImpl.getGithubProfile(userName).test()
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValueCount(1)

        val resultGhProfile = testObserver.values()[0] as GitHubProfile
        assertEquals(profile.name, resultGhProfile.name)
        assertEquals(profile.avatarUrl, resultGhProfile.avatarUrl)
        repos.forEachIndexed { index, repo ->
            val ghRepo = resultGhProfile.repositories[index]
            assertEquals(repo.name, ghRepo.name)
            assertEquals(repo.description, ghRepo.description)
            assertEquals(expectedGHDateString, ghRepo.lastUpdated)
            assertEquals(repo.stargazerCount, ghRepo.stargazerCount)
            assertEquals(repo.forksCount, ghRepo.forksCount)
        }

        Mockito.verify(gitHubService).getProfile(userName)
        Mockito.verify(gitHubService).getProfileRepos(userName)
    }
}