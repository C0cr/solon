package org.noear.solon.core.handle;

import org.noear.solon.Utils;

import java.io.*;
import java.util.Date;

/**
 * 下载文件模型
 *
 * @author noear
 * @since 1.5
 */
public class DownloadedFile extends FileBase implements Closeable {
    /**
     * 是否附件（即下载模式）
     */
    private boolean attachment = true;
    private int maxAgeSeconds = 0;
    private String eTag = null;
    private Date lastModified;
    private File file;

    /**
     * 是否附件输出
     */
    public boolean isAttachment() {
        return attachment;
    }

    /**
     * 获取最大缓存时间（0表示不缓存）
     */
    public int getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    /**
     * 获取 eTag
     */
    public String getETag() {
        return eTag;
    }

    /**
     * 获取最后修改时间
     */
    public Date getLastModified() {
        if (lastModified == null) {
            return new Date();
        } else {
            return lastModified;
        }
    }

    /**
     * 作为附件输出
     */
    public DownloadedFile asAttachment(boolean attachment) {
        this.attachment = attachment;
        return this;
    }

    /**
     * 缓存控制
     */
    public DownloadedFile cacheControl(int maxAgeSeconds) {
        this.maxAgeSeconds = maxAgeSeconds;
        return this;
    }

    /**
     * eTag 配置
     */
    public DownloadedFile eTag(String eTag) {
        this.eTag = eTag;
        return this;
    }

    /**
     * 最后更新时间配置（单位：毫秒）
     */
    public DownloadedFile lastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    /**
     * 内容
     */
    public InputStream getContent() throws IOException {
        if (content == null) {
            content = new FileInputStream(file);
        }

        return content;
    }

    /**
     * 内容大小
     */
    public long getContentSize() throws IOException {
        if (contentSize > 0) {
            return contentSize;
        } else {
            return getContent().available();
        }
    }

    @Override
    public void close() throws IOException {
        if (content != null) {
            content.close();
        }
    }

    /**
     * 下载文件
     *
     * @param contentType 内容类型
     * @param contentSize 内容大小
     * @param content     内容流
     * @param name        文件名
     */
    public DownloadedFile(String contentType, long contentSize, InputStream content, String name) {
        super(contentType, contentSize, content, name);
    }

    /**
     * 下载文件
     *
     * @param contentType 内容类型
     * @param content     内容流
     * @param name        文件名
     */
    public DownloadedFile(String contentType, InputStream content, String name) {
        super(contentType, 0, content, name);
    }

    /**
     * 下载文件
     *
     * @param contentType 内容类型
     * @param content     内容流
     * @param name        文件名
     */
    public DownloadedFile(String contentType, byte[] content, String name) {
        super(contentType, content.length, new ByteArrayInputStream(content), name);
    }

    /**
     * 下载文件
     *
     * @param file 文件
     * @throws FileNotFoundException
     */
    public DownloadedFile(File file) throws FileNotFoundException {
        this(file, file.getName());
    }

    /**
     * 下载文件
     *
     * @param file 文件
     * @param name 名字
     * @throws FileNotFoundException
     * @since 2.5
     */
    public DownloadedFile(File file, String name) throws FileNotFoundException {
        this(file, name, Utils.mime(file.getName()));
    }

    /**
     * 下载文件
     *
     * @param file 文件
     * @param name 名字
     * @throws FileNotFoundException
     * @since 2.5
     */
    public DownloadedFile(File file, String name, String contentType) throws FileNotFoundException {
        super(contentType, file.length(), null, name);

        this.file = file;
        //记录更新时间
        lastModified(new Date(file.lastModified()));
    }
}