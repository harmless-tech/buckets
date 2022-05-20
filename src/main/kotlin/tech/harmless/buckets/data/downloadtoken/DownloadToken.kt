package tech.harmless.buckets.data.downloadtoken

enum class DownloadTokenPermissions(
    permission: Int
) {
    ONE(0x000001),
    TWO(0x000002),
    THREE(0x000004),
    FOUR(0x000008),
}

fun downloadTokenHasPermission(permissions: Int, permission: Int): Boolean {
    return (permissions and permission) > 0
}
