package org.wordpress.android.ui.debug.previews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import org.wordpress.android.ui.main.jetpack.staticposter.JetpackStaticPosterFragment
import org.wordpress.android.ui.main.jetpack.staticposter.UiData
import org.wordpress.android.util.config.JetpackFeatureRemovalStaticPostersConfig.Companion.JETPACK_FEATURE_REMOVAL_STATIC_POSTERS_REMOTE_FIELD
import android.R as AndroidR

@AndroidEntryPoint
class PreviewFragmentActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.commit {
            val key = requireNotNull(intent.getStringExtra(KEY))
            val factory = requireNotNull(PREVIEWS[key])
            add(AndroidR.id.content, factory.invoke())
        }
    }

    companion object {
        const val KEY = "KEY"

        fun Fragment.previewFragmentInActivity(key: String) {
            startActivity(
                Intent(requireContext(), this@Companion::class.java.enclosingClass).apply {
                    putExtra(KEY, key)
                }
            )
        }

        fun Activity.previewFragmentInActivity(key: String) {
            startActivity(
                Intent(this, this@Companion::class.java.enclosingClass).apply {
                    putExtra(KEY, key)
                }
            )
        }
    }
}

val PREVIEWS = mapOf(
    JETPACK_FEATURE_REMOVAL_STATIC_POSTERS_REMOTE_FIELD to { JetpackStaticPosterFragment.newInstance(UiData.STATS) },
)
