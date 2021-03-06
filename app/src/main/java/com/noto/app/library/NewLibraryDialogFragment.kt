package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NewLibraryDialogFragmentBinding
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewLibraryDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NewLibraryDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NewLibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            val baseDialogFragment = setupBaseDialogFragment()
            setupState(baseDialogFragment)
            setupListeners()
        }

    private fun NewLibraryDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            if (args.libraryId == 0L) {
                tvDialogTitle.text = context.stringResource(R.string.new_library)
            } else {
                tvDialogTitle.text = context.stringResource(R.string.edit_library)
                btnCreate.text = context.stringResource(R.string.done)
            }
        }
    }

    private fun NewLibraryDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.clipToOutline = true
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

        if (args.libraryId == 0L) {
            et.requestFocus()
            activity?.showKeyboard(root)
        }

        viewModel.library
            .onEach { library -> setupLibrary(library, baseDialogFragment) }
            .launchIn(lifecycleScope)

        viewModel.notoColors
            .onEach { pairs -> setupNotoColors(pairs) }
            .launchIn(lifecycleScope)
    }

    private fun NewLibraryDialogFragmentBinding.setupListeners() {
        btnCreate.setOnClickListener {
            val title = et.text.toString()
            if (title.isBlank()) {
                til.isErrorEnabled = true
                context?.let { context ->
                    til.error = context.stringResource(R.string.empty_title)
                }
            } else {
                activity?.hideKeyboard(root)
                updatePinnedShortcut(title)
                viewModel.createOrUpdateLibrary(
                    title,
                    tlLibraryLayout.selectedTabPosition.let {
                        when (it) {
                            0 -> Layout.Linear
                            else -> Layout.Grid
                        }
                    },
                    sNotePreviewSize.value.toInt(),
                    swShowNoteCreationDate.isChecked,
                    swSetNewNoteCursor.isChecked,
                ).invokeOnCompletion {
                    context?.updateAllWidgetsData()
                    context?.updateLibraryListWidgets()
                    context?.updateNoteListWidgets(viewModel.library.value.id)
                    dismiss()
                }
            }
        }
    }

    private fun NewLibraryDialogFragmentBinding.setupLibrary(library: Library, baseDialogFragment: BaseDialogFragmentBinding) {
        et.setText(library.title)
        et.setSelection(library.title.length)
        rv.smoothScrollToPosition(library.color.ordinal)
        val tab = when (library.layout) {
            Layout.Linear -> tlLibraryLayout.getTabAt(0)
            Layout.Grid -> tlLibraryLayout.getTabAt(1)
        }
        tlLibraryLayout.selectTab(tab)
        swShowNoteCreationDate.isChecked = library.isShowNoteCreationDate
        swSetNewNoteCursor.isChecked = library.isSetNewNoteCursorOnTitle
        context?.let { context ->
            if (library.id != 0L) {
                val color = context.colorResource(library.color.toResource())
                val colorStateList = color.toColorStateList()
                baseDialogFragment.tvDialogTitle.setTextColor(color)
                baseDialogFragment.vHead.background?.mutate()?.setTint(color)
                tlLibraryLayout.setSelectedTabIndicatorColor(color)
                sNotePreviewSize.value = library.notePreviewSize.toFloat()
                if (colorStateList != null) {
                    tlLibraryLayout.tabRippleColor = colorStateList
                    sNotePreviewSize.trackActiveTintList = colorStateList
                    sNotePreviewSize.thumbTintList = colorStateList
                    sNotePreviewSize.tickInactiveTintList = colorStateList
                }
                swShowNoteCreationDate.setupColors(thumbCheckedColor = color, trackCheckedColor = color)
                swSetNewNoteCursor.setupColors(thumbCheckedColor = color, trackCheckedColor = color)
            } else {
                swShowNoteCreationDate.setupColors()
                swSetNewNoteCursor.setupColors()
            }
        }
    }

    private fun NewLibraryDialogFragmentBinding.setupNotoColors(pairs: List<Pair<NotoColor, Boolean>>) {
        rv.withModels {
            pairs.forEach { pair ->
                notoColorItem {
                    id(pair.first.ordinal)
                    notoColor(pair.first)
                    isChecked(pair.second)
                    onClickListener { _ ->
                        viewModel.selectNotoColor(pair.first)
                    }
                }
            }
        }
    }

    private fun NewLibraryDialogFragmentBinding.updatePinnedShortcut(title: String) {
        val library = viewModel.library.value.copy(
            title = title,
            color = viewModel.notoColors.value.first { it.second }.first
        )
        context?.let { context ->
            ShortcutManagerCompat.updateShortcuts(context, listOf(context.createPinnedShortcut(library)))
        }
    }
}