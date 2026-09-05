package com.sanitova.sanitovacheck

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Scan : Screen("scan")
    object Report : Screen("report/{scanId}") {
        fun createRoute(scanId: String) = "report/$scanId"
    }
    object History : Screen("history")
    object Learn : Screen("learn")
    object ArticleDetail : Screen("article/{articleId}") {
        fun createRoute(articleId: String) = "article/$articleId"
    }
    object Settings : Screen("settings")
    object Paywall : Screen("paywall")
}

@Composable
fun SanitovaCheckNavHost() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val startDestination = if (OnboardingPrefs.hasSeenOnboarding(context)) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onStartScan = { navController.navigate(Screen.Scan.route) },
                onOpenHistory = { navController.navigate(Screen.History.route) },
                onOpenLearn = { navController.navigate(Screen.Learn.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Scan.route) {
            ScanScreen(
                onScanComplete = { scanId ->
                    navController.navigate(Screen.Report.createRoute(scanId))
                },
                onRequirePro = { navController.navigate(Screen.Paywall.route) }
            )
        }

        composable(Screen.Report.route) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getString("scanId") ?: ""
            ReportScreen(
                scanId = scanId,
                onRequirePro = { navController.navigate(Screen.Paywall.route) }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onRequirePro = { navController.navigate(Screen.Paywall.route) },
                onOpenRecord = { scanId ->
                    navController.navigate(Screen.Report.createRoute(scanId))
                }
            )
        }

        composable(Screen.Learn.route) {
            LearnScreen(
                onOpenArticle = { articleId ->
                    navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                }
            )
        }

        composable(Screen.ArticleDetail.route) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailScreen(
                articleId = articleId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onRequirePro = { navController.navigate(Screen.Paywall.route) })
        }

        composable(Screen.Paywall.route) {
            PaywallScreen(onDismiss = { navController.popBackStack() })
        }
    }
}
