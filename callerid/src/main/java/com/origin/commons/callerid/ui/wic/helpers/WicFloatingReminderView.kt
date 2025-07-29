package com.origin.commons.callerid.ui.wic.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.origin.commons.callerid.timepicker.DateHelper
import com.origin.commons.callerid.databinding.FloatingCallerReminderBinding
import com.origin.commons.callerid.db.entity.ReminderEntity
import com.origin.commons.callerid.di.AppProvider
import com.origin.commons.callerid.extensions.beGoneIf
import com.origin.commons.callerid.extensions.beVisibleIf
import com.origin.commons.callerid.extensions.etClearFocus
import com.origin.commons.callerid.extensions.etRequestFocus
import com.origin.commons.callerid.extensions.formatedTime1
import com.origin.commons.callerid.extensions.getUID
import com.origin.commons.callerid.extensions.hideKeyboard
import com.origin.commons.callerid.extensions.logE
import com.origin.commons.callerid.extensions.showCustomToast
import com.origin.commons.callerid.extensions.showKeyboard
import com.origin.commons.callerid.extensions.value
import com.origin.commons.callerid.helpers.Utils.isNotificationPermissionGranted
import com.origin.commons.callerid.states.ReminderState
import com.origin.commons.callerid.ui.adapter.ReminderAdapter
import com.origin.commons.callerid.viewmodel.NotificationFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@SuppressLint("ViewConstructor")
class WicFloatingReminderView(
    private val context: Context,
    private val windowManager: WindowManager,
    private val layoutParams: WindowManager.LayoutParams,
    private val onDismiss: () -> Unit
) : LinearLayout(context) {


    private val appProvider by lazy { AppProvider(context) }

    // ✅ Correct, non-null scope
    private val viewScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val viewModel: NotificationFragmentViewModel by lazy {
        NotificationFragmentViewModel(appProvider.reminderRepository)
    }

    private val _binding: FloatingCallerReminderBinding
    private lateinit var reminderAdapter: ReminderAdapter

    private var hourVal: String = ""
    private var minuteVal: String = ""
    private var dateVal: String? = null

    private var createReminderVisible = false
    private var updateReminder = false
    private var updateModel: ReminderEntity? = ReminderEntity()

    init {
        removeAllViews()
        _binding = FloatingCallerReminderBinding.inflate(LayoutInflater.from(context), this, true)
        initViews()
    }

    private fun initViews() {
        prepareRecyclerViewFOrReminder()
        setUpObserver()

        val rightNow = Calendar.getInstance()
        rightNow.add(Calendar.MINUTE, 1)
        hourVal = formatedTime1(rightNow.get(Calendar.HOUR_OF_DAY))
        minuteVal = formatedTime1(rightNow.get(Calendar.MINUTE))
        val df = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        dateVal = df.format(rightNow.time)

        Log.e(" check: ", "initViews:check:: $hourVal:$minuteVal -- $dateVal")

        _binding.ivCollapse.setOnClickListener {
            dismiss()
        }
        _binding.llCreateNew1.setOnClickListener {
            createNewReminder()
        }
        _binding.ivCreateNew2.setOnClickListener {
            createNewReminder()
        }
        _binding.teTitle.setOnFocusChangeListener { v, hasFocus ->
            try {
                if (hasFocus) {
                    // show flag
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    windowManager.updateViewLayout(this@WicFloatingReminderView, layoutParams)
                    v.showKeyboard()
                } else {
                    // hide flag
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    windowManager.updateViewLayout(this@WicFloatingReminderView, layoutParams)
                    v.hideKeyboard()
                }
            } catch (_: Exception) {
            }
        }

        val maxLen1 = 30
        var toastDisplayed1 = false
        _binding.teTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    val msg = "Title required"
                    _binding.tlTitle.error = msg
                } else {
                    if (_binding.tlTitle.error != null) {
                        _binding.tlTitle.error = null
                    }
                }
                if (s != null && s.length < maxLen1) {
                    toastDisplayed1 = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length >= maxLen1) {
                    if (!toastDisplayed1) {
                        context.showCustomToast("Character limit of $maxLen1 reached")
                        toastDisplayed1 = true
                    }
                }
            }

        })

        val maxLen2 = 60
        var toastDisplayed2 = false
        _binding.teMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length < maxLen2) {
                    toastDisplayed2 = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length >= maxLen2) {
                    if (!toastDisplayed2) {
                        context.showCustomToast("Character limit of $maxLen2 reached")
                        toastDisplayed2 = true
                    }
                }
            }
        })

        _binding.teMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                _binding.teMessage.etClearFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }


        _binding.teMessage.setOnFocusChangeListener { v, hasFocus ->
            try {
                if (hasFocus) {
                    // show flag
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    windowManager.updateViewLayout(this@WicFloatingReminderView, layoutParams)
                    v.showKeyboard()
                } else {
                    // hide flag
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    windowManager.updateViewLayout(this@WicFloatingReminderView, layoutParams)
                    v.hideKeyboard()
                }
            } catch (_: Exception) {
            }
        }

        _binding.tlTitle.setOnClickListener {
            _binding.teTitle.etClearFocus()
            _binding.teMessage.etRequestFocus()
        }
        _binding.tlMessage.setOnClickListener {
            _binding.teMessage.etClearFocus()
            _binding.teMessage.etRequestFocus()
        }
        _binding.dateTimePicker.setDateHelper(DateHelper())
        _binding.dateTimePicker.setMustBeOnFuture(true)
        _binding.dateTimePicker.setCurved(true)
        _binding.dateTimePicker.setStepSizeMinutes(1)

        _binding.dateTimePicker.addOnDateChangedListener { displayed, date ->
            val selectedTime = Calendar.getInstance()
            selectedTime.setTime(date)
            hourVal = formatedTime1(selectedTime.get(Calendar.HOUR_OF_DAY))
            minuteVal = formatedTime1(selectedTime.get(Calendar.MINUTE))
            val df2 = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
            dateVal = df2.format(selectedTime.time)
            logE(" check: ", "addOnDateChangedListener:check:: $displayed -- $date")
        }

        _binding.btnSave.setOnClickListener {
            clearAllETFocus()
            val overallDate = "${hourVal}:${minuteVal}, $dateVal"
            logE("check:22334455: $overallDate")
            if (overallDate.isEmpty()) {
                return@setOnClickListener
            }
            if (_binding.teTitle.value.isEmpty()) {
                _binding.nsvAddReminderView.scrollTo(0, 0)
                val msg = "Title required"
                _binding.tlTitle.error = msg
                context.showCustomToast(msg)
                return@setOnClickListener
            }
            if (!isNotificationPermissionGranted(context)) {
                context.showCustomToast("Notification permission not allowed")
                return@setOnClickListener
            }
            val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
            datetimeToAlarm.timeInMillis = System.currentTimeMillis()
            datetimeToAlarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourVal))
            datetimeToAlarm.set(Calendar.MINUTE, Integer.parseInt(minuteVal))
            datetimeToAlarm.set(Calendar.SECOND, 0)
            datetimeToAlarm.set(Calendar.MILLISECOND, 0)
            if (dateVal == "Today") {
                datetimeToAlarm.set(Calendar.DAY_OF_MONTH, rightNow.get(Calendar.DAY_OF_MONTH))
                datetimeToAlarm.set(Calendar.MONTH, rightNow.get(Calendar.MONTH))
            } else {
                val df1 = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
                val readDate = dateVal?.let { it1 -> df1.parse(it1) }
                val cal = Calendar.getInstance()
                if (readDate != null) {
                    cal.timeInMillis = readDate.time
                }
                datetimeToAlarm.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
                datetimeToAlarm.set(Calendar.MONTH, cal.get(Calendar.MONTH))
            }
            datetimeToAlarm.set(Calendar.YEAR, rightNow.get(Calendar.YEAR))

            val title = _binding.teTitle.value.ifEmpty { "No Title" }
            val msg = _binding.teMessage.value.ifEmpty { "" }
            if (updateReminder) {
                val reminderEntity = ReminderEntity(id = updateModel?.id, title = title, date = dateVal, hours = hourVal, minutes = minuteVal, message = msg)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.updateReminder(reminderEntity)
                }
                updateReminder = false
                updateModel = null
                context.showCustomToast("Reminder reset")
            } else {
                val reminderEntity = ReminderEntity(id = context.getUID(), title = title, date = dateVal, hours = hourVal, minutes = minuteVal, message = msg)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.saveReminder(reminderEntity)
                }
                context.showCustomToast("Reminder set")
            }
            createReminderVisible = !createReminderVisible
            _binding.clReminderView.visibility = View.VISIBLE
            _binding.nsvAddReminderView.visibility = View.INVISIBLE
            _binding.teMessage.text?.clear()
        }

        _binding.btnCancel.setOnClickListener {
            clearAllETFocus()
            createReminderVisible = !createReminderVisible
            if (createReminderVisible) {
                _binding.clReminderView.visibility = View.INVISIBLE
                _binding.nsvAddReminderView.visibility = View.VISIBLE
            } else {
                _binding.clReminderView.visibility = View.VISIBLE
                _binding.nsvAddReminderView.visibility = View.INVISIBLE
                if (_binding.teMessage.value.isNotEmpty()) {
                    _binding.teMessage.text?.clear()
                }
            }
        }
    }

    private fun createNewReminder() {
        if (!isNotificationPermissionGranted(context)) {
            context.showCustomToast("Notification permission not allowed")
            return
        }
        createReminderVisible = !createReminderVisible
        if (createReminderVisible) {
            _binding.clReminderView.visibility = View.INVISIBLE
            _binding.nsvAddReminderView.visibility = View.VISIBLE
        } else {
            _binding.nsvAddReminderView.visibility = View.INVISIBLE
            _binding.clReminderView.visibility = View.VISIBLE
        }
        if (_binding.teTitle.value.isNotEmpty()) {
            _binding.teTitle.setText("")
        }
        if (_binding.teMessage.value.isNotEmpty()) {
            _binding.teMessage.setText("")
        }
        _binding.teTitle.etRequestFocus()
    }

    fun clearAllETFocus() {
        try {
            _binding.teTitle.etClearFocus()
            _binding.teMessage.etClearFocus()
        } catch (_: Exception) {
        }
    }


    private fun prepareRecyclerViewFOrReminder() {
        _binding.rvReminder.apply {
            reminderAdapter = ReminderAdapter(onItemClick = {
                createReminderVisible = !createReminderVisible
                updateReminder = true
                updateModel = it
                _binding.clReminderView.visibility = View.INVISIBLE
                _binding.nsvAddReminderView.visibility = View.VISIBLE
                _binding.teTitle.setText(it.title)
                _binding.teMessage.setText(it.message)
            }, onDeleteClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.deleteReminder(it)
                }
                context.showCustomToast("Reminder deleted")
            })
            layoutManager = LinearLayoutManager(context)
            adapter = reminderAdapter
            setHasFixedSize(true)
        }
    }

    private fun setUpObserver() {
        viewScope.launch {
            viewModel.reminderState.collectLatest {
                when (it) {
                    ReminderState.Loading -> _binding.progressBar.visibility = View.VISIBLE
                    is ReminderState.Success -> {
                        _binding.progressBar.visibility = View.GONE
                        val mList = it.data.ifEmpty { emptyList() }
                        reminderAdapter.submitList(mList)
                        val isListEmpty = mList.isEmpty()
                        _binding.llCreateNew1.beVisibleIf(isListEmpty)
                        _binding.ivCreateNew2.beGoneIf(isListEmpty)
                    }
                }
            }
        }
    }

    private fun dismiss() {
        try {
            clearAllETFocus()
            if (isAttachedToWindow) {
                windowManager.removeView(this)
            }
            viewScope.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onDismiss.invoke()
    }

}