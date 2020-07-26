package com.noto.data.source.fake

import com.noto.data.source.local.LibraryLocalDataSource
import com.noto.domain.model.Library
import com.noto.domain.replaceWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLibraryDao : LibraryLocalDataSource {

    private val libraries = mutableListOf<Library>()

    override fun getLibraries(): Flow<List<Library>> = flowOf(libraries)

    override fun getLibrary(libraryId: Long): Flow<Library> = flowOf(libraries.first { library -> library.libraryId == libraryId })

    override suspend fun createLibrary(library: Library) {
        libraries.add(library)
    }

    override suspend fun updateLibrary(library: Library) = libraries.replaceWith(library) {
        it.libraryId == library.libraryId
    }

    override suspend fun deleteLibrary(library: Library) {
        libraries.remove(library)
    }

    override suspend fun countLibraryNotos(libraryId: Long): Int {
        TODO("Not yet implemented")
    }

}