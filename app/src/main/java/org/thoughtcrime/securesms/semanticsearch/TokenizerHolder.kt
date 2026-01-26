package org.thoughtcrime.securesms.semanticsearch

import android.content.Context

object TokenizerHolder {
    private var tokenizer: BertTokenizer? = null

    fun getTokenizer(context: Context): BertTokenizer {
        if (tokenizer == null) {
            tokenizer = BertTokenizer(context)
        }
        return tokenizer!!
    }
}
