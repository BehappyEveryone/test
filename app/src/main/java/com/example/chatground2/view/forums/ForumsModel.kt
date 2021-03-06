package com.example.chatground2.view.forums

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import com.example.chatground2.model.dto.ForumDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumsModel (context: Context) {
    private val serviceGenerator: ServiceGenerator = ServiceGenerator(context)

    //포럼 목록
    fun callForums(
        hashMap: HashMap<String, Any>,
        callBack: ForumsContract.CallBack
    ) {
        serviceGenerator.instance.callForums(hashMap)
            .enqueue(object : Callback<ArrayList<ForumDto>?> {
                override fun onFailure(call: Call<ArrayList<ForumDto>?>, t: Throwable) {
                    callBack.onError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<ForumDto>?>,
                    response: Response<ArrayList<ForumDto>?>
                ) {
                    if (response.isSuccessful) {
                        callBack.onCallForumsSuccess(response.body())
                    } else {
                        callBack.onCallForumsFailure()
                    }
                }
            })
    }
}