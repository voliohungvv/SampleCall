package com.volio.vn.b1_project.ui.home

import android.app.Dialog
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.volio.vn.b1_project.BuildConfig
import com.volio.vn.b1_project.ItemBitcoinBindingModel_
import com.volio.vn.b1_project.R
import com.volio.vn.b1_project.base.BaseFragment
import com.volio.vn.b1_project.databinding.FragmentHomeBinding
import com.volio.vn.b1_project.utils.openLoading
import com.volio.vn.callscreen.extensions.PERMISSION_WRITE_CONTACTS
import com.volio.vn.callscreen.extensions.hasPermission
import com.volio.vn.callscreen.extensions.isQPlus
import com.volio.vn.callscreen.helpers.CallVibrateManager
import com.volio.vn.callscreen.helpers.SimpleContactsHelper
import com.volio.vn.callscreen.helpers.setPhoto
import com.volio.vn.callscreen.helpers.setRingtone
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.InputStream


private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeNavigation>() {

    val viewModel: HomeViewModel by viewModels()
    override val navigation = HomeNavigation(this)

    override fun getLayoutId() = R.layout.fragment_home

    val dialog by lazy { Dialog(requireContext()) }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                if (requireContext().hasPermission(PERMISSION_WRITE_CONTACTS)) {
                    //val path = Uri.parse("file:///android_asset/raw/ringtone.mp3")


                    val inputStream: InputStream =
                        requireContext().resources.openRawResource(R.raw.ringtone)


                    val pathExternal =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    // val file = File(pathExternal, "ringtone.mp3")

                    val ringtoneFile = File(pathExternal, "ringtone.mp3")
                    /*setRingtone(
                        requireContext(),
                        "0398593045",
                        ringtoneFile.absolutePath
                    )*/
                    val path =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(path, "image.jpg")
                    val uri = Uri.fromFile(file)
                    setPhoto(
                        requireContext(),
                        "0398593045",
                        file.absolutePath,

                        )

                    /*  inputStream.use { input ->
                          FileOutputStream(ringtoneFile).use { output ->
                              input.copyTo(output)

                          }

                      }*/





                    Log.e(TAG, "Set am nhac thành cong ")
                } else {
                    Log.e(TAG, "Not permistion : ")
                }
            } else {
                Log.e(TAG, "Not permistion : ")
            }
        }

    private val resultDefaultDialerIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            context?.let {
                if (it.checkIsAppCallDefault()) {
                    // navigation.navToHome(true, args.layoutName, args.pathMedia, args.typeMedia)
                } else {
                    Toast.makeText(
                        activity?.applicationContext,
                        "Permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                    //  navigation.navToHome(false, args.layoutName, args.pathMedia, args.typeMedia)
                }
            }
        }

    override fun observersData() {
        viewModel.bitcoinList.observe(viewLifecycleOwner) {
            binding.recyclerViewBitcoin.requestModelBuild()

        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) dialog.openLoading() else dialog.dismiss()
        }
    }

    override fun onViewReady() {
        initEpoxyRecyclerView()
        viewModel.getBitcoinList()

        val simpleContentProvider = SimpleContactsHelper(requireContext())


        binding.buttonClick1.setOnClickListener {
            launchSetDefaultDialerIntent()


            Timber.tag(TAG).e(
                "get all contact name " + simpleContentProvider.getContactNames(false).toString()
            )

        }

        binding.buttonClick2.setOnClickListener {
            //launchSetDefaultDialerIntent()


            requestPermissionLauncher.launch(android.Manifest.permission.WRITE_CONTACTS)
            CallVibrateManager.vibrateWithPatten(requireContext())
        }

        binding.buttonClick3.setOnClickListener {
            //launchSetDefaultDialerIntent()
            //CallVibrateManager.cancelVibrate(requireContext())

            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(path, "image.jpg")
            val uri = Uri.fromFile(file)
            /*setPhoto(
                requireContext(),
                "0398593045",
                file.absolutePath,

                )*/

            Log.e(TAG, "${SimpleContactsHelper(requireContext()).getPhotoUriFromPhoneNumber("0398593045")} ", )
        }

        /*if (getSystemService(
                requireContext(),
                NotificationManager::class.java
            )!!.isNotificationPolicyAccessGranted
        ) {
            getSystemService(requireContext(), AudioManager::class.java)!!.ringerMode =
                AudioManager.RINGER_MODE_SILENT
        } else {
            // Ask the user to grant access
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivityForResult(intent, 123)
        }*/
    }


    fun Context.checkIsAppCallDefault(): Boolean {
        return if (isQPlus()) {
            val roleManager = this.getSystemService(RoleManager::class.java)
            !(roleManager!!.isRoleAvailable(RoleManager.ROLE_DIALER) && !roleManager.isRoleHeld(
                RoleManager.ROLE_DIALER
            ))
        } else {
            this.getSystemService(TelecomManager::class.java)?.defaultDialerPackage == BuildConfig.APPLICATION_ID
        }
    }

    private fun launchSetDefaultDialerIntent() {
        if (isQPlus()) {
            val roleManager = context?.getSystemService(RoleManager::class.java)
            if (roleManager!!.isRoleAvailable(RoleManager.ROLE_DIALER) && !roleManager.isRoleHeld(
                    RoleManager.ROLE_DIALER
                )
            ) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                resultDefaultDialerIntent.launch(intent)
            } else {
                //  navigation.navToHome(true, args.layoutName, args.pathMedia, args.typeMedia)
            }
        } else {
            if (context?.getSystemService(TelecomManager::class.java)?.defaultDialerPackage != BuildConfig.APPLICATION_ID) {
                try {
                    Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
                        TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                        BuildConfig.APPLICATION_ID
                    ).apply {
                        resultDefaultDialerIntent.launch(this)
                    }
                } catch (e: Exception) {
                    //  navigation.navToHome(true, args.layoutName, args.pathMedia, args.typeMedia)
                }
            } else {
                // navigation.navToHome(true, args.layoutName, args.pathMedia, args.typeMedia)
            }
        }

    }

    private fun initEpoxyRecyclerView() {
        binding.recyclerViewBitcoin.buildModelsWith(object :
            EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                viewModel.bitcoinList.value?.let {
                    it.forEachIndexed { index, bitcoinModel ->
                        val model = ItemBitcoinBindingModel_().id(index).apply {
                            date(bitcoinModel.date)
                            price(bitcoinModel.price.toString())
                            volume(bitcoinModel.volume)
                            index(index.plus(1).toString())
                            onClickItem { _ ->
                                viewModel.removeItem(index)
                            }
                        }
                        controller.add(model)
                    }
                }

            }
        })
    }


}