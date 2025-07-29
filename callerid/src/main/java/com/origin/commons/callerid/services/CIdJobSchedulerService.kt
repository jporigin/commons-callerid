//package com.origin.commons.callerid.services
//
//
//import android.annotation.SuppressLint
//import android.app.job.JobInfo
//import android.app.job.JobParameters
//import android.app.job.JobScheduler
//import android.app.job.JobService
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Build
//import android.os.PersistableBundle
//import com.origin.commons.callerid.extensions.logE
//import com.origin.commons.callerid.receivers.ActionReceiver
//import com.origin.commons.callerid.receivers.CallerIdCallReceiver
//
//@SuppressLint("SpecifyJobSchedulerIdRange")
//class CIdJobSchedulerService : JobService() {
//
//    private val scD: ActionReceiver = ActionReceiver()
//    private val CyB: CallerIdCallReceiver = CallerIdCallReceiver()
//
////    private val inm: OreoUpgradeReceiver = OreoUpgradeReceiver()
////    private val Ghu = IntentFilter()
//
//    //    private val jf1 = IntentFilter()
//    private val sGR = IntentFilter()
//    private val Lry = IntentFilter()
//
//
//    companion object{
//        fun scheduleJob(context: Context, i: Int) {
//            logE("CIdJobSchedulerService:: Starting JobScheduler")
//            val jobScheduler = context.applicationContext.getSystemService("jobscheduler") as JobScheduler
//            val persistableBundle = PersistableBundle()
//            persistableBundle.putInt("job_scheduler_source", i)
//            val builder = JobInfo.Builder(666, ComponentName(context, CIdJobSchedulerService::class.java as Class<*>))
//            builder.setExtras(persistableBundle).setPersisted(true).setMinimumLatency(0L)
//            if (jobScheduler == null) {
//                logE("CIdJobSchedulerService:: Jobscheduler is null")
//                return
//            }
//            if (jobScheduler.allPendingJobs.size > 50) {
//                for (jobInfo in jobScheduler.allPendingJobs) {
//                    logE("CIdJobSchedulerService:: job = $jobInfo")
//                }
//                jobScheduler.cancelAll()
//            }
//            if (jobScheduler.getPendingJob(666) != null) {
//                jobScheduler.cancel(666)
//            }
//            jobScheduler.schedule(builder.build())
//        }
//    }
//
//
//    override fun onCreate() {
//        super.onCreate()
//        logE("CIdJobSchedulerService:: OnCreate called")
////        jf1.addAction(IntentUtil.IntentConstants.WHITELABEL_ID)
////        jf1.addAction(IntentUtil.IntentConstants.INITSDK)
////        jf1.addAction(IntentUtil.IntentConstants.DATA_CLEARED)
//        sGR.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
//        sGR.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED)
//        sGR.addAction(Intent.ACTION_PACKAGE_ADDED)
//        sGR.addAction("android.intent.action.PACKAGE_REPLACED")
//        sGR.addAction(Intent.ACTION_MY_PACKAGE_REPLACED)
//        Lry.addAction("android.intent.action.PHONE_STATE")
//        if (Build.VERSION.SDK_INT >= 33) {
////            registerReceiver(this.scD, this.Ghu, 4)
////            registerReceiver(this.scD, this.jf1, 4)
//            registerReceiver(this.scD, this.sGR, 4)
//            registerReceiver(this.CyB, this.Lry, 4)
//        } else {
////            registerReceiver(this.scD, this.Ghu)
////            registerReceiver(this.scD, this.jf1)
//            registerReceiver(this.scD, this.sGR)
//            registerReceiver(this.CyB, this.Lry)
//        }
////        registerReceiver(this.inm, IntentFilter(IntentUtil.IntentConstants.ACTION_MY_PACKAGE_REPLACED))
//        logE("CIdJobSchedulerService:: Action Receiver registered")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(this.scD)
//        unregisterReceiver(this.CyB)
//    }
//
//    override fun onStartJob(jobParameters: JobParameters?): Boolean {
//        if (jobParameters?.extras == null || jobParameters.extras.getInt("job_scheduler_source") == 0) {
//            logE("CIdJobSchedulerService:: No job to do")
//        } else {
//            val i = jobParameters.extras.getInt("job_scheduler_source")
//            logE("CIdJobSchedulerService:: jobSchedulerSource = $i")
//            if (i == 0) {
//                logE("CIdJobSchedulerService:: Job source is unknown")
//            } else if (i != 1) {
//                logE("CIdJobSchedulerService:: No job source")
//            } else {
//                logE("CIdJobSchedulerService:: Job source init")
////                CalldoradoApplication.scD(this).Xqk().Ghu().jf1(true);
////                CalldoradoEventsManager.getInstance().setCalldoradoEventCallback(new QI_ ());
////                CyB.QI_(this, str);
////                sGR.nZj(this)
//            }
//
//        }
//        return true
//    }
//
//    override fun onStopJob(jobParameters: JobParameters?): Boolean {
//        logE("CIdJobSchedulerService:: OnStopJob called")
//        return false
//    }
//}