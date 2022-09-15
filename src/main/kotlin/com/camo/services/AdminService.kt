package main.kotlin.com.camo.services
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.models.Product
import main.kotlin.com.camo.repos.AdminRepoIF

class AdminService(
    private val repo: AdminRepoIF
) {
    fun getProposals(filter: Boolean?): ArrayList<Product>? = repo.getProposals(filter)
    fun approveProposal(productId: Int): Boolean = repo.approveProposal(productId)
    fun removeProduct(productId: Int): Boolean = repo.removeProduct(productId)
    fun approveTransaction(transactionId: String, amount: Int): Boolean = repo.approveTransaction(transactionId, amount)
}
