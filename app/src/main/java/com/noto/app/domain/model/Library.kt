package com.noto.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "libraries")
data class Library(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "position")
    val position: Int,

    @ColumnInfo(name = "color")
    val color: NotoColor = NotoColor.Gray,

    @ColumnInfo(name = "creation_date")
    val creationDate: Instant = Clock.System.now(),

    @ColumnInfo(name = "layout", defaultValue = "0")
    val layout: Layout = Layout.Linear,

    @ColumnInfo(name = "note_preview_size", defaultValue = "15")
    val notePreviewSize: Int = 15,

    @ColumnInfo(name = "is_archived", defaultValue = "0")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "is_pinned", defaultValue = "0")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_show_note_creation_date", defaultValue = "0")
    val isShowNoteCreationDate: Boolean = false,

    @ColumnInfo(name = "is_set_new_note_cursor_on_title", defaultValue = "0")
    val isSetNewNoteCursorOnTitle: Boolean = false,

    @ColumnInfo(name = "sorting_type", defaultValue = "1")
    val sortingType: NoteListSortingType = NoteListSortingType.CreationDate,

    @ColumnInfo(name = "sorting_order", defaultValue = "1")
    val sortingOrder: SortingOrder = SortingOrder.Descending,

    @ColumnInfo(name = "grouping", defaultValue = "0")
    val grouping: Grouping = Grouping.Default,

    @ColumnInfo(name = "is_vaulted", defaultValue = "0")
    val isVaulted: Boolean = false,
)