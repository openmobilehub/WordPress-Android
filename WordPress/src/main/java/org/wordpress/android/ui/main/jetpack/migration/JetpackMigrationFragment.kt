package org.wordpress.android.ui.main.jetpack.migration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.wordpress.android.fluxc.Dispatcher
import org.wordpress.android.ui.ActivityLauncher
import org.wordpress.android.ui.accounts.HelpActivity.Origin.JETPACK_MIGRATION_HELP
import org.wordpress.android.ui.compose.theme.AppTheme
import org.wordpress.android.ui.main.jetpack.migration.JetpackMigrationViewModel.JetpackMigrationActionEvent
import org.wordpress.android.ui.main.jetpack.migration.JetpackMigrationViewModel.JetpackMigrationActionEvent.CompleteFlow
import org.wordpress.android.ui.main.jetpack.migration.JetpackMigrationViewModel.JetpackMigrationActionEvent.ShowHelp
import org.wordpress.android.ui.main.jetpack.migration.JetpackMigrationViewModel.UiState.Error
import org.wordpress.android.ui.main.jetpack.migration.JetpackMigrationViewModel.UiState.Content
import org.wordpress.android.ui.main.jetpack.migration.JetpackMigrationViewModel.UiState.Loading
import org.wordpress.android.ui.main.jetpack.migration.compose.state.DoneStep
import org.wordpress.android.ui.main.jetpack.migration.compose.state.ErrorStep
import org.wordpress.android.ui.main.jetpack.migration.compose.state.LoadingState
import org.wordpress.android.ui.main.jetpack.migration.compose.state.NotificationsStep
import org.wordpress.android.ui.main.jetpack.migration.compose.state.WelcomeStep
import javax.inject.Inject

@AndroidEntryPoint
class JetpackMigrationFragment : Fragment() {
    @Inject lateinit var dispatcher: Dispatcher

    private val viewModel: JetpackMigrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            AppTheme {
                JetpackMigrationScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModelEvents()
        viewModel.start()
    }

    private fun observeViewModelEvents() {
        viewModel.actionEvents.onEach(this::handleActionEvents).launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleActionEvents(actionEvent: JetpackMigrationActionEvent) {
        when (actionEvent) {
            is CompleteFlow -> ActivityLauncher.showMainActivity(requireContext())
            is ShowHelp -> launchHelpScreen()
        }
    }

    private fun launchHelpScreen() {
        ActivityLauncher.viewHelpAndSupport(
                requireContext(),
                JETPACK_MIGRATION_HELP,
                null,
                null
        )
    }
}

@Composable
private fun JetpackMigrationScreen(viewModel: JetpackMigrationViewModel = viewModel()) {
    Box {
        val uiState by viewModel.uiState.collectAsState(Loading)

        Crossfade(targetState = uiState) { state ->
            when (state) {
                is Content.Welcome -> WelcomeStep(state)
                is Content.Notifications -> NotificationsStep(state)
                is Content.Done -> DoneStep(state)
                is Error -> ErrorStep(state)
                is Loading -> LoadingState()
            }
        }
    }
}