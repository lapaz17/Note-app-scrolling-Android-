package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.MainArchiveFragmentBinding
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainArchiveFragment : Fragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        MainArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
            setupListeners()
            setupState()
        }

    private fun MainArchiveFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }
    }

    private fun MainArchiveFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

        combine(
            viewModel.archivedLibraries,
            viewModel.sortingType,
            viewModel.sortingOrder,
            viewModel.isShowNotesCount,
        ) { libraries, sortingType, sortingOrder, isShowNotesCount ->
            setupLibraries(libraries.map { it.sorted(sortingType, sortingOrder) }, isShowNotesCount)
        }
            .launchIn(lifecycleScope)

        viewModel.layout
            .onEach { layout -> setupLayoutManager(layout) }
            .launchIn(lifecycleScope)
    }

    private fun MainArchiveFragmentBinding.setupLayoutManager(layout: Layout) {
        when (layout) {
            Layout.Linear -> rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            Layout.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun MainArchiveFragmentBinding.setupLibraries(state: UiState<List<Pair<Library, Int>>>, isShowNotesCount: Boolean) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val libraries = state.value

                rv.withModels {
                    val items = { items: List<Pair<Library, Int>> ->
                        items.forEach { entry ->
                            libraryItem {
                                id(entry.first.id)
                                library(entry.first)
                                notesCount(entry.second)
                                isManualSorting(false)
                                isShowNotesCount(isShowNotesCount)
                                onClickListener { _ ->
                                    navController?.navigateSafely(MainArchiveFragmentDirections.actionMainArchiveFragmentToLibraryFragment(entry.first.id))
                                }
                                onLongClickListener { _ ->
                                    navController?.navigateSafely(MainArchiveFragmentDirections.actionMainArchiveFragmentToLibraryDialogFragment(entry.first.id))
                                    true
                                }
                                onDragHandleTouchListener { _, _ -> false }
                            }
                        }
                    }
                    context?.let { context ->
                        if (libraries.isEmpty()) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.archive_is_empty))
                            }
                        } else {
                            val pinnedLibraries = libraries.filter { it.first.isPinned }
                            val notPinnedLibraries = libraries.filterNot { it.first.isPinned }

                            if (pinnedLibraries.isNotEmpty()) {
                                headerItem {
                                    id("pinned")
                                    title(context.stringResource(R.string.pinned))
                                }

                                items(pinnedLibraries)

                                if (notPinnedLibraries.isNotEmpty())
                                    headerItem {
                                        id("libraries")
                                        title(context.stringResource(R.string.libraries))
                                    }
                            }
                            items(notPinnedLibraries)
                        }
                    }
                }
            }
        }
    }
}