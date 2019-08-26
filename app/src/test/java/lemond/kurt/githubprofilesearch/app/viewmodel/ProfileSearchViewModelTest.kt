package lemond.kurt.githubprofilesearch.app.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.argumentCaptor
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import lemond.kurt.githubprofilesearch.domain.model.GitHubRepo
import lemond.kurt.githubprofilesearch.domain.repository.GitHubRepository
import lemond.kurt.githubprofilesearch.mockGitHubProfile
import lemond.kurt.githubprofilesearch.randomString
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.mockito.Mockito
import org.mockito.Mockito.never
import java.lang.IllegalArgumentException

class ProfileSearchViewModelTest {

    private lateinit var gitHubRepository: GitHubRepository
    private lateinit var profileSearchViewModel: ProfileSearchViewModel

    private lateinit var userNameObserver: Observer<String>
    private lateinit var imageUrlObserver: Observer<String>
    private lateinit var gitHubReposObserver: Observer<List<GitHubRepo>>

    private lateinit var errorMessageObserver: Observer<String>


    @Rule
    @JvmField
    val archComponentRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline()}
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        gitHubRepository = Mockito.mock(GitHubRepository::class.java)

        userNameObserver = Mockito.mock(Observer::class.java) as Observer<String>
        imageUrlObserver = Mockito.mock(Observer::class.java) as Observer<String>
        gitHubReposObserver = Mockito.mock(Observer::class.java) as Observer<List<GitHubRepo>>
        errorMessageObserver = Mockito.mock(Observer::class.java) as Observer<String>

        profileSearchViewModel = ProfileSearchViewModel(gitHubRepository)

        profileSearchViewModel.userName.observeForever(userNameObserver)
        profileSearchViewModel.avatarImageUrl.observeForever(imageUrlObserver)
        profileSearchViewModel.gitHubRepos.observeForever(gitHubReposObserver)
        profileSearchViewModel.errorMessage.observeForever(errorMessageObserver)

    }

    @After
    fun tearDown() {
        profileSearchViewModel.userName.removeObserver(userNameObserver)
        profileSearchViewModel.avatarImageUrl.removeObserver(imageUrlObserver)
        profileSearchViewModel.gitHubRepos.removeObserver(gitHubReposObserver)
        profileSearchViewModel.errorMessage.removeObserver(errorMessageObserver)
    }

    @Test
    fun beginProfileSearch_succeeds() {
        val userName = randomString()
        val expectedGitHubProfile = mockGitHubProfile()

        val errorMessage = randomString()
        val mockException = IllegalArgumentException(errorMessage)

        Mockito.`when`(gitHubRepository.getGithubProfile(userName)).thenReturn(Single.just(expectedGitHubProfile))

        profileSearchViewModel.userNameEntry.value = userName
        profileSearchViewModel.beginProfileSearch()

        Mockito.verify(gitHubRepository).getGithubProfile(userName)

        Mockito.verify(userNameObserver).onChanged(expectedGitHubProfile.name)
        Mockito.verify(imageUrlObserver).onChanged(expectedGitHubProfile.avatarUrl)

        val ghReposCaptor = argumentCaptor<List<GitHubRepo>>()
        Mockito.verify(gitHubReposObserver).onChanged(ghReposCaptor.capture())

        val resultGitHubRepos = ghReposCaptor.firstValue
        expectedGitHubProfile.repositories.forEachIndexed { index, gitHubRepo ->
            assertEquals(gitHubRepo.name, resultGitHubRepos[index].name)
            assertEquals(gitHubRepo.description, resultGitHubRepos[index].description)
            assertEquals(gitHubRepo.lastUpdated, resultGitHubRepos[index].lastUpdated)
            assertEquals(gitHubRepo.stargazerCount, resultGitHubRepos[index].stargazerCount)
            assertEquals(gitHubRepo.forksCount, resultGitHubRepos[index].forksCount)
        }

        Mockito.verify(errorMessageObserver, never()).onChanged(errorMessage)
    }

    @Test
    fun beginProfileSearch_fails() {
        val userName = randomString()
        val expectedGitHubProfile = mockGitHubProfile()

        val errorMessage = randomString()
        val mockException = IllegalArgumentException(errorMessage)

        Mockito.`when`(gitHubRepository.getGithubProfile(userName)).thenReturn(Single.error(mockException))

        profileSearchViewModel.userNameEntry.value = userName
        profileSearchViewModel.beginProfileSearch()

        Mockito.verify(gitHubRepository).getGithubProfile(userName)

        Mockito.verify(userNameObserver, never()).onChanged(expectedGitHubProfile.name)
        Mockito.verify(imageUrlObserver, never()).onChanged(expectedGitHubProfile.avatarUrl)
        Mockito.verify(gitHubReposObserver, never()).onChanged(expectedGitHubProfile.repositories)

        Mockito.verify(errorMessageObserver).onChanged(errorMessage)
    }
}