package com.example.pulseup

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.domain.models.isFilled
import com.example.domain.usecases.auth.IsUserLoggedInUseCase
import com.example.pulseup.ui.setUp.SetUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val setUpViewModel: SetUpViewModel by viewModels()
    @Inject
    lateinit var isUserLoggedInUseCase: IsUserLoggedInUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1)
            }
        }
        setupObservers()
        setUpViewModel.loadUserData()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupObservers() {
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment).navController
        lifecycleScope.launch {
            setUpViewModel.loadUserInfoFlow.collectLatest { response ->
                when{
                    response.isSuccess -> {
                       val user = response.getOrNull()
                        user?.let {
                            if(user.isFilled()){
                                Log.d("mydebug", "setupObservers FILLED: $response")
                                navController.navigate(NavGraphDirections.actionGlobalToMainFragment())
                            }
                            else{
                                Log.d("mydebug", "setupObservers UNFILLED: $response")
                                navController.navigate(NavGraphDirections.actionGlobalToGenderFragment())
                            }
                        }
                        if(user == null){
                            navController.navigate(NavGraphDirections.actionGlobalToStartFragment())
                        }
                    }
                    response.isFailure -> {
                        Log.d("mydebug", "setupObservers FAILURE: $response")
                        navController.navigate(NavGraphDirections.actionGlobalToStartFragment())
                    }
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        setUpViewModel.saveUser()
    }
}