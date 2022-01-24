package com.tjcg.menuo.data.response

import com.google.gson.annotations.SerializedName

class PaymentMethodRS {

    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: PaymentData? = null

    inner class PaymentData {
        @SerializedName("payment_method")
        var payment_method: List<PaymentMethod>? = null

        @SerializedName("credit_card")
        var credit_card: List<CreditCardData>? = null
    }

}