package com.volio.vn.b1_project.ui.detail

import androidx.fragment.app.viewModels
import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding, DetailNavigation>() {

    val viewModel: DetailViewModel by viewModels()

    override val navigation = DetailNavigation(this)

    override fun getLayoutId() = R.layout.fragment_detail


    override fun observersData() {

    }

    override fun onViewReady() {

    }
}
