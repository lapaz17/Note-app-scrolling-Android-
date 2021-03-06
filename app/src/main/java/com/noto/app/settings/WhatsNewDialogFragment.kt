package com.noto.app.settings

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.WhatsNewDialogFragmentBinding
import com.noto.app.domain.model.Release_1_8_0
import com.noto.app.util.BounceEdgeEffectFactory
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class WhatsNewTab {
    CurrentRelease, PreviousReleases;

    companion object {
        val Default = CurrentRelease
    }
}

class WhatsNewDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = WhatsNewDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupState()
        setupListeners()
    }

    private fun WhatsNewDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            tvDialogTitle.text = context.stringResource(R.string.whats_new)
        }
    }

    private fun WhatsNewDialogFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        viewModel.whatsNewTab
            .mapNotNull { tab ->
                context?.let { context ->
                    when (tab) {
                        WhatsNewTab.CurrentRelease -> listOf(Release_1_8_0(context.stringResource(R.string.release_1_8_0)))
                        WhatsNewTab.PreviousReleases -> listOf(Release_1_8_0(context.stringResource(R.string.release_1_8_0)))
                    }
                }
            }
            .onEach { releases ->
                rv.withModels {
                    releases.forEach { release ->
                        releaseItem {
                            id(release.version)
                            release(release)
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun WhatsNewDialogFragmentBinding.setupListeners() {
        tlWhatsNew.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.position == 0)
                        viewModel.setWhatsNewTab(WhatsNewTab.CurrentRelease)
                    else
                        viewModel.setWhatsNewTab(WhatsNewTab.PreviousReleases)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )

        btnOkay.setOnClickListener {
            viewModel.updateLastVersion().invokeOnCompletion {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.updateLastVersion()
        super.onDismiss(dialog)
    }
}