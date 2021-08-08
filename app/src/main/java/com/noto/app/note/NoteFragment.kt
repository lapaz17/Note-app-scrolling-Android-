package com.noto.app.note

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.databinding.NoteFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.util.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class NoteFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId, args.body) }

    private val args by navArgs<NoteFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun NoteFragmentBinding.setupState() {

        if (args.noteId == 0L) {
            etNoteBody.requestFocus()
            imm.showKeyboard()
        }

        nsv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
        val archiveMenuItem = bab.menu.findItem(R.id.archive_note)

        viewModel.note
            .filterNotNull()
            .onEach { note -> setupNote(note, archiveMenuItem) }
            .launchIn(lifecycleScope)

        viewModel.library
            .onEach { library -> setupLibrary(library) }
            .launchIn(lifecycleScope)
    }

    private fun NoteFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            findNavController()
                .navigate(NoteFragmentDirections.actionNoteFragmentToNoteReminderDialogFragment(args.libraryId, args.noteId))
        }

        val backCallback = {
            if (args.body != null)
                findNavController().popBackStack(R.id.mainFragment, false)
            findNavController().navigateUp()
            viewModel.createOrUpdateNote(
                etNoteTitle.text.toString(),
                etNoteBody.text.toString(),
            )
            imm.hideKeyboard(etNoteBody.windowToken)
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) { backCallback() }
            .isEnabled = true

        tb.setNavigationOnClickListener {
            backCallback()
        }

        bab.setNavigationOnClickListener {
            findNavController().navigate(
                NoteFragmentDirections.actionNoteFragmentToNoteDialogFragment(
                    args.libraryId,
                    args.noteId,
                    R.id.libraryFragment
                )
            )
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share_noto -> {
                    launchShareNoteIntent(viewModel.note.value)
                    true
                }
                R.id.archive_note -> {
                    if (viewModel.note.value.isArchived) {
                        viewModel.toggleNoteIsArchived()
                        root.snackbar(getString(R.string.note_unarchived), anchorView = fab)
                    } else {
                        viewModel.toggleNoteIsArchived()
                        root.snackbar(getString(R.string.note_archived), anchorView = fab)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun NoteFragmentBinding.setupLibrary(library: Library) {
        val color = resources.colorResource(library.color.toResource())

        tb.title = library.title
        tb.setTitleTextColor(color)
        tvCreatedAt.setTextColor(color)
        tb.navigationIcon?.mutate()?.setTint(color)
        fab.backgroundTintList = resources.colorStateResource(library.color.toResource())
        bab.menu.forEach { it.icon?.mutate()?.setTint(color) }
        bab.navigationIcon?.mutate()?.setTint(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fab.outlineAmbientShadowColor = color
            fab.outlineSpotShadowColor = color
        }
    }

    private fun NoteFragmentBinding.setupNote(note: Note, archiveMenuItem: MenuItem) {
        etNoteTitle.setText(note.title)
        etNoteBody.setText(note.body)
        etNoteTitle.setSelection(note.title.length)
        etNoteBody.setSelection(note.body.length)
        tvCreatedAt.text = "${getString(R.string.created)} ${note.formatCreationDate()}"

        if (note.isArchived) archiveMenuItem.icon = resources.drawableResource(R.drawable.ic_round_unarchive_24)
        else archiveMenuItem.icon = resources.drawableResource(R.drawable.ic_round_archive_24)

        val color = viewModel.library.value.color.toResource()
        val resource = resources.colorResource(color)
        archiveMenuItem.icon?.mutate()?.setTint(resource)

        if (note.reminderDate == null) fab.setImageDrawable(resources.drawableResource(R.drawable.ic_round_notification_add_24))
        else fab.setImageDrawable(resources.drawableResource(R.drawable.ic_round_edit_notifications_24))
    }

}