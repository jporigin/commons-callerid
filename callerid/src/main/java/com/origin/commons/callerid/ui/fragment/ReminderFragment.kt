package com.origin.commons.callerid.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.origin.commons.callerid.R
import com.origin.commons.callerid.databinding.FragmentReminderBinding
import com.origin.commons.callerid.db.entity.ReminderEntity
import com.origin.commons.callerid.di.AppProvider
import com.origin.commons.callerid.extensions.beGoneIf
import com.origin.commons.callerid.extensions.beVisibleIf
import com.origin.commons.callerid.extensions.etClearFocus
import com.origin.commons.callerid.extensions.etRequestFocus
import com.origin.commons.callerid.extensions.formatedTime
import com.origin.commons.callerid.extensions.getUID
import com.origin.commons.callerid.extensions.hideKeyboard
import com.origin.commons.callerid.extensions.showCustomToast
import com.origin.commons.callerid.extensions.showKeyboard
import com.origin.commons.callerid.extensions.value
import com.origin.commons.callerid.helpers.CallerIdUtils.isNotificationPermissionGranted
import com.origin.commons.callerid.states.ReminderState
import com.origin.commons.callerid.ui.adapter.ReminderAdapter
import com.origin.commons.callerid.viewmodel.ReminderFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderFragment : Fragment() {
    private val appProvider by lazy { AppProvider(requireContext()) }

    private val viewModel: ReminderFragmentViewModel by lazy {
        ReminderFragmentViewModel(appProvider.reminderRepository)
    }

    private val _binding by lazy {
        FragmentReminderBinding.inflate(layoutInflater)
    }

    private lateinit var reminderAdapter: ReminderAdapter

    private var hourVal: String = ""
    private var minuteVal: String = ""
    private var dateVal: String? = null

    private var createReminderVisible = false
    private var updateReminder = false
    private var updateModel: ReminderEntity? = ReminderEntity()

    private var selectedDate: Calendar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return _binding.root
    }

    override fun onPause() {
        super.onPause()
        clearAllETFocus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        prepareRecyclerViewFOrReminder()
        setUpObserver()

        val rightNow = Calendar.getInstance()
        rightNow.add(Calendar.MINUTE, 1)
        hourVal = rightNow.get(Calendar.HOUR_OF_DAY).formatedTime()
        minuteVal = rightNow.get(Calendar.MINUTE).formatedTime()
        val df = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        dateVal = df.format(rightNow.time)
        _binding.teTitle.text = null
        _binding.teMessage.text = null

        _binding.llCreateNew1.setOnClickListener {
            createNewReminder()
        }
        _binding.ivCreateNew2.setOnClickListener {
            createNewReminder()
        }
        _binding.teTitle.setOnFocusChangeListener { v, hasFocus ->
            try {
                if (hasFocus) {
                    v.showKeyboard()
                } else {
                    v.hideKeyboard()
                }
            } catch (_: Exception) {
            }
        }

        val maxLen1 = 30
        var toastDisplayed1 = false
        var lastLength = 0
        _binding.teTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (_binding.tlTitle.error != null) {
                    _binding.tlTitle.error = null
                }
                if (s != null && s.length < maxLen1) {
                    toastDisplayed1 = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.e("TitleMessageLimitTest", "afterTextChanged")
                if (s != null) {
                    val len = s.length
                    if (len >= maxLen1 && lastLength < maxLen1) {
                        if (!toastDisplayed1) {
                            context?.showCustomToast("Character limit of $maxLen1 reached")
                            toastDisplayed1 = true
                        }
                    }
                    lastLength = len
                }
            }
        })

        val maxLen2 = 60
        var toastDisplayed2 = false
        var lastLength2 = 0
        _binding.teMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length < maxLen2) {
                    toastDisplayed2 = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.e("TitleMessageLimitTest", "afterTextChanged")
                if (s != null) {
                    val len = s.length
                    if (len >= maxLen2 && lastLength2 < maxLen2) {
                        if (!toastDisplayed2) {
                            context?.showCustomToast("Character limit of $maxLen2 reached")
                            toastDisplayed2 = true
                        }
                    }
                    lastLength2 = len
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
                    v.showKeyboard()
                } else {
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


        _binding.teDate.setText(dateVal)
        _binding.teDate.setOnClickListener {
            showDatePickerIfNeeded()
        }

        val formattedTime = getString(R.string.time_format, hourVal, minuteVal)
        _binding.teTime.setText(formattedTime)
        _binding.teTime.setOnClickListener {
            showTimePickerIfNeeded()
        }

        _binding.btnSave.setOnClickListener {
            activity?.let { mActivity ->
                clearAllETFocus()

                if (_binding.teTitle.value.isEmpty()) {
                    _binding.nsvAddReminderView.scrollTo(0, 0)
                    val msg = "Title required"
                    _binding.tlTitle.error = msg
                    mActivity.showCustomToast(msg)
                    return@setOnClickListener
                } else {
                    if (_binding.tlTitle.error != null) {
                        _binding.tlTitle.error = null
                    }
                }

                // Validate required fields
                if (hourVal.isEmpty() || minuteVal.isEmpty() || dateVal.isNullOrEmpty()) {
                    mActivity.showCustomToast("Please select date & time")
                    return@setOnClickListener
                }

                if (!isNotificationPermissionGranted(mActivity)) {
                    mActivity.showCustomToast("Notification permission not allowed")
                    return@setOnClickListener
                }

                val rightNow = Calendar.getInstance(Locale.getDefault())
                val datetimeToAlarm = Calendar.getInstance(Locale.getDefault()).apply {
                    set(Calendar.HOUR_OF_DAY, hourVal.toInt())
                    set(Calendar.MINUTE, minuteVal.toInt())
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    if (dateVal == "Today") {
                        set(Calendar.YEAR, rightNow.get(Calendar.YEAR))
                        set(Calendar.DAY_OF_MONTH, rightNow.get(Calendar.DAY_OF_MONTH))
                        set(Calendar.MONTH, rightNow.get(Calendar.MONTH))
                    } else {
                        val df = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
                        val readDate = dateVal?.let { dateVal -> df.parse(dateVal) }
                        val cal = Calendar.getInstance()
                        if (readDate != null) cal.time = readDate

                        set(Calendar.YEAR, rightNow.get(Calendar.YEAR))
                        set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
                        set(Calendar.MONTH, cal.get(Calendar.MONTH))
                    }
                }

                if (datetimeToAlarm.before(rightNow)) {
                    mActivity.showCustomToast("Please select a valid future date & time")
                    return@setOnClickListener
                }

                // Proceed to save reminder
                val title = _binding.teTitle.value.ifEmpty { "No Title" }
                val msg = _binding.teMessage.value.ifEmpty { "" }

                if (updateReminder) {
                    val reminderEntity = ReminderEntity(
                        id = updateModel?.id,
                        title = title,
                        date = dateVal,
                        hours = hourVal,
                        minutes = minuteVal,
                        message = msg
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.updateReminder(reminderEntity)
                    }
                    updateReminder = false
                    updateModel = null
                    mActivity.showCustomToast("Reminder reset")
                } else {
                    val reminderEntity = ReminderEntity(
                        id = mActivity.getUID(),
                        title = title,
                        date = dateVal,
                        hours = hourVal,
                        minutes = minuteVal,
                        message = msg
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.saveReminder(reminderEntity)
                    }
                    mActivity.showCustomToast("Reminder set")
                }

                createReminderVisible = !createReminderVisible
                _binding.clReminderView.visibility = View.VISIBLE
                _binding.nsvAddReminderView.visibility = View.INVISIBLE
                _binding.teMessage.text?.clear()
            }
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
        context?.let { mContext ->
            if (!isNotificationPermissionGranted(mContext)) {
                mContext.showCustomToast("Notification permission not allowed")
                return@let
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
    }

    private fun clearAllETFocus() {
        try {
            _binding.teTitle.etClearFocus()
            _binding.teMessage.etClearFocus()
        } catch (_: Exception) {
        }
    }

    private val datePicker: MaterialDatePicker<Long> by lazy {
        MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.select_date)).setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build().apply {
            addOnPositiveButtonClickListener { selection ->
                val pickedDate = Calendar.getInstance().apply { timeInMillis = selection }
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (pickedDate.before(today)) {
                    requireContext().showCustomToast(R.string.invalid_past_date)
                } else {
                    val formatter = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
                    dateVal = formatter.format(pickedDate.time)
                    _binding.teDate.setText(dateVal)

                    selectedDate = pickedDate
                }
            }
        }
    }

    private fun showDatePickerIfNeeded() {
        if (!datePicker.isAdded && !datePicker.isVisible) {
            datePicker.show(parentFragmentManager, "DatePickerDialogTag") // Using a constant tag
        }
    }

    private val timePicker: MaterialTimePicker by lazy {
        val rightNow = Calendar.getInstance()
        val currentHourIn24Format: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = rightNow.get(Calendar.MINUTE)

        MaterialTimePicker.Builder().setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD).setTimeFormat(TimeFormat.CLOCK_24H) // Or TimeFormat.CLOCK_12H
            .setHour(currentHourIn24Format).setMinute(minutes).setTitleText(getString(R.string.select_time)) // Using a string resource
            .build().apply {
                addOnPositiveButtonClickListener {
                    val pickedDateTime = (selectedDate ?: Calendar.getInstance()).apply {
                        set(Calendar.HOUR_OF_DAY, timePicker.hour)
                        set(Calendar.MINUTE, timePicker.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val now = Calendar.getInstance()
                    if (pickedDateTime.before(now)) {
                        requireContext().showCustomToast(R.string.invalid_past_time)
                    } else {
                        hourVal = timePicker.hour.toString()
                        minuteVal = timePicker.minute.toString()
                        val formattedTime = getString(R.string.time_format, hourVal, minuteVal)
                        _binding.teTime.setText(formattedTime)
                    }
                }
            }
    }

    private fun showTimePickerIfNeeded() {
        if (!timePicker.isAdded && !timePicker.isVisible) {
            timePicker.show(parentFragmentManager, "TimePickerDialogTag") // Using a constant tag
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
                requireActivity().showCustomToast("Reminder deleted")
            })
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reminderAdapter
            setHasFixedSize(true)
        }
    }

    private fun setUpObserver() {
        lifecycleScope.launch(Dispatchers.Main) {
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

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TitleMessageLimitTest", "onDestroy")
    }
}

