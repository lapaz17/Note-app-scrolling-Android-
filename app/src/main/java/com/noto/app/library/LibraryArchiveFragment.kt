package com.noto.app.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.LibraryArchiveFragmentBinding
import com.noto.app.domain.model.*
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryArchiveFragment : Fragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<LibraryArchiveFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LibraryArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
            setupListeners()
            setupState()
        }

    private fun LibraryArchiveFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }
    }

    private fun LibraryArchiveFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

        viewModel.library
            .onEach { library -> setupLibrary(library) }
            .distinctUntilChangedBy { library -> library.layout }
            .onEach { library -> setupLayoutManger(library.layout) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.archivedNotes,
            viewModel.font,
            viewModel.library,
        ) { archivedNotes, font, library ->
            setupArchivedNotes(archivedNotes.map { it.sorted(library.sortingType, library.sortingOrder) }, font, library)
        }.launchIn(lifecycleScope)
    }

    private fun LibraryArchiveFragmentBinding.setupLayoutManger(layout: Layout) {
        when (layout) {
            Layout.Linear -> rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            Layout.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show))
    }

    private fun LibraryArchiveFragmentBinding.setupLibrary(library: Library) {
        context?.let { context ->
            val color = context.colorResource(library.color.toResource())
            tb.navigationIcon?.mutate()?.setTint(color)
            tb.title = context.stringResource(R.string.archive, library.title)
            tb.setTitleTextColor(color)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun LibraryArchiveFragmentBinding.setupArchivedNotes(state: UiState<List<NoteWithLabels>>, font: Font, library: Library) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator(library.color)
            is UiState.Success -> {
                val archivedNotes = state.value

                rv.withModels {
                    val items = { items: List<NoteWithLabels> ->
                        items.forEach { archivedNote ->
                            noteItem {
                                id(archivedNote.first.id)
                                note(archivedNote.first)
                                font(font)
                                previewSize(library.notePreviewSize)
                                isShowCreationDate(library.isShowNoteCreationDate)
                                color(library.color)
                                labels(archivedNote.second)
                                isManualSorting(false)
                                onClickListener { _ ->
                                    navController
                                        ?.navigateSafely(
                                            LibraryArchiveFragmentDirections.actionLibraryArchiveFragmentToNoteFragment(
                                                archivedNote.first.libraryId,
                                                archivedNote.first.id
                                            )
                                        )
                                }
                                onLongClickListener { _ ->
                                    navController
                                        ?.navigateSafely(
                                            LibraryArchiveFragmentDirections.actionLibraryArchiveFragmentToNoteDialogFragment(
                                                archivedNote.first.libraryId,
                                                archivedNote.first.id,
                                                R.id.libraryArchiveFragment
                                            )
                                        )
                                    true
                                }
                                onDragHandleTouchListener { _, _ -> false }
                            }
                        }
                    }

                    context?.let { context ->
                        if (archivedNotes.isEmpty())
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.archive_is_empty))
                            }
                        else
                            buildNotesModels(context, library, archivedNotes, items)
                    }
                }
            }
        }
    }
}