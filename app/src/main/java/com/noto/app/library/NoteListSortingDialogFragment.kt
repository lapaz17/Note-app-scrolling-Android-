package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteListSortingDialogFragmentBinding
import com.noto.app.domain.model.NoteListSortingType
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListSortingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteListSortingDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteListSortingDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        val baseDialog = BaseDialogFragmentBinding.bind(root)
            .apply {
                context?.let { context ->
                    tvDialogTitle.text = context.stringResource(R.string.notes_sorting)
                }
            }

        viewModel.library
            .onEach { library ->
                context?.let { context ->
                    val color = context.colorResource(library.color.toResource())
                    baseDialog.tvDialogTitle.setTextColor(color)
                    baseDialog.vHead.background?.mutate()?.setTint(color)
                    listOf(tvGrouping, tvSortingType, tvSortingOrder).onEach {
                        TextViewCompat.setCompoundDrawableTintList(it, color.toColorStateList())
                    }
                }
                when (library.sortingType) {
                    NoteListSortingType.Manual -> tvSortingOrder.isVisible = false
                    else -> tvSortingOrder.isVisible = true
                }
            }
            .launchIn(lifecycleScope)

        tvGrouping.setOnClickListener {
            navController?.navigateSafely(
                NoteListSortingDialogFragmentDirections.actionNoteListSortingDialogFragmentToNoteListGroupingDialogFragment(
                    args.libraryId
                )
            )
        }

        tvSortingType.setOnClickListener {
            navController?.navigateSafely(
                NoteListSortingDialogFragmentDirections.actionNoteListSortingDialogFragmentToNoteListSortingTypeDialogFragment(
                    args.libraryId
                )
            )
        }

        tvSortingOrder.setOnClickListener {
            navController?.navigateSafely(
                NoteListSortingDialogFragmentDirections.actionNoteListSortingDialogFragmentToNoteListSortingOrderDialogFragment(
                    args.libraryId
                )
            )
        }

        btnDone.setOnClickListener {
            dismiss()
        }
    }
}
