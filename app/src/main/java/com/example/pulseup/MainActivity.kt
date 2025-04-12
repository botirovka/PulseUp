package com.example.pulseup

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
        setupObservers()
        setUpViewModel.loadUserData()

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
                                navController.navigate(NavGraphDirections.actionGlobalToHomeFragment())
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