package com.example.weatherapp.animations

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class WeatherAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animationType = AnimationType.NONE
    private val particles = mutableListOf<Particle>()
    private var animator: ValueAnimator? = null

    enum class AnimationType {
        NONE, RAIN, SNOW, CLOUDS, THUNDER, CLEAR
    }

    data class Particle(
        var x: Float,
        var y: Float,
        var speed: Float,
        var size: Float,
        var alpha: Float = 255f,
        var angle: Float = 0f
    )

    fun setWeatherAnimation(type: AnimationType) {
        animationType = type
        setupAnimation()
    }

    fun setWeatherAnimation(typeString: String) {
        val type = when (typeString.lowercase()) {
            "rain" -> AnimationType.RAIN
            "snow" -> AnimationType.SNOW
            "clouds" -> AnimationType.CLOUDS
            "thunder" -> AnimationType.THUNDER
            "clear" -> AnimationType.CLEAR
            else -> AnimationType.NONE
        }
        setWeatherAnimation(type)
    }

    private fun setupAnimation() {
        animator?.cancel()
        particles.clear()

        when (animationType) {
            AnimationType.RAIN -> setupRainAnimation()
            AnimationType.SNOW -> setupSnowAnimation()
            AnimationType.CLOUDS -> setupCloudAnimation()
            AnimationType.THUNDER -> setupThunderAnimation()
            AnimationType.CLEAR -> setupClearAnimation()
            AnimationType.NONE -> return
        }

        startAnimation()
    }

    private fun setupRainAnimation() {
        repeat(50) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height,
                    speed = Random.nextFloat() * 15f + 10f,
                    size = Random.nextFloat() * 3f + 2f
                )
            )
        }
    }

    private fun setupSnowAnimation() {
        repeat(30) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height,
                    speed = Random.nextFloat() * 5f + 2f,
                    size = Random.nextFloat() * 8f + 4f,
                    angle = Random.nextFloat() * 360f
                )
            )
        }
    }

    private fun setupCloudAnimation() {
        repeat(8) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height * 0.3f,
                    speed = Random.nextFloat() * 2f + 1f,
                    size = Random.nextFloat() * 40f + 30f,
                    alpha = Random.nextFloat() * 100f + 100f
                )
            )
        }
    }

    private fun setupThunderAnimation() {
        setupRainAnimation() // Rain effect for thunderstorm
    }

    private fun setupClearAnimation() {
        repeat(20) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height,
                    speed = Random.nextFloat() * 3f + 1f,
                    size = Random.nextFloat() * 4f + 2f,
                    alpha = Random.nextFloat() * 150f + 100f
                )
            )
        }
    }

    private fun startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = Long.MAX_VALUE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                updateParticles()
                invalidate()
            }
            start()
        }
    }

    private fun updateParticles() {
        when (animationType) {
            AnimationType.RAIN -> updateRainParticles()
            AnimationType.SNOW -> updateSnowParticles()
            AnimationType.CLOUDS -> updateCloudParticles()
            AnimationType.THUNDER -> updateRainParticles()
            AnimationType.CLEAR -> updateClearParticles()
            AnimationType.NONE -> {}
        }
    }

    private fun updateRainParticles() {
        particles.forEach { particle ->
            particle.y += particle.speed
            if (particle.y > height) {
                particle.y = -particle.size
                particle.x = Random.nextFloat() * width
            }
        }
    }

    private fun updateSnowParticles() {
        particles.forEach { particle ->
            particle.y += particle.speed
            particle.x += kotlin.math.sin(particle.angle) * 2f
            particle.angle += 0.05f
            if (particle.y > height) {
                particle.y = -particle.size
                particle.x = Random.nextFloat() * width
            }
        }
    }

    private fun updateCloudParticles() {
        particles.forEach { particle ->
            particle.x += particle.speed
            if (particle.x > width + particle.size) {
                particle.x = -particle.size
                particle.y = Random.nextFloat() * height * 0.3f
            }
        }
    }

    private fun updateClearParticles() {
        particles.forEach { particle ->
            particle.y += particle.speed
            particle.alpha = (particle.alpha - 2f).coerceAtLeast(50f)
            if (particle.y > height) {
                particle.y = -particle.size
                particle.x = Random.nextFloat() * width
                particle.alpha = Random.nextFloat() * 150f + 100f
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        when (animationType) {
            AnimationType.RAIN -> drawRain(canvas)
            AnimationType.SNOW -> drawSnow(canvas)
            AnimationType.CLOUDS -> drawClouds(canvas)
            AnimationType.THUNDER -> drawThunder(canvas)
            AnimationType.CLEAR -> drawClear(canvas)
            AnimationType.NONE -> {}
        }
    }

    private fun drawRain(canvas: Canvas) {
        paint.color = Color.argb(200, 100, 150, 255)
        paint.strokeWidth = 2f

        particles.forEach { particle ->
            canvas.drawLine(
                particle.x,
                particle.y,
                particle.x - 5f,
                particle.y + particle.size * 3f,
                paint
            )
        }
    }

    private fun drawSnow(canvas: Canvas) {
        paint.color = Color.argb(200, 255, 255, 255)
        paint.style = Paint.Style.FILL

        particles.forEach { particle ->
            canvas.drawCircle(particle.x, particle.y, particle.size, paint)
        }
    }

    private fun drawClouds(canvas: Canvas) {
        paint.style = Paint.Style.FILL

        particles.forEach { particle ->
            paint.color = Color.argb(particle.alpha.toInt(), 200, 200, 200)
            canvas.drawCircle(particle.x, particle.y, particle.size, paint)
            canvas.drawCircle(particle.x + particle.size * 0.7f, particle.y, particle.size * 0.8f, paint)
            canvas.drawCircle(particle.x - particle.size * 0.7f, particle.y, particle.size * 0.8f, paint)
        }
    }

    private fun drawThunder(canvas: Canvas) {
        // Draw rain first
        drawRain(canvas)
        
        // Add lightning effect occasionally
        if (Random.nextInt(100) < 5) { // 5% chance per frame
            paint.color = Color.argb(200, 255, 255, 100)
            paint.strokeWidth = 8f
            val startX = Random.nextFloat() * width
            val startY = 0f
            val endX = startX + Random.nextFloat() * 200f - 100f
            val endY = height * 0.6f
            
            // Zigzag lightning
            var currentX = startX
            var currentY = startY
            val segments = 8
            val segmentHeight = (endY - startY) / segments
            
            for (i in 0 until segments) {
                val nextX = currentX + Random.nextFloat() * 60f - 30f
                val nextY = currentY + segmentHeight
                canvas.drawLine(currentX, currentY, nextX, nextY, paint)
                currentX = nextX
                currentY = nextY
            }
        }
    }

    private fun drawClear(canvas: Canvas) {
        paint.style = Paint.Style.FILL

        particles.forEach { particle ->
            paint.color = Color.argb(particle.alpha.toInt(), 255, 255, 150)
            
            // Draw sparkle effect
            val sparkleSize = particle.size
            canvas.drawCircle(particle.x, particle.y, sparkleSize, paint)
            
            // Add cross sparkle
            paint.strokeWidth = 2f
            paint.style = Paint.Style.STROKE
            canvas.drawLine(
                particle.x - sparkleSize * 2,
                particle.y,
                particle.x + sparkleSize * 2,
                particle.y,
                paint
            )
            canvas.drawLine(
                particle.x,
                particle.y - sparkleSize * 2,
                particle.x,
                particle.y + sparkleSize * 2,
                paint
            )
            paint.style = Paint.Style.FILL
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
