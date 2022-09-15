package main.kotlin.com.camo.repos
/* ktlint-disable no-wildcard-imports */
import main.kotlin.com.camo.models.Product

interface AdminRepoIF {
    fun getProposals(filter: Boolean?): ArrayList<Product>?
    fun approveProposal(productId: Int): Boolean
    fun removeProduct(productId: Int): Boolean
    fun approveTransaction(transactionId: String, amount: Int): Boolean
}
