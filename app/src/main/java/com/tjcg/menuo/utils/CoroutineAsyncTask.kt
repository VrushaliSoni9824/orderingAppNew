package com.tjcg.menuo.utils

import kotlinx.coroutines.*

abstract class CoroutineAsyncTask<Params, Progress, Result> {

    var job: Job = Job()
    var jobDeferred:Deferred<Any>?=null
    var num:Any?=null

    open fun onPreExecute() {}

    abstract fun doInBackground(vararg params: Params?): Result?

    open fun onProgressUpdate(vararg values: Progress?) {}

    open fun onPostExecute(result: Result?) {}

    open fun onCancelled(result: Boolean) {}

    protected var isCancelled = false

    protected fun publishProgress(vararg progress: Progress?) {
        GlobalScope.launch(Dispatchers.Main) {
            onProgressUpdate(*progress)
        }
    }

    fun execute(vararg params: Params?) {
        onPreExecute()
        GlobalScope.launch(Dispatchers.Default) {
            val result = doInBackground(*params)

            withContext(Dispatchers.Main + job) {
                onPostExecute(result)
            }
            // job.join()
        }

    }

    fun cancel(mayInterruptIfRunning: Boolean) {
        onCancelled(mayInterruptIfRunning)
        runBlocking {
            delay(1000)
        }
        job.cancel()
    }


    open fun executeData(vararg params: Params?){
        onPreExecute()
        jobDeferred = GlobalScope.async(Dispatchers.Default) {
            val result = doInBackground(*params)

            withContext(Dispatchers.Main + job) {
                onPostExecute(result)

            }
            num = jobDeferred!!.await()
        }
    }


    fun getData(): Any? {
        executeData()
        return num
    }

    fun executeWithResult(vararg params: Params?) {
        onPreExecute()
        GlobalScope.launch(Dispatchers.Default) {
            val result = doInBackground(*params)

            withContext(Dispatchers.Main + job) {
                onPostExecute(result)
            }
            //launch { result }
            result
        }
    }
}