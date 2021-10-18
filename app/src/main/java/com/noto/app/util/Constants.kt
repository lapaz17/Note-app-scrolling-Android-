package com.noto.app.util

object Constants {
    const val ThemeKey = "Theme"
    const val FontKey = "Font"
    const val LibraryListLayoutManagerKey = "Library_List_Layout_Manager" // Remove this after two releases
    const val LibraryListLayoutKey = "Library_List_Layout"
    const val LibraryListSortingKey = "Library_List_Sorting"
    const val LibraryListSortingTypeKey = "Library_List_Sorting_Type"
    const val LibraryListSortingOrderKey = "Library_List_Sorting_Order"
    const val ShowNotesCountKey = "Show_Notes_Count"
    const val LanguageKey = "Language"
    const val LibraryId = "library_id"
    const val NoteId = "note_id"
    const val Body = "body"
    const val SelectedLibraryItemClickListener = "select_library_item_click_listener"
    const val Libraries = "Libraries"
    const val Notes = "Notes"
    const val Labels = "Labels"
    const val NoteLabels = "NoteLabels"
    const val Settings = "Settings"

    object Intent {
        const val ActionCreateLibrary = "com.noto.intent.action.CREATE_LIBRARY"
        const val ActionCreateNote = "com.noto.intent.action.CREATE_NOTE"
    }
}