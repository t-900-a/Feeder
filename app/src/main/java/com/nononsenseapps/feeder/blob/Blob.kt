package com.nononsenseapps.feeder.blob

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.net.URLEncoder
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun blobFile(url: URL, filesDir: File): File =
    File(filesDir, "${URLEncoder.encode(url.toString(), "utf8")}.txt.gz")

fun blobFile(itemId: Long, filesDir: File): File =
    File(filesDir, "$itemId.txt.gz")

@Throws(IOException::class)
fun blobInputStream(itemId: Long, filesDir: File): InputStream =
    blobInputStreamFromFile(blobFile(itemId = itemId, filesDir = filesDir))

@Throws(IOException::class)
fun blobOutputStream(itemId: Long, filesDir: File): OutputStream =
    blobOutputStreamFromFile(blobFile(itemId = itemId, filesDir = filesDir))

fun blobFullFile(itemId: Long, filesDir: File): File =
    File(filesDir, "$itemId.full.html.gz")

@Throws(IOException::class)
fun blobFullInputStream(itemId: Long, filesDir: File): InputStream =
    blobInputStreamFromFile(blobFullFile(itemId = itemId, filesDir = filesDir))

@Throws(IOException::class)
fun blobFullOutputStream(itemId: Long, filesDir: File): OutputStream =
    blobOutputStreamFromFile(blobFullFile(itemId = itemId, filesDir = filesDir))

@Throws(IOException::class)
fun blobInputStreamFromFile(file: File): InputStream =
    GZIPInputStream(file.inputStream())

@Throws(IOException::class)
fun blobOutputStreamFromFile(file: File): OutputStream =
    GZIPOutputStream(file.outputStream())

//@Throws(IOException::class)
//fun blobInputStream(url: URL, filesDir: File): InputStream =
//    blobInputStreamFromFile(blobFile(url = url, filesDir = filesDir))
//
@Throws(IOException::class)
fun blobOutputStream(url: URL, filesDir: File): OutputStream =
    blobOutputStreamFromFile(blobFile(url = url, filesDir = filesDir))
