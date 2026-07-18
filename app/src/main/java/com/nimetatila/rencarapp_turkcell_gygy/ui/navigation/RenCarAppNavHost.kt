package com.nimetatila.rencarapp_turkcell_gygy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.LicenseVerificationScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.LoginScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.VerifyScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.SplashScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.RegisterScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.MainDashboardScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.SelfieVerificationScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.VehiclePhotoUploadScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.ReservationApprovalScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.ActiveRentalScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.AuthViewModel
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.LicenseViewModel
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.ReservationViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Verify : Screen("verify/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "verify/$phoneNumber"
    }
    object LicenseVerification : Screen("license_verification")
    object SelfieVerification : Screen("selfie_verification")
    object MainDashboard : Screen("main_dashboard")
    object VehiclePhotoUpload : Screen("vehicle_photo_upload/{vehicleId}") {
        fun createRoute(vehicleId: String) = "vehicle_photo_upload/$vehicleId"
    }
    object ReservationApproval : Screen("reservation_approval/{vehicleId}") {
        fun createRoute(vehicleId: String) = "reservation_approval/$vehicleId"
    }
    object PaymentSummary : Screen("payment_summary/{rentalId}") {
        fun createRoute(rentalId: String) = "payment_summary/$rentalId"
    }
    object ActiveRental : Screen("active_rental/{rentalId}") {
        fun createRoute(rentalId: String) = "active_rental/$rentalId"
    }
}

@Composable
fun RenCarAppNavHost(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState = authViewModel.authState.collectAsState().value
    val licenseViewModel: LicenseViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                isDarkTheme = isDarkTheme
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    authViewModel.resetState()
                    navController.navigate(Screen.LicenseVerification.route) {
                        popUpTo(Screen.Splash.route)
                    }
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route)
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onCodeSent = { phoneNumber ->
                    authViewModel.resetState()
                    navController.navigate(Screen.Verify.createRoute(phoneNumber))
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Splash.route)
                    }
                }
            )
        }
        composable(
            route = Screen.Verify.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            VerifyScreen(
                phoneNumber = phoneNumber,
                onBackClick = { navController.popBackStack() },
                onChangeNumberClick = {
                    navController.popBackStack(Screen.Login.route, inclusive = false)
                },
                onVerifySuccess = {
                    authViewModel.resetState()
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.MainDashboard.route) {
            MainDashboardScreen(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onLogoutClick = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                onLicenseClick = {
                    navController.navigate(Screen.LicenseVerification.route)
                },
                onReserveClick = { vehicleId ->
                    navController.navigate(Screen.VehiclePhotoUpload.createRoute(vehicleId))
                },
                onActiveRentalFound = { rentalId ->
                    navController.navigate(Screen.ActiveRental.createRoute(rentalId)) {
                        popUpTo(Screen.MainDashboard.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.LicenseVerification.route) {
            LicenseVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(Screen.SelfieVerification.route)
                },
                viewModel = licenseViewModel
            )
        }
        composable(Screen.SelfieVerification.route) {
            SelfieVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                viewModel = licenseViewModel
            )
        }
        composable(
            route = Screen.VehiclePhotoUpload.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            val reservationViewModel: ReservationViewModel = hiltViewModel()
            VehiclePhotoUploadScreen(
                vehicleId = vehicleId,
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(Screen.ReservationApproval.createRoute(vehicleId))
                },
                viewModel = reservationViewModel
            )
        }
        composable(
            route = Screen.ReservationApproval.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.VehiclePhotoUpload.route)
            }
            val reservationViewModel: ReservationViewModel = hiltViewModel(parentEntry)
            val authViewModel: AuthViewModel = hiltViewModel()
            ReservationApprovalScreen(
                vehicleId = vehicleId,
                onBackClick = { navController.popBackStack() },
                onReservationSuccess = {
                    // Update auth state to make sure active reservation triggers screen updates if needed
                    authViewModel.getProfile()
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.MainDashboard.route) { inclusive = true }
                    }
                },
                viewModel = reservationViewModel
            )
        }
        composable(
            route = Screen.PaymentSummary.route,
            arguments = listOf(navArgument("rentalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rentalId = backStackEntry.arguments?.getString("rentalId") ?: ""
            com.nimetatila.rencarapp_turkcell_gygy.ui.screens.PaymentSummaryScreen(
                rentalId = rentalId,s
                onPaymentSuccess = {
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.ActiveRental.route,
            arguments = listOf(navArgument("rentalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rentalId = backStackEntry.arguments?.getString("rentalId") ?: ""
            ActiveRentalScreen(
                rentalId = rentalId,
                onEndSuccess = { rId ->
                    navController.navigate(Screen.PaymentSummary.createRoute(rId)) {
                        popUpTo(Screen.MainDashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
