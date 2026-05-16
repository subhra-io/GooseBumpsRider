package com.goosebumps.rider.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.goosebumps.rider.ui.screens.delivery.DeliveryNavigationScreen
import com.goosebumps.rider.ui.screens.delivery.DeliveryOtpScreen
import com.goosebumps.rider.ui.screens.delivery.DeliverySuccessScreen
import com.goosebumps.rider.ui.screens.earnings.EarningsScreen
import com.goosebumps.rider.ui.screens.home.HomeDashboardScreen
import com.goosebumps.rider.ui.screens.login.LoginScreen
import com.goosebumps.rider.ui.screens.login.OtpScreen
import com.goosebumps.rider.ui.screens.order.IncomingOrderScreen
import com.goosebumps.rider.ui.screens.order.OrderHistoryScreen
import com.goosebumps.rider.ui.screens.pickup.PickupConfirmationScreen
import com.goosebumps.rider.ui.screens.pickup.PickupNavigationScreen
import com.goosebumps.rider.ui.screens.profile.ProfileScreen
import com.goosebumps.rider.ui.screens.settings.SettingsScreen
import com.goosebumps.rider.ui.screens.splash.SplashScreen

@Composable
fun RiderNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onNavigateToOtp = { phone, code ->
                    navController.navigate(NavRoutes.otpRoute(phone, code))
                }
            )
        }

        composable(
            route = NavRoutes.OTP,
            arguments = listOf(
                navArgument("phone") { type = NavType.StringType },
                navArgument("countryCode") { type = NavType.StringType }
            )
        ) { backStack ->
            val phone = backStack.arguments?.getString("phone") ?: ""
            val code = backStack.arguments?.getString("countryCode") ?: "+91"
            OtpScreen(
                phone = phone,
                countryCode = code,
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME) {
            HomeDashboardScreen(
                onNavigateToIncomingOrder = { orderId ->
                    navController.navigate(NavRoutes.incomingOrderRoute(orderId))
                },
                onNavigateToEarnings = { navController.navigate(NavRoutes.EARNINGS) },
                onNavigateToHistory = { navController.navigate(NavRoutes.ORDER_HISTORY) },
                onNavigateToProfile = { navController.navigate(NavRoutes.PROFILE) }
            )
        }

        composable(
            route = NavRoutes.INCOMING_ORDER,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            IncomingOrderScreen(
                orderId = orderId,
                onAccepted = { navController.navigate(NavRoutes.pickupNavRoute(orderId)) },
                onDeclined = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.PICKUP_NAVIGATION,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            PickupNavigationScreen(
                orderId = orderId,
                onArrived = { navController.navigate(NavRoutes.pickupConfirmRoute(orderId)) }
            )
        }

        composable(
            route = NavRoutes.PICKUP_CONFIRMATION,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            PickupConfirmationScreen(
                orderId = orderId,
                onPickedUp = { navController.navigate(NavRoutes.deliveryNavRoute(orderId)) }
            )
        }

        composable(
            route = NavRoutes.DELIVERY_NAVIGATION,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            DeliveryNavigationScreen(
                orderId = orderId,
                onDelivered = { navController.navigate(NavRoutes.deliveryOtpRoute(orderId)) }
            )
        }

        composable(
            route = NavRoutes.DELIVERY_OTP,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            DeliveryOtpScreen(
                orderId = orderId,
                onVerified = {
                    navController.navigate(NavRoutes.deliverySuccessRoute(orderId)) {
                        popUpTo(NavRoutes.HOME) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = NavRoutes.DELIVERY_SUCCESS,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            DeliverySuccessScreen(
                orderId = orderId,
                onNextOrder = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.EARNINGS) {
            EarningsScreen(onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.ORDER_HISTORY) {
            OrderHistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) },
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
