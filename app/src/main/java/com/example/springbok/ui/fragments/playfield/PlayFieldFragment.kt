package com.example.springbok.ui.fragments.playfield

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.springbok.R
import com.example.springbok.data.ItemData
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
            if (repeatIntervalMillis >= 400 && downTime >= 500) {
                repeatIntervalMillis -= 40
                downTime -= 20
            }else{
                repeatIntervalMillis += 400
                downTime += 200
            }
            handler.postDelayed(this, Constants.CHANGE_INTERVAL_TIME)
        }
    }

    private val resumeItem: Runnable = object : Runnable {
        override fun run() {
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
                findNavController().navigate(R.id.action_playFieldFragment_to_loseFragment)
            }
        }

        viewModel.coinCount.observe(viewLifecycleOwner) {
            binding.tvCoins.text = getString(R.string.coins, it)
        }
    }

    private fun items(gravityX: Int, image: Int = R.drawable.cub) {
        var item = ImageView(requireContext())
        val layoutParams = ViewGroup.LayoutParams(150, 150)
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
                    item.isInvisible = true
                    viewModel.addCoin()
                    binding.root.removeView(item)
                }

                R.drawable.coin1 -> {
                    item.isInvisible = true
                    viewModel.addCoin()
                    binding.root.removeView(item)
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