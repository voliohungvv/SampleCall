package com.volio.vn.b1_project.ui.splash

import androidx.fragment.app.viewModels
import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.databinding.FragmentSplashBinding
import com.volio.vn.common.utils.delay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashNavigation>() {

    val viewModel: SplashViewModel by viewModels()
    override val navigation = SplashNavigation(this)

    override fun getLayoutId() = R.layout.fragment_splash

    override fun observersData() {

    }

    override fun onViewReady() {
        //viewModel.getBitcoinList()
        delay(2000) {
            navigation.gotoHome()
        }
    }
}