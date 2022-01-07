package com.ptruiz.test_face
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.text.format.Time
import android.util.Log
import android.view.SurfaceHolder
import android.view.WindowInsets
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyWatchFace : CanvasWatchFaceService() {
    override fun onCreateEngine(): Engine {
        return WatchFaceEngine()
    }

    companion object {
        private const val MSG_UPDATE_TIME_ID = 42
        private const val DEFAULT_UPDATE_RATE_MS: Long = 1000
    }

    private inner class WatchFaceEngine : Engine() {
        //Member variables
        private val WATCH_TEXT_TYPEFACE = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        private var mUpdateRateMs: Long = 1000
        private var mBackgroundColorPaint: Paint? = null
        private var mTextColorPaint: Paint? = null
        private var mHasTimeZoneReceiverBeenRegistered = false
        private var mIsInMuteMode = false
        private var mIsLowBitAmbient = false
        private var mXOffset_1 = 0f
        private var mYOffset_1 = 0f

        private var mXOffset_2 = 0f
        private var mYOffset_2 = 0f

        private var mXOffset_3 = 0f
        private var mYOffset_3 = 0f

        private var mXOffset_4 = 0f
        private var mYOffset_4 = 0f

        private var text1 = "Hello World"
        private var text2 = "Hello World"
        private var text3 = "Hello World"
        private var text4 = "Hello World"

        private val mBackgroundColor = Color.parseColor("black")
        private val mTextColor = Color.parseColor("green")

        private var database: FirebaseDatabase? = null

        //Overridden methods
        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)
            FirebaseApp.initializeApp(baseContext)
            database = FirebaseDatabase.getInstance()
            setWatchFaceStyle(
                WatchFaceStyle.Builder(this@MyWatchFace)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setShowSystemUiTime(false)
                    .build()
            )
            initBackground()
            initDisplayText()

            database?.reference?.child("1")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    text1 = snapshot.value as String
                    invalidate()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            database?.reference?.child("2")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    text2 = snapshot.value as String
                    invalidate()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            database?.reference?.child("3")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    text3 = snapshot.value as String
                    invalidate()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            database?.reference?.child("4")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    text4 = snapshot.value as String
                    invalidate()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        override fun onApplyWindowInsets(insets: WindowInsets) {
            super.onApplyWindowInsets(insets)
            mYOffset_1 = resources.getDimension(R.dimen.y_offset_1)
            mXOffset_1 = resources.getDimension(R.dimen.x_offset_1)

            mYOffset_2 = resources.getDimension(R.dimen.y_offset_2)
            mXOffset_2 = resources.getDimension(R.dimen.x_offset_2)

            mYOffset_3 = resources.getDimension(R.dimen.y_offset_3)
            mXOffset_3 = resources.getDimension(R.dimen.x_offset_3)

            mYOffset_4 = resources.getDimension(R.dimen.y_offset_4)
            mXOffset_4 = resources.getDimension(R.dimen.x_offset_4)
        }

        override fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)
            if (properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false)) {
                mIsLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false)
            }
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            if (inAmbientMode) {
                mTextColorPaint!!.color = Color.parseColor("white")
            } else {
                mTextColorPaint!!.color = Color.parseColor("green")
            }
            if (mIsLowBitAmbient) {
                mTextColorPaint!!.isAntiAlias = !inAmbientMode
            }
            invalidate()
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            super.onDraw(canvas, bounds)
            drawBackground(canvas, bounds)
            drawDisplayInfo1(canvas)
            drawDisplayInfo2(canvas)
            drawDisplayInfo3(canvas)
            drawDisplayInfo4(canvas)
        }

        //Utility methods
        private fun initBackground() {
            mBackgroundColorPaint = Paint()
            mBackgroundColorPaint!!.color = mBackgroundColor
        }

        private fun initDisplayText() {
            mTextColorPaint = Paint()
            mTextColorPaint!!.color = mTextColor
            mTextColorPaint!!.typeface = WATCH_TEXT_TYPEFACE
            mTextColorPaint!!.isAntiAlias = true
            mTextColorPaint!!.textSize = resources.getDimension(R.dimen.text_size)
        }

        private fun drawBackground(canvas: Canvas, bounds: Rect) {
            canvas.drawRect(
                0f, 0f, bounds.width().toFloat(), bounds.height().toFloat(),
                mBackgroundColorPaint!!
            )
        }

        private fun drawDisplayInfo1(canvas: Canvas) {
            canvas.drawText(text1, mXOffset_1, mYOffset_1, mTextColorPaint!!)
        }

        private fun drawDisplayInfo2(canvas: Canvas) {
            canvas.drawText(text2, mXOffset_2, mYOffset_2, mTextColorPaint!!)
        }

        private fun drawDisplayInfo3(canvas: Canvas) {
            canvas.drawText(text3, mXOffset_3, mYOffset_3, mTextColorPaint!!)
        }

        private fun drawDisplayInfo4(canvas: Canvas) {
            canvas.drawText(text4, mXOffset_4, mYOffset_4, mTextColorPaint!!)
        }
    }
}