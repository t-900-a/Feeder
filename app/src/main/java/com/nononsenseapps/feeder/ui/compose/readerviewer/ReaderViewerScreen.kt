package com.nononsenseapps.feeder.ui.compose.readerviewer

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.nononsenseapps.feeder.R
import com.nononsenseapps.feeder.blob.blobInputStreamFromFile
import com.nononsenseapps.feeder.model.TextToSpeechViewModel
import com.nononsenseapps.feeder.model.getPlainTextOfHtmlStream
import com.nononsenseapps.feeder.ui.compose.readaloud.HideableReadAloudPlayer
import com.nononsenseapps.feeder.ui.compose.text.htmlFormattedText
import com.nononsenseapps.feeder.ui.compose.theme.keyline1Padding
import com.nononsenseapps.feeder.util.openLinkInCustomTab
import java.net.URL


/**
 * A version of Reader which parses any URL into the native reader view
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReaderViewerScreen(
    viewModel: ReaderViewerScreenViewModel,
    readAloudViewModel: TextToSpeechViewModel,
    onLinkClick: (String) -> Unit,
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current

    val onShare = remember(viewModel.currentLink) {
        {
            val intent = Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, viewModel.currentLink)
                    type = "text/plain"
                },
                null
            )
            context.startActivity(intent)
        }
    }

    val isLightTheme = MaterialTheme.colors.isLight

    @DrawableRes
    val placeHolder: Int by remember(isLightTheme) {
        derivedStateOf {
            if (isLightTheme) {
                R.drawable.placeholder_image_article_day
            } else {
                R.drawable.placeholder_image_article_night
            }
        }
    }

    val toolbarColor = MaterialTheme.colors.primarySurface.toArgb()

    val fullTextFile by viewModel.fullTextFile.collectAsState()

    ReaderViewerScreen(
        title = URL(viewModel.currentLink).host,
        onShare = onShare,
        onOpenInCustomTab = {
            openLinkInCustomTab(context, viewModel.currentLink, toolbarColor)
        },
        readAloudPlayer = {
            HideableReadAloudPlayer(readAloudViewModel)
        },
        onReadAloudStart = {
            val fullText = fullTextFile?.let { fullTextFile ->
                blobInputStreamFromFile(fullTextFile).use {
                    getPlainTextOfHtmlStream(
                        inputStream = it,
                        baseUrl = viewModel.currentLink,
                    )
                }
            }

            if (fullText==null) {
                // TODO show error some message
            } else {
                readAloudViewModel.readAloud(
                    title = URL(viewModel.currentLink).host,
                    fullText = fullText
                )
            }
        },
        onNavigateUp = onNavigateUp
    ) {
        fullTextFile?.let { fullTextFile ->
            blobInputStreamFromFile(fullTextFile).use {
                htmlFormattedText(
                    inputStream = it,
                    baseUrl = viewModel.currentLink,
                    imagePlaceholder = placeHolder,
                    onLinkClick = onLinkClick,
                )
            }
        }
    }
}

@Composable
fun ReaderViewerScreen(
    title: String,
    onShare: () -> Unit,
    onOpenInCustomTab: () -> Unit,
    onNavigateUp: () -> Unit,
    readAloudPlayer: @Composable () -> Unit,
    onReadAloudStart: () -> Unit,
    articleBody: LazyListScope.() -> Unit,
) {
    var showMenu by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                contentPadding = rememberInsetsPaddingValues(
                    LocalWindowInsets.current.statusBars,
                    applyBottom = false,
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenInCustomTab) {
                        Icon(
                            Icons.Default.OpenInBrowser,
                            contentDescription = stringResource(id = R.string.open_in_web_view)
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(id = R.string.open_menu),
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    showMenu = false
                                    onShare()
                                }
                            ) {
                                Text(stringResource(id = R.string.share))
                            }
                            DropdownMenuItem(
                                onClick = {
                                    showMenu = false
                                    onReadAloudStart()
                                }
                            ) {
                                Text(stringResource(id = R.string.read_article))
                            }
                        }
                    }
                }
            )
        },
        bottomBar = readAloudPlayer
    ) { padding ->
        SelectionContainer {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 92.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = keyline1Padding)
                    .fillMaxWidth()
            ) {
                articleBody()
            }
        }
    }
}
