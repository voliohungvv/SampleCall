package com.volio.vn.b1_project.base

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import timber.log.Timber

abstract class BaseNavigation {

    abstract fun fragment(): BaseFragment<*, *>

    val navController: NavController?
        get() {
            return try {
                if (fragment().activity != null && fragment().isAdded) {
                    fragment().findNavController()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


    private val currentNavDestination: NavDestination?
        get() {
            return navController?.currentDestination
        }


    private var defaultLifecycleObserver: DefaultLifecycleObserver? = null


    /**
     * Xử lý chuyển màn hình
     * @param currentNavId -> truyền vào id màn hình hiện tại để kiểm tra xem có đúng đang đứng ở màn hình hiện tại k
     * @param directions -> đích cần đến (đã bao gồm param rồi nhé)
     */
    fun navigateTo(currentNavId: Int, directions: NavDirections, navOptions: NavOptions? = null, navOnResumed: Boolean = true) {
        fun executeNavigate() {
            if (navController != null && currentNavDestination?.id == currentNavId) {
                navController?.navigate(directions, navOptions)
                return
            }
        }

        if (!navOnResumed) {
            executeNavigate()
        } else {
            try {
                navController?.navigate(directions, navOptions)
            } catch (e: Exception) {
                val lifeCycleState = fragment().lifecycle.currentState
                if (lifeCycleState == Lifecycle.State.RESUMED) {
                    executeNavigate()
                } else {
                    if (defaultLifecycleObserver != null) {
                        fragment().lifecycle.removeObserver(defaultLifecycleObserver!!)
                    }
                    defaultLifecycleObserver = object : DefaultLifecycleObserver {
                        override fun onResume(owner: LifecycleOwner) {
                            super.onResume(owner)
                            fragment().lifecycle.removeObserver(this)
                            executeNavigate()
                        }
                    }

                    fragment().lifecycle.addObserver(defaultLifecycleObserver as DefaultLifecycleObserver)
                }
            }
        }
    }

    fun popBackStack() {
        navController?.popBackStack()
    }

    fun popBackStack(currentNavId: Int, navOnResumed: Boolean = true) {

        fun pop() {
            if (navController?.currentDestination?.id == currentNavId) {
                val result = navController?.navigateUp()
                Timber.tag("BaseNavigation").e("popBackStack with id = $currentNavId result = $result")
                return
            } else {
                Timber.tag("BaseNavigation").e("popBackStack with id = $currentNavId FAILURE")
            }
        }

        val lifeCycleState = fragment().lifecycle.currentState

        if (navOnResumed) {
            if (lifeCycleState == Lifecycle.State.RESUMED) {
                pop()
            } else {
                fragment().viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        if (event == Lifecycle.Event.ON_RESUME) {
                            fragment().viewLifecycleOwner.lifecycle.removeObserver(this)
                            pop()
                        }
                    }
                })
            }
        } else {
            pop()
        }
    }
}