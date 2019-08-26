package lemond.kurt.githubprofilesearch.app.ui

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import lemond.kurt.githubprofilesearch.R
import lemond.kurt.githubprofilesearch.app.GitHubUserSearchApp
import lemond.kurt.githubprofilesearch.app.viewmodel.ProfileSearchViewModel
import lemond.kurt.githubprofilesearch.databinding.ActivityMainBinding
import lemond.kurt.githubprofilesearch.domain.model.GitHubRepo
import lemond.kurt.githubprofilesearch.databinding.DialogMainRepoDetailsBinding
import android.util.TypedValue
import android.view.animation.LinearInterpolator


class MainActivity : AppCompatActivity(), ReposAdapter.OnRepoSelectedListener, UrlImageView.OnImageLoadListener{

    private lateinit var viewModel: ProfileSearchViewModel
    private lateinit var activityViewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityViewBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        activityViewBinding.lifecycleOwner = this
        activityViewBinding.recyclerViewUserRepos.layoutManager = LinearLayoutManager(this)
        activityViewBinding.imageViewUserAvatar.onImageLoadListener = this

        val gitHubRepository = (application as GitHubUserSearchApp).createGitHubRepository()
        val vmFactory = ProfileSearchViewModel.Factory(gitHubRepository)
        viewModel = ViewModelProviders.of(this, vmFactory)[ProfileSearchViewModel::class.java]

        activityViewBinding.viewModel = viewModel

        val repoAdapter = ReposAdapter(this)
        activityViewBinding.recyclerViewUserRepos.adapter = repoAdapter

        viewModel.gitHubRepos.observe(this, Observer {
            repoAdapter.repoList = it
        })

        viewModel.errorMessage.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.errorMessage.value = ""
            }
        })
    }

    override fun onRepoItemSelected(gitHubRepo: GitHubRepo) {
        val repoDialog = createRepoDialog(gitHubRepo)
        repoDialog.window?.run {
            this.setGravity(Gravity.BOTTOM)
            this.setBackgroundDrawableResource(R.drawable.background_dialog_inset)
            this.attributes.windowAnimations = R.style.RepoDialogAnimation
        }
        repoDialog.show()
    }

    private fun createRepoDialog(gitHubRepo: GitHubRepo): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(this)
        val viewBinding = DataBindingUtil.inflate<DialogMainRepoDetailsBinding>(layoutInflater,
            R.layout.dialog_main_repo_details, null, false)

        viewBinding.lastUpdated = gitHubRepo.lastUpdated
        viewBinding.forksCount = gitHubRepo.forksCount.toString()
        viewBinding.starsCount = gitHubRepo.stargazerCount.toString()

        dialogBuilder.setView(viewBinding.root)
        return dialogBuilder.create()
    }

    override fun onImageLoad() {
        animateAvatarAndUserName()
    }

    private fun animateAvatarAndUserName() {
        val movementHeightDp = 20f
        val animDuration = 400L
        val animDelay = 100L
        val movementHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, movementHeightDp, resources.displayMetrics)

        val movementAndNameAlpha = ValueAnimator.ofFloat(movementHeightPx, 0f).apply {
            this.addUpdateListener {
                activityViewBinding.textViewUserName.translationY = it.animatedValue as Float
                activityViewBinding.imageViewUserAvatar.translationY = it.animatedValue as Float
                activityViewBinding.textViewUserName.alpha = it.animatedFraction
            }
        }

        val avatarAlpha = ValueAnimator.ofFloat(0f, 1f).apply {
            this.startDelay = animDelay
            this.addUpdateListener {
                activityViewBinding.imageViewUserAvatar.alpha = it.animatedFraction
            }
        }

        val userProfileAnimator = AnimatorSet().apply {
            this.duration = animDuration
            this.playTogether(movementAndNameAlpha, avatarAlpha)
        }

        val userRepoAnimator = ValueAnimator.ofFloat(movementHeightPx, 0f).apply {
            this.duration = animDuration
            this.startDelay = animDelay
            this.addUpdateListener {
                activityViewBinding.recyclerViewUserRepos.translationY = it.animatedValue as Float
                activityViewBinding.recyclerViewUserRepos.alpha = it.animatedFraction
            }
        }

        AnimatorSet().apply {
            this.interpolator = LinearInterpolator()
            this.playSequentially(userProfileAnimator, userRepoAnimator)
            this.start()
        }
    }


}
