package lemond.kurt.githubprofilesearch.app.viewmodel

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import lemond.kurt.githubprofilesearch.domain.model.GitHubRepo
import lemond.kurt.githubprofilesearch.domain.repository.GitHubRepository


class ProfileSearchViewModel(private val gitHubRepository: GitHubRepository): ViewModel() {

    private var fetchProfileDisposable: Disposable? = null

    val userName = MutableLiveData<String>()
    val avatarImageUrl = MutableLiveData<String>()
    val gitHubRepos = MutableLiveData<List<GitHubRepo>>()

    val userNameEntry = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()

    fun onSearchClick(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
        beginProfileSearch()
    }

    @VisibleForTesting
    fun beginProfileSearch() {
        if (!userNameEntry.value.isNullOrEmpty()) {
            fetchProfileDisposable = gitHubRepository.getGithubProfile(userNameEntry.value!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { ghProfile ->
                        userName.postValue(ghProfile.name)
                        avatarImageUrl.postValue(ghProfile.avatarUrl)
                        gitHubRepos.postValue(ghProfile.repositories)
                    },
                    { throwable ->
                        errorMessage.postValue(throwable.message)
                    }
                )
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchProfileDisposable?.dispose()
    }

    class Factory(private val gitHubRepository: GitHubRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ProfileSearchViewModel::class.java)) {
                ProfileSearchViewModel(gitHubRepository) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

}