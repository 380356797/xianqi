package com.example.myapplication.domain.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import kotlin.math.sin

/**
 * 音效管理器，管理游戏中的音效播放
 * 走棋、吃子、将军等音效使用代码合成生成，无需外部音频文件
 */
class SoundManager(private val context: Context) {

    private var bgMediaPlayer: MediaPlayer? = null

    private var isInitialized = false
    private var isMusicEnabled = true
    private var isSoundEffectEnabled = true
    private var musicVolume = 0.7f
    private var soundEffectVolume = 0.8f

    // 合成的音效数据
    private var moveSoundData: ShortArray? = null
    private var captureSoundData: ShortArray? = null
    private var checkSoundData: ShortArray? = null
    private var winSoundData: ShortArray? = null
    private var loseSoundData: ShortArray? = null

    companion object {
        private const val SAMPLE_RATE = 22050
    }

    /**
     * 初始化音效资源
     */
    fun init() {
        if (isInitialized) return

        // 合成音效数据
        moveSoundData = generateMoveSound()
        captureSoundData = generateCaptureSound()
        checkSoundData = generateCheckSound()
        winSoundData = generateWinSound()
        loseSoundData = generateLoseSound()

        // 尝试加载背景音乐
        try {
            val resId = context.resources.getIdentifier("background_music", "raw", context.packageName)
            if (resId != 0) {
                bgMediaPlayer = MediaPlayer.create(context, resId)?.apply {
                    isLooping = true
                    setVolume(musicVolume, musicVolume)
                }
            }
        } catch (e: Exception) {
            // 背景音乐文件不存在时静默处理
        }

        isInitialized = true
    }

    /**
     * 生成走棋音效：清脆的木质落子声（高频短音）
     * 模拟棋子落在棋盘上的"嗒"声
     */
    private fun generateMoveSound(): ShortArray {
        val durationMs = 80
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            // 包络：快速起音，快速衰减
            val envelope = if (i < numSamples * 0.05) {
                i / (numSamples * 0.05)
            } else {
                1.0 - (i - numSamples * 0.05) / (numSamples * 0.95)
            }
            // 主频率 1200Hz + 泛音 2400Hz，模拟木质音
            val value = (sin(2 * Math.PI * 1200 * t) * 0.7
                    + sin(2 * Math.PI * 2400 * t) * 0.3) * envelope
            samples[i] = (value * Short.MAX_VALUE * 0.8).toInt().toShort()
        }
        return samples
    }

    /**
     * 生成吃子音效：低沉有力的撞击声
     * 模拟棋子被吃掉时的"啪"声
     */
    private fun generateCaptureSound(): ShortArray {
        val durationMs = 150
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            // 包络：快速起音，较慢衰减
            val envelope = if (i < numSamples * 0.03) {
                i / (numSamples * 0.03)
            } else {
                Math.pow(1.0 - (i - numSamples * 0.03) / (numSamples * 0.97), 1.5)
            }
            // 主频率 400Hz + 600Hz 泛音 + 低频冲击 150Hz
            val value = (sin(2 * Math.PI * 150 * t) * 0.4
                    + sin(2 * Math.PI * 400 * t) * 0.4
                    + sin(2 * Math.PI * 600 * t) * 0.2) * envelope
            samples[i] = (value * Short.MAX_VALUE * 0.9).toInt().toShort()
        }
        return samples
    }

    /**
     * 生成将军音效：两声警示音
     */
    private fun generateCheckSound(): ShortArray {
        val durationMs = 250
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val samples = ShortArray(numSamples)
        val halfSamples = numSamples / 2

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val inFirstHalf = i < halfSamples
            val localIndex = if (inFirstHalf) i else (i - halfSamples)
            val localTotal = halfSamples

            // 包络
            val envelope = if (localIndex < localTotal * 0.1) {
                localIndex / (localTotal * 0.1)
            } else {
                1.0 - (localIndex - localTotal * 0.1) / (localTotal * 0.9)
            }

            // 第一声 800Hz，第二声 1000Hz
            val freq = if (inFirstHalf) 800.0 else 1000.0
            val value = sin(2 * Math.PI * freq * t) * envelope
            samples[i] = (value * Short.MAX_VALUE * 0.7).toInt().toShort()
        }
        return samples
    }

    /**
     * 生成胜利音效：上升音阶
     */
    private fun generateWinSound(): ShortArray {
        val durationMs = 600
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            // 频率从 523Hz (C5) 上升到 1047Hz (C6)
            val freq = 523.0 + (1047.0 - 523.0) * progress
            // 包络
            val envelope = if (progress < 0.8) 1.0 else 1.0 - (progress - 0.8) / 0.2
            val value = sin(2 * Math.PI * freq * t) * envelope
            samples[i] = (value * Short.MAX_VALUE * 0.6).toInt().toShort()
        }
        return samples
    }

    /**
     * 生成失败音效：下降音调
     */
    private fun generateLoseSound(): ShortArray {
        val durationMs = 500
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            // 频率从 600Hz 下降到 200Hz
            val freq = 600.0 - 400.0 * progress
            // 包络
            val envelope = 1.0 - progress * 0.5
            val value = sin(2 * Math.PI * freq * t) * envelope
            samples[i] = (value * Short.MAX_VALUE * 0.6).toInt().toShort()
        }
        return samples
    }

    /**
     * 播放合成音效
     */
    private fun playSynthSound(data: ShortArray?, volume: Float) {
        if (!isSoundEffectEnabled || data == null) return

        Thread {
            try {
                val bufferSize = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(SAMPLE_RATE)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(maxOf(bufferSize, data.size * 2))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.setVolume(volume)
                track.write(data, 0, data.size)
                track.play()

                // 等待播放完成后释放
                val durationMs = (data.size * 1000L) / SAMPLE_RATE + 50
                Thread.sleep(durationMs)
                track.stop()
                track.release()
            } catch (e: Exception) {
                // 播放失败静默处理
            }
        }.start()
    }

    /**
     * 播放走棋音效
     */
    fun playMoveSound() {
        playSynthSound(moveSoundData, soundEffectVolume)
    }

    /**
     * 播放吃子音效
     */
    fun playCaptureSound() {
        playSynthSound(captureSoundData, soundEffectVolume)
    }

    /**
     * 播放将军音效
     */
    fun playCheckSound() {
        playSynthSound(checkSoundData, soundEffectVolume)
    }

    /**
     * 播放胜利音效
     */
    fun playWinSound() {
        playSynthSound(winSoundData, soundEffectVolume)
    }

    /**
     * 播放失败音效
     */
    fun playLoseSound() {
        playSynthSound(loseSoundData, soundEffectVolume)
    }

    /**
     * 播放背景音乐
     */
    fun playBackgroundMusic() {
        if (!isMusicEnabled) return
        try {
            bgMediaPlayer?.start()
        } catch (e: Exception) {
            // 背景音乐文件不存在时静默处理
        }
    }

    /**
     * 暂停背景音乐
     */
    fun pauseBackgroundMusic() {
        bgMediaPlayer?.pause()
    }

    /**
     * 恢复背景音乐
     */
    fun resumeBackgroundMusic() {
        if (isMusicEnabled) {
            bgMediaPlayer?.start()
        }
    }

    /**
     * 停止背景音乐
     */
    fun stopBackgroundMusic() {
        bgMediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        bgMediaPlayer = null
    }

    /**
     * 设置音乐开关
     */
    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            playBackgroundMusic()
        } else {
            pauseBackgroundMusic()
        }
    }

    /**
     * 设置音效开关
     */
    fun setSoundEffectEnabled(enabled: Boolean) {
        isSoundEffectEnabled = enabled
    }

    /**
     * 设置音乐音量
     */
    fun setMusicVolume(volume: Float) {
        musicVolume = volume
        bgMediaPlayer?.setVolume(volume, volume)
    }

    /**
     * 设置音效音量
     */
    fun setSoundEffectVolume(volume: Float) {
        soundEffectVolume = volume
    }

    /**
     * 释放资源
     */
    fun release() {
        stopBackgroundMusic()
        moveSoundData = null
        captureSoundData = null
        checkSoundData = null
        winSoundData = null
        loseSoundData = null
        isInitialized = false
    }
}
