package com.example.gains.home.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface BottomNav {
    val route: String
    val icon: ImageVector
    val label: String  // Make sure this is String type

    data object Home : BottomNav {
        override val route: String = "home"
        override val icon: ImageVector = Icons.Default.Home
        override val label: String = "Home"
    }

    data object Add : BottomNav {
        override val route: String = "add"
        override val icon: ImageVector = Icons.Default.Add
        override val label: String = "Add"
    }
    data object View : BottomNav {
        override val route: String = "view"
        override val icon: ImageVector = Icons.Default.RemoveRedEye
        override val label: String = "View"
    }
    data object Profile : BottomNav {
        override val route: String = "profile"
        override val icon: ImageVector = Icons.Default.Person
        override val label: String = "Profile"
    }
    data object Exercises : BottomNav {
        override val route: String = "exercises"
        override val icon: ImageVector = Icons.Default.Settings
        override val label: String = "Exercises"
    }
}