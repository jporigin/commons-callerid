package com.origin.commons.callerid.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.origin.commons.callerid.states.ReminderState
import com.origin.commons.callerid.databinding.FragmentNotificationBinding
import com.origin.commons.callerid.db.entity.ReminderEntity
import com.origin.commons.callerid.extensions.prefsHelper
import com.origin.commons.callerid.extensions.showCustomToast
import com.origin.commons.callerid.extensions.value
import com.origin.commons.callerid.ui.adapter.ReminderAdapter
import com.origin.commons.callerid.viewmodel.NotificationFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private val _binding by lazy {
        FragmentNotificationBinding.inflate(layoutInflater)
    }

    private lateinit var reminderAdapter: ReminderAdapter

    private var dates: Array<String>? = null
    private var hourVal: String? = null
    private var minuteVal: String? = null
    private var dateVal: String? = null

    private var createReminderVisible = false
    private var updateReminder = false
    private var updateModel: ReminderEntity? = ReminderEntity()
    private var TIME_PICKER_INTERVAL = 5

    private val viewModel: NotificationFragmentViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        init()
        return _binding.root
    }

    private fun init() {
        prepareRecyclerViewFOrReminder()
        setUpObserver()
        _binding.btnCreateReminder.setOnClickListener {
            createReminderVisible = !createReminderVisible
            if (createReminderVisible) {
                _binding.clReminderView.visibility = View.INVISIBLE
                _binding.clAddReminderView.visibility = View.VISIBLE
            } else {
                _binding.clReminderView.visibility = View.VISIBLE
                _binding.clAddReminderView.visibility = View.INVISIBLE
            }

            _binding.etMsg.postDelayed({
                _binding.etMsg.showKeyboard()
            }, 200)
        }

        dates = getDatesFromCalender()

        val rightNow = Calendar.getInstance()

        _binding.hourNumberPicker.minValue = 0
        _binding.hourNumberPicker.maxValue = 23
        _binding.hourNumberPicker.setFormatter { i -> String.format("%02d", i) }
        _binding.hourNumberPicker.value = rightNow.get(Calendar.HOUR_OF_DAY)
        _binding.hourNumberPicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->
            hourVal = prepareFormatTime(newVal)

            if (minuteVal.isNullOrEmpty()) {
                val minuteRightNow = rightNow.get(Calendar.MINUTE)
                minuteVal = prepareFormatTime(minuteRightNow)
            }
        }


        val displayedValues = Array(60 / TIME_PICKER_INTERVAL) { i ->
            String.format("%02d", i * TIME_PICKER_INTERVAL)
        }
        _binding.minuteNumberPicker.wrapSelectorWheel = true
        _binding.minuteNumberPicker.minValue = 0
        _binding.minuteNumberPicker.maxValue = ((60 / TIME_PICKER_INTERVAL) - 1)
        _binding.minuteNumberPicker.displayedValues = displayedValues

        val currentMinute = rightNow.get(Calendar.MINUTE)
        val position = ceil(currentMinute / TIME_PICKER_INTERVAL.toDouble()).toInt()
        val finalPosition = position.coerceAtMost(displayedValues.size - 1)

        _binding.minuteNumberPicker.value = finalPosition
        _binding.minuteNumberPicker.setOnValueChangedListener { _, oldVal, newVal ->
            val selectedMinute = newVal * TIME_PICKER_INTERVAL
            minuteVal = prepareFormatTime(selectedMinute)

            if (hourVal.isNullOrEmpty()) {
                val hourRightNow = rightNow.get(Calendar.HOUR)
                hourVal = prepareFormatTime(hourRightNow)
            }
        }


        _binding.dateNumberPicker.minValue = 0
        _binding.dateNumberPicker.wrapSelectorWheel = false
        _binding.dateNumberPicker.maxValue = dates?.size?.minus(1)!!
        _binding.dateNumberPicker.setFormatter { value -> dates!![value] }
        _binding.dateNumberPicker.displayedValues = dates
        _binding.dateNumberPicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->
            dateVal = numberPicker.displayedValues[newVal]
        }

        dateVal = "Today"

        val hour = rightNow.get(Calendar.HOUR_OF_DAY)
        hourVal = prepareFormatTime(hour)

        val minute = rightNow.get(Calendar.MINUTE)
        minuteVal = prepareFormatTime(minute)

        _binding.ivEdit.setOnClickListener {
            _binding.etMsg.postDelayed({
                _binding.etMsg.showKeyboard()
            }, 200)
        }

        _binding.btnSave.setOnClickListener {
            requireActivity().dismissKeyboard()
            val overallDate = "${hourVal}:${minuteVal}, $dateVal"
            if (overallDate.isEmpty())
                return@setOnClickListener

            if (_binding.etMsg.value.isEmpty()) {
                requireActivity().showCustomToast("Please add reminder title")
                return@setOnClickListener
            }

            val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
            datetimeToAlarm.timeInMillis = System.currentTimeMillis()
            datetimeToAlarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourVal!!))
            datetimeToAlarm.set(Calendar.MINUTE, Integer.parseInt(minuteVal!!))
            datetimeToAlarm.set(Calendar.SECOND, 0)
            datetimeToAlarm.set(Calendar.MILLISECOND, 0)
            Log.e("dateVal", "" + dateVal)
            Log.e("hourVal", "" + hourVal)
            Log.e("minuteVal", "" + minuteVal)
            if (dateVal == "Today") {
                datetimeToAlarm.set(Calendar.DAY_OF_MONTH, rightNow.get(Calendar.DAY_OF_MONTH))
                datetimeToAlarm.set(Calendar.MONTH, rightNow.get(Calendar.MONTH))
            } else {
                val df = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
                val readDate = dateVal?.let { it1 -> df.parse(it1) }
                val cal = Calendar.getInstance()
                if (readDate != null) {
                    cal.timeInMillis = readDate.time
                }
                datetimeToAlarm.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
                datetimeToAlarm.set(Calendar.MONTH, cal.get(Calendar.MONTH))
            }
            datetimeToAlarm.set(Calendar.YEAR, rightNow.get(Calendar.YEAR))

            val title = _binding.etMsg.value.ifEmpty { "No Title" }

            if (updateReminder) {
                val reminderEntity = ReminderEntity(
                    id = updateModel?.id,
                    title = title,
                    date = dateVal,
                    hours = hourVal,
                    minutes = minuteVal
                )
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.updateReminder(reminderEntity)
                }
                updateReminder = false
                updateModel = null
            } else {
                val reminderEntity = ReminderEntity(
                    id = generateUnique4DigitId().toLong(),
                    title = title,
                    date = dateVal,
                    hours = hourVal,
                    minutes = minuteVal
                )
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.saveReminder(reminderEntity)
                }
                requireActivity().showCustomToast("Reminder set")
            }
            createReminderVisible = !createReminderVisible
            _binding.clReminderView.visibility = View.VISIBLE
            _binding.clAddReminderView.visibility = View.INVISIBLE

            _binding.etMsg.text.clear()
        }

        _binding.btnCancel.setOnClickListener {
            requireActivity().dismissKeyboard()
            createReminderVisible = !createReminderVisible
            if (createReminderVisible) {
                _binding.clReminderView.visibility = View.INVISIBLE
                _binding.clAddReminderView.visibility = View.VISIBLE
            } else {
                _binding.clReminderView.visibility = View.VISIBLE
                _binding.clAddReminderView.visibility = View.INVISIBLE
                if (_binding.etMsg.value.isNotEmpty()) {
                    _binding.etMsg.text.clear()
                }
            }
        }

    }

    private fun generateUnique4DigitId(): Int {
        val usedIds: MutableList<Int> = requireContext().prefsHelper.savedReminderIds.split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim().toInt() }
            .toMutableList()
        var id: Int
        do {
            id = (1000..9999).random()
        } while (usedIds.contains(id))
        usedIds.add(id)
        requireContext().prefsHelper.savedReminderIds = TextUtils.join(",", usedIds)
        return id
    }


    private fun prepareRecyclerViewFOrReminder() {
        _binding.rvReminder.apply {
            reminderAdapter = ReminderAdapter(
                onItemClick = {
                    createReminderVisible = !createReminderVisible
                    updateReminder = true
                    updateModel = it
                    _binding.clReminderView.visibility = View.INVISIBLE
                    _binding.clAddReminderView.visibility = View.VISIBLE
                    _binding.etMsg.setText(it.title)
                    _binding.hourNumberPicker.value = it.hours?.toInt()!!

                    val minute = it.minutes?.toInt() ?: 0
                    val position = (minute / TIME_PICKER_INTERVAL.toDouble()).roundToInt()
                    val finalPosition = position.coerceIn(
                        _binding.minuteNumberPicker.minValue,
                        _binding.minuteNumberPicker.maxValue
                    )

                    _binding.minuteNumberPicker.value = finalPosition

                    _binding.etMsg.postDelayed({
                        _binding.etMsg.showKeyboard()
                        it.title?.length?.let { index ->
                            _binding.etMsg.setSelection(index)
                        }
                    }, 200)
                },
                onDeleteClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.deleteReminder(it)
                    }
                    requireActivity().showCustomToast("Reminder deleted")
                }
            )
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
                        Log.e(TAG, "setUpObserver: reminderList: ${it.data}")
                        if (it.data.isNotEmpty()) {
                            reminderAdapter.submitList(it.data)
                        } else {
                            reminderAdapter.submitList(emptyList())
                        }
                    }
                }
            }
        }
    }

    private fun prepareFormatTime(time: Int): String {
        return if (time in 0..9)
            "0$time"
        else
            "" + time
    }

    private fun getDatesFromCalender(): Array<String> {
        val c1 = Calendar.getInstance()

        val dates = ArrayList<String>()
        val dateFormat = SimpleDateFormat("EEE, MMM dd")
        dates.add("Today")

        for (i in 0..364) {
            c1.add(Calendar.DATE, 1)
            dates.add(dateFormat.format(c1.time))
        }

        Log.e("DATES", "" + dates)
        return dates.toTypedArray()
    }

    companion object {
        private const val TAG = "NotificationFragment"
    }

}

fun Activity.dismissKeyboard() {
    window.currentFocus?.let { focus ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(focus.windowToken, 0)
        focus.clearFocus()
    }
}

fun EditText.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}