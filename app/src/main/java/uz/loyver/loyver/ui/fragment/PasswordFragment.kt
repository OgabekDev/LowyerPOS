package uz.loyver.loyver.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.FragmentPasswordBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.hideKeyboard

class PasswordFragment : Fragment(R.layout.fragment_password) {

    private lateinit var binding: FragmentPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPasswordBinding.inflate(inflater, container, false)

        isLoggedIn()

        return binding.root

    }

    private fun isLoggedIn() {
        if (SharedPref(requireContext()).getBoolean("isLoggedIn")) {
            findNavController().navigate(R.id.action_passwordFragment_to_homeFragment)
        } else {
            initViews()
        }
    }

    private fun initViews() {
        setToolbar()

        binding.apply {
            btnLogin.setOnClickListener {
                if (etPassword.text.toString() == "1234") {
                    SharedPref(requireContext()).saveBoolean("isLoggedIn", true)
                    findNavController().navigate(R.id.homeFragment)
                } else {
                    showErrorDialog()
                }
            }
        }

    }

    private fun showErrorDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(getString(R.string.str_wrong_password))
        dialog.setPositiveButton(getString(R.string.ok)) { _, _ ->}
        dialog.create().show()
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_log_in)
            toolbar.menu.clear()
            toolbar.setBackgroundColor(Color.parseColor("#0071DC"))

            toolbar.setTitleTextAppearance(requireContext(), R.style.titleWhite)

            val toggle = ActionBarDrawerToggle(
                requireActivity(),
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState()

            llOnHome.visibility = View.GONE

            toggle.isDrawerIndicatorEnabled = false

            toggle.setToolbarNavigationClickListener {
                requireActivity().finish()
            }

            toggle.setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

}