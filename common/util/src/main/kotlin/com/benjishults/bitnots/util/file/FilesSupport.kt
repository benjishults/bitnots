package com.benjishults.bitnots.util.file

val allowedInFilenames =
    (('a'..'z') + ('A'..'Z') +
            ('0'..'9') + '~' +
            '!' + '^' + '+' +
            '-' + '_' + '=' + ' ').toTypedArray()

fun String?.isValidFileName(): Boolean {
    return this?.let { this.all { it in allowedInFilenames } } ?: false
}
