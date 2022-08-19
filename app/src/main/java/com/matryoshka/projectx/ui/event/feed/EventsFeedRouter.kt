package com.matryoshka.projectx.ui.event.feed

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.matryoshka.projectx.navigation.navToEventEditingScreen
import com.matryoshka.projectx.navigation.navToEventViewingScreen
import com.matryoshka.projectx.ui.common.scaffold.NavigationBottomBar

@Composable
fun EventsFeedRouter(
    viewModel: EventsFeedViewModel = hiltViewModel(),
    navController: NavController
) {
    EventsFeedScreen(
        state = viewModel.state,
        bottomBar = { NavigationBottomBar(navController = navController) },
        onAddEventClick = { navController.navToEventEditingScreen() },
        onEventClick = { eventId -> navController.navToEventViewingScreen(eventId) }
    )
}