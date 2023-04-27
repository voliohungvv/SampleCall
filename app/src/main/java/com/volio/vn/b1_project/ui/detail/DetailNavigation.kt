package com.volio.vn.b1_project.ui.detail

import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.base.BaseNavigation

class DetailNavigation(val fragment: DetailFragment) : BaseNavigation() {

    override fun fragment(): BaseFragment<*, *> {
        return fragment
    }
}
