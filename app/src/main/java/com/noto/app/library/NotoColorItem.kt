package com.noto.app.library

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.widget.TooltipCompat
import androidx.core.graphics.toColor
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.NotoColorItemBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.colorResource
import com.noto.app.util.toColorStateList
import com.noto.app.util.toResource

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.noto_color_item)
abstract class NotoColorItem : EpoxyModelWithHolder<NotoColorItem.Holder>() {

    @EpoxyAttribute
    lateinit var notoColor: NotoColor

    @EpoxyAttribute
    open var isChecked: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            rb.backgroundTintList = context.colorResource(notoColor.toResource()).toColorStateList()
        }
        rb.isChecked = isChecked
        rb.setOnClickListener(onClickListener)
        TooltipCompat.setTooltipText(rb, notoColor.name)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: NotoColorItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NotoColorItemBinding.bind(itemView)
        }
    }
}