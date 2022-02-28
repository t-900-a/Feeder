package com.nononsenseapps.feeder.ui.compose.utils

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * https://stackoverflow.com/a/30035735/535073
 *
 *  the only reason why "it works" is because that is what PhoneWindowManager uses to decide where
 *  to put the system bar. If PhoneWindowManager changes, so will isSystemBarOnBottom() have to
 *  change. This is all a hack, but a hack is all we got.
 */
fun isSystemBarOnBottom(context: Context): Boolean {
    val cfg: Configuration = context.resources.configuration
    val dm: DisplayMetrics = context.resources.displayMetrics
    val canMove = dm.widthPixels != dm.heightPixels &&
            cfg.smallestScreenWidthDp < 600
    return !canMove || dm.widthPixels < dm.heightPixels
}

@Composable
fun IsSystemBarOnBottom(): Boolean {
    return isSystemBarOnBottom(LocalContext.current).also { Log.d("JONAS", "Bottom? $it") }
}
