package lemond.kurt.githubprofilesearch.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import lemond.kurt.githubprofilesearch.R
import lemond.kurt.githubprofilesearch.databinding.ItemMainGitRepoBinding
import lemond.kurt.githubprofilesearch.domain.model.GitHubRepo

class ReposAdapter(private val onRepoSelectedListener: OnRepoSelectedListener): RecyclerView.Adapter<ReposAdapter.RepoViewHolder>(){

    var repoList: List<GitHubRepo>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return if (repoList != null) {
            repoList!!.size
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val gitRepoBinding = DataBindingUtil.inflate<ItemMainGitRepoBinding>(LayoutInflater.from(parent.context), R.layout.item_main_git_repo, parent, false)
        return RepoViewHolder(gitRepoBinding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.gitRepoBinding.repoTitle = repoList!![position].name
        holder.gitRepoBinding.repoDesc = repoList!![position].description
        holder.itemView.setOnClickListener {
            onRepoSelectedListener.onRepoItemSelected(repoList!![position])
        }
    }

    data class RepoViewHolder(val gitRepoBinding: ItemMainGitRepoBinding): RecyclerView.ViewHolder(gitRepoBinding.root)

    interface OnRepoSelectedListener {
        fun onRepoItemSelected(gitHubRepo: GitHubRepo)
    }

}