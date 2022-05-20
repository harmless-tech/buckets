package tech.harmless.buckets.security.oauth.github

import tech.harmless.buckets.util.Tuple

object GitHubAdmins {
    val usernames: Array<String>
    val ids: Array<String>
    val organizations: Array<String>
    val teams: Array<Tuple<String, String>>

    init {
        val env1 = System.getenv("GH_ADMIN_USERNAMES")
        usernames = if (env1 != null) {
            val usernames = env1.split(",").map { user -> user.trim() }
            usernames.toTypedArray()
        } else {
            arrayOf()
        }

        val env2 = System.getenv("GH_ADMIN_IDS")
        ids = if (env2 != null) {
            val ids = env2.split(",").map { id -> id.trim() }
            ids.toTypedArray()
        } else {
            arrayOf()
        }

        val env3 = System.getenv("GH_ADMIN_ORGS")
        organizations = if (env3 != null) {
            val o = env3.split(",").map { org -> org.trim() }
            o.toTypedArray()
        } else {
            arrayOf()
        }

        val env4 = System.getenv("GH_ADMIN_TEAMS")
        teams = if (env4 != null) {
            val t = env4.split(",").map { c ->
                val s = c.trim().split("&")
                if (s.size < 2) { Tuple("", "") } else { Tuple(s[0].trim(), s[1].trim()) }
            }
            t.toTypedArray()
        } else {
            arrayOf()
        }
    }
}
