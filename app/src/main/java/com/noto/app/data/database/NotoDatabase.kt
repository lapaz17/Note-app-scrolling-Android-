package com.noto.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noto.app.data.database.migration.*
import com.noto.app.data.source.LabelDao
import com.noto.app.data.source.LibraryDao
import com.noto.app.data.source.NoteDao
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NoteLabel

private const val NOTO_DATABASE = "Noto Database"

@TypeConverters(
    NotoColorConverter::class,
    LocalDateConverter::class,
    ZonedDateTimeConverter::class,
    SortingMethodConverter::class,
    SortingTypeConverter::class,
)
@Database(entities = [Note::class, Library::class, Label::class, NoteLabel::class], version = 6, exportSchema = false)
abstract class NotoDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao

    abstract val libraryDao: LibraryDao

    abstract val labelDao: LabelDao

    companion object {

        @Volatile
        private var INSTANCE: NotoDatabase? = null

        fun getInstance(context: Context): NotoDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context)
                .also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, NotoDatabase::class.java, NOTO_DATABASE)
                .addMigrations(Migration1To2, Migration2To3, Migration3To4, Migration4To5, Migration5To6)
                .build()
    }
}