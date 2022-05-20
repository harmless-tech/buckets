package tech.harmless.buckets.data.downloadtoken

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Service
import java.util.Optional

@Document("download_tokens")
data class DownloadTokenItem(
    @Id val id: ObjectId = ObjectId.get(),
    @Indexed(name = "dl_token", unique = true) val dlToken: String,
    val permissions: Int,
)

interface DownloadTokenItemRepo : MongoRepository<DownloadTokenItem, String> {

    @Query("{id:  '?0'}")
    fun findById(id: ObjectId): Optional<DownloadTokenItem>

    @Query("{dlToken:  '?0'}")
    fun findDownloadTokenItemByDlToken(dlToken: String): Optional<DownloadTokenItem>

    override fun count(): Long
}

@Service
class DownloadTokenService(
    @Autowired private val dlTokenRepo: DownloadTokenItemRepo
) {
    fun save(dlToken: String, perms: Int): Boolean {
        if (dlTokenRepo.findDownloadTokenItemByDlToken(dlToken).isEmpty) {
            dlTokenRepo.save(DownloadTokenItem(dlToken = dlToken, permissions = perms))
            return true
        }
        return false
    }
}
