package com.jarvis.assistant.presentation.ui.overlay

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.jarvis.assistant.R

class OverlayView : FrameLayout {

    private var bubbleView: View? = null
    private var expandedView: View? = null
    private var isExpanded = false
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.fragment_overlay, this)
        isClickable = true
        isFocusable = true
        setBackgroundColor(android.graphics.Color.TRANSPARENT)

        bubbleView = findViewById(R.id.bubbleContainer)
        expandedView = findViewById(R.id.expandedContainer)

        setupBubbleDrag()
        setupBubbleClick()
        setupExpandedControls()
    }

    private fun setupBubbleDrag() {
        bubbleView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = (layoutParams as WindowManager.LayoutParams).x
                    initialY = (layoutParams as WindowManager.LayoutParams).y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val params = layoutParams as WindowManager.LayoutParams
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.updateViewLayout(this, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    snapToEdge()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBubbleClick() {
        bubbleView?.setOnClickListener {
            expandOverlay()
        }
    }

    private fun setupExpandedControls() {
        expandedView?.findViewById<View>(R.id.btnCloseOverlay)?.setOnClickListener {
            collapseOverlay()
        }
        expandedView?.findViewById<View>(R.id.btnVoiceInput)?.setOnClickListener {
            // Trigger voice input - will be wired to VoiceAssistantFragment via navigation
        }
    }

    private fun expandOverlay() {
        isExpanded = true
        bubbleView?.visibility = View.GONE
        expandedView?.visibility = View.VISIBLE

        val params = layoutParams as? WindowManager.LayoutParams ?: return
        params.width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        params.height = (resources.displayMetrics.heightPixels * 0.55).toInt()
        params.x = (resources.displayMetrics.widthPixels - params.width) / 2
        params.y = (resources.displayMetrics.heightPixels - params.height) / 3
        params.flags = params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.updateViewLayout(this, params)
    }

    fun collapseOverlay() {
        isExpanded = false
        expandedView?.visibility = View.GONE
        bubbleView?.visibility = View.VISIBLE

        val params = layoutParams as? WindowManager.LayoutParams ?: return
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        snapToEdge()
    }

    private fun snapToEdge() {
        val params = layoutParams as? WindowManager.LayoutParams ?: return
        val displayWidth = resources.displayMetrics.widthPixels
        val bubbleWidth = resources.getDimensionPixelSize(R.dimen.bubble_size)

        params.x = if (params.x + bubbleWidth / 2 < displayWidth / 2) 0 else displayWidth - bubbleWidth
        (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.updateViewLayout(this, params)
    }

    fun isOverlayExpanded(): Boolean = isExpanded
}
