package com.nononsenseapps.feeder.ui.compose.readerviewer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nononsenseapps.feeder.FeederApplication
import com.nononsenseapps.feeder.base.DIAwareViewModel
import com.nononsenseapps.feeder.blob.blobFile
import com.nononsenseapps.feeder.model.parseFullArticle
import java.io.File
import java.net.URL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

class ReaderViewerScreenViewModel(di: DI, state: SavedStateHandle) : DIAwareViewModel(di) {
    val currentLink: String = state["link"]
        ?: throw IllegalArgumentException("Missing url in state!")

    private val _fullTextFileState = MutableStateFlow<File?>(null)
    val fullTextFile: StateFlow<File?>
        get() = _fullTextFileState.asStateFlow()

    init {
        viewModelScope.launch {
            val cacheDir = getApplication<FeederApplication>().cacheDir
            val cachedFile = blobFile(URL(currentLink), cacheDir)

            if (!cachedFile.isFile) {
                parseFullArticle(currentLink, di.direct.instance(), cacheDir)
            }

            if (cachedFile.isFile) {
                _fullTextFileState.value = cachedFile
            }
        }
    }
}
