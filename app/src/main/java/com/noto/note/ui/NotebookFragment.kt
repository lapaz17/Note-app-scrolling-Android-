package com.noto.note.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.NotoDialog
import com.noto.R
import com.noto.database.NotoColor
import com.noto.databinding.FragmentNotebookBinding
import com.noto.network.Repos
import com.noto.note.adapter.NavigateToNote
import com.noto.note.adapter.NotebookRVAdapter
import com.noto.note.model.Notebook
import com.noto.note.viewModel.NotebookViewModel
import com.noto.note.viewModel.NotebookViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class NotebookFragment : Fragment(), NavigateToNote {

    // Binding
    private lateinit var binding: FragmentNotebookBinding

    private lateinit var adapter: NotebookRVAdapter

    private val args by navArgs<NotebookFragmentArgs>()

    private val viewModel by viewModels<NotebookViewModel> {
        NotebookViewModelFactory(Repos.notebookRepository, Repos.noteRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotebookBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        when (args.notoColor) {
            NotoColor.GRAY -> setGray()
            NotoColor.BLUE -> setBlue()
            NotoColor.PINK -> setPink()
            NotoColor.CYAN -> setCyan()
        }

        // RV
        binding.rv.let { rv ->

            viewModel.getNotes(args.notebookId)

            adapter = NotebookRVAdapter(this)

            rv.adapter = adapter

            rv.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            viewModel.notes.observe(viewLifecycleOwner, Observer {

                it?.let {

                    if (it.isEmpty()) {
                        rv.visibility = View.GONE
                        binding.emptyNotebook.visibility = View.VISIBLE
                    } else {

                        rv.visibility = View.VISIBLE
                        binding.emptyNotebook.visibility = View.GONE
                        adapter.submitList(it)
                    }

                }

            })

        }

        // Collapsing Toolbar
        binding.ctb.let { ctb ->

            ctb.title = args.notebookTitle

            ctb.setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_bold))

            ctb.setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_medium))
        }

        binding.fab.setOnClickListener {
            this.findNavController().navigate(
                NotebookFragmentDirections.actionNotebookFragmentToNoteFragment(
                    0L,
                    args.notebookId,
                    args.notebookTitle,
                    args.notoColor
                )
            )
        }

        binding.tb.let { tb ->

            tb.setNavigationOnClickListener {
                this.findNavController().navigateUp()
            }

            tb.setOnMenuItemClickListener {

                when (it.itemId) {
                    R.id.style -> {
                        NotoDialog(
                            context!!,
                            Notebook(args.notebookId, args.notebookTitle, args.notoColor),
                            null
                        ).apply {
                            this.notebook!!

                            dialogBinding.createBtn.text = resources.getString(R.string.update)

                            dialogBinding.et.setText(args.notebookTitle)

                            dialogBinding.et.setSelection(args.notebookTitle.length)

                            this.dialogBinding.et.hint = "Notebook title"

                            this.dialogBinding.createBtn.setOnClickListener {

                                when {
//                                    viewModel.notebooks.value!!.any {
//                                        it.notebookTitle ==
//                                                dialogBinding.et.text.toString()
//                                    } -> {
//
//                                        this.dialogBinding.til.error =
//                                            "Notebook with the same title already exists!"
//
//                                    }
                                    this.dialogBinding.et.text.toString().isBlank() -> {

                                        this.dialogBinding.til.error =
                                            "Notebook title can't be empty"

                                        this.dialogBinding.til.counterTextColor =
                                            ColorStateList.valueOf(
                                                resources.getColor(
                                                    R.color.colorOnPrimaryPink,
                                                    null
                                                )
                                            )

                                    }
                                    else -> {
                                        this.notebook.notebookTitle =
                                            this.dialogBinding.et.text.toString()
                                        viewModel.updateNotebook(this.notebook)
                                        this.dismiss()
                                    }
                                }
                            }
                            this.create()
                            this.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                            this.show()
                            dialogBinding.et.requestFocus()
                            this.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                        }
                        true
                    }

                    R.id.delete_notebook -> {
                        viewModel.deleteNotebook(args.notebookId)
                        this.findNavController().navigateUp()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }



        return binding.root
    }


    private fun setGray() {

        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.colorPrimaryGray, null)

            it.root.setBackgroundColor(resources.getColor(R.color.colorPrimaryGray, null))

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryGray,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.colorPrimaryGray,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryGray,
                    null
                )
            )


            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))
        }
    }

    private fun setBlue() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.colorPrimaryBlue, null)

            it.root.setBackgroundColor(resources.getColor(R.color.colorPrimaryBlue, null))

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryBlue,
                    null
                )
            )

            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.colorPrimaryBlue,
                    null
                )
            )

            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryBlue,
                    null
                )
            )


            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryBlue, null))
        }
    }

    private fun setPink() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.colorPrimaryPink, null)


            it.root.setBackgroundColor(resources.getColor(R.color.colorPrimaryPink, null))

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryPink,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.colorPrimaryPink,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryPink,
                    null
                )
            )

            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))
        }
    }

    private fun setCyan() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.colorPrimaryCyan, null)


            it.root.setBackgroundColor(resources.getColor(R.color.colorPrimaryCyan, null))

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryCyan,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.colorPrimaryCyan,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryCyan,
                    null
                )
            )

            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryCyan, null))
        }
    }

    override fun navigate(id: Long) {
        this.findNavController()
            .navigate(
                NotebookFragmentDirections.actionNotebookFragmentToNoteFragment(
                    id,
                    args.notebookId,
                    args.notebookTitle,
                    args.notoColor
                )
            )
    }
}
