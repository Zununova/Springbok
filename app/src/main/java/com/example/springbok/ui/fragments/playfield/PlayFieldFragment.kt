package com.example.springbok.ui.fragments.playfield

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.springbok.R
import com.example.springbok.data.ItemData
import com.example.springbok.databinding.DefeatCardViewBinding
import com.example.springbok.databinding.FragmentPlayFieldBinding
import com.example.springbok.ui.constants.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random

class PlayFieldFragment : Fragment(R.layout.fragment_play_field) {

    private val binding by viewBinding(FragmentPlayFieldBinding::bind)
    private val viewModel by viewModels<PlayFieldViewModel>()

    private var downTime: Long = 2000
    private val handler = Handler(Looper.getMainLooper())
    private var repeatIntervalMillis: Long = 1000
    private val itemData = ItemData()

    private val changeInterval: Runnable = object : Runnable {
        override fun run() {
            viewLifecycleOwner.lifecycleScope.launch {
                if (repeatIntervalMillis >= 400 && downTime >= 500) {
                    repeatIntervalMillis -= 40
                    downTime -= 20
                } else {
                    repeatIntervalMillis += 400
                    downTime += 200
                }
            }
            handler.postDelayed(this, Constants.CHANGE_INTERVAL_TIME)
        }
    }

    private val resumeItem: Runnable = object : Runnable {
        override fun run() {
            viewLifecycleOwner.lifecycleScope.launch {
                val random = Random()
                val randomPercent = random.nextInt(100)
                var imageId: Int = R.drawable.coin2
                for (item in itemData.setImage()) {
                    if (randomPercent <= item.percent) {
                        imageId = item.imageId
                        break
                    }
                }
                val gravityX = resources.displayMetrics.widthPixels * random.nextInt(4) / 4
                items(image = imageId, gravityX = gravityX)
            }
            handler.postDelayed(this, repeatIntervalMillis)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.postDelayed(changeInterval, 5000)
        handler.postDelayed(resumeItem, repeatIntervalMillis)
        initialize()
    }

    private fun initialize() {
        viewModel.lifeCount.observe(viewLifecycleOwner) {
            if (it >= 1) {
                binding.tvLives.text = getString(R.string.lives, it)
            } else {
                handler.removeCallbacks(changeInterval)
                handler.removeCallbacks(resumeItem)
                createDefeatCard()
            }
        }
        viewModel.coinCount.observe(viewLifecycleOwner) {
            binding.tvCoins.text = getString(R.string.coins, it)
        }
    }

    private fun createDefeatCard() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val defeat = inflater.inflate(R.layout.defeat_card_view, null)
        dialogBuilder.setView(defeat)
        val btnRestart = defeat.findViewById<Button>(R.id.btn_restart)
        val btnGoToMenu = defeat.findViewById<Button>(R.id.btn_go_to_menu)

        val dialog = dialogBuilder.create()
        dialog.window?.attributes?.gravity = Gravity.CENTER
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.width = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        btnRestart.setOnClickListener {
            restartGame()
            dialog.dismiss()
        }
        btnGoToMenu.setOnClickListener {
            findNavController().navigate(R.id.action_playFieldFragment_to_loseFragment)
            dialog.dismiss()
        }
    }

    private fun restartGame() {
        viewModel.restartGame()
        handler.postDelayed(changeInterval, 5000)
        handler.postDelayed(resumeItem, repeatIntervalMillis)
    }

    private fun items(gravityX: Int, image: Int = R.drawable.cub) {
        var item = ImageView(requireContext())
        val layoutParams = ViewGroup.LayoutParams(170, 390)
        item.setPadding(10, 120, 10, 120)
        item.setImageResource(image)
        item.layoutParams = layoutParams
        item.x = gravityX.toFloat()
        item.y = 60.toFloat()
        binding.root.addView(item)
        itemMoveDown(item)
        setupListeners(item, image)
        lifecycleScope.launch {
            delay(downTime)
            binding.root.removeView(item)
        }
    }

    private fun setupListeners(item: View, image: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            item.setOnClickListener {
                when (image) {
                    R.drawable.button -> {
                        viewModel.minusLifeCount()
                        binding.root.removeView(item)
                    }

                    R.drawable.cub -> {
                        viewModel.plusLifeCount()
                        binding.root.removeView(item)
                    }

                    R.drawable.coin2 -> {
                        viewModel.addCoin()
                        binding.root.removeView(item)
                    }

                    R.drawable.coin1 -> {
                        viewModel.addCoin()
                        binding.root.removeView(item)
                    }
                }
            }
        }
    }

    private fun itemMoveDown(item: View) {
        val animator = ObjectAnimator.ofFloat(
            item,
            "translationY",
            resources.displayMetrics.heightPixels.toFloat()
        )
        animator.duration = downTime
        animator.start()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(resumeItem)
        handler.removeCallbacks(changeInterval)
    }
}