/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.cheesefinder

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cheeses.*

class CheeseActivity : BaseSearchActivity() {


    override fun onStart() {
        super.onStart()
        val searchTextObservable = createButtonClickObservable()

        //Subscribe to the observable with subscribe(),
        // and supply a simple Consumer.
        searchTextObservable
                .subscribeOn(AndroidSchedulers.mainThread()) //because the emissions are clicks on UI thread
                .doOnNext {showProgress()} //Add the doOnNext operator so that showProgress() will be called every time a new item is emitted.
                .observeOn(Schedulers.io()) //the next operator will work on IO thread
                .map {
                    cheeseSearchEngine.search(it)!! //For each search query, you return a list of results.
                }
                .observeOn(AndroidSchedulers.mainThread()) //the next operator will work on Main thread
                .subscribe {
                    //simple Consumer

                    hideProgress()
                    showResult(it)
                }
    }

    //observe button clicks
    private fun createButtonClickObservable(): Observable<String> {
        //create Observable by Observable.create(),
        // and supply it with a new ObservableOnSubscribe.
        return Observable.create {
            //ObservableOnSubscribe

            emitter ->
            searchButton.setOnClickListener {
                emitter.onNext(queryEditText.text.toString())
            }


            emitter.setCancellable {
                //will be called when the Observable is disposed such as
                // when the Observable is completed or all Observers have unsubscribed from it.
                searchButton.setOnClickListener(null)
            }
        }
    }
}