package main.kotlin.com.camo.repos
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.database.DatabaseManager
import main.kotlin.com.camo.models.Product

class MySQLAdminRepo(private val db: DatabaseManager) : AdminRepoIF {
    override fun getProposals(filter: Boolean?): ArrayList<Product>? = db.getProposals(filter)
    override fun approveProposal(productId: Int): Boolean = db.approveProposal(productId)
    override fun removeProduct(productId: Int): Boolean = db.removeProduct(productId)
    override fun approveTransaction(transactionId: String, amount: Int): Boolean = db.approveTransaction(transactionId, amount)
}
