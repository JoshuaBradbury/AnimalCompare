package uk.co.newagedev.animalcompare.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.imageLoader
import com.google.accompanist.coil.LocalImageLoader
import dagger.hilt.android.AndroidEntryPoint
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.ui.screens.Screen
import uk.co.newagedev.animalcompare.ui.screens.favourites.FavouritesScreen
import uk.co.newagedev.animalcompare.ui.screens.review.ReviewScreen
import uk.co.newagedev.animalcompare.ui.screens.swipe.SwipeScreen
import uk.co.newagedev.animalcompare.ui.theme.AnimalCompareTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimalCompareTheme {
                CompositionLocalProvider(LocalImageLoader provides LocalContext.current.imageLoader) {
                    Surface(color = MaterialTheme.colors.background) {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                Screen.allScreens.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                screen.getIcon(),
                                contentDescription = stringResource(screen.title)
                            )
                        },
                        label = { Text(stringResource(screen.title)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Dogs.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dogs.route) { SwipeScreen(animalType = AnimalType.Dog) }
            composable(Screen.Cats.route) { SwipeScreen(animalType = AnimalType.Cat) }
            composable(Screen.Foxes.route) { SwipeScreen(animalType = AnimalType.Fox) }
            composable(Screen.Review.route) { ReviewScreen() }
            composable(Screen.TopAnimals.route) { FavouritesScreen() }
        }
    }
}