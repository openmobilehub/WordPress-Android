package org.wordpress.android.ui.compose.views

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DimenRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import org.wordpress.android.R
import org.wordpress.android.ui.avatars.TrainOfAvatarsItem.AvatarItem
import org.wordpress.android.ui.compose.components.TrainOfIcons
import org.wordpress.android.ui.compose.theme.AppTheme
import org.wordpress.android.util.DisplayUtils

@Suppress("MemberVisibilityCanBePrivate", "unused")
class TrainOfAvatarsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {
    private val avatarsState: MutableState<List<AvatarItem>> = mutableStateOf(emptyList())

    // icon size in DP used by the Composable
    private val iconSizeState: MutableState<Int> = mutableStateOf(DEFAULT_ICON_SIZE_DP)

    // icon border width in DP used by the Composable
    private val iconBorderWidthState: MutableState<Int> = mutableStateOf(DEFAULT_ICON_BORDER_WIDTH_DP)

    var avatars: List<AvatarItem>
        get() = avatarsState.value
        set(value) {
            if (avatarsState.value != value) avatarsState.value = value
        }

    /**
     * Icon size in pixels
     */
    var iconSize: Int
        get() = DisplayUtils.dpToPx(context, iconSizeState.value)
        set(value) {
            val iconSizeDp = DisplayUtils.pxToDp(context, value)
            if (iconSizeState.value != iconSizeDp) iconSizeState.value = iconSizeDp
        }

    /**
     * Icon border width in pixels
     */
    var iconBorderWidth: Int
        get() = DisplayUtils.dpToPx(context, iconBorderWidthState.value)
        set(value) {
            val borderWidthDp = DisplayUtils.pxToDp(context, value)
            if (iconBorderWidthState.value != borderWidthDp) iconBorderWidthState.value = borderWidthDp
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TrainOfAvatarsView)
        ta.getDimensionPixelSize(R.styleable.TrainOfAvatarsView_iconSize, -1)
            .takeIf { it != -1 }
            ?.let { iconSize = it }
        ta.getDimensionPixelSize(R.styleable.TrainOfAvatarsView_iconBorderWidth, -1)
            .takeIf { it != -1 }
            ?.let { iconBorderWidth = it }
        ta.recycle()
    }

    @Composable
    override fun Content() {
        if (avatarsState.value.isEmpty()) return

        AppTheme {
            TrainOfIcons(
                iconModels = avatarsState.value.map { it.userAvatarUrl },
                iconSize = iconSizeState.value.dp,
                iconBorderWidth = iconBorderWidthState.value.dp,
            )
        }
    }

    fun setIconSizeRes(@DimenRes sizeRes: Int) {
        val newIconSize = context.resources.getDimensionPixelSize(sizeRes)
        if (newIconSize != iconSize) iconSize = newIconSize
    }

    fun setIconBorderWidthRes(@DimenRes sizeRes: Int) {
        val newIconBorderWidth = context.resources.getDimensionPixelSize(sizeRes)
        if (newIconBorderWidth != iconBorderWidth) iconBorderWidth = newIconBorderWidth
    }

    companion object {
        private const val DEFAULT_ICON_SIZE_DP = 32
        private const val DEFAULT_ICON_BORDER_WIDTH_DP = 2
    }
}
