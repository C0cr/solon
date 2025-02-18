package org.noear.solon.ai.rag.splitter;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 符号文本分割器
 *
 * @author noear
 * @since 3.1
 */
public class TokenTextSplitter extends TextSplitter {
    private final Encoding encoding;
    private final int chunkSize;
    private final int minChunkSizeChars;
    private final int minChunkLengthToEmbed;
    private final int maxChunkCount;
    private final boolean keepSeparator;

    public TokenTextSplitter() {
        this(800, 350, 5, 1000, true);
    }

    public TokenTextSplitter(int chunkSize, int minChunkSizeChars, int minChunkLengthToEmbed, int maxChunkCount, boolean keepSeparator) {
        this.encoding = Encodings.newLazyEncodingRegistry().getEncoding(EncodingType.CL100K_BASE);

        this.chunkSize = chunkSize;
        this.minChunkSizeChars = minChunkSizeChars;
        this.minChunkLengthToEmbed = minChunkLengthToEmbed;
        this.maxChunkCount = maxChunkCount;
        this.keepSeparator = keepSeparator;
    }

    @Override
    protected List<String> splitText(String text) {
        List<String> chunks = new ArrayList();

        if (text != null && !text.trim().isEmpty()) {
            List<Integer> tokens = this.encodeTokens(text);
            int chunksCount = 0;

            while (!tokens.isEmpty() && chunksCount < this.maxChunkCount) {
                List<Integer> chunk = tokens.subList(0, Math.min(chunkSize, tokens.size()));
                String chunkText = this.decodeTokens(chunk);
                if (chunkText.trim().isEmpty()) {
                    tokens = tokens.subList(chunk.size(), tokens.size());
                } else {
                    int lastPunctuation = Math.max(chunkText.lastIndexOf(46), Math.max(chunkText.lastIndexOf(63), Math.max(chunkText.lastIndexOf(33), chunkText.lastIndexOf(10))));
                    if (lastPunctuation != -1 && lastPunctuation > this.minChunkSizeChars) {
                        chunkText = chunkText.substring(0, lastPunctuation + 1);
                    }

                    String chunkTextToAppend = this.keepSeparator ? chunkText.trim() : chunkText.replace(System.lineSeparator(), " ").trim();
                    if (chunkTextToAppend.length() > this.minChunkLengthToEmbed) {
                        chunks.add(chunkTextToAppend);
                    }

                    tokens = tokens.subList(this.encodeTokens(chunkText).size(), tokens.size());
                    ++chunksCount;
                }
            }

            if (!tokens.isEmpty()) {
                String remaining_text = this.decodeTokens(tokens).replace(System.lineSeparator(), " ").trim();
                if (remaining_text.length() > this.minChunkLengthToEmbed) {
                    chunks.add(remaining_text);
                }
            }
        }

        return chunks;
    }

    /**
     * 编码符号
     */
    private List<Integer> encodeTokens(String text) {
        Objects.requireNonNull(text, "tokens is null");

        return this.encoding.encode(text).boxed();
    }

    /**
     * 解码符号
     */
    private String decodeTokens(List<Integer> tokens) {
        Objects.requireNonNull(tokens, "tokens is null");

        IntArrayList tmp = new IntArrayList(tokens.size());
        tokens.forEach(tmp::add);
        return this.encoding.decode(tmp);
    }
}