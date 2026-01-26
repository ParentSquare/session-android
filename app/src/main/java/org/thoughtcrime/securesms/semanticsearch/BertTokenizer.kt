package org.thoughtcrime.securesms.semanticsearch

import android.content.Context

/**
 * A simple WordPiece tokenizer compatible with BERT/MiniLM models.
 * Loads vocab.txt from assets and tokenizes text into token IDs.
 */
class BertTokenizer(context: Context) {
    
    private val vocab: Map<String, Int>
    private val idToToken: Map<Int, String>
    
    val clsTokenId: Int
    val sepTokenId: Int
    val padTokenId: Int
    val unkTokenId: Int
    
    init {
        // Load vocabulary from assets
        val vocabLines = context.assets.open("tokenizer/vocab.txt").bufferedReader().readLines()
        vocab = vocabLines.mapIndexed { index, token -> token to index }.toMap()
        idToToken = vocab.entries.associate { it.value to it.key }
        
        clsTokenId = vocab["[CLS]"] ?: error("Missing [CLS] token in vocab")
        sepTokenId = vocab["[SEP]"] ?: error("Missing [SEP] token in vocab")
        padTokenId = vocab["[PAD]"] ?: error("Missing [PAD] token in vocab")
        unkTokenId = vocab["[UNK]"] ?: error("Missing [UNK] token in vocab")
    }
    
    /**
     * Tokenizes text into token IDs with [CLS] and [SEP] tokens.
     * Returns (inputIds, attentionMask)
     */
    fun encode(text: String, maxLength: Int = 128): Pair<LongArray, LongArray> {
        val tokens = tokenize(text)
        
        // Truncate to maxLength - 2 (for [CLS] and [SEP])
        val truncatedTokens = tokens.take(maxLength - 2)
        
        // Build input IDs: [CLS] + tokens + [SEP] + padding
        val inputIds = LongArray(maxLength) { padTokenId.toLong() }
        val attentionMask = LongArray(maxLength) { 0L }
        
        inputIds[0] = clsTokenId.toLong()
        attentionMask[0] = 1L
        
        truncatedTokens.forEachIndexed { index, tokenId ->
            inputIds[index + 1] = tokenId.toLong()
            attentionMask[index + 1] = 1L
        }
        
        inputIds[truncatedTokens.size + 1] = sepTokenId.toLong()
        attentionMask[truncatedTokens.size + 1] = 1L
        
        return inputIds to attentionMask
    }
    
    /**
     * Tokenizes text into token IDs using WordPiece algorithm.
     */
    private fun tokenize(text: String): List<Int> {
        val tokens = mutableListOf<Int>()
        
        // Basic preprocessing: lowercase and split on whitespace/punctuation
        val words = text.lowercase().split(Regex("[\\s\\p{Punct}]+")).filter { it.isNotEmpty() }
        
        for (word in words) {
            val wordTokens = wordPieceTokenize(word)
            tokens.addAll(wordTokens)
        }
        
        return tokens
    }
    
    /**
     * Applies WordPiece tokenization to a single word.
     */
    private fun wordPieceTokenize(word: String): List<Int> {
        val tokens = mutableListOf<Int>()
        var start = 0
        
        while (start < word.length) {
            var end = word.length
            var foundToken: Int? = null
            
            // Try to find the longest matching token
            while (start < end) {
                val substr = if (start == 0) {
                    word.substring(start, end)
                } else {
                    "##" + word.substring(start, end)
                }
                
                val tokenId = vocab[substr]
                if (tokenId != null) {
                    foundToken = tokenId
                    break
                }
                end--
            }
            
            if (foundToken != null) {
                tokens.add(foundToken)
                start = end
            } else {
                // Unknown character, use [UNK] and move forward
                tokens.add(unkTokenId)
                start++
            }
        }
        
        return tokens
    }
}

