package com.volio.vn.b1_project.ui.splash

import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.base.BaseNavigation

class SplashNavigation(val fragment: SplashFragment) : BaseNavigation() {
    override fun fragment(): BaseFragment<*, *> = fragment

    fun gotoHome() {
        val directions = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
        navigateTo(R.id.splashFragment, directions)
    }
}