package com.noto.app.library

import android.annotation.SuppressLint
import android.view.View
import androidx.core.graphics.ColorUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.NoteListSortingItemBinding
import com.noto.app.domain.model.NoteListSortingType
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.*

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.note_list_sorting_item)
abstract class NoteListSortingItem : EpoxyModelWithHolder<NoteListSortingItem.Holder>() {

    @EpoxyAttribute
    lateinit var sortingType: NoteListSortingType

    @EpoxyAttribute
    lateinit var sortingOrder: SortingOrder

    @EpoxyAttribute
    lateinit var notoColor: NotoColor

    @EpoxyAttribute
    var notesCount: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val color = context.colorResource(notoColor.toResource())
            tvLibraryNotesCount.text = context.pluralsResource(R.plurals.notes_count, notesCount, notesCount).lowercase()
            tvSorting.background?.mutate()?.setTint(ColorUtils.setAlphaComponent(color, 25))
            tvSorting.compoundDrawables[0]?.mutate()?.setTint(color)
            tvLibraryNotesCount.setTextColor(color)
            tvSorting.setTextColor(color)
            tvSorting.text = when (sortingType) {
                NoteListSortingType.Manual -> context.stringResource(R.string.manual_sorting)
                NoteListSortingType.CreationDate -> context.stringResource(R.string.creation_date_sorting)
                NoteListSortingType.Alphabetical -> context.stringResource(R.string.alphabetical_sorting)
            }
        }
        tvSorting.setOnClickListener(onClickListener)
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: NoteListSortingItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NoteListSortingItemBinding.bind(itemView)
        }
    }

}